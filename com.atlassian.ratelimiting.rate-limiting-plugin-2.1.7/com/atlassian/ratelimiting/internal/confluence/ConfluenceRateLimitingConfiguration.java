/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 *  org.springframework.context.annotation.Primary
 */
package com.atlassian.ratelimiting.internal.confluence;

import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ConfluenceImport;
import com.atlassian.ratelimiting.dmz.DmzRateLimitSettingsModificationService;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.history.RateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.internal.confluence.featureflag.ConfluenceRateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.confluence.history.ConfluenceRateLimitHistoryReportResultMapper;
import com.atlassian.ratelimiting.internal.confluence.user.keyprovider.ConfluenceUserKeyProvider;
import com.atlassian.ratelimiting.internal.properties.SystemProperties;
import com.atlassian.ratelimiting.internal.properties.SystemPropertyConfigurableRateLimitingProperties;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultRateLimitUiRequestHandler;
import com.atlassian.ratelimiting.internal.user.CrowdUserService;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@ConfluenceComponent
public class ConfluenceRateLimitingConfiguration {
    private static final String COOKIE_SERAPH_TOKEN = "seraph.confluence";
    @ConfluenceImport
    @Autowired
    private CrowdService crowdService;
    @ConfluenceImport
    @Autowired
    private UserManager userManager;
    @ConfluenceImport
    @Autowired
    private UserAccessor userAccessor;

    @Bean
    public WhitelistedEndpoints whitelistedEndpoints() {
        return () -> ImmutableSet.of((Object)"/**/rest/applinks/**", (Object)"/**/rest/capabilities", (Object)"/**/rest/capabilities/navigation", (Object)"/**/rest/gadgets/1.0/g/**", (Object)"/**/rest/mywork/latest/status/notification/count", (Object)"/**/rest/jira-metadata/1.0/metadata/cache", (Object[])new String[]{"/**/rest/api/content", "/**/rest/quickreload/latest/0", "/**/rest/mywork/1/client", "/**/rest/token-auth/api/**"});
    }

    @Bean
    public RateLimitUiRequestHandler confluenceRateLimitUiRequestHandler() {
        ImmutableSet headerNames = ImmutableSet.of((Object)"Referer", (Object)"origin");
        ImmutableSet cookieNames = ImmutableSet.of((Object)COOKIE_SERAPH_TOKEN, (Object)"JSESSIONID");
        int quorumCount = 2;
        return new DefaultRateLimitUiRequestHandler((Set<String>)headerNames, (Set<String>)cookieNames, 2);
    }

    @Bean
    public RateLimitingProperties confluenceRateLimitingProperties(WhitelistedEndpoints defaultWhitelistedEndpoints, WhitelistedOAuthConsumers defaultWhitelistedOAuthConsumers, SystemProperties systemProperties) {
        return new SystemPropertyConfigurableRateLimitingProperties(defaultWhitelistedEndpoints, defaultWhitelistedOAuthConsumers, systemProperties, false);
    }

    @Bean
    public UserService userService(UserManager userManager, CrowdService crowdService) {
        return new CrowdUserService(userManager, crowdService);
    }

    @Bean
    public UserKeyProvider confluenceUserKeyProvider(UserAccessor userAccessor) {
        return new ConfluenceUserKeyProvider(userAccessor);
    }

    @Bean
    public RateLimitingFeatureFlagService confluenceRateLimitingFeatureFlagService(EventPublisher eventPublisher) {
        return new ConfluenceRateLimitingFeatureFlagService(eventPublisher);
    }

    @Bean
    @Primary
    public RateLimitHistoryReportResultMapper historyReportResultMapper(UserService userService, DmzRateLimitSettingsModificationService rateLimitSettingsModificationService, UserAccessor userAccessor) {
        return new ConfluenceRateLimitHistoryReportResultMapper(userService, rateLimitSettingsModificationService, userAccessor);
    }
}

