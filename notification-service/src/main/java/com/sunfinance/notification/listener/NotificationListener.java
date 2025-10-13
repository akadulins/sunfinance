
package com.sunfinance.notification.listener;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.sunfinance.common.model.Verification;

@Component
public class NotificationListener {
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationListener.class);

	@KafkaListener(topics = "verification.created", groupId = "notification-service")
	public void handle(Verification event) {
	    log.info("Received event: " + event);
	}
}
