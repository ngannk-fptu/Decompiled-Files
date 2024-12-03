/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.spi.entity.AuditEntityTransformationService
 *  com.atlassian.audit.spi.feature.DatabaseAuditingFeature
 *  com.atlassian.audit.spi.feature.DelegatedViewFeature
 *  com.atlassian.audit.spi.feature.FileAuditingFeature
 *  com.atlassian.audit.spi.lookup.AuditingResourcesLookupService
 *  com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator
 *  com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider
 *  com.atlassian.audit.spi.permission.ResourceContextPermissionChecker
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.spi.entity.AuditEntityTransformationService;
import com.atlassian.audit.spi.feature.DatabaseAuditingFeature;
import com.atlassian.audit.spi.feature.DelegatedViewFeature;
import com.atlassian.audit.spi.feature.FileAuditingFeature;
import com.atlassian.audit.spi.lookup.AuditingResourcesLookupService;
import com.atlassian.audit.spi.migration.LegacyAuditEntityMigrator;
import com.atlassian.audit.spi.migration.LegacyRetentionConfigProvider;
import com.atlassian.audit.spi.permission.ResourceContextPermissionChecker;
import com.atlassian.audit.transform.ErrorIgnoredTransformationService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;

public class AuditOsgiImportsConfiguration {
    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public ActiveObjects ao() {
        return OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public AuditEntityTransformationService entityTransformationServiceSupplier() {
        return new ErrorIgnoredTransformationService(OsgiServices.importOsgiService(AuditEntityTransformationService.class));
    }

    @Bean
    public AuditingResourcesLookupService auditResourceLookupProvider() {
        return OsgiServices.importOsgiService(AuditingResourcesLookupService.class);
    }

    @Bean
    public AuditService auditService() {
        return OsgiServices.importOsgiService(AuditService.class);
    }

    @Bean
    public DarkFeatureManager featureManager() {
        return OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public DatabaseAuditingFeature databaseAuditingFeature() {
        return OsgiServices.importOsgiService(DatabaseAuditingFeature.class);
    }

    @Bean
    public DelegatedViewFeature delegatedViewFeatureChecker() {
        return OsgiServices.importOsgiService(DelegatedViewFeature.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public FileAuditingFeature fileAuditingFeature() {
        return OsgiServices.importOsgiService(FileAuditingFeature.class);
    }

    @Bean
    public HelpPathResolver helpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }

    @Bean
    public HttpContext httpContext() {
        return OsgiServices.importOsgiService(HttpContext.class);
    }

    @Bean
    public I18nResolver i18nResolverFactoryBean() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public LegacyAuditEntityMigrator legacyAuditEntityMigrator() {
        return OsgiServices.importOsgiService(LegacyAuditEntityMigrator.class);
    }

    @Bean
    public LegacyRetentionConfigProvider productRetentionConfigProvider() {
        return OsgiServices.importOsgiService(LegacyRetentionConfigProvider.class);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return OsgiServices.importOsgiService(LicenseHandler.class);
    }

    @Bean
    public LocaleResolver localeResolver() {
        return OsgiServices.importOsgiService(LocaleResolver.class);
    }

    @Bean
    public LoginUriProvider uriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return OsgiServices.importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public ResourceContextPermissionChecker resourceContextPermissionChecker() {
        return OsgiServices.importOsgiService(ResourceContextPermissionChecker.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public TimeZoneManager timeZoneManager() {
        return OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public TransactionTemplate transactionTemplate() {
        return OsgiServices.importOsgiService(TransactionTemplate.class);
    }

    @Bean
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public WebResourceUrlProvider webResourceUrlProvider() {
        return OsgiServices.importOsgiService(WebResourceUrlProvider.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }
}

