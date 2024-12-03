/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.config.properties.ApplicationProperties
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.BooleanUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.StringUtils
 */
package com.atlassian.ratelimiting.internal.jira.properties;

import com.atlassian.jira.config.properties.ApplicationProperties;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

public class JiraRateLimitingProperties
implements RateLimitingProperties {
    private static final Logger logger = LoggerFactory.getLogger(JiraRateLimitingProperties.class);
    private final WhitelistedEndpoints whitelistedEndpoints;
    private final WhitelistedOAuthConsumers whitelistedOAuthConsumers;
    private final ApplicationProperties jiraProperties;
    private final boolean defaultPreAuthFilterEnabled;
    private Set<String> whitelistedUrlPatterns;
    private Set<String> whitelistedOAuthConsumerKeys;
    private boolean preAuthFilterEnabled;

    public JiraRateLimitingProperties(WhitelistedEndpoints whitelistedEndpoints, WhitelistedOAuthConsumers whitelistedOAuthConsumers, ApplicationProperties jiraProperties, boolean defaultPreAuthFilterEnabled) {
        this.whitelistedEndpoints = whitelistedEndpoints;
        this.whitelistedOAuthConsumers = whitelistedOAuthConsumers;
        this.jiraProperties = jiraProperties;
        this.defaultPreAuthFilterEnabled = defaultPreAuthFilterEnabled;
        this.reloadCache();
    }

    @Override
    @Nonnull
    public Set<String> getWhitelistedUrlPatterns() {
        return Collections.unmodifiableSet(this.whitelistedUrlPatterns);
    }

    @Override
    @Nonnull
    public Set<String> getWhitelistedOAuthConsumers() {
        return Collections.unmodifiableSet(this.whitelistedOAuthConsumerKeys);
    }

    @Override
    public boolean isPreAuthFilterEnabled() {
        return this.preAuthFilterEnabled;
    }

    @Override
    public void reloadCache() {
        this.updateWhitelistedUrlPatterns();
        this.updateWhitelistedOAuthConsumers();
        this.updatePreAuthFilterEnabledFlag();
    }

    private void updateWhitelistedUrlPatterns() {
        Set<String> defaultUrlPatterns = this.whitelistedEndpoints.getEndpoints();
        Set overridingUrlPatterns = StringUtils.commaDelimitedListToSet((String)this.jiraProperties.getDefaultBackedString("com.atlassian.ratelimiting.whitelisted-url-patterns"));
        Set<String> whitelistedUrlPatterns = RateLimitingProperties.sanitizeTrimmingWhitespace(defaultUrlPatterns, overridingUrlPatterns);
        if (this.whitelistedUrlPatterns == null || !this.whitelistedUrlPatterns.equals(whitelistedUrlPatterns)) {
            this.whitelistedUrlPatterns = whitelistedUrlPatterns;
            logger.trace("Updated whitelisted URL patterns: [{}]", whitelistedUrlPatterns);
        }
    }

    private void updateWhitelistedOAuthConsumers() {
        Set overridingConsumerKeys = StringUtils.commaDelimitedListToSet((String)this.jiraProperties.getDefaultBackedString("com.atlassian.ratelimiting.whitelisted-oauth-consumers"));
        Set<String> atlassianOAuthConsumerKeys = this.whitelistedOAuthConsumers.getConsumers();
        Set<String> whitelistedOAuthConsumerKeys = RateLimitingProperties.sanitizeTrimmingWhitespace(overridingConsumerKeys, atlassianOAuthConsumerKeys);
        if (this.whitelistedOAuthConsumerKeys == null || !this.whitelistedOAuthConsumerKeys.equals(whitelistedOAuthConsumerKeys)) {
            this.whitelistedOAuthConsumerKeys = whitelistedOAuthConsumerKeys;
            logger.trace("Updated OAuth consumers: [{}]", whitelistedOAuthConsumerKeys);
        }
    }

    private void updatePreAuthFilterEnabledFlag() {
        String preAuthFilterEnabledProperty = this.jiraProperties.getDefaultBackedString("com.atlassian.ratelimiting.enable-pre-auth-filter");
        if (preAuthFilterEnabledProperty != null) {
            boolean preAuthFilterEnabled = BooleanUtils.toBoolean((String)preAuthFilterEnabledProperty);
            if (this.preAuthFilterEnabled != preAuthFilterEnabled) {
                this.preAuthFilterEnabled = preAuthFilterEnabled;
                logger.trace("Updated Pre Auth enabled flag: [{}]", (Object)preAuthFilterEnabled);
            }
        } else {
            this.preAuthFilterEnabled = this.defaultPreAuthFilterEnabled;
        }
    }
}

