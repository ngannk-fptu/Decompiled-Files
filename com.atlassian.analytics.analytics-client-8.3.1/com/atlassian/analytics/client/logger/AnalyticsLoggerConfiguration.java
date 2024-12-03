/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.context.annotation.Bean
 *  org.springframework.core.annotation.Order
 */
package com.atlassian.analytics.client.logger;

import com.atlassian.analytics.client.ServerIdProvider;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.logger.AnalyticsConfigurationFactory;
import com.atlassian.analytics.client.logger.AnalyticsLog4jLogger;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.LoggingProperties;
import com.atlassian.analytics.client.properties.ProductProperties;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.event.logging.LogEventFormatter;
import org.apache.logging.log4j.core.config.ConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Order(value=-2147483648)
public class AnalyticsLoggerConfiguration {
    @Bean
    ProductProperties productInformation(SenProvider senProvider, ServerIdProvider serverIdProvider, ClusterInformationProvider clusterInformationProvider) {
        return new ProductProperties(senProvider, serverIdProvider, clusterInformationProvider);
    }

    @Bean
    LoggingProperties analyticsLoggerProperties(ProductProperties productInformation, AnalyticsPropertyService applicationProperties) {
        return new LoggingProperties(productInformation, applicationProperties);
    }

    @Bean
    @Order(value=-2147483648)
    AnalyticsConfigurationFactory analyticsConfigurationFactory(LoggingProperties analyticsLoggerInformation) {
        AnalyticsConfigurationFactory analyticsConfigurationFactory = new AnalyticsConfigurationFactory(analyticsLoggerInformation);
        ConfigurationFactory.setConfigurationFactory(analyticsConfigurationFactory);
        return analyticsConfigurationFactory;
    }

    @Bean
    AnalyticsLog4jLogger log4jAnalyticsLogger(LogEventFormatter logEventFormatter) {
        return new AnalyticsLog4jLogger(logEventFormatter);
    }
}

