package com.adria.bank.chatbot.web;

import com.adria.bank.chatbot.agent.BankingAgentService;
import com.adria.bank.chatbot.domain.dto.ChatMessageDTO;
import com.adria.bank.chatbot.domain.dto.ChatResponseDTO;
import com.adria.bank.chatbot.mcp.McpClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRestController {

    private final BankingAgentService bankingAgentService;
    private final McpClientService mcpClientService;

    @PostMapping
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatMessageDTO message) {
        log.info("REST chat request: '{}'", message.getMessage());
        return ResponseEntity.ok(bankingAgentService.processUserMessage(message));
    }

    @GetMapping("/tools")
    public ResponseEntity<Map<String, Object>> getMcpTools() {
        log.info("REST request to list all discovered MCP tools");
        return ResponseEntity.ok(mcpClientService.discoverAllTools());
    }

    @PostMapping("/telegram/webhook")
    public ResponseEntity<Map<String, Object>> telegramWebhook(@RequestBody Map<String, Object> telegramUpdate) {
        log.info("Telegram Webhook received: {}", telegramUpdate);
        String text = "Hello Telegram user!";
        if (telegramUpdate.containsKey("message") && telegramUpdate.get("message") instanceof Map msgMap) {
            text = (String) msgMap.getOrDefault("text", "Aide");
        }
        ChatResponseDTO response = bankingAgentService.processUserMessage(ChatMessageDTO.builder().message(text).build());
        return ResponseEntity.ok(Map.of(
                "method", "sendMessage",
                "text", response.getReply()
        ));
    }

    @PostMapping("/discord/webhook")
    public ResponseEntity<Map<String, Object>> discordWebhook(@RequestBody Map<String, Object> discordInteraction) {
        log.info("Discord Webhook interaction received: {}", discordInteraction);
        String text = (String) discordInteraction.getOrDefault("content", "Aide");
        ChatResponseDTO response = bankingAgentService.processUserMessage(ChatMessageDTO.builder().message(text).build());
        return ResponseEntity.ok(Map.of(
                "content", response.getReply()
        ));
    }
}

