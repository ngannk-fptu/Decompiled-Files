/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.user.UserManager
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.product;

import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.ConfluenceBaseDataLogger;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.cluster.ConfluenceClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.detect.DefaultSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SalProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.ConfluencePropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.ConfluenceLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.ConfluenceEventListener;
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
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.user.UserManager;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={ConfluenceOnly.class})
public class ConfluenceBeans {
    @Bean
    public AnalyticsConfig analyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager) {
        return new AnalyticsConfig(pluginSettingsFactory, eventPublisher, cacheManager);
    }

    @Bean
    public AnalyticsPropertyService analyticsPropertyService(ApplicationProperties applicationProperties) {
        return new DefaultPropertyService(applicationProperties);
    }

    @Bean
    public BaseDataLogger baseDataLogger(EventPublisher publisher, UserManager userManager, PageManager pageManager, SpaceManager spaceManager, CommentManager commentManager, LicenseService licenseService) {
        return new ConfluenceBaseDataLogger(publisher, userManager, pageManager, spaceManager, commentManager, licenseService);
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider(ClusterManager clusterManager) {
        return new ConfluenceClusterInformationProvider(clusterManager);
    }

    @Bean
    public ConfluenceEventListener confluenceEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        return new ConfluenceEventListener(eventPublisher, productAnalyticsEventListener, periodicEventUploaderScheduler, whitelistFilter, productUUIDProvider);
    }

    @Bean
    public LicenseProvider licenseProvider(LicenseService licenseService) {
        return new ConfluenceLicenseProvider(licenseService);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector(DarkFeatureManager darkFeatureManager) {
        return new SalProgrammaticAnalyticsDetector(darkFeatureManager);
    }

    @Bean
    public PropertyExtractor confluencePropertyExtractor(com.atlassian.sal.api.user.UserManager userManager) {
        return new ConfluencePropertyExtractor(userManager);
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
    public ClusterManager clusterManager() {
        return OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    public CommentManager commentManager() {
        return OsgiServices.importOsgiService(CommentManager.class);
    }

    @Bean
    public LicenseService licenseService() {
        return OsgiServices.importOsgiService(LicenseService.class);
    }

    @Bean
    public PageManager pageManager() {
        return OsgiServices.importOsgiService(PageManager.class);
    }

    @Bean
    public SpaceManager spaceManager() {
        return OsgiServices.importOsgiService(SpaceManager.class);
    }

    @Bean
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportConfluenceEventListener(ConfluenceEventListener confluenceEventListener) {
        return SharedExports.exportAsLifecycleAware(confluenceEventListener);
    }
}

