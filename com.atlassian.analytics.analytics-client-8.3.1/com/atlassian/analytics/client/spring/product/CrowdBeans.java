/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.crowd.service.cluster.ClusterService
 *  com.atlassian.crowd.service.license.LicenseService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.product;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.DefaultBaseDataLogger;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.cluster.CrowdClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.detect.DefaultProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.DefaultSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.CrowdPropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.CrowdLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.CrowdEventListener;
import com.atlassian.analytics.client.listener.DefaultAnalyticsEventListener;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.DefaultPropertyService;
import com.atlassian.analytics.client.sen.DefaultSenProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.cache.CacheManager;
import com.atlassian.crowd.service.cluster.ClusterService;
import com.atlassian.crowd.service.license.LicenseService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.CrowdOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.web.context.HttpContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={CrowdOnly.class})
public class CrowdBeans {
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
        return new DefaultBaseDataLogger();
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider(ClusterService clusterService) {
        return new CrowdClusterInformationProvider(clusterService);
    }

    @Bean
    public CrowdEventListener crowdEventListener(EventPublisher eventPublisher, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        return new CrowdEventListener(eventPublisher, periodicEventUploaderScheduler, whitelistFilter, productUUIDProvider);
    }

    @Bean
    public DefaultAnalyticsEventListener defaultAnalyticsEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener) {
        return new DefaultAnalyticsEventListener(eventPublisher, productAnalyticsEventListener);
    }

    @Bean
    public LicenseProvider licenseProvider(LicenseService licenseService) {
        return new CrowdLicenseProvider(licenseService);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector() {
        return new DefaultProgrammaticAnalyticsDetector();
    }

    @Bean
    public PropertyExtractor propertyExtractor(UserManager userManager) {
        return new CrowdPropertyExtractor(userManager);
    }

    @Bean
    public SenProvider senProvider(LicenseHandler licenseHandler) {
        return new DefaultSenProvider(licenseHandler);
    }

    @Bean
    public SessionIdProvider sessionIdProvider(HttpContext httpContext) {
        return new SalSessionIdProvider(httpContext);
    }

    @Bean
    public SystemShutdownDetector systemShutdownDetector() {
        return new DefaultSystemShutdownDetector();
    }

    @Bean
    public ClusterService clusterService() {
        return OsgiServices.importOsgiService(ClusterService.class);
    }

    @Bean
    public LicenseService licenseService() {
        return OsgiServices.importOsgiService(LicenseService.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportCrowdEventListener(CrowdEventListener crowdEventListener) {
        return SharedExports.exportAsLifecycleAware(crowdEventListener);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportDefaultAnalyticsEventListener(DefaultAnalyticsEventListener defaultAnalyticsEventListener) {
        return SharedExports.exportAsLifecycleAware(defaultAnalyticsEventListener);
    }
}

