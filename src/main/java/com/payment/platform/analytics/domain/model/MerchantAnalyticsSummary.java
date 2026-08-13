package com.payment.platform.analytics.domain.model;

import java.math.BigDecimal;

/**
 * Pristine, framework-insulated Domain POJO representing cumulative merchant volumetric matrices.
 * Compliant with Platform Constitution Section 3.1: Zero external library imports.
 */
public class MerchantAnalyticsSummary {

    private String merchantId;
    private BigDecimal totalVolume;
    private long successCount;
    private long failureCount;

    public MerchantAnalyticsSummary() {}

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public BigDecimal getTotalVolume() { return totalVolume; }
    public void setTotalVolume(BigDecimal totalVolume) { this.totalVolume = totalVolume; }

    public long getSuccessCount() { return successCount; }
    public void setSuccessCount(long successCount) { this.successCount = successCount; }

    public long getFailureCount() { return failureCount; }
    public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
}
