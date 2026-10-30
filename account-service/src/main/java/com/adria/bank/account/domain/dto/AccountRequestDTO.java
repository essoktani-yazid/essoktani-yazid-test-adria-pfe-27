package com.adria.bank.account.domain.dto;

import com.adria.bank.account.domain.AccountStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountRequestDTO {

    @NotBlank(message = "Customer reference is required")
    private String customerReference;

    @NotNull(message = "Initial status is required")
    private AccountStatus status;

    @NotBlank(message = "Currency is required")
    @Size(min = 3, max = 3, message = "Currency must be a 3-letter ISO code (e.g. EUR, MAD, USD)")
    private String currency;

    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance cannot be negative")
    private double balance;
}

