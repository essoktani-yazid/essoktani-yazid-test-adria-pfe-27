package com.adria.bank.chatbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class AgenticChatbotApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgenticChatbotApplication.class, args);
    }
}

