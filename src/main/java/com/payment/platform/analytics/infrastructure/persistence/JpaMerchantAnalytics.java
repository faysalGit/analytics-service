package com.payment.platform.analytics.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "realtime_merchant_analytics")
public class JpaMerchantAnalytics {

    @Id
    @Column(name = "merchant_id", nullable = false, updatable = false)
    private String merchantId;

    @Column(name = "total_volume", nullable = false, precision = 18, scale = 4)
    private BigDecimal totalVolume;

    @Column(name = "success_count", nullable = false)
    private long successCount;

    @Column(name = "failure_count", nullable = false)
    private long failureCount;

    public JpaMerchantAnalytics() {}

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public BigDecimal getTotalVolume() { return totalVolume; }
    public void setTotalVolume(BigDecimal totalVolume) { this.totalVolume = totalVolume; }

    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }

    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
}
