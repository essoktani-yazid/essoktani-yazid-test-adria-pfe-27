package com.adria.bank.audit.web;

import com.adria.bank.audit.consumer.TransactionEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditRestController {

    private final TransactionEventConsumer eventConsumer;

    @GetMapping("/logs")
    public ResponseEntity<List<Map<String, Object>>> getAuditLogs() {
        log.info("REST request to fetch audit logs from Kafka consumer");
        return ResponseEntity.ok(eventConsumer.getAuditEventHistory());
    }

    @GetMapping("/health-check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "notification-audit-service",
                "totalAuditedEvents", eventConsumer.getAuditEventHistory().size()
        ));
    }
}

