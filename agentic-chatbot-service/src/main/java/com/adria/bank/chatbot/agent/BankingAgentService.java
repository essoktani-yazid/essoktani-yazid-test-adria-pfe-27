package com.adria.bank.chatbot.agent;

import com.adria.bank.chatbot.domain.dto.ChatMessageDTO;
import com.adria.bank.chatbot.domain.dto.ChatResponseDTO;
import com.adria.bank.chatbot.mcp.McpClientService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class BankingAgentService {

    private final McpClientService mcpClientService;
    private final ObjectMapper objectMapper;

    // Pattern for UUID extraction
    private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
    // Pattern for numbers/amounts
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("(\\d+(\\.\\d{1,2})?)");

    public ChatResponseDTO processUserMessage(ChatMessageDTO messageDto) {
        String query = messageDto.getMessage() != null ? messageDto.getMessage().trim() : "";
        log.info("Agent received user prompt: '{}'", query);

        List<String> toolsInvoked = new ArrayList<>();
        List<Map<String, Object>> toolResults = new ArrayList<>();
        String reply;

        String lower = query.toLowerCase();

        // 1. Intent: List accounts
        if (lower.contains("liste") || lower.contains("comptes") || lower.contains("all accounts") || lower.contains("mes comptes")) {
            toolsInvoked.add("account-service:list_all_accounts");
            Map<String, Object> mcpResponse = mcpClientService.executeAccountTool("list_all_accounts", Collections.emptyMap());
            toolResults.add(mcpResponse);
            reply = formatAccountsList(mcpResponse);
        }
        // 2. Intent: Check balance / account details
        else if (lower.contains("solde") || lower.contains("balance") || lower.contains("consulter") || lower.contains("info")) {
            Matcher uuidMatcher = UUID_PATTERN.matcher(query);
            if (uuidMatcher.find()) {
                String accountId = uuidMatcher.group();
                toolsInvoked.add("account-service:get_account_details");
                Map<String, Object> mcpResponse = mcpClientService.executeAccountTool("get_account_details", Map.of("accountId", accountId));
                toolResults.add(mcpResponse);
                reply = formatAccountDetails(mcpResponse, accountId);
            } else {
                // If no UUID given, fetch all accounts so user can see them
                toolsInvoked.add("account-service:list_all_accounts");
                Map<String, Object> mcpResponse = mcpClientService.executeAccountTool("list_all_accounts", Collections.emptyMap());
                toolResults.add(mcpResponse);
                reply = "Voici la liste de vos comptes avec leurs soldes actuels :\n\n" + formatAccountsList(mcpResponse);
            }
        }
        // 3. Intent: Transfer money
        else if (lower.contains("virement") || lower.contains("transfer") || lower.contains("envoyer")) {
            List<String> foundUuids = new ArrayList<>();
            Matcher uuidMatcher = UUID_PATTERN.matcher(query);
            while (uuidMatcher.find()) {
                foundUuids.add(uuidMatcher.group());
            }

            Matcher amountMatcher = AMOUNT_PATTERN.matcher(query);
            Double amount = null;
            if (amountMatcher.find()) {
                try {
                    amount = Double.parseDouble(amountMatcher.group(1));
                } catch (Exception ignored) {}
            }

            if (foundUuids.size() >= 2 && amount != null) {
                String src = foundUuids.get(0);
                String tgt = foundUuids.get(1);
                String currency = lower.contains("eur") ? "EUR" : "MAD";

                toolsInvoked.add("transaction-service:transfer_funds_between_accounts");
                Map<String, Object> args = Map.of(
                        "sourceAccountId", src,
                        "targetAccountId", tgt,
                        "amount", amount,
                        "currency", currency,
                        "description", "Virement automatisé via Agent IA"
                );
                Map<String, Object> mcpResponse = mcpClientService.executeTransactionTool("transfer_funds_between_accounts", args);
                toolResults.add(mcpResponse);
                reply = formatTransferResult(mcpResponse, amount, currency, src, tgt);
            } else {
                reply = "Pour effectuer un virement automatique, veuillez préciser le compte source (UUID), le compte destinataire (UUID) et le montant.\n"
                        + "Exemple : `Effectuer un virement de 100 MAD depuis <UUID_SOURCE> vers <UUID_DEST>`";
            }
        }
        // 4. Intent: Transactions history
        else if (lower.contains("historique") || lower.contains("transactions") || lower.contains("history")) {
            Matcher uuidMatcher = UUID_PATTERN.matcher(query);
            if (uuidMatcher.find()) {
                String accountId = uuidMatcher.group();
                toolsInvoked.add("transaction-service:get_account_transactions");
                Map<String, Object> mcpResponse = mcpClientService.executeTransactionTool("get_account_transactions", Map.of("accountId", accountId));
                toolResults.add(mcpResponse);
                reply = "Historique des transactions pour le compte `" + accountId + "` :\n\n" + formatTransactionsList(mcpResponse);
            } else {
                toolsInvoked.add("transaction-service:list_all_transactions");
                Map<String, Object> mcpResponse = mcpClientService.executeTransactionTool("list_all_transactions", Collections.emptyMap());
                toolResults.add(mcpResponse);
                reply = "Historique global des transactions bancaires récentes :\n\n" + formatTransactionsList(mcpResponse);
            }
        }
        // 5. Help or generic greeting
        else {
            reply = """
                    Bonjour ! Je suis votre **Assistant Bancaire Intelligent ADRIA**.
                    Je suis directement connecté aux serveurs **MCP** (Model Context Protocol) des microservices de la banque.
                    
                    Voici les opérations que je peux exécuter pour vous :
                    - 📋 **Consulter les comptes** : *"Affiche tous mes comptes"* ou *"Quel est le solde du compte <UUID> ?"*
                    - 💸 **Virements bancaires** : *"Faire un virement de 200 MAD de <UUID_SOURCE> vers <UUID_DEST>"*
                    - 📜 **Historique des opérations** : *"Affiche l'historique des transactions"*
                    - 💳 **Nouvelle transaction** : *"Créer un paiement de 50 MAD..."*
                    
                    Que souhaitez-vous accomplir aujourd'hui ?
                    """;
        }

        return ChatResponseDTO.builder()
                .reply(reply)
                .toolsInvoked(toolsInvoked)
                .toolResults(toolResults)
                .timestamp(Instant.now())
                .build();
    }

    private String formatAccountsList(Map<String, Object> mcpResponse) {
        try {
            if (mcpResponse != null && mcpResponse.get("content") instanceof List contentList && !contentList.isEmpty()) {
                Map first = (Map) contentList.get(0);
                String text = (String) first.get("text");
                List<Map<String, Object>> accounts = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {});
                if (accounts.isEmpty()) return "Aucun compte bancaire trouvé.";

                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> acc : accounts) {
                    sb.append(String.format("• **%s** (%s) — Solde : **%.2f %s** | Statut : `%s`\n  ID : `%s`\n\n",
                            acc.get("customerReference"),
                            acc.get("currency"),
                            ((Number) acc.get("balance")).doubleValue(),
                            acc.get("currency"),
                            acc.get("status"),
                            acc.get("id")));
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("Error formatting accounts list: {}", e.getMessage());
        }
        return "Impossible de récupérer les comptes bancaires via MCP.";
    }

    private String formatAccountDetails(Map<String, Object> mcpResponse, String accountId) {
        try {
            if (mcpResponse != null && mcpResponse.get("content") instanceof List contentList && !contentList.isEmpty()) {
                Map first = (Map) contentList.get(0);
                String text = (String) first.get("text");
                if (text.startsWith("Error")) return "❌ Erreur : " + text;

                Map<String, Object> acc = objectMapper.readValue(text, new TypeReference<Map<String, Object>>() {});
                return String.format("Détails du compte :\n• **Titulaire** : %s\n• **Solde** : **%.2f %s**\n• **Statut** : `%s`\n• **Identifiant** : `%s`",
                        acc.get("customerReference"),
                        ((Number) acc.get("balance")).doubleValue(),
                        acc.get("currency"),
                        acc.get("status"),
                        acc.get("id"));
            }
        } catch (Exception e) {
            log.error("Error formatting account details: {}", e.getMessage());
        }
        return "Compte introuvable ou erreur de communication MCP pour l'ID : " + accountId;
    }

    private String formatTransferResult(Map<String, Object> mcpResponse, Double amount, String currency, String src, String tgt) {
        try {
            if (mcpResponse != null && mcpResponse.get("content") instanceof List contentList && !contentList.isEmpty()) {
                Map first = (Map) contentList.get(0);
                String text = (String) first.get("text");
                if (text.startsWith("Error")) {
                    return "❌ **Échec du virement** : " + text;
                }
                return String.format("✅ **Virement exécuté avec succès !**\n\n"
                                + "• **Montant** : %.2f %s\n"
                                + "• **Depuis** : `%s`\n"
                                + "• **Vers** : `%s`\n"
                                + "• Événements asynchrones publiés dans Kafka (`bank.transactions.created`).\n"
                                + "• Les soldes des comptes ont été actualisés en temps réel.",
                        amount, currency, src, tgt);
            }
        } catch (Exception e) {
            log.error("Error formatting transfer result: {}", e.getMessage());
        }
        return "❌ Une erreur est survenue lors de l'exécution du virement bancaire.";
    }

    private String formatTransactionsList(Map<String, Object> mcpResponse) {
        try {
            if (mcpResponse != null && mcpResponse.get("content") instanceof List contentList && !contentList.isEmpty()) {
                Map first = (Map) contentList.get(0);
                String text = (String) first.get("text");
                List<Map<String, Object>> txs = objectMapper.readValue(text, new TypeReference<List<Map<String, Object>>>() {});
                if (txs.isEmpty()) return "Aucune transaction enregistrée pour le moment.";

                StringBuilder sb = new StringBuilder();
                for (Map<String, Object> tx : txs) {
                    sb.append(String.format("• **%.2f %s** | Catégorie : `%s` | Pays : `%s`\n  Date : `%s`\n  ID : `%s`\n\n",
                            ((Number) tx.get("amount")).doubleValue(),
                            tx.get("currency"),
                            tx.get("merchantCategory"),
                            tx.get("country"),
                            tx.get("createdAt"),
                            tx.get("id")));
                }
                return sb.toString();
            }
        } catch (Exception e) {
            log.error("Error formatting transactions list: {}", e.getMessage());
        }
        return "Historique des transactions indisponible via MCP.";
    }
}

