
package com.sunfinance.verification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EntityScan(basePackages = {"com.sunfinance.common.model"})
@EnableJpaRepositories(basePackages = {"com.sunfinance.verification.repository"})
@ComponentScan(basePackages = {"com.sunfinance"})
@EnableScheduling
public class VerificationApplication {
    public static void main(String[] args) {
        SpringApplication.run(VerificationApplication.class, args);
    }
}
