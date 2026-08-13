package com.payment.platform.analytics.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataAnalyticsRepository extends JpaRepository<JpaMerchantAnalytics, String> {}
