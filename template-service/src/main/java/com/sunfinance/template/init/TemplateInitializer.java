package com.sunfinance.template.init;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.sunfinance.template.entity.Template;
import com.sunfinance.template.repository.TemplateRepository;

@Component
public class TemplateInitializer implements CommandLineRunner {
    private final TemplateRepository repository;

    public TemplateInitializer(TemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws IOException {
        if (repository.count() == 0) {
        	String mobileContent = readResourceFile("templates/mobile-verification.txt");
            String emailContent = readResourceFile("templates/email-verification.html");

            repository.save(new Template("mobile-verification", mobileContent, "plain"));
            repository.save(new Template("email-verification", emailContent, "html"));

            System.out.println("Default templates loaded into database.");
        }
    }
    
    private String readResourceFile(String path) throws IOException {
        try (var inputStream = new ClassPathResource(path).getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
