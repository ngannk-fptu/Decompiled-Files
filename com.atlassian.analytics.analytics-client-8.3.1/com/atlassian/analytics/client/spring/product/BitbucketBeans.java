/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.extractor.ProductProvidedPropertyExtractor
 *  com.atlassian.analytics.api.listener.ProductAnalyticsEventListener
 *  com.atlassian.bitbucket.cluster.ClusterService
 *  com.atlassian.bitbucket.license.LicenseService
 *  com.atlassian.bitbucket.project.ProjectService
 *  com.atlassian.bitbucket.repository.RepositoryService
 *  com.atlassian.bitbucket.server.ApplicationPropertiesService
 *  com.atlassian.bitbucket.user.SecurityService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.web.context.HttpContext
 *  org.osgi.framework.ServiceRegistration
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.product;

import com.atlassian.analytics.api.extractor.ProductProvidedPropertyExtractor;
import com.atlassian.analytics.api.listener.ProductAnalyticsEventListener;
import com.atlassian.analytics.client.base.BaseDataLogger;
import com.atlassian.analytics.client.base.BitbucketBaseDataLogger;
import com.atlassian.analytics.client.cluster.BitbucketClusterInformationProvider;
import com.atlassian.analytics.client.cluster.ClusterInformationProvider;
import com.atlassian.analytics.client.configuration.AnalyticsConfig;
import com.atlassian.analytics.client.configuration.BitbucketAnalyticsConfig;
import com.atlassian.analytics.client.configuration.BitbucketAnalyticsSettings;
import com.atlassian.analytics.client.detect.DefaultProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.DefaultSystemShutdownDetector;
import com.atlassian.analytics.client.detect.ProgrammaticAnalyticsDetector;
import com.atlassian.analytics.client.detect.SystemShutdownDetector;
import com.atlassian.analytics.client.eventfilter.whitelist.WhitelistFilter;
import com.atlassian.analytics.client.extractor.BitbucketPropertyExtractor;
import com.atlassian.analytics.client.extractor.PropertyExtractor;
import com.atlassian.analytics.client.license.BitbucketLicenseProvider;
import com.atlassian.analytics.client.license.LicenseProvider;
import com.atlassian.analytics.client.listener.BitbucketEventListener;
import com.atlassian.analytics.client.properties.AnalyticsPropertyService;
import com.atlassian.analytics.client.properties.BitbucketPropertyService;
import com.atlassian.analytics.client.sen.BitbucketSenProvider;
import com.atlassian.analytics.client.sen.SenProvider;
import com.atlassian.analytics.client.session.SalSessionIdProvider;
import com.atlassian.analytics.client.session.SessionIdProvider;
import com.atlassian.analytics.client.spring.shared.SharedExports;
import com.atlassian.analytics.client.upload.PeriodicEventUploaderScheduler;
import com.atlassian.analytics.client.uuid.ProductUUIDProvider;
import com.atlassian.bitbucket.cluster.ClusterService;
import com.atlassian.bitbucket.license.LicenseService;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.bitbucket.user.SecurityService;
import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.web.context.HttpContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(value={BitbucketOnly.class})
public class BitbucketBeans {
    @Bean
    public AnalyticsConfig analyticsConfig(PluginSettingsFactory pluginSettingsFactory, EventPublisher eventPublisher, CacheManager cacheManager, ApplicationPropertiesService applicationPropertiesService, BitbucketAnalyticsSettings bitbucketAnalyticsSettings) {
        return new BitbucketAnalyticsConfig(pluginSettingsFactory, eventPublisher, cacheManager, applicationPropertiesService, bitbucketAnalyticsSettings);
    }

    @Bean
    public AnalyticsPropertyService analyticsPropertyService(ApplicationProperties applicationProperties) {
        return new BitbucketPropertyService(applicationProperties);
    }

    @Bean
    public BaseDataLogger baseDataLogger(EventPublisher eventPublisher, ProjectService projectService, RepositoryService repositoryService, LicenseService licenseService, SecurityService securityService) {
        return new BitbucketBaseDataLogger(eventPublisher, projectService, repositoryService, licenseService, securityService);
    }

    @Bean
    public BitbucketAnalyticsSettings bitbucketAnalyticsSettings(ApplicationPropertiesService applicationPropertiesService, PluginSettingsFactory pluginSettingsFactory) {
        return new BitbucketAnalyticsSettings(applicationPropertiesService, pluginSettingsFactory);
    }

    @Bean
    public BitbucketEventListener bitbucketEventListener(EventPublisher eventPublisher, ProductAnalyticsEventListener productAnalyticsEventListener, PeriodicEventUploaderScheduler periodicEventUploaderScheduler, WhitelistFilter whitelistFilter, ProductUUIDProvider productUUIDProvider) {
        return new BitbucketEventListener(eventPublisher, productAnalyticsEventListener, periodicEventUploaderScheduler, whitelistFilter, productUUIDProvider);
    }

    @Bean
    public ClusterInformationProvider clusterInformationProvider(ClusterService clusterService) {
        return new BitbucketClusterInformationProvider(clusterService);
    }

    @Bean
    public LicenseProvider licenseProvider(LicenseService licenseService) {
        return new BitbucketLicenseProvider(licenseService);
    }

    @Bean
    public ProgrammaticAnalyticsDetector programmaticAnalyticsDetector() {
        return new DefaultProgrammaticAnalyticsDetector();
    }

    @Bean
    public PropertyExtractor propertyExtractor(ProductProvidedPropertyExtractor productProvidedPropertyExtractor) {
        return new BitbucketPropertyExtractor(productProvidedPropertyExtractor);
    }

    @Bean
    public SenProvider senProvider(ApplicationPropertiesService applicationPropertiesService, BitbucketAnalyticsSettings bitbucketAnalyticsSettings, LicenseHandler licenseHandler) {
        return new BitbucketSenProvider(applicationPropertiesService, bitbucketAnalyticsSettings, licenseHandler);
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
    public ApplicationPropertiesService applicationPropertiesService() {
        return OsgiServices.importOsgiService(ApplicationPropertiesService.class);
    }

    @Bean
    public ProductProvidedPropertyExtractor productProvidedPropertyExtractor() {
        return OsgiServices.importOsgiService(ProductProvidedPropertyExtractor.class);
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
    public ProjectService projectService() {
        return OsgiServices.importOsgiService(ProjectService.class);
    }

    @Bean
    public RepositoryService repositoryService() {
        return OsgiServices.importOsgiService(RepositoryService.class);
    }

    @Bean
    public SecurityService securityService() {
        return OsgiServices.importOsgiService(SecurityService.class);
    }

    @Bean
    public FactoryBean<ServiceRegistration> exportBitbucketEventListener(BitbucketEventListener bitbucketEventListener) {
        return SharedExports.exportAsLifecycleAware(bitbucketEventListener);
    }
}

