package com.payment.platform.analytics.domain.ports;

import com.payment.platform.analytics.domain.model.MerchantAnalyticsSummary;
import java.util.Optional;

/**
 * Outbound domain port abstraction registry for transaction aggregate metrics storage.
 */
public interface AnalyticsRepository {
    Optional<MerchantAnalyticsSummary> findByMerchantId(String merchantId);
    MerchantAnalyticsSummary save(MerchantAnalyticsSummary summary);
}
