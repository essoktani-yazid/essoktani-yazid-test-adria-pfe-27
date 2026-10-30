package com.adria.bank.account.domain.dto;

import com.adria.bank.account.domain.Account;
import com.adria.bank.account.domain.AccountStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountResponseDTO {

    private UUID id;
    private String customerReference;
    private AccountStatus status;
    private String currency;
    private double balance;
    private Instant createdAt;

    public static AccountResponseDTO fromEntity(Account account) {
        if (account == null) return null;
        return AccountResponseDTO.builder()
                .id(account.getId())
                .customerReference(account.getCustomerReference())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}

