/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.osgi.javaconfig.OsgiServices
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.auth.LoginUriProvider
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.license.LicenseHandler
 *  com.atlassian.sal.api.permission.PermissionEnforcer
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.plugins.cleanuphub.configuration;

import com.atlassian.plugins.osgi.javaconfig.OsgiServices;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.license.LicenseHandler;
import com.atlassian.sal.api.permission.PermissionEnforcer;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {
    @Bean
    public SoyTemplateRenderer soyTemplateRenderer() {
        return (SoyTemplateRenderer)OsgiServices.importOsgiService(SoyTemplateRenderer.class);
    }

    @Bean
    public LoginUriProvider loginUriProvider() {
        return (LoginUriProvider)OsgiServices.importOsgiService(LoginUriProvider.class);
    }

    @Bean
    public UserManager userManager() {
        return (UserManager)OsgiServices.importOsgiService(UserManager.class);
    }

    @Bean
    public ApplicationProperties applicationProperties() {
        return (ApplicationProperties)OsgiServices.importOsgiService(ApplicationProperties.class);
    }

    @Bean
    public DarkFeatureManager darkFeatureManager() {
        return (DarkFeatureManager)OsgiServices.importOsgiService(DarkFeatureManager.class);
    }

    @Bean
    public PermissionEnforcer permissionEnforcer() {
        return (PermissionEnforcer)OsgiServices.importOsgiService(PermissionEnforcer.class);
    }

    @Bean
    public LicenseHandler licenseHandler() {
        return (LicenseHandler)OsgiServices.importOsgiService(LicenseHandler.class);
    }
}

