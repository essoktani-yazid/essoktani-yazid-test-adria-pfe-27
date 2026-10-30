package com.adria.bank.transaction.web.graphql;

import com.adria.bank.transaction.domain.dto.TransactionRequestDTO;
import com.adria.bank.transaction.domain.dto.TransactionResponseDTO;
import com.adria.bank.transaction.domain.dto.TransferRequestDTO;
import com.adria.bank.transaction.domain.dto.TransferResponseDTO;
import com.adria.bank.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionGraphQLController {

    private final TransactionService transactionService;

    @QueryMapping
    public TransactionResponseDTO getTransactionById(@Argument String id) {
        log.info("GraphQL Query getTransactionById: {}", id);
        return transactionService.getTransactionById(UUID.fromString(id));
    }

    @QueryMapping
    public List<TransactionResponseDTO> allTransactions() {
        log.info("GraphQL Query allTransactions");
        return transactionService.getAllTransactions();
    }

    @QueryMapping
    public List<TransactionResponseDTO> getTransactionsByAccount(@Argument String accountId) {
        log.info("GraphQL Query getTransactionsByAccount: {}", accountId);
        return transactionService.getTransactionsByAccountId(UUID.fromString(accountId));
    }

    @MutationMapping
    public TransactionResponseDTO createTransaction(@Argument Map<String, Object> input) {
        log.info("GraphQL Mutation createTransaction: {}", input);
        String accountId = (String) input.get("accountId");
        Number amount = (Number) input.get("amount");
        String currency = (String) input.get("currency");
        String merchantCategory = (String) input.get("merchantCategory");
        String country = (String) input.get("country");

        TransactionRequestDTO dto = TransactionRequestDTO.builder()
                .accountId(UUID.fromString(accountId))
                .amount(BigDecimal.valueOf(amount.doubleValue()))
                .currency(currency)
                .merchantCategory(merchantCategory)
                .country(country)
                .build();

        return transactionService.createTransaction(dto);
    }

    @MutationMapping
    public TransferResponseDTO transferFunds(@Argument Map<String, Object> input) {
        log.info("GraphQL Mutation transferFunds: {}", input);
        String sourceAccountId = (String) input.get("sourceAccountId");
        String targetAccountId = (String) input.get("targetAccountId");
        Number amount = (Number) input.get("amount");
        String currency = (String) input.get("currency");
        String description = (String) input.getOrDefault("description", "Transfer");

        TransferRequestDTO dto = TransferRequestDTO.builder()
                .sourceAccountId(UUID.fromString(sourceAccountId))
                .targetAccountId(UUID.fromString(targetAccountId))
                .amount(BigDecimal.valueOf(amount.doubleValue()))
                .currency(currency)
                .description(description)
                .build();

        return transactionService.transferFunds(dto);
    }
}

