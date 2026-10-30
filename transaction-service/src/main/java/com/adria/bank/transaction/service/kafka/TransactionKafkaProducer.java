package com.adria.bank.transaction.service.kafka;

import com.adria.bank.transaction.domain.event.TransactionCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionKafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${bank.kafka.topic.transaction-created:bank.transactions.created}")
    private String topicName;

    public void publishTransactionCreated(TransactionCreatedEvent event) {
        CompletableFuture.runAsync(() -> {
            try {
                String key = event.getAccountId() != null ? event.getAccountId().toString() : UUID.randomUUID().toString();
                String jsonPayload = objectMapper.writeValueAsString(event);
                log.info("Publishing TransactionCreated event to Kafka topic '{}' with key '{}': {}", topicName, key, jsonPayload);
                kafkaTemplate.send(topicName, key, jsonPayload).whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Kafka event successfully delivered to partition {} offset {}",
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.warn("Kafka event publishing encountered warning (Kafka broker might be offline): {}", ex.getMessage());
                    }
                });
            } catch (Exception e) {
                log.error("Failed to serialize or dispatch Kafka event: {}", e.getMessage(), e);
            }
        });
    }
}

