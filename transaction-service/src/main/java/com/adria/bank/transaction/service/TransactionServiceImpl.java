package com.adria.bank.transaction.service;

import com.adria.bank.transaction.client.AccountFeignClient;
import com.adria.bank.transaction.domain.Transaction;
import com.adria.bank.transaction.domain.dto.*;
import com.adria.bank.transaction.domain.event.TransactionCreatedEvent;
import com.adria.bank.transaction.exception.TransactionNotFoundException;
import com.adria.bank.transaction.exception.TransactionValidationException;
import com.adria.bank.transaction.repository.TransactionRepository;
import com.adria.bank.transaction.service.kafka.TransactionKafkaProducer;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountFeignClient accountFeignClient;
    private final TransactionKafkaProducer kafkaProducer;

    @Value("${bank.transaction.max-amount:1000}")
    private double maxAmount;

    @Override
    @Transactional
    @CircuitBreaker(name = "accountService", fallbackMethod = "createTransactionFallback")
    public TransactionResponseDTO createTransaction(TransactionRequestDTO request) {
        log.info("Processing transaction request for account: {}, amount: {} {}",
                request.getAccountId(), request.getAmount(), request.getCurrency());

        // 1. Validation: Montant strictement positif
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Transaction amount must be strictly positive");
        }

        // 2. Validation: Respect du plafond configuré
        if (request.getAmount().doubleValue() > maxAmount) {
            throw new TransactionValidationException(
                    String.format("Transaction amount %.2f exceeds maximum configured limit of %.2f",
                            request.getAmount().doubleValue(), maxAmount));
        }

        // 3. Consultation du compte via OpenFeign
        AccountDTO account = accountFeignClient.getAccountById(request.getAccountId());
        if (account == null) {
            throw new TransactionValidationException("Bank account not found with ID: " + request.getAccountId());
        }

        // 4. Validation: Compte doit être actif
        if (!"ACTIVE".equalsIgnoreCase(account.getStatus())) {
            throw new TransactionValidationException("Account " + request.getAccountId() + " is " + account.getStatus() + ". Only ACTIVE accounts can transact.");
        }

        // 5. Validation: La devise doit correspondre à celle du compte
        if (!account.getCurrency().equalsIgnoreCase(request.getCurrency())) {
            throw new TransactionValidationException(
                    String.format("Transaction currency '%s' does not match account currency '%s'",
                            request.getCurrency(), account.getCurrency()));
        }

        // 6. Consultation du solde
        if (account.getBalance() < request.getAmount().doubleValue()) {
            throw new TransactionValidationException(
                    String.format("Insufficient funds. Account balance: %.2f %s, Requested: %.2f %s",
                            account.getBalance(), account.getCurrency(), request.getAmount().doubleValue(), request.getCurrency()));
        }

        // 7. Enregistrement de la transaction
        Transaction transaction = Transaction.builder()
                .accountId(request.getAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory(request.getMerchantCategory())
                .country(request.getCountry().toUpperCase())
                .createdAt(Instant.now())
                .build();

        Transaction savedTx = transactionRepository.save(transaction);
        log.info("Transaction persisted with ID: {}", savedTx.getId());

        // 8. Mise à jour du solde du compte après enregistrement
        accountFeignClient.updateBalance(request.getAccountId(),
                BalanceUpdateDTO.builder()
                        .amount(request.getAmount().doubleValue())
                        .operationType("DEBIT")
                        .build());
        log.info("Account {} balance debited by {}", request.getAccountId(), request.getAmount());

        // 9. Publication d'événement Kafka dans bank.transactions.created
        TransactionCreatedEvent event = TransactionCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TransactionCreated")
                .transactionId(savedTx.getId())
                .accountId(savedTx.getAccountId())
                .amount(savedTx.getAmount())
                .currency(savedTx.getCurrency())
                .occurredAt(savedTx.getCreatedAt())
                .build();
        kafkaProducer.publishTransactionCreated(event);

        return TransactionResponseDTO.fromEntity(savedTx);
    }

    public TransactionResponseDTO createTransactionFallback(TransactionRequestDTO request, Throwable throwable) {
        log.error("Resilience4J Fallback triggered in createTransaction: {}", throwable.getMessage());
        if (throwable instanceof TransactionValidationException) {
            throw (TransactionValidationException) throwable;
        }
        throw new TransactionValidationException("Transaction failed: downstream service error - " + throwable.getMessage());
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponseDTO getTransactionById(UUID id) {
        Transaction tx = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with ID: " + id));
        return TransactionResponseDTO.fromEntity(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getAllTransactions() {
        return transactionRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TransactionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId) {
        return transactionRepository.findByAccountIdOrderByCreatedAtDesc(accountId).stream()
                .map(TransactionResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TransferResponseDTO transferFunds(TransferRequestDTO request) {
        log.info("Processing transfer from {} to {} for amount {} {}",
                request.getSourceAccountId(), request.getTargetAccountId(), request.getAmount(), request.getCurrency());

        if (request.getSourceAccountId().equals(request.getTargetAccountId())) {
            throw new TransactionValidationException("Source and target accounts must be distinct");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransactionValidationException("Transfer amount must be strictly positive");
        }

        // Check source account
        AccountDTO sourceAccount = accountFeignClient.getAccountById(request.getSourceAccountId());
        if (sourceAccount == null || !"ACTIVE".equalsIgnoreCase(sourceAccount.getStatus())) {
            throw new TransactionValidationException("Source account is missing or not ACTIVE");
        }

        // Check target account
        AccountDTO targetAccount = accountFeignClient.getAccountById(request.getTargetAccountId());
        if (targetAccount == null || !"ACTIVE".equalsIgnoreCase(targetAccount.getStatus())) {
            throw new TransactionValidationException("Target account is missing or not ACTIVE");
        }

        if (!sourceAccount.getCurrency().equalsIgnoreCase(request.getCurrency())) {
            throw new TransactionValidationException("Transfer currency must match source account currency");
        }

        if (sourceAccount.getBalance() < request.getAmount().doubleValue()) {
            throw new TransactionValidationException("Insufficient balance in source account");
        }

        // Debit source
        accountFeignClient.updateBalance(request.getSourceAccountId(),
                BalanceUpdateDTO.builder()
                        .amount(request.getAmount().doubleValue())
                        .operationType("DEBIT")
                        .build());

        // Credit target
        accountFeignClient.updateBalance(request.getTargetAccountId(),
                BalanceUpdateDTO.builder()
                        .amount(request.getAmount().doubleValue())
                        .operationType("CREDIT")
                        .build());

        // Persist debit transaction
        Transaction debitTx = transactionRepository.save(Transaction.builder()
                .accountId(request.getSourceAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory("TRANSFER_OUT")
                .country("MA")
                .createdAt(Instant.now())
                .build());

        // Persist credit transaction
        Transaction creditTx = transactionRepository.save(Transaction.builder()
                .accountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .merchantCategory("TRANSFER_IN")
                .country("MA")
                .createdAt(Instant.now())
                .build());

        // Publish Kafka event for both
        kafkaProducer.publishTransactionCreated(TransactionCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TransactionCreated")
                .transactionId(debitTx.getId())
                .accountId(debitTx.getAccountId())
                .amount(debitTx.getAmount())
                .currency(debitTx.getCurrency())
                .occurredAt(debitTx.getCreatedAt())
                .build());

        kafkaProducer.publishTransactionCreated(TransactionCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType("TransactionCreated")
                .transactionId(creditTx.getId())
                .accountId(creditTx.getAccountId())
                .amount(creditTx.getAmount())
                .currency(creditTx.getCurrency())
                .occurredAt(creditTx.getCreatedAt())
                .build());

        return TransferResponseDTO.builder()
                .transferId(UUID.randomUUID())
                .sourceAccountId(request.getSourceAccountId())
                .targetAccountId(request.getTargetAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency().toUpperCase())
                .status("SUCCESS")
                .debitTransactionId(debitTx.getId())
                .creditTransactionId(creditTx.getId())
                .executedAt(Instant.now())
                .message("Transfer completed successfully")
                .build();
    }
}

