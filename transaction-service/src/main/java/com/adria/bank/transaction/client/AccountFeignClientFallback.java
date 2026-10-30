package com.adria.bank.transaction.client;

import com.adria.bank.transaction.domain.dto.AccountDTO;
import com.adria.bank.transaction.domain.dto.BalanceUpdateDTO;
import com.adria.bank.transaction.exception.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class AccountFeignClientFallback implements AccountFeignClient {

    @Override
    public AccountDTO getAccountById(UUID id) {
        log.warn("Resilience4J Fallback triggered: account-service is currently unavailable while fetching account: {}", id);
        throw new ServiceUnavailableException("Account service is temporarily unavailable. Please retry later.");
    }

    @Override
    public AccountDTO updateBalance(UUID id, BalanceUpdateDTO balanceUpdate) {
        log.warn("Resilience4J Fallback triggered: account-service is currently unavailable while updating balance for account: {}", id);
        throw new ServiceUnavailableException("Account service is temporarily unavailable. Balance update could not be completed.");
    }
}

