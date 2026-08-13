package com.payment.platform.analytics.application.usecase;

import com.payment.platform.analytics.domain.model.MerchantAnalyticsSummary;
import com.payment.platform.analytics.domain.ports.AnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * UpdateAnalyticsUseCase handles atomic adjustments to live merchant metrics registers.
 */
@Service
public class UpdateAnalyticsUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateAnalyticsUseCase.class);
    private final AnalyticsRepository analyticsRepository;

    public UpdateAnalyticsUseCase(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    /**
     * Integrates transactional outcomes into cumulative volume profiles.
     */
    @Transactional
    public void processEventMetric(String merchantId, BigDecimal amount, boolean isSuccess) {
        log.info("Aggregating streaming metric payload for Merchant: {}, Success State: {}", merchantId, isSuccess);

        MerchantAnalyticsSummary summary = analyticsRepository.findByMerchantId(merchantId)
                .orElseGet(() -> {
                    log.info("Initializing fresh metric ledger block for merchant reference: {}", merchantId);
                    MerchantAnalyticsSummary fresh = new MerchantAnalyticsSummary();
                    fresh.setMerchantId(merchantId);
                    fresh.setTotalVolume(BigDecimal.ZERO);
                    fresh.setSuccessCount(0L);
                    fresh.setFailureCount(0L);
                    return fresh;
                });

        if (isSuccess) {
            summary.setSuccessCount(summary.getSuccessCount() + 1);
            if (amount != null) {
                summary.setTotalVolume(summary.getTotalVolume().add(amount));
            }
        } else {
            summary.setFailureCount(summary.getFailureCount() + 1);
        }

        analyticsRepository.save(summary);
        log.debug("Merchant dashboard aggregates updated. Total Cumulative Successes: {}", summary.getSuccessCount());
    }
}
