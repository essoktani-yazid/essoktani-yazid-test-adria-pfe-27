package com.adria.bank.account;

import com.adria.bank.account.domain.Account;
import com.adria.bank.account.domain.AccountStatus;
import com.adria.bank.account.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.time.Instant;
import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class AccountServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner initDatabase(AccountRepository accountRepository) {
        return args -> {
            accountRepository.save(Account.builder()
                    .id(UUID.fromString("412b6b04-4c67-4aea-bc82-f0a99091de4b"))
                    .customerReference("Yazid ESSOKTANI")
                    .currency("MAD")
                    .status(AccountStatus.ACTIVE)
                    .balance(50000.0)
                    .createdAt(Instant.parse("2026-10-30T10:05:06.953774Z"))
                    .build());

            accountRepository.save(Account.builder()
                    .id(UUID.fromString("77a9c995-e043-408e-ae21-032b13b106f7"))
                    .customerReference("Adria Enterprise Partner")
                    .currency("MAD")
                    .status(AccountStatus.ACTIVE)
                    .balance(125000.0)
                    .createdAt(Instant.parse("2026-10-30T10:05:07.021275Z"))
                    .build());

            accountRepository.save(Account.builder()
                    .id(UUID.fromString("d0bd3eca-ee24-4af9-92e3-b38033baf3bc"))
                    .customerReference("ENSET Mohammedia Lab")
                    .currency("EUR")
                    .status(AccountStatus.ACTIVE)
                    .balance(8500.0)
                    .createdAt(Instant.parse("2026-10-30T10:05:07.024557Z"))
                    .build());
        };
    }
}

