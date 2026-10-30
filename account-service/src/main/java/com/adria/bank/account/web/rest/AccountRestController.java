package com.adria.bank.account.web.rest;

import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.domain.dto.AccountRequestDTO;
import com.adria.bank.account.domain.dto.AccountResponseDTO;
import com.adria.bank.account.domain.dto.BalanceUpdateDTO;
import com.adria.bank.account.service.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Slf4j
public class AccountRestController {

    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@Valid @RequestBody AccountRequestDTO request) {
        log.info("REST request to create account: {}", request.getCustomerReference());
        AccountResponseDTO created = accountService.createAccount(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountResponseDTO> getAccountById(@PathVariable UUID id) {
        log.info("REST request to get account by id: {}", id);
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @GetMapping
    public ResponseEntity<List<AccountResponseDTO>> getAllAccounts() {
        log.info("REST request to get all accounts");
        return ResponseEntity.ok(accountService.getAllAccounts());
    }

    @RequestMapping(value = "/{id}/balance", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<AccountResponseDTO> updateBalance(
            @PathVariable UUID id,
            @Valid @RequestBody BalanceUpdateDTO balanceUpdate) {
        log.info("REST request to update balance for account {}: {} ({})", id, balanceUpdate.getAmount(), balanceUpdate.getOperationType());
        AccountResponseDTO updated = accountService.updateBalance(id, balanceUpdate.getAmount(), balanceUpdate.getOperationType());
        return ResponseEntity.ok(updated);
    }

    @RequestMapping(value = "/{id}/status", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public ResponseEntity<AccountResponseDTO> updateStatus(
            @PathVariable UUID id,
            @RequestBody Map<String, String> statusBody) {
        String statusStr = statusBody.get("status");
        AccountStatus status = AccountStatus.valueOf(statusStr.toUpperCase());
        log.info("REST request to update status of account {}: {}", id, status);
        return ResponseEntity.ok(accountService.updateStatus(id, status));
    }

    @GetMapping("/{id}/is-active")
    public ResponseEntity<Map<String, Object>> checkActive(@PathVariable UUID id) {
        boolean active = accountService.checkAccountActive(id);
        return ResponseEntity.ok(Map.of("id", id, "active", active));
    }
}

