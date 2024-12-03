/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditCoverageConfigService
 *  com.atlassian.event.api.EventPublisher
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.api.AuditCoverageConfigService;
import com.atlassian.audit.broker.AuditConsumerExceptionHandler;
import com.atlassian.audit.broker.AuditConsumerRegistry;
import com.atlassian.audit.broker.AuditEntityRejectionHandler;
import com.atlassian.audit.broker.AuditPolicy;
import com.atlassian.audit.broker.CompositeRejectionHandler;
import com.atlassian.audit.broker.ExcludedActionsAwareAuditBroker;
import com.atlassian.audit.broker.InternalAuditBroker;
import com.atlassian.audit.broker.LoggingAuditConsumerExceptionHandler;
import com.atlassian.audit.broker.LoggingRejectionHandler;
import com.atlassian.audit.broker.RaisingAnalyticsRejectionHandler;
import com.atlassian.audit.broker.RaisingEventRejectionHandler;
import com.atlassian.audit.broker.ScatterAuditBroker;
import com.atlassian.audit.broker.TranslatingAuditBroker;
import com.atlassian.audit.coverage.CachingAuditCoverageService;
import com.atlassian.audit.denylist.ExcludedActionsService;
import com.atlassian.audit.plugin.AuditPluginInfo;
import com.atlassian.audit.plugin.configuration.PermissionsNotEnforced;
import com.atlassian.audit.plugin.configuration.PropertiesProvider;
import com.atlassian.audit.policy.CoverageBasedAuditPolicy;
import com.atlassian.audit.service.TranslationService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.ExportOptions;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditBrokerConfiguration {
    @Bean
    public FactoryBean<ServiceRegistration> exportInternalAuditBroker(TranslatingAuditBroker broker) {
        return OsgiServices.exportOsgiService(broker, ExportOptions.as(InternalAuditBroker.class, new Class[0]));
    }

    @Bean
    public ScatterAuditBroker scatterBroker(EventPublisher eventPublisher, AuditPolicy auditPolicy, AuditEntityRejectionHandler rejectionHandler, AuditConsumerExceptionHandler exceptionHandler, PropertiesProvider propertiesProvider) {
        int consumerBufferSize = propertiesProvider.getInteger("plugin.audit.consumer.buffer.size", 10000);
        int consumerBatchSize = propertiesProvider.getInteger("plugin.audit.broker.default.batch.size", 3000);
        return new ScatterAuditBroker(eventPublisher, auditPolicy, rejectionHandler, exceptionHandler, consumerBufferSize, consumerBatchSize);
    }

    @Bean
    public TranslatingAuditBroker translatingDelegatingScatterBroker(ExcludedActionsAwareAuditBroker excludedActionsAwareAuditBroker, TranslationService translationService) {
        return new TranslatingAuditBroker(excludedActionsAwareAuditBroker, translationService);
    }

    @Bean
    public ExcludedActionsAwareAuditBroker denyListAwareDelegatingScatterBroker(ScatterAuditBroker scatterBroker, ExcludedActionsService excludedActionsService) {
        return new ExcludedActionsAwareAuditBroker(scatterBroker, excludedActionsService);
    }

    @Bean
    public AuditConsumerRegistry consumerRegistry(EventPublisher eventPublisher, BundleContext bundleContext) {
        return new AuditConsumerRegistry(eventPublisher, bundleContext);
    }

    @Bean
    public CachingAuditCoverageService cachingAuditCoverageService(EventPublisher eventPublisher, @PermissionsNotEnforced AuditCoverageConfigService coverageConfigService, PropertiesProvider propertiesProvider) {
        int coverageCacheExpiration = propertiesProvider.getInteger("plugin.audit.coverage.cache.read.expiration.seconds", 30);
        return new CachingAuditCoverageService(eventPublisher, coverageConfigService, coverageCacheExpiration);
    }

    @Bean
    public AuditPolicy auditPolicy(CachingAuditCoverageService cachingAuditCoverageService) {
        return new CoverageBasedAuditPolicy(cachingAuditCoverageService);
    }

    @Bean
    public AuditEntityRejectionHandler auditEntityRejectionHandler(EventPublisher eventPublisher, AuditPluginInfo auditPluginInfo) {
        return new CompositeRejectionHandler(new LoggingRejectionHandler(LoggingRejectionHandler.LOGGER), new RaisingEventRejectionHandler(), new RaisingAnalyticsRejectionHandler(eventPublisher, auditPluginInfo));
    }

    @Bean
    public AuditConsumerExceptionHandler auditConsumerExceptionHandler(PropertiesProvider propertiesProvider) {
        return new LoggingAuditConsumerExceptionHandler(LoggingAuditConsumerExceptionHandler.LOGGER, propertiesProvider);
    }
}

