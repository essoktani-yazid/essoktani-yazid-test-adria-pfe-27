package com.adria.bank.transaction.web.rest;

import com.adria.bank.transaction.domain.dto.TransactionRequestDTO;
import com.adria.bank.transaction.domain.dto.TransactionResponseDTO;
import com.adria.bank.transaction.domain.dto.TransferRequestDTO;
import com.adria.bank.transaction.domain.dto.TransferResponseDTO;
import com.adria.bank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionRestController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@Valid @RequestBody TransactionRequestDTO request) {
        log.info("REST request to create transaction for account: {}", request.getAccountId());
        TransactionResponseDTO created = transactionService.createTransaction(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(@PathVariable UUID id) {
        log.info("REST request to get transaction: {}", id);
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(
            @RequestParam(required = false) UUID accountId) {
        if (accountId != null) {
            log.info("REST request to get transactions for account: {}", accountId);
            return ResponseEntity.ok(transactionService.getTransactionsByAccountId(accountId));
        }
        log.info("REST request to get all transactions");
        return ResponseEntity.ok(transactionService.getAllTransactions());
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransferResponseDTO> transferFunds(@Valid @RequestBody TransferRequestDTO request) {
        log.info("REST request for funds transfer from {} to {}", request.getSourceAccountId(), request.getTargetAccountId());
        TransferResponseDTO response = transactionService.transferFunds(request);
        return ResponseEntity.ok(response);
    }
}

