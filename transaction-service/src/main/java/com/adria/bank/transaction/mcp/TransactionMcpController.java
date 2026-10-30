package com.adria.bank.transaction.mcp;

import com.adria.bank.transaction.domain.dto.TransactionRequestDTO;
import com.adria.bank.transaction.domain.dto.TransferRequestDTO;
import com.adria.bank.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@Slf4j
public class TransactionMcpController {

    private final TransactionService transactionService;
    private final ObjectMapper objectMapper;

    /**
     * MCP Protocol: Return list of tools offered by transaction-service
     */
    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> listTools() {
        log.info("MCP request: listing tools for transaction-service");
        List<Map<String, Object>> tools = List.of(
                Map.of(
                        "name", "create_banking_transaction",
                        "description", "Record a new financial transaction against a bank account with amount, currency, merchant category and country",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "accountId", Map.of("type", "string", "description", "Bank account UUID"),
                                        "amount", Map.of("type", "number", "description", "Amount strictly positive up to configured limit"),
                                        "currency", Map.of("type", "string", "description", "3-letter currency code (e.g. MAD, EUR, USD)"),
                                        "merchantCategory", Map.of("type", "string", "description", "Category (e.g. GROCERY, TECH, RESTAURANT)"),
                                        "country", Map.of("type", "string", "description", "2-letter country code (e.g. MA, FR, US)")
                                ),
                                "required", List.of("accountId", "amount", "currency", "merchantCategory", "country")
                        )
                ),
                Map.of(
                        "name", "transfer_funds_between_accounts",
                        "description", "Transfer funds from a source bank account to a target bank account",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "sourceAccountId", Map.of("type", "string", "description", "Source account UUID"),
                                        "targetAccountId", Map.of("type", "string", "description", "Target account UUID"),
                                        "amount", Map.of("type", "number", "description", "Transfer amount strictly positive"),
                                        "currency", Map.of("type", "string", "description", "Currency code (e.g. MAD, EUR)"),
                                        "description", Map.of("type", "string", "description", "Optional transfer note")
                                ),
                                "required", List.of("sourceAccountId", "targetAccountId", "amount", "currency")
                        )
                ),
                Map.of(
                        "name", "get_account_transactions",
                        "description", "Fetch recent transaction history for a specific bank account",
                        "inputSchema", Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "accountId", Map.of("type", "string", "description", "Bank account UUID")
                                ),
                                "required", List.of("accountId")
                        )
                ),
                Map.of(
                        "name", "list_all_transactions",
                        "description", "Fetch all recorded transactions across all accounts",
                        "inputSchema", Map.of("type", "object", "properties", Map.of())
                )
        );

        return ResponseEntity.ok(Map.of("tools", tools));
    }

    /**
     * MCP Protocol: Execute a tool by name with arguments
     */
    @PostMapping("/tools/call")
    public ResponseEntity<Map<String, Object>> callTool(@RequestBody Map<String, Object> callRequest) {
        String toolName = (String) callRequest.get("name");
        @SuppressWarnings("unchecked")
        Map<String, Object> arguments = (Map<String, Object>) callRequest.getOrDefault("arguments", Collections.emptyMap());

        log.info("MCP Tool execution requested in transaction-service: '{}' with args: {}", toolName, arguments);

        try {
            Object result;
            switch (toolName) {
                case "create_banking_transaction" -> {
                    UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                    Number amount = (Number) arguments.get("amount");
                    String currency = (String) arguments.get("currency");
                    String merchantCat = (String) arguments.get("merchantCategory");
                    String country = (String) arguments.get("country");

                    TransactionRequestDTO dto = TransactionRequestDTO.builder()
                            .accountId(accountId)
                            .amount(BigDecimal.valueOf(amount.doubleValue()))
                            .currency(currency)
                            .merchantCategory(merchantCat)
                            .country(country)
                            .build();

                    result = transactionService.createTransaction(dto);
                }
                case "transfer_funds_between_accounts" -> {
                    UUID src = UUID.fromString((String) arguments.get("sourceAccountId"));
                    UUID tgt = UUID.fromString((String) arguments.get("targetAccountId"));
                    Number amount = (Number) arguments.get("amount");
                    String currency = (String) arguments.get("currency");
                    String desc = (String) arguments.getOrDefault("description", "MCP Transfer");

                    TransferRequestDTO dto = TransferRequestDTO.builder()
                            .sourceAccountId(src)
                            .targetAccountId(tgt)
                            .amount(BigDecimal.valueOf(amount.doubleValue()))
                            .currency(currency)
                            .description(desc)
                            .build();

                    result = transactionService.transferFunds(dto);
                }
                case "get_account_transactions" -> {
                    UUID accountId = UUID.fromString((String) arguments.get("accountId"));
                    result = transactionService.getTransactionsByAccountId(accountId);
                }
                case "list_all_transactions" -> {
                    result = transactionService.getAllTransactions();
                }
                default -> throw new IllegalArgumentException("Unknown tool: " + toolName);
            }

            String jsonResult = objectMapper.writeValueAsString(result);
            return ResponseEntity.ok(Map.of(
                    "content", List.of(Map.of("type", "text", "text", jsonResult)),
                    "isError", false
            ));
        } catch (Exception e) {
            log.error("Error executing MCP tool in transaction-service '{}': {}", toolName, e.getMessage());
            return ResponseEntity.ok(Map.of(
                    "content", List.of(Map.of("type", "text", "text", "Error: " + e.getMessage())),
                    "isError", true
            ));
        }
    }
}

