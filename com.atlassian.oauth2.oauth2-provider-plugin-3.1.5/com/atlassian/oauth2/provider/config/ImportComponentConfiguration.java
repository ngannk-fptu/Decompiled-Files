/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor
 *  com.atlassian.bamboo.setup.BambooHomeLocator
 *  com.atlassian.bamboo.user.BambooUserManager
 *  com.atlassian.beehive.ClusterLockService
 *  com.atlassian.bitbucket.auth.AuthenticationService
 *  com.atlassian.bitbucket.user.UserService
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.confluence.themes.ColourSchemeManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.config.properties.LookAndFeelBean
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.jira.user.util.UserManager
 *  com.atlassian.oauth2.scopes.api.ScopeDescriptionService
 *  com.atlassian.oauth2.scopes.api.ScopeResolver
 *  com.atlassian.oauth2.scopes.api.ScopesRequestCache
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.AuthenticationListener
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Conditional
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.provider.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.audit.api.AuditService;
import com.atlassian.bamboo.configuration.AdministrationConfigurationAccessor;
import com.atlassian.bamboo.setup.BambooHomeLocator;
import com.atlassian.bamboo.user.BambooUserManager;
import com.atlassian.beehive.ClusterLockService;
import com.atlassian.bitbucket.auth.AuthenticationService;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.cache.CacheFactory;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.config.properties.LookAndFeelBean;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.jira.user.util.UserManager;
import com.atlassian.oauth2.scopes.api.ScopeDescriptionService;
import com.atlassian.oauth2.scopes.api.ScopeResolver;
import com.atlassian.oauth2.scopes.api.ScopesRequestCache;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BambooOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.BitbucketOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.ConfluenceOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.JiraOnly;
import com.atlassian.plugins.osgi.javaconfig.conditions.product.RefappOnly;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.AuthenticationListener;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportComponentConfiguration {
    @Bean
    public ApplicationProperties applicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public ActiveObjects activeObjects() {
        return OsgiServices.importOsgiService(ActiveObjects.class);
    }

    @Bean
    public SchedulerService schedulerService() {
        return OsgiServices.importOsgiService(SchedulerService.class);
    }

    @Bean
    public PermissionEnforcer permissionEnforcer() {
        return OsgiServices.importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    public com.atlassian.sal.api.user.UserManager salUserManager() {
        return OsgiServices.importOsgiService(com.atlassian.sal.api.user.UserManager.class);
    }

    @Bean
    public I18nResolver i18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public EventPublisher eventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }

    @Bean
    public WebSudoManager webSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    public ClusterLockService clusterLockService() {
        return OsgiServices.importOsgiService(ClusterLockService.class);
    }

    @Bean
    public AuthenticationListener authenticationListener() {
        return OsgiServices.importOsgiService(AuthenticationListener.class);
    }

    @Bean
    public PluginSettingsFactory pluginSettingsFactory() {
        return OsgiServices.importOsgiService(PluginSettingsFactory.class);
    }

    @Bean
    public CacheFactory cacheFactory() {
        return OsgiServices.importOsgiService(CacheFactory.class);
    }

    @Bean
    public PluginAccessor pluginAccessor() {
        return OsgiServices.importOsgiService(PluginAccessor.class);
    }

    @Bean
    public ScopesRequestCache scopesRequestCache() {
        return OsgiServices.importOsgiService(ScopesRequestCache.class);
    }

    @Bean
    public ScopeResolver scopeResolver() {
        return OsgiServices.importOsgiService(ScopeResolver.class);
    }

    @Bean
    public ScopeDescriptionService scopeDescriptionService() {
        return OsgiServices.importOsgiService(ScopeDescriptionService.class);
    }

    @Bean
    public HelpPathResolver helpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }

    @Bean
    public AuditService auditService() {
        return OsgiServices.importOsgiService(AuditService.class);
    }

    @Configuration
    @Conditional(value={RefappOnly.class})
    public static class RefappConfiguration {
        @Bean(value={"atlassianUserManager"})
        public com.atlassian.user.UserManager atlassianUserManager() {
            return OsgiServices.importOsgiService(com.atlassian.user.UserManager.class);
        }
    }

    @Configuration
    @Conditional(value={BambooOnly.class})
    public static class BambooConfiguration {
        @Bean
        public BambooUserManager bambooUserManager() {
            return OsgiServices.importOsgiService(BambooUserManager.class);
        }

        @Bean
        public AdministrationConfigurationAccessor administrationConfigurationAccessor() {
            return OsgiServices.importOsgiService(AdministrationConfigurationAccessor.class);
        }

        @Bean
        public BambooHomeLocator bambooHomeLocator() {
            return OsgiServices.importOsgiService(BambooHomeLocator.class);
        }
    }

    @Configuration
    @Conditional(value={BitbucketOnly.class})
    public static class BitbucketConfiguration {
        @Bean
        public UserService bitbucketUserService() {
            return OsgiServices.importOsgiService(UserService.class);
        }

        @Bean
        public AuthenticationService authenticationService() {
            return OsgiServices.importOsgiService(AuthenticationService.class);
        }
    }

    @Configuration
    @Conditional(value={ConfluenceOnly.class})
    public static class ConfluenceConfiguration {
        @Bean
        public UserAccessor confluenceUserAccessor() {
            return OsgiServices.importOsgiService(UserAccessor.class);
        }

        @Bean
        public ColourSchemeManager colourSchemeManager() {
            return OsgiServices.importOsgiService(ColourSchemeManager.class);
        }
    }

    @Configuration
    @Conditional(value={JiraOnly.class})
    public static class JiraConfiguration {
        @Bean
        public UserManager jiraUserManager() {
            return OsgiServices.importOsgiService(UserManager.class);
        }

        @Bean
        public com.atlassian.jira.config.properties.ApplicationProperties jiraApplicationProperties() {
            return OsgiServices.importOsgiService(com.atlassian.jira.config.properties.ApplicationProperties.class);
        }

        @Bean
        public LookAndFeelBean lookAndFeelBean(com.atlassian.jira.config.properties.ApplicationProperties jiraApplicationProperties) {
            return LookAndFeelBean.getInstance((com.atlassian.jira.config.properties.ApplicationProperties)jiraApplicationProperties);
        }

        @Bean
        public UserKeyService jiraUserKeyService() {
            return OsgiServices.importOsgiService(UserKeyService.class);
        }
    }
}

