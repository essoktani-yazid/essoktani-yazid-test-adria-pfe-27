package com.adria.bank.account.service;

import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.domain.dto.AccountRequestDTO;
import com.adria.bank.account.domain.dto.AccountResponseDTO;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    AccountResponseDTO createAccount(AccountRequestDTO request);
    AccountResponseDTO getAccountById(UUID id);
    List<AccountResponseDTO> getAllAccounts();
    AccountResponseDTO updateBalance(UUID id, double amount, String operationType);
    AccountResponseDTO updateStatus(UUID id, AccountStatus status);
    AccountResponseDTO debit(UUID id, double amount);
    AccountResponseDTO credit(UUID id, double amount);
    boolean checkAccountActive(UUID id);
}

