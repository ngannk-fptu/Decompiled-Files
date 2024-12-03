/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.jira.auditing.AuditingManager
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  com.atlassian.jira.user.UserKeyService
 *  com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.ImmutableSet
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.annotation.Bean
 */
package com.atlassian.ratelimiting.internal.jira;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.jira.auditing.AuditingManager;
import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.jira.user.UserKeyService;
import com.atlassian.plugin.spring.scanner.annotation.component.JiraComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.JiraImport;
import com.atlassian.ratelimiting.featureflag.RateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.jira.featureflag.JiraRateLimitingFeatureFlagService;
import com.atlassian.ratelimiting.internal.jira.properties.JiraRateLimitingProperties;
import com.atlassian.ratelimiting.internal.jira.user.keyprovider.JiraUserKeyProvider;
import com.atlassian.ratelimiting.internal.requesthandler.DefaultRateLimitUiRequestHandler;
import com.atlassian.ratelimiting.internal.user.CrowdUserService;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import com.atlassian.ratelimiting.requesthandler.RateLimitUiRequestHandler;
import com.atlassian.ratelimiting.user.UserService;
import com.atlassian.ratelimiting.user.keyprovider.UserKeyProvider;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.ImmutableSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

@JiraComponent
public class JiraRateLimitingConfiguration {
    @JiraImport
    @Autowired
    private ApplicationProperties jiraAppProperties;
    @JiraImport
    @Autowired
    private UserKeyService userKeyService;
    @JiraImport
    @Autowired
    private CrowdService crowdService;
    @JiraImport
    @Autowired
    private AuditingManager auditingManager;
    @JiraImport
    @Autowired
    private UserManager userManager;

    @Bean
    public UserService userService(UserManager userManager, CrowdService crowdService) {
        return new CrowdUserService(userManager, crowdService);
    }

    @Bean
    public WhitelistedEndpoints whitelistedEndpoints() {
        return () -> ImmutableSet.of((Object)"/**/rest/gadgets/1.0/g/**", (Object)"/**/webResources/1.0/resources", (Object)"/**/rest/auth/1/session", (Object)"/**/rest/jpo/1.0/authentication/**", (Object)"/**/rest/healthCheck/1.0/checkDetails.json", (Object)"/**/rest/remote-link-aggregation/1/aggregation", (Object[])new String[]{"/**/rest/capabilities", "/**/rest/capabilities/navigation", "/**/rest/remote-event-consumer/1/capabilities", "/**/rest/applinks/**", "/**/rest/applinks-oauth/**", "/**/rest/**/remotelink", "/**/rest/mywork-client/*/registration", "/**/rest/collectors/1.0/configuration/**", "/**/rest/analytics/**", "/**/rest/api/*/serverInfo", "/**/rest/jello/**", "/**/rest/servicedesk/1/customer/pages/portal/**", "/**/rest/servicedesk/1/customer/feedback/portal/**", "/**/rest/servicedesk/1/customer/pages/**", "/**/rest/servicedesk/1/customer/subscription/unsubscribe/portal/**"});
    }

    @Bean
    public RateLimitUiRequestHandler jiraRateLimitUiRequestHandler() {
        ImmutableSet headerNames = ImmutableSet.of((Object)"Referer", (Object)"origin");
        ImmutableSet cookieNames = ImmutableSet.of((Object)"atlassian.xsrf.token", (Object)"JSESSIONID");
        int quorumCount = 2;
        return new DefaultRateLimitUiRequestHandler((Set<String>)headerNames, (Set<String>)cookieNames, 2);
    }

    @Bean
    public RateLimitingProperties jiraRateLimitingProperties(WhitelistedEndpoints defaultWhitelistedEndpoints, WhitelistedOAuthConsumers whitelistedOAuthConsumers, ApplicationProperties jiraApplicationProperties) {
        return new JiraRateLimitingProperties(defaultWhitelistedEndpoints, whitelistedOAuthConsumers, jiraApplicationProperties, true);
    }

    @Bean
    public RateLimitingFeatureFlagService jiraRateLimitingFeatureFlagService(DarkFeatureManager darkFeatureManager, EventPublisher eventPublisher) {
        return new JiraRateLimitingFeatureFlagService(darkFeatureManager, eventPublisher);
    }

    @Bean
    public UserKeyProvider jiraUserKeyProvider(UserKeyService userKeyService) {
        return new JiraUserKeyProvider(userKeyService);
    }
}

