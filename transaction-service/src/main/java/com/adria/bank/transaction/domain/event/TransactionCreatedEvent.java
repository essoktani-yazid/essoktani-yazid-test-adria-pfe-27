package com.adria.bank.transaction.domain.event;

import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionCreatedEvent {

    private UUID eventId;
    private String eventType; // "TransactionCreated"
    private UUID transactionId;
    private UUID accountId;
    private BigDecimal amount;
    private String currency;
    private Instant occurredAt;
}

