/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.RefappImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.ratelimiting.internal.refapp;

import com.atlassian.plugin.spring.scanner.annotation.component.RefappComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.RefappImport;
import com.atlassian.ratelimiting.audit.AuditService;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.audit.ConsoleAuditService;
import com.atlassian.ratelimiting.internal.featureflag.SalRateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.properties.DefaultRateLimitingProperties;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultRateLimitUiRequestHandler;
import com.atlassian.ratelimiting.internal.user.AtlassianUserService;
import com.atlassian.ratelimiting.internal.user.keyprovider.SalUserKeyProvider;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@RefappComponent
public class RefAppRateLimitingConfiguration {
    @RefappImport(value="com.atlassian.user.UserManager")
    @Autowired
    private UserManager atlassianUserManager;

    @Bean
    public UserService userService(com.atlassian.sal.api.user.UserManager userManager, UserManager atlassianUserManager) {
        return new AtlassianUserService(userManager, atlassianUserManager);
    }

    @Bean
    public RateLimitUiRequestHandler refappRateLimitUiRequestHandler() {
        ImmutableSet headerNames = ImmutableSet.of((Object)"Referer", (Object)"origin");
        ImmutableSet cookieNames = ImmutableSet.of((Object)"atlassian.xsrf.token", (Object)"JSESSIONID");
        int quorumCount = 2;
        return new DefaultRateLimitUiRequestHandler((Set<String>)headerNames, (Set<String>)cookieNames, 2);
    }

    @Bean
    public RateLimitingProperties refappRateLimitingProperties(WhitelistedOAuthConsumers defaultWhitelistedOAuthConsumers) {
        return new DefaultRateLimitingProperties(Collections::emptySet, defaultWhitelistedOAuthConsumers, false);
    }

    @Bean
    public RateLimitingFeatureFlagService salRateLimitingFeatureFlagService(DarkFeatureManager darkFeatureManager) {
        return new SalRateLimitingFeatureFlagService(darkFeatureManager);
    }

    @Bean
    public UserKeyProvider salUserKeyProvider(com.atlassian.sal.api.user.UserManager userManager) {
        return new SalUserKeyProvider(userManager);
    }

    @Bean
    public AuditService auditService() {
        return new ConsoleAuditService();
    }
}

