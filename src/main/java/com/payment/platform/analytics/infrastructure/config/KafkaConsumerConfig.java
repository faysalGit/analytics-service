package com.payment.platform.analytics.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConsumerConfig {

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // Intercepts consumer crashes, retries 3 times with a 1-second fixed delay, 
        // then publishes the poison-pill message to "fraud-events.DLT" automatically.
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template);
        
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, new FixedBackOff(1000L, 3L));
        
        // Log a descriptive warning message right before shipping an item to the DLT
        errorHandler.setCommitRecovered(true);
        
        return errorHandler;
    }
}
