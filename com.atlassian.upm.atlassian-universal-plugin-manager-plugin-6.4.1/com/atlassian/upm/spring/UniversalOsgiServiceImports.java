/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.net.RequestFactory
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.osgi.service.packageadmin.PackageAdmin
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.upm.spring;

import com.atlassian.beehive.ClusterLockService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.osgi.service.packageadmin.PackageAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UniversalOsgiServiceImports {
    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public CacheFactory cacheFactory() {
        return OsgiServices.importOsgiService(CacheFactory.class);
    }

    @Bean
    public ClusterLockService clusterLockService() {
        return OsgiServices.importOsgiService(ClusterLockService.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public I18nResolver i18n() {
        return OsgiServices.importOsgiService(I18nResolver.class);
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
    public LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public PackageAdmin packageAdmin() {
        return OsgiServices.importOsgiService(PackageAdmin.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public PluginController pluginController() {
        return OsgiServices.importOsgiService(PluginController.class);
    }

    @Bean
    public PluginEventManager pluginEventManager() {
        return OsgiServices.importOsgiService(PluginEventManager.class);
    }

    @Bean
    public PluginMetadataManager pluginMetadataManager() {
        return OsgiServices.importOsgiService(PluginMetadataManager.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return OsgiServices.importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public RequestFactory requestFactory() {
        return OsgiServices.importOsgiService(RequestFactory.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public TemplateRenderer templateRenderer() {
        return OsgiServices.importOsgiService(TemplateRenderer.class);
    }

    @Bean
    public ThreadLocalDelegateExecutorFactory threadLocalDelegateExecutorFactory() {
        return OsgiServices.importOsgiService(ThreadLocalDelegateExecutorFactory.class);
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
    public WebResourceManager webResourceManager() {
        return OsgiServices.importOsgiService(WebResourceManager.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }
}

