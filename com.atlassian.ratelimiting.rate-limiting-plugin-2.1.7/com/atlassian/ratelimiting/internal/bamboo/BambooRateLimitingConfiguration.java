/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.message.LocaleResolver
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.ratelimiting.internal.bamboo;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.plugin.spring.scanner.annotation.component.BambooComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.BambooImport;
import com.atlassian.ratelimiting.audit.AuditService;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.audit.ObservabilityAuditService;
import com.atlassian.ratelimiting.internal.featureflag.SalRateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.properties.SystemProperties;
import com.atlassian.ratelimiting.internal.properties.SystemPropertyConfigurableRateLimitingProperties;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultRateLimitUiRequestHandler;
import com.atlassian.ratelimiting.internal.user.CrowdUserService;
import com.atlassian.ratelimiting.internal.user.keyprovider.SalUserKeyProvider;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.message.LocaleResolver;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@BambooComponent
public class BambooRateLimitingConfiguration {
    @BambooImport
    @Autowired
    private CrowdService crowdService;
    @BambooImport
    @Autowired
    private UserManager userManager;
    @BambooImport
    @Autowired
    private com.atlassian.audit.api.AuditService auditService;
    @BambooImport
    @Autowired
    private LocaleResolver localeResolver;
    @BambooImport
    @Autowired
    private I18nResolver i18nResolver;

    @Bean
    public UserService userService(UserManager userManager, CrowdService crowdService) {
        return new CrowdUserService(userManager, crowdService);
    }

    @Bean
    public RateLimitUiRequestHandler bambooRateLimitUiRequestHandler() {
        ImmutableSet headerNames = ImmutableSet.of((Object)"Referer", (Object)"origin");
        ImmutableSet cookieNames = ImmutableSet.of((Object)"atlassian.xsrf.token", (Object)"JSESSIONID");
        int quorumCount = 2;
        return new DefaultRateLimitUiRequestHandler((Set<String>)headerNames, (Set<String>)cookieNames, 2);
    }

    @Bean
    public RateLimitingProperties bambooRateLimitingProperties(WhitelistedEndpoints defaultWhitelistedEndpoints, WhitelistedOAuthConsumers defaultWhitelistedOAuthConsumers, SystemProperties systemProperties) {
        return new SystemPropertyConfigurableRateLimitingProperties(defaultWhitelistedEndpoints, defaultWhitelistedOAuthConsumers, systemProperties, false);
    }

    @Bean
    public RateLimitingFeatureFlagService salRateLimitingFeatureFlagService(DarkFeatureManager darkFeatureManager) {
        return new SalRateLimitingFeatureFlagService(darkFeatureManager);
    }

    @Bean
    public UserKeyProvider salUserKeyProvider(UserManager userManager) {
        return new SalUserKeyProvider(userManager);
    }

    @Bean
    public AuditService auditService() {
        return new ObservabilityAuditService(this.auditService, this.localeResolver, this.i18nResolver);
    }

    @Bean
    public WhitelistedEndpoints whitelistedEndpoints() {
        return () -> ImmutableSet.of((Object)"/**/rest/analytics/**", (Object)"/**/rest/api/*/serverInfo", (Object)"/**/rest/applinks/**", (Object)"/**/rest/applinks-oauth/**", (Object)"/**/rest/capabilities", (Object)"/**/rest/capabilities/navigation", (Object[])new String[]{"/**/rest/gadgets/1.0/g/**", "/**/rest/jira-dev/**", "/**/rest/healthCheck/1.0/checkDetails.json", "/**/rest/token-auth/api/**", "/**/rest/remote-link-aggregation/1/aggregation", "/**/rest/remote-event-consumer/1/capabilities", "/**/rest/remote-event/**", "/**/rest/quickreload/latest/0", "/**/webResources/1.0/resources"});
    }
}

