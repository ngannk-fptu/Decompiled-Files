/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.user.UserManager
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Configuration
 */
package com.atlassian.audit.plugin.configuration;

import com.atlassian.audit.permission.PermissionChecker;
import com.atlassian.audit.plugin.onboarding.OnboardingSeenService;
import com.atlassian.audit.plugin.onboarding.rest.v1.OnboardingRestResource;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuditOnboardingConfiguration {
    @Bean
    public OnboardingRestResource onboardingRestResource(OnboardingSeenService onboardingSeenService) {
        return new OnboardingRestResource(onboardingSeenService);
    }

    @Bean
    public OnboardingSeenService onboardingSeenService(UserManager userManager, PluginSettingsFactory pluginSettingsFactory, PermissionChecker permissionChecker) {
        return new OnboardingSeenService(userManager, pluginSettingsFactory, permissionChecker);
    }
}

