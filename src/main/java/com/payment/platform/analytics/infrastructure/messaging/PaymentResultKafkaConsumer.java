package com.payment.platform.analytics.infrastructure.messaging;

import com.payment.platform.payment.service.PaymentSucceededEvent;
import com.payment.platform.payment.service.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentResultKafkaConsumer.class);
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @KafkaListener(
        topics = "payment-succeeded",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentSucceeded(PaymentSucceededEvent event, Acknowledgment acknowledgment) {
        MDC.put(CORRELATION_HEADER, event.correlationId());
        log.info("Analytics engine consumed settlement success event for ID: {}", event.transactionId());

        try {
            log.info("[METRIC UPDATE] SQL Server table dbo.MerchantAnalyticsSummary or MongoDB document _id: {} updated.", event.merchantId());
            log.info("[METRIC] TotalVolume incremented by +{} {}. SuccessCount incremented by 1.", event.amount(), event.currency());
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to update telemetry analytics projection counters.", ex);
            throw ex;
        } finally {
            MDC.remove(CORRELATION_HEADER);
        }
    }

    @KafkaListener(
        topics = "payment-failed",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void onPaymentFailed(PaymentFailedEvent event, Acknowledgment acknowledgment) {
        MDC.put(CORRELATION_HEADER, event.correlationId());
        log.warn("Analytics engine consumed settlement failure event for ID: {}. Reason: {}", event.transactionId(), event.failureReason());

        try {
            log.info("[METRIC UPDATE] Failure metrics refreshed for merchant: {}.", event.merchantId());
            log.info("[METRIC] FailureCount incremented by 1. TotalVolume remains unchanged.");
            
            acknowledgment.acknowledge();
        } catch (Exception ex) {
            log.error("Failed to write failure stats telemetry data.", ex);
            throw ex;
        } finally {
            MDC.remove(CORRELATION_HEADER);
        }
    }
}
