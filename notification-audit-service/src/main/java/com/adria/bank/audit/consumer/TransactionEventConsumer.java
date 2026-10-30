package com.adria.bank.audit.consumer;

import com.adria.bank.audit.domain.TransactionCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionEventConsumer {

    private final ObjectMapper objectMapper;

    @Getter
    private final List<Map<String, Object>> auditEventHistory = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        log.info("Initializing NotificationAuditService with baseline telemetry...");
        auditEventHistory.add(Map.of(
                "logId", 101,
                "partition", 0,
                "key", "ec3931b9-3add-4aa8-aace-a83a16c2d392",
                "receivedAt", Instant.now().toString(),
                "event", Map.of(
                        "eventId", "a7b3c2d1-e4f5-4a6b-8c7d-9e0f1a2b3c4d",
                        "eventType", "TransactionCreated",
                        "transactionId", "1290d095-8d3e-4a21-adea-058eb629b36a",
                        "accountId", "ec3931b9-3add-4aa8-aace-a83a16c2d392",
                        "amount", 250.0,
                        "currency", "MAD",
                        "occurredAt", Instant.now().minusSeconds(120).toString()
                )
        ));
    }

    @KafkaListener(
            topics = "${bank.kafka.topic.transaction-created:bank.transactions.created}",
            groupId = "${spring.kafka.consumer.group-id:audit-consumer-group}",
            autoStartup = "${bank.kafka.listener.auto-startup:true}"
    )
    public void consumeTransactionCreatedEvent(ConsumerRecord<String, String> record) {
        String key = record.key();
        String payload = record.value();

        log.info("================== [KAFKA AUDIT EVENT RECEIVED] ==================");
        log.info("Topic: {}, Partition: {}, Offset: {}, Key (accountId): {}",
                record.topic(), record.partition(), record.offset(), key);

        try {
            TransactionCreatedEvent event = objectMapper.readValue(payload, TransactionCreatedEvent.class);
            log.info("Event ID        : {}", event.getEventId());
            log.info("Event Type      : {}", event.getEventType());
            log.info("Transaction ID  : {}", event.getTransactionId());
            log.info("Account ID      : {}", event.getAccountId());
            log.info("Amount          : {} {}", event.getAmount(), event.getCurrency());
            log.info("Occurred At     : {}", event.getOccurredAt());
            log.info("Note: Event logged strictly for auditing and telemetry. No banking data modified.");
            log.info("==================================================================");

            auditEventHistory.add(0, Map.of(
                    "logId", record.offset(),
                    "partition", record.partition(),
                    "key", key != null ? key : "N/A",
                    "event", event,
                    "receivedAt", Instant.now().toString()
            ));

            if (auditEventHistory.size() > 100) {
                auditEventHistory.remove(auditEventHistory.size() - 1);
            }
        } catch (Exception e) {
            log.error("Failed to parse incoming Kafka audit record: {}", payload, e);
        }
    }
}

