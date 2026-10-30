package com.adria.bank.account.web.graphql;

import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.domain.dto.AccountRequestDTO;
import com.adria.bank.account.domain.dto.AccountResponseDTO;
import com.adria.bank.account.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AccountGraphQLController {

    private final AccountService accountService;

    @QueryMapping
    public AccountResponseDTO getAccountById(@Argument String id) {
        log.info("GraphQL Query getAccountById: {}", id);
        return accountService.getAccountById(UUID.fromString(id));
    }

    @QueryMapping
    public List<AccountResponseDTO> allAccounts() {
        log.info("GraphQL Query allAccounts");
        return accountService.getAllAccounts();
    }

    @QueryMapping
    public boolean isAccountActive(@Argument String id) {
        log.info("GraphQL Query isAccountActive: {}", id);
        return accountService.checkAccountActive(UUID.fromString(id));
    }

    @MutationMapping
    public AccountResponseDTO createAccount(@Argument Map<String, Object> input) {
        log.info("GraphQL Mutation createAccount: {}", input);
        String customerRef = (String) input.get("customerReference");
        String currency = (String) input.get("currency");
        String statusStr = (String) input.getOrDefault("status", "ACTIVE");
        Number balanceNum = (Number) input.getOrDefault("balance", 0.0);

        AccountRequestDTO dto = AccountRequestDTO.builder()
                .customerReference(customerRef)
                .currency(currency)
                .status(AccountStatus.valueOf(statusStr.toUpperCase()))
                .balance(balanceNum.doubleValue())
                .build();

        return accountService.createAccount(dto);
    }

    @MutationMapping
    public AccountResponseDTO updateAccountBalance(
            @Argument String id,
            @Argument Double amount,
            @Argument String operationType) {
        log.info("GraphQL Mutation updateAccountBalance: id={}, amount={}, type={}", id, amount, operationType);
        return accountService.updateBalance(UUID.fromString(id), amount, operationType);
    }

    @MutationMapping
    public AccountResponseDTO updateAccountStatus(@Argument String id, @Argument String status) {
        log.info("GraphQL Mutation updateAccountStatus: id={}, status={}", id, status);
        return accountService.updateStatus(UUID.fromString(id), AccountStatus.valueOf(status.toUpperCase()));
    }
}

