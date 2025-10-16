package com.sunfinance.verification.config;

import java.time.Duration;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@ConfigurationProperties(prefix = "verification")
public class VerificationConfig {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VerificationConfig.class);

    private int codeLength = 6; // default
    private Duration validityPeriod = Duration.ofMinutes(5); // default 5 minutes

    public int getCodeLength() { return codeLength; }
    public void setCodeLength(int codeLength) { this.codeLength = codeLength; }

    public Duration getValidityPeriod() { return validityPeriod; }
    public void setValidityPeriod(Duration validityPeriod) { this.validityPeriod = validityPeriod; }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @PostConstruct
    public void logConfig() {
        log.info("Verification TTL = " + validityPeriod);
    }
}
