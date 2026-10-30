package com.adria.bank.transaction.domain.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateDTO {
    private double amount;
    private String operationType; // "DEBIT" or "CREDIT"
}

