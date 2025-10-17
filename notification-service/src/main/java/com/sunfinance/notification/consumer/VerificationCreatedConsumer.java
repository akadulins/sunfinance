package com.sunfinance.notification.consumer;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunfinance.common.dto.NotificationCreated;
import com.sunfinance.common.dto.NotificationDispatched;
import com.sunfinance.common.dto.RenderTemplateRequest;
import com.sunfinance.common.events.DomainEventPublisher;
import com.sunfinance.common.model.Verification;
import com.sunfinance.notification.entity.Notification;
import com.sunfinance.notification.repository.NotificationRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;



@Service
public class VerificationCreatedConsumer {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(VerificationCreatedConsumer.class);

	private final NotificationRepository notificationRepository;
	private final RestTemplate restTemplate;
	private final DomainEventPublisher eventPublisher;
	private final JavaMailSender mailSender;
	private final ObjectMapper objectMapper;

   
    private String gotifyUrl; 
	
    private String gotifyToken;
    
    private String senderEmail;

    public VerificationCreatedConsumer(
            NotificationRepository notificationRepository,
            JavaMailSender mailSender,
            RestTemplate restTemplate,
            DomainEventPublisher eventPublisher,
            ObjectMapper objectMapper,
            @Value("${GOTIFY_URL}")  String gotifyUrl,
            @Value("${GOTIFY_TOKEN}") String gotifyToken,
            @Value("${SENDER_EMAIL}") String senderEmail
    ) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
        this.eventPublisher = eventPublisher;
        this.objectMapper = objectMapper;
        this.gotifyUrl = gotifyUrl;
        this.gotifyToken = gotifyToken;
        this.senderEmail = senderEmail;
    }
    
    @KafkaListener(topics = "verification.created", groupId = "notification-service")
    public void handle(Verification event) throws JsonMappingException, JsonProcessingException {
    	
        try {
        	log.info("Received verification event: " + event);
            log.info("Subject: " + event.getSubject());
            log.info("Type: " + event.getSubject().getType());
            log.info("ID: " + event.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        try {
            String slug = switch (event.getSubject().getType()) {
                case EMAIL_CONFIRMATION -> "email-verification";
                case MOBILE_CONFIRMATION -> "mobile-verification";
            };

            RenderTemplateRequest req = new RenderTemplateRequest(slug, Map.of("code", event.getCode()));
            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://template-service:8084/templates/render",
                    req,
                    String.class
            );

            log.info("Template service responded with: " + response.getStatusCode());

            String body = response.getBody();
            String channel = slug.startsWith("email") ? "email" : "sms";

            Notification notification = new Notification(event.getSubject().getIdentity(), channel, body);
            notificationRepository.save(notification);
            eventPublisher.publish(new NotificationCreated(notification.getId(), event.getSubject()));

            log.info("Saved notification: " + notification.getId());

            if ("email".equals(channel)) {
                log.info("Sending email...");
                sendEmail(notification);
            } else {
                log.info("Sending SMS...");
                sendSms(notification);
            }

            notification.markDispatched();
            notificationRepository.save(notification);
            eventPublisher.publish(new NotificationDispatched(notification.getId(), notification.getRecipient(), Instant.now()));

            log.info("Notification dispatched successfully! ");

        } catch (Exception e) {
            log.error("Error while processing verification event:");
            e.printStackTrace();
            throw e;
        }
    }
    private String getTemplateContent(String templateName, Map<String, Object> params) {
        try {
        	
        	 String url = "http://template-service:8084/templates/render";
        	 Map<String, Object> body = Map.of(
        	            "slug", templateName,
        	            "variables", params
        	        );

        	 HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body);
        	 ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        	 return response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to get template content from template-service");
            e.printStackTrace();
            return "Default fallback message.";
        }
    }

    private void sendEmail(Notification notification)  {
        String htmlBody =  notification.getBody();
        String plainText = htmlBody == null ? "" : htmlBody.replaceAll("<[^>]*>", "").trim();
        log.info("Body... " + htmlBody);
        MimeMessage message = mailSender.createMimeMessage();
      
        try {
        	MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        	helper.setFrom(senderEmail);
            message.setSender(new InternetAddress(senderEmail));

            helper.setTo(notification.getRecipient());
            helper.setSubject("Your verification code");
            helper.setText(plainText, htmlBody);
           
		} catch (MessagingException e) {
			log.error("Failed to send Mailhog message: " + e.getMessage());
		}
        mailSender.send(message);
    }


    private void sendSms(Notification notification) {
    	try {
            // Build Gotify JSON payload
    		log.info("Gotify request: " + notification.getBody());
            String payload = String.format(
                "{\"title\": \"%s\", \"message\": \"%s\", \"priority\": 5}",
                "Verification",
                notification.getBody()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Gotify-Key", gotifyToken);

            HttpEntity<String> request = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(gotifyUrl + "/message", request, Void.class);
            
            ResponseEntity<String> response = restTemplate.postForEntity(gotifyUrl, request, String.class);

            log.info("Gotify response: " + response.getStatusCode());
        } catch (Exception ex) {
        	log.error("Failed to send Gotify message: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}