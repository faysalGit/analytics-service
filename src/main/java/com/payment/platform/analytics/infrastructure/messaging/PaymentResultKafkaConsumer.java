package com.payment.platform.analytics.infrastructure.messaging;

import com.payment.platform.analytics.application.usecase.UpdateAnalyticsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

/**
 * PaymentResultKafkaConsumer acts as the real-time event listener checkpoint for analytics-service.
 */
@Component
public class PaymentResultKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultKafkaConsumer.class);
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    private final UpdateAnalyticsUseCase updateAnalyticsUseCase;

    public PaymentResultKafkaConsumer(UpdateAnalyticsUseCase updateAnalyticsUseCase) {
        this.updateAnalyticsUseCase = updateAnalyticsUseCase;
    }

    /**
     * Subscribes concurrently to terminal state topics to extract dimensional performance indicators.
     */
    @KafkaListener(
            topics = {"payment-succeeded", "payment-failed"},
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void onResultEvent(
            Map<String, Object> eventPayload,
            @Header(name = "kafka_receivedTopic") String topic,
            Acknowledgment acknowledgment
    ) {
        String correlationId = (String) eventPayload.getOrDefault("correlationId", "UNKNOWN_TRACE");
        String merchantId = (String) eventPayload.getOrDefault("customerId", "MERCHANT_UNKNOWN");
        
        MDC.put(CORRELATION_HEADER, correlationId);
        log.info("Analytics ingestion pipeline intercepted message signal from topic: {}", topic);

        try {
            Object rawAmount = eventPayload.get("amount");
            BigDecimal amount = BigDecimal.ZERO;
            if (rawAmount instanceof Number number) {
                amount = BigDecimal.valueOf(number.doubleValue());
            } else if (rawAmount instanceof String str) {
                amount = new BigDecimal(str);
            }

            boolean isSuccess = "payment-succeeded".equalsIgnoreCase(topic);

            // Execute core use case logic
            updateAnalyticsUseCase.processEventMetric(merchantId, amount, isSuccess);

            // Manual offset commit security guard
            acknowledgment.acknowledge();
            log.debug("Analytics broker index offset successfully committed.");

        } catch (Exception e) {
            log.error("Fatal failure encountered during stream metrics tracking routines.", e);
            acknowledgment.acknowledge(); // Protect queue from poison blocking states
        } finally {
            MDC.remove(CORRELATION_HEADER);
        }
    }
}
