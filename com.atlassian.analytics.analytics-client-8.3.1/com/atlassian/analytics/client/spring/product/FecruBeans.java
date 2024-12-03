/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fecru.util.LicenseInfoService
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.product;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.FecruBaseDataLogger;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.cluster.DefaultClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.detect.DefaultProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.DefaultSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.FecruPropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.FecruLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.FecruEventListener;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.DefaultPropertyService;
import com.atlassian.analytics.client.sen.DefaultSenProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.client.session.DefaultSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fecru.util.LicenseInfoService;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.FecruOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={FecruOnly.class})
public class FecruBeans {
    @Bean
    public AnalyticsConfig analyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager) {
        return new AnalyticsConfig(pluginSettingsFactory, eventPublisher, cacheManager);
    }

    @Bean
    public AnalyticsPropertyService analyticsPropertyService(ApplicationProperties applicationProperties) {
        return new DefaultPropertyService(applicationProperties);
    }

    @Bean
    public BaseDataLogger baseDataLogger() {
        return new FecruBaseDataLogger();
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider() {
        return new DefaultClusterInformationProvider();
    }

    @Bean
    public FecruEventListener fecruEventListener(EventPublisher eventPublisher, PluginEventManager pluginEventManager, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        return new FecruEventListener(eventPublisher, pluginEventManager, productAnalyticsEventListener, periodicEventUploaderScheduler, whitelistFilter, productUUIDProvider);
    }

    @Bean
    public LicenseProvider licenseProvider(LicenseInfoService licenseInfoService) {
        return new FecruLicenseProvider(licenseInfoService);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector() {
        return new DefaultProgrammaticAnalyticsDetector();
    }

    @Bean
    public PropertyExtractor propertyExtractor(UserManager userManager) {
        return new FecruPropertyExtractor(userManager);
    }

    @Bean
    public SenProvider senProvider(LicenseHandler licenseHandler) {
        return new DefaultSenProvider(licenseHandler);
    }

    @Bean
    public SessionIdProvider sessionIdProvider() {
        return new DefaultSessionIdProvider();
    }

    @Bean
    public SystemShutdownDetector systemShutdownDetector() {
        return new DefaultSystemShutdownDetector();
    }

    @Bean
    public LicenseInfoService licenseInfoService() {
        return OsgiServices.importOsgiService(LicenseInfoService.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportFecruEventListener(FecruEventListener fecruEventListener) {
        return SharedExports.exportAsLifecycleAware(fecruEventListener);
    }
}

