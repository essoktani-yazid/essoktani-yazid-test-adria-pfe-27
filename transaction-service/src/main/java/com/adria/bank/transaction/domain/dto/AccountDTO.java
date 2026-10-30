package com.adria.bank.transaction.domain.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
    private UUID id;
    private String customerReference;
    private String status; // "ACTIVE", "BLOCKED"
    private String currency;
    private double balance;
}

