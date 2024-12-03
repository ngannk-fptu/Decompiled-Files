/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.services.AnalyticsConfigService
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.config.ApplicationConfiguration
 *  com.atlassian.confluence.api.service.content.ContentService
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.cluster.ClusterManager
 *  com.atlassian.confluence.content.service.SpaceService
 *  com.atlassian.confluence.license.LicenseService
 *  com.atlassian.confluence.macro.browser.MacroMetadataManager
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.templates.PageTemplateManager
 *  com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.security.SpacePermissionManager
 *  com.atlassian.confluence.setup.BootstrapManager
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdDirectoryService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.manager.directory.DirectoryManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.user.GroupManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.migration.agent;

import com.atlassian.analytics.api.services.AnalyticsConfigService;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheManager;
import com.atlassian.config.ApplicationConfiguration;
import com.atlassian.confluence.api.service.content.ContentService;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.cluster.ClusterManager;
import com.atlassian.confluence.content.service.SpaceService;
import com.atlassian.confluence.license.LicenseService;
import com.atlassian.confluence.macro.browser.MacroMetadataManager;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.templates.PageTemplateManager;
import com.atlassian.confluence.plugins.recentlyviewed.RecentlyViewedManager;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.security.SpacePermissionManager;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdDirectoryService;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.manager.directory.DirectoryManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.user.GroupManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportedOsgiServiceBeans {
    @Bean
    public AnalyticsConfigService analyticsConfigService() {
        return (AnalyticsConfigService)OsgiServices.importOsgiService(AnalyticsConfigService.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public AttachmentManager attachmentManager() {
        return (AttachmentManager)OsgiServices.importOsgiService(AttachmentManager.class);
    }

    @Bean
    public BootstrapManager bootstrapManager() {
        return (BootstrapManager)OsgiServices.importOsgiService(BootstrapManager.class);
    }

    @Bean(destroyMethod="")
    public CacheManager cacheManager() {
        return (CacheManager)OsgiServices.importOsgiService(CacheManager.class);
    }

    @Bean
    public ClusterManager clusterManager() {
        return (ClusterManager)OsgiServices.importOsgiService(ClusterManager.class);
    }

    @Bean
    public CQLSearchService cqlSearchService() {
        return (CQLSearchService)OsgiServices.importOsgiService(CQLSearchService.class);
    }

    @Bean
    public CrowdDirectoryService crowdDirectoryService() {
        return (CrowdDirectoryService)OsgiServices.importOsgiService(CrowdDirectoryService.class);
    }

    @Bean
    public DarkFeatureManager darkFeatureManager() {
        return (DarkFeatureManager)OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return (EventPublisher)OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public GroupManager groupManager() {
        return (GroupManager)OsgiServices.importOsgiService(GroupManager.class);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return (LicenseHandler)OsgiServices.importOsgiService(LicenseHandler.class);
    }

    @Bean
    public LicenseService licenseService() {
        return (LicenseService)OsgiServices.importOsgiService(LicenseService.class);
    }

    @Bean
    public MacroMetadataManager macroMetadataManager() {
        return (MacroMetadataManager)OsgiServices.importOsgiService(MacroMetadataManager.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return (PluginAccessor)OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public PluginMetadataManager pluginMetadataManager() {
        return (PluginMetadataManager)OsgiServices.importOsgiService(PluginMetadataManager.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return (PluginSettingsFactory)OsgiServices.importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public PredefinedSearchBuilder predefinedSearchBuilder() {
        return (PredefinedSearchBuilder)OsgiServices.importOsgiService(PredefinedSearchBuilder.class);
    }

    @Bean
    public RecentlyViewedManager recentlyViewedManager() {
        return (RecentlyViewedManager)OsgiServices.importOsgiService(RecentlyViewedManager.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return (SchedulerService)OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public SearchManager searchManager() {
        return (SearchManager)OsgiServices.importOsgiService(SearchManager.class);
    }

    @Bean
    public SpaceManager spaceManager() {
        return (SpaceManager)OsgiServices.importOsgiService(SpaceManager.class);
    }

    @Bean
    public SpaceService spaceService() {
        return (SpaceService)OsgiServices.importOsgiService(SpaceService.class);
    }

    @Bean
    public PageTemplateManager pageTemplateManager() {
        return (PageTemplateManager)OsgiServices.importOsgiService(PageTemplateManager.class);
    }

    @Bean
    public SpacePermissionManager spacePermissionsManager() {
        return (SpacePermissionManager)OsgiServices.importOsgiService(SpacePermissionManager.class);
    }

    @Bean
    public SystemInformationService systemInformationService() {
        return (SystemInformationService)OsgiServices.importOsgiService(SystemInformationService.class);
    }

    @Bean
    public ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory() {
        return (ThreadLocalDelegateExecutorFactory)OsgiServices.importOsgiService(ThreadLocalDelegateExecutorFactory.class);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return (TransactionTemplate)OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public UserAccessor userAccessor() {
        return (UserAccessor)OsgiServices.importOsgiService(UserAccessor.class);
    }

    @Bean
    public ClusterLockService clusterLockService() {
        return (ClusterLockService)OsgiServices.importOsgiService(ClusterLockService.class);
    }

    @Bean
    public UserManager userManager1() {
        return (UserManager)OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public SettingsManager settingsManagers() {
        return (SettingsManager)OsgiServices.importOsgiService(SettingsManager.class);
    }

    @Bean
    public ApplicationConfiguration applicationConfiguration() {
        return (ApplicationConfiguration)OsgiServices.importOsgiService(ApplicationConfiguration.class);
    }

    @Bean
    public CrowdService crowdService() {
        return (CrowdService)OsgiServices.importOsgiService(CrowdService.class);
    }

    @Bean
    public DirectoryManager directoryManager() {
        return (DirectoryManager)OsgiServices.importOsgiService(DirectoryManager.class);
    }

    @Bean
    public TimeZoneManager timeZoneManager() {
        return (TimeZoneManager)OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public ApplicationLinkService applicationLinkService() {
        return (ApplicationLinkService)OsgiServices.importOsgiService(ApplicationLinkService.class);
    }

    @Bean
    public ContentService contentService() {
        return (ContentService)OsgiServices.importOsgiService(ContentService.class);
    }
}

