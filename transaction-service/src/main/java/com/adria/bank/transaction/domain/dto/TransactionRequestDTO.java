package com.adria.bank.transaction.domain.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionRequestDTO {

    @NotNull(message = "Account ID is required")
    private UUID accountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", inclusive = true, message = "Amount must be strictly positive")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be 3 uppercase letters (e.g. EUR, MAD, USD)")
    private String currency;

    @NotBlank(message = "Merchant category is required")
    private String merchantCategory;

    @NotBlank(message = "Country code is required (2 letters)")
    @Size(min = 2, max = 2, message = "Country code must be 2 letters (e.g. MA, FR, US)")
    private String country;
}

