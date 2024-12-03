/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.DiagnosticsConfiguration
 *  com.atlassian.diagnostics.MonitoringService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.HelpPathResolver
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.websudo.WebSudoManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 *  org.springframework.context.annotation.Import
 */
package com.atlassian.diagnostics.internal.spring;

import com.atlassian.diagnostics.DiagnosticsConfiguration;
import com.atlassian.diagnostics.MonitoringService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.HelpPathResolver;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.websudo.WebSudoManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(value={PluginAccessorBean.class})
@Configuration
public class OsgiServiceImportsConfig {
    @Bean
    public ApplicationProperties ApplicationProperties() {
        return OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public DarkFeatureManager darkFeatureManager() {
        return OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public DiagnosticsConfiguration DiagnosticsConfiguration() {
        return OsgiServices.importOsgiService(DiagnosticsConfiguration.class);
    }

    @Bean
    public HelpPathResolver HelpPathResolver() {
        return OsgiServices.importOsgiService(HelpPathResolver.class);
    }

    @Bean
    public I18nResolver I18nResolver() {
        return OsgiServices.importOsgiService(I18nResolver.class);
    }

    @Bean
    public LoginUriProvider LoginUriProvider() {
        return OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public MonitoringService MonitoringService() {
        return OsgiServices.importOsgiService(MonitoringService.class);
    }

    @Bean
    public PermissionEnforcer PermissionEnforcer() {
        return OsgiServices.importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    public SoyTemplateRenderer SoyTemplateRenderer() {
        return OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public WebSudoManager WebSudoManager() {
        return OsgiServices.importOsgiService(WebSudoManager.class);
    }

    @Bean
    public UserManager UserManager() {
        return OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public TimeZoneManager TimeZoneManager() {
        return OsgiServices.importOsgiService(TimeZoneManager.class);
    }

    @Bean
    public EventPublisher EventPublisher() {
        return OsgiServices.importOsgiService(EventPublisher.class);
    }
}

