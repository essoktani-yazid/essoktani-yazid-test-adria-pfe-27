package com.adria.bank.account.service;

import com.adria.bank.account.domain.Account;
import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.domain.dto.AccountRequestDTO;
import com.adria.bank.account.domain.dto.AccountResponseDTO;
import com.adria.bank.account.exception.AccountNotFoundException;
import com.adria.bank.account.exception.BusinessValidationException;
import com.adria.bank.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public AccountResponseDTO createAccount(AccountRequestDTO request) {
        log.info("Creating bank account for customer: {}", request.getCustomerReference());
        Account account = Account.builder()
                .customerReference(request.getCustomerReference())
                .status(request.getStatus() != null ? request.getStatus() : AccountStatus.ACTIVE)
                .currency(request.getCurrency().toUpperCase())
                .balance(request.getBalance())
                .build();
        Account saved = accountRepository.save(account);
        log.info("Bank account successfully created with ID: {}", saved.getId());
        return AccountResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponseDTO getAccountById(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        return AccountResponseDTO.fromEntity(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccountResponseDTO> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public AccountResponseDTO updateBalance(UUID id, double amount, String operationType) {
        if ("DEBIT".equalsIgnoreCase(operationType)) {
            return debit(id, amount);
        } else if ("CREDIT".equalsIgnoreCase(operationType)) {
            return credit(id, amount);
        } else {
            throw new BusinessValidationException("Unsupported operation type: " + operationType + ". Allowed: DEBIT, CREDIT");
        }
    }

    @Override
    public AccountResponseDTO debit(UUID id, double amount) {
        if (amount <= 0) {
            throw new BusinessValidationException("Debit amount must be strictly positive");
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessValidationException("Cannot debit account " + id + ": Status is " + account.getStatus());
        }

        if (account.getBalance() < amount) {
            throw new BusinessValidationException("Insufficient funds. Available balance: " + account.getBalance() + " " + account.getCurrency());
        }

        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        Account saved = accountRepository.save(account);
        log.info("Debited {} {} from account {}. New balance: {}", amount, account.getCurrency(), id, newBalance);
        return AccountResponseDTO.fromEntity(saved);
    }

    @Override
    public AccountResponseDTO credit(UUID id, double amount) {
        if (amount <= 0) {
            throw new BusinessValidationException("Credit amount must be strictly positive");
        }
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));

        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new BusinessValidationException("Cannot credit account " + id + ": Status is " + account.getStatus());
        }

        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        Account saved = accountRepository.save(account);
        log.info("Credited {} {} to account {}. New balance: {}", amount, account.getCurrency(), id, newBalance);
        return AccountResponseDTO.fromEntity(saved);
    }

    @Override
    public AccountResponseDTO updateStatus(UUID id, AccountStatus status) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with ID: " + id));
        account.setStatus(status);
        Account saved = accountRepository.save(account);
        log.info("Updated account {} status to: {}", id, status);
        return AccountResponseDTO.fromEntity(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkAccountActive(UUID id) {
        return accountRepository.findById(id)
                .map(acc -> acc.getStatus() == AccountStatus.ACTIVE)
                .orElse(false);
    }
}

