/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.web.context.HttpContext
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.analytics.client.spring.shared;

import com.atlassian.cache.CacheManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.executor.ThreadLocalDelegateExecutorFactory;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.web.context.HttpContext;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.templaterenderer.TemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SharedImports {
    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean(destroyMethod="")
    public CacheManager cacheManager() {
        return OsgiServices.importOsgiService(CacheManager.class);
    }

    @Bean
    public DarkFeatureManager darkFeatureManager() {
        return OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public HttpContext httpContext() {
        return OsgiServices.importOsgiService(HttpContext.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return OsgiServices.importOsgiService(LicenseHandler.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public PluginEventManager pluginEventManager() {
        return OsgiServices.importOsgiService(PluginEventManager.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return OsgiServices.importOsgiService(PluginSettingsFactory.class);
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
    public UserManager salUserManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }
}

