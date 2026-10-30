package com.adria.bank.transaction.domain.dto;

import com.adria.bank.transaction.domain.Transaction;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionResponseDTO {

    private UUID id;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private String merchantCategory;
    private String country;
    private Instant createdAt;

    public static TransactionResponseDTO fromEntity(Transaction tx) {
        if (tx == null) return null;
        return TransactionResponseDTO.builder()
                .id(tx.getId())
                .accountId(tx.getAccountId())
                .amount(tx.getAmount())
                .currency(tx.getCurrency())
                .merchantCategory(tx.getMerchantCategory())
                .country(tx.getCountry())
                .createdAt(tx.getCreatedAt())
                .build();
    }
}

