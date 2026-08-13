package com.payment.platform.analytics.infrastructure.persistence;

import com.payment.platform.analytics.domain.model.MerchantAnalyticsSummary;
import com.payment.platform.analytics.domain.ports.AnalyticsRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AnalyticsRepositoryAdapter implements AnalyticsRepository {

    private final SpringDataAnalyticsRepository jpaRepository;

    public AnalyticsRepositoryAdapter(SpringDataAnalyticsRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<MerchantAnalyticsSummary> findByMerchantId(String merchantId) {
        return jpaRepository.findById(merchantId).map(entity -> {
            MerchantAnalyticsSummary model = new MerchantAnalyticsSummary();
            model.setMerchantId(entity.getMerchantId());
            model.setTotalVolume(entity.getTotalVolume());
            model.setSuccessCount(entity.getSuccessCount());
            model.setFailureCount(entity.getFailureCount());
            return model;
        });
    }

    @Override
    public MerchantAnalyticsSummary save(MerchantAnalyticsSummary summary) {
        JpaMerchantAnalytics entity = new JpaMerchantAnalytics();
        entity.setMerchantId(summary.getMerchantId());
        entity.setTotalVolume(summary.getTotalVolume());
        entity.setSuccessCount(summary.getSuccessCount());
        entity.setFailureCount(summary.getFailureCount());

        JpaMerchantAnalytics saved = jpaRepository.save(entity);

        MerchantAnalyticsSummary result = new MerchantAnalyticsSummary();
        result.setMerchantId(saved.getMerchantId());
        result.setTotalVolume(saved.getTotalVolume());
        result.setSuccessCount(saved.getSuccessCount());
        result.setFailureCount(saved.getFailureCount());
        return result;
    }
}
