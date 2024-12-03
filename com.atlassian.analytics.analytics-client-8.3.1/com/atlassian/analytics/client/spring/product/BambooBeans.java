/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.bamboo.license.BambooLicenseManager
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
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
import com.atlassian.analytics.client.cluster.DefaultClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.detect.DefaultSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SalProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.BambooPropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.BambooLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.BambooEventListener;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.DefaultPropertyService;
import com.atlassian.analytics.client.sen.DefaultSenProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.bamboo.license.BambooLicenseManager;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
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
@Conditional(value={BambooOnly.class})
public class BambooBeans {
    @Bean
    public AnalyticsConfig analyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager) {
        return new AnalyticsConfig(pluginSettingsFactory, eventPublisher, cacheManager);
    }

    @Bean
    public AnalyticsPropertyService analyticsPropertyService(ApplicationProperties applicationProperties) {
        return new DefaultPropertyService(applicationProperties);
    }

    @Bean
    public BambooEventListener bambooEventListener(ProductAnalyticsEventListener productAnalyticsEventListener, EventPublisher eventPublisher, PluginEventManager pluginEventManager, WhitelistFilter whitelistFilter, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, ProductUUIDProvider productUUIDProvider) {
        return new BambooEventListener(productAnalyticsEventListener, eventPublisher, pluginEventManager, whitelistFilter, periodicEventUploaderScheduler, productUUIDProvider);
    }

    @Bean
    public BaseDataLogger baseDataLogger() {
        return new DefaultBaseDataLogger();
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider() {
        return new DefaultClusterInformationProvider();
    }

    @Bean
    public LicenseProvider licenseProvider(BambooLicenseManager bambooLicenseManager) {
        return new BambooLicenseProvider(bambooLicenseManager);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector(DarkFeatureManager darkFeatureManager) {
        return new SalProgrammaticAnalyticsDetector(darkFeatureManager);
    }

    @Bean
    public PropertyExtractor propertyExtractor(UserManager userManager) {
        return new BambooPropertyExtractor(userManager);
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
    public BambooLicenseManager bambooLicenseManager() {
        return OsgiServices.importOsgiService(BambooLicenseManager.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportBambooEventListener(BambooEventListener bambooEventListener) {
        return SharedExports.exportAsLifecycleAware(bambooEventListener);
    }
}

