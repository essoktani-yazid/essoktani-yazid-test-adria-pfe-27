package com.adria.bank.chatbot.domain.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponseDTO {
    private String reply;
    private List<String> toolsInvoked;
    private List<Map<String, Object>> toolResults;
    private Instant timestamp;
}

