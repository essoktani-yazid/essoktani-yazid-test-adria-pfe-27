package com.adria.bank.account.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BalanceUpdateDTO {

    @DecimalMin(value = "0.01", message = "Amount must be strictly positive")
    private double amount;

    @NotBlank(message = "Operation type is required (DEBIT or CREDIT)")
    private String operationType; // "DEBIT" or "CREDIT"
}

