/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.scheduler.SchedulerService
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.oauth2.client.config;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.scheduler.SchedulerService;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ImportComponentConfiguration {
    @Bean
    public HelpPathResolver helpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }

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
    public UserManager userManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public PermissionEnforcer permissionEnforcer() {
        return OsgiServices.importOsgiService(PermissionEnforcer.class);
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
}

