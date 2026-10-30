package com.adria.bank.chatbot.mcp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class McpClientService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplateBuilder().build();

    @Value("${services.account.url:http://localhost:8081}")
    private String accountServiceUrl;

    @Value("${services.transaction.url:http://localhost:8082}")
    private String transactionServiceUrl;

    /**
     * Call an MCP tool on account-service
     */
    public Map<String, Object> executeAccountTool(String toolName, Map<String, Object> args) {
        String url = accountServiceUrl + "/mcp/tools/call";
        return executeMcpCall(url, toolName, args);
    }

    /**
     * Call an MCP tool on transaction-service
     */
    public Map<String, Object> executeTransactionTool(String toolName, Map<String, Object> args) {
        String url = transactionServiceUrl + "/mcp/tools/call";
        return executeMcpCall(url, toolName, args);
    }

    private Map<String, Object> executeMcpCall(String url, String toolName, Map<String, Object> args) {
        try {
            log.info("Dispatching MCP tool invocation '{}' to {}", toolName, url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = Map.of(
                    "name", toolName,
                    "arguments", args != null ? args : Collections.emptyMap()
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return objectMapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to execute MCP tool '{}' on {}: {}", toolName, url, e.getMessage());
            return Map.of("isError", true, "error", e.getMessage());
        }
        return Map.of("isError", true, "error", "Empty response from MCP server");
    }

    /**
     * Discovers all available MCP tools across microservices
     */
    public Map<String, Object> discoverAllTools() {
        List<Object> allTools = new ArrayList<>();
        try {
            Map resp = restTemplate.getForObject(accountServiceUrl + "/mcp/tools", Map.class);
            if (resp != null && resp.containsKey("tools")) {
                allTools.addAll((List) resp.get("tools"));
            }
        } catch (Exception e) {
            log.warn("Could not fetch tools from account-service: {}", e.getMessage());
        }

        try {
            Map resp = restTemplate.getForObject(transactionServiceUrl + "/mcp/tools", Map.class);
            if (resp != null && resp.containsKey("tools")) {
                allTools.addAll((List) resp.get("tools"));
            }
        } catch (Exception e) {
            log.warn("Could not fetch tools from transaction-service: {}", e.getMessage());
        }

        return Map.of("tools", allTools, "count", allTools.size());
    }
}

