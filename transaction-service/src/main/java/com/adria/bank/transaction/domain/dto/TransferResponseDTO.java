package com.adria.bank.transaction.domain.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDTO {

    private UUID transferId;
    private UUID sourceAccountId;
    private UUID targetAccountId;
    private BigDecimal amount;
    private String currency;
    private String status; // "SUCCESS", "FAILED"
    private UUID debitTransactionId;
    private UUID creditTransactionId;
    private Instant executedAt;
    private String message;
}

