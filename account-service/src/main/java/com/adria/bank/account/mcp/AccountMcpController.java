package com.adria.bank.account.mcp;

import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.domain.dto.AccountRequestDTO;
import com.adria.bank.account.domain.dto.AccountResponseDTO;
import com.adria.bank.account.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@Slf4j
public class AccountMcpController {

    private final AccountService accountService;
    private final ObjectMapper objectMapper;

    /**
     * MCP Protocol: Return list of tools offered by account-service
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> listTools() {
        log.info("MCP request: listing tools for account-service");
        List<Map<String, Object>> tools = List.of(
                Map.of(
                        "name", "get_account_details",
                        "description", "Retrieve detailed banking account information by UUID",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "accountId", Map.of("type", "string", "description", "The UUID of the bank account")
                                ),
                                "required", List.of("accountId")
                        )
                ),
                Map.of(
                        "name", "list_all_accounts",
                        "description", "Retrieve all bank accounts currently registered in the banking platform",
                        "inputSchema", Map.of("type", "object", "properties", Map.of())
                ),
                Map.of(
                        "name", "check_account_active",
                        "description", "Verify if a bank account exists and is in ACTIVE status",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "accountId", Map.of("type", "string", "description", "The UUID of the bank account")
                                ),
                                "required", List.of("accountId")
                        )
                ),
                Map.of(
                        "name", "create_new_account",
                        "description", "Create a new bank account with customer reference, currency, and initial balance",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "customerReference", Map.of("type", "string", "description", "Customer name or reference"),
                                        "currency", Map.of("type", "string", "description", "3-letter currency code (e.g. EUR, MAD, USD)"),
                                        "balance", Map.of("type", "number", "description", "Initial balance")
                                ),
                                "required", List.of("customerReference", "currency")
                        )
                )
        );

        return ResponseEntity.ok(Map.of("tools", tools));
    }

    /**
     * MCP Protocol: Call a tool by name with arguments
     */
    @PostMapping("/tools/call")
    public ResponseEntity<Map<String, Object>> callTool(@RequestBody Map<String, Object> callRequest) {
        String toolName = (String) callRequest.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) callRequest.getOrDefault("arguments", Collections.emptyMap());

        log.info("MCP Tool execution requested: '{}' with args: {}", toolName, arguments);

        try {
            Object result;
            switch (toolName) {
                case "get_account_details" -> {
                    UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                    result = accountService.getAccountById(accountId);
                }
                case "list_all_accounts" -> {
                    result = accountService.getAllAccounts();
                }
                case "check_account_active" -> {
                    UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                    boolean active = accountService.checkAccountActive(accountId);
                    result = Map.of("accountId", accountId, "active", active);
                }
                case "create_new_account" -> {
                    String customerRef = (String) arguments.get("customerReference");
                    String currency = (String) arguments.get("currency");
                    Number bal = (Number) arguments.getOrDefault("balance", 0.0);
                    AccountRequestDTO dto = AccountRequestDTO.builder()
                            .customerReference(customerRef)
                            .currency(currency)
                            .status(AccountStatus.ACTIVE)
                            .balance(bal.doubleValue())
                            .build();
                    result = accountService.createAccount(dto);
                }
                default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
            }

            String jsonResult = objectMapper.writeValueAsString(result);
            return ResponseEntity.ok(Map.of(
                    "content", List.of(Map.of("type", "text", "text", jsonResult)),
                    "isError", false
            ));
        } catch (Exception e) {
            log.error("Error executing MCP tool '{}': {}", toolName, e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "content", List.of(Map.of("type", "text", "text", "Error: " + e.getMessage())),
                    "isError", true
            ));
        }
    }
}

