/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.properties;

import com.atlassian.ratelimiting.internal.properties.SystemProperties;
import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SystemPropertyConfigurableRateLimitingProperties
implements RateLimitingProperties {
    private static final Logger logger = LoggerFactory.getLogger(SystemPropertyConfigurableRateLimitingProperties.class);
    private final SystemProperties systemProperties;
    private final WhitelistedEndpoints whitelistedEndpoints;
    private final WhitelistedOAuthConsumers whitelistedOAuthConsumers;
    private final boolean defaultPreAuthFilterEnabled;
    private Set<String> whitelistedUrlPatterns;
    private Set<String> whitelistedOAuthConsumerKeys;
    private boolean preAuthFilterEnabled;

    public SystemPropertyConfigurableRateLimitingProperties(WhitelistedEndpoints whitelistedEndpoints, WhitelistedOAuthConsumers whitelistedOAuthConsumers, SystemProperties systemProperties, boolean defaultPreAuthFilterEnabled) {
        this.systemProperties = systemProperties;
        this.whitelistedEndpoints = whitelistedEndpoints;
        this.whitelistedOAuthConsumers = whitelistedOAuthConsumers;
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
        Set<String> defaultWhitelistedUrlPatterns = this.whitelistedEndpoints.getEndpoints();
        Set<String> systemPropertiesWhitelistedUrlPatterns = this.systemProperties.getWhitelistedUrlPatterns();
        Set<String> whitelistedUrlPatterns = RateLimitingProperties.sanitizeTrimmingWhitespace(defaultWhitelistedUrlPatterns, systemPropertiesWhitelistedUrlPatterns);
        if (this.whitelistedUrlPatterns == null || !this.whitelistedUrlPatterns.equals(whitelistedUrlPatterns)) {
            this.whitelistedUrlPatterns = whitelistedUrlPatterns;
            logger.trace("Updated whitelisted URL patterns: [{}]", whitelistedUrlPatterns);
        }
    }

    private void updateWhitelistedOAuthConsumers() {
        Set<String> systemPropertyOauthConsumerKeys = this.systemProperties.getWhitelistedOAuthConsumers();
        Set<String> atlassianOauthConsumerKeys = this.whitelistedOAuthConsumers.getConsumers();
        Set<String> whitelistedOAuthConsumerKeys = RateLimitingProperties.sanitizeTrimmingWhitespace(systemPropertyOauthConsumerKeys, atlassianOauthConsumerKeys);
        if (this.whitelistedOAuthConsumerKeys == null || !this.whitelistedOAuthConsumerKeys.equals(whitelistedOAuthConsumerKeys)) {
            this.whitelistedOAuthConsumerKeys = whitelistedOAuthConsumerKeys;
            logger.trace("Updated OAuth consumers: [{}]", whitelistedOAuthConsumerKeys);
        }
    }

    private void updatePreAuthFilterEnabledFlag() {
        boolean preAuthFilterEnabled = this.systemProperties.isPreAuthFilterEnabled(this.defaultPreAuthFilterEnabled);
        if (this.preAuthFilterEnabled != preAuthFilterEnabled) {
            this.preAuthFilterEnabled = preAuthFilterEnabled;
            logger.trace("Updated Pre Auth enabled flag: [{}]", (Object)preAuthFilterEnabled);
        }
    }
}

