package com.adria.bank.transaction.client;

import com.adria.bank.transaction.domain.dto.AccountDTO;
import com.adria.bank.transaction.domain.dto.BalanceUpdateDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${account.service.url:http://localhost:8081}", fallback = AccountFeignClientFallback.class)
public interface AccountFeignClient {

    @GetMapping("/api/accounts/{id}")
    AccountDTO getAccountById(@PathVariable("id") UUID id);

    @PutMapping("/api/accounts/{id}/balance")
    AccountDTO updateBalance(@PathVariable("id") UUID id, @RequestBody BalanceUpdateDTO balanceUpdate);
}

