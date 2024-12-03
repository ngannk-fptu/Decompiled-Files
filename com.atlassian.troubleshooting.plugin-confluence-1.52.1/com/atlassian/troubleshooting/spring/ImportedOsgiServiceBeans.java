/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginController
 *  com.atlassian.plugin.event.PluginEventManager
 *  com.atlassian.plugin.metadata.PluginMetadataManager
 *  com.atlassian.plugin.module.ModuleFactory
 *  com.atlassian.plugin.servlet.ServletContextFactory
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.plugins.authentication.api.config.IdpConfigService
 *  com.atlassian.plugins.authentication.api.config.SsoConfigService
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.rdbms.TransactionalExecutorFactory
 *  com.atlassian.sal.api.scheduling.PluginScheduler
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.usersettings.UserSettingsService
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.templaterenderer.TemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.troubleshooting.spring;

import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginController;
import com.atlassian.plugin.event.PluginEventManager;
import com.atlassian.plugin.metadata.PluginMetadataManager;
import com.atlassian.plugin.module.ModuleFactory;
import com.atlassian.plugin.servlet.ServletContextFactory;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.plugins.authentication.api.config.IdpConfigService;
import com.atlassian.plugins.authentication.api.config.SsoConfigService;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.rdbms.TransactionalExecutorFactory;
import com.atlassian.sal.api.scheduling.PluginScheduler;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.usersettings.UserSettingsService;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.templaterenderer.TemplateRenderer;
import com.atlassian.troubleshooting.stp.annotations.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportedOsgiServiceBeans {
    @Bean
    public ApplicationLinkService applicationLinkService() {
        return OsgiServices.importOsgiService(ApplicationLinkService.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
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
    public HelpPathResolver helpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
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
    public ModuleFactory moduleFactory() {
        return OsgiServices.importOsgiService(ModuleFactory.class);
    }

    @Bean
    public NonMarshallingRequestFactory<? extends Request<?, ?>> nonMarshallingRequestFactory() {
        return OsgiServices.importOsgiService(NonMarshallingRequestFactory.class);
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
    public PluginScheduler pluginScheduler() {
        return OsgiServices.importOsgiService(PluginScheduler.class);
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
    public ServletContextFactory servletContextFactory() {
        return OsgiServices.importOsgiService(ServletContextFactory.class);
    }

    @Bean
    public TemplateRenderer templateRenderer() {
        return OsgiServices.importOsgiService(TemplateRenderer.class);
    }

    @Bean
    public TimeZoneManager timeZoneManager() {
        return OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public TransactionalExecutorFactory transactionalExecutorFactory() {
        return OsgiServices.importOsgiService(TransactionalExecutorFactory.class);
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
    public UserSettingsService userSettingsService() {
        return OsgiServices.importOsgiService(UserSettingsService.class);
    }

    @Bean
    public WebResourceManager webResourceManager() {
        return OsgiServices.importOsgiService(WebResourceManager.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    public WebInterfaceManager webInterfaceManager() {
        return OsgiServices.importOsgiService(WebInterfaceManager.class);
    }

    @Configuration
    @ConditionalOnClass(value={SsoConfigService.class})
    public static class SsoConfigBeans {
        @Bean
        public SsoConfigService ssoConfigService() {
            return OsgiServices.importOsgiService(SsoConfigService.class);
        }
    }

    @Configuration
    @ConditionalOnClass(value={IdpConfigService.class})
    public static class IdpConfigBeans {
        @Bean
        public IdpConfigService idpConfigService() {
            return OsgiServices.importOsgiService(IdpConfigService.class);
        }
    }

    @Configuration
    @ConditionalOnClass(value={AuditService.class})
    public static class AuditBeans {
        @Bean
        public AuditService auditService() {
            return OsgiServices.importOsgiService(AuditService.class);
        }
    }
}

