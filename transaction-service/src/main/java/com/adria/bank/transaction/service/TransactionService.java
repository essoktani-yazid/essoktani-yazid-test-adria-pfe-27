package com.adria.bank.transaction.service;

import com.adria.bank.transaction.domain.dto.TransactionRequestDTO;
import com.adria.bank.transaction.domain.dto.TransactionResponseDTO;
import com.adria.bank.transaction.domain.dto.TransferRequestDTO;
import com.adria.bank.transaction.domain.dto.TransferResponseDTO;

import java.util.List;
import java.util.UUID;

public interface TransactionService {
    TransactionResponseDTO createTransaction(TransactionRequestDTO request);
    TransactionResponseDTO getTransactionById(UUID id);
    List<TransactionResponseDTO> getAllTransactions();
    List<TransactionResponseDTO> getTransactionsByAccountId(UUID accountId);
    TransferResponseDTO transferFunds(TransferRequestDTO transferRequest);
}

