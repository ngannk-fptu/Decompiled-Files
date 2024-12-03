/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.properties;

import com.atlassian.ratelimiting.properties.RateLimitingProperties;
import com.atlassian.ratelimiting.properties.WhitelistedEndpoints;
import com.atlassian.ratelimiting.properties.WhitelistedOAuthConsumers;
import java.util.Collections;
import java.util.Set;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRateLimitingProperties
implements RateLimitingProperties {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRateLimitingProperties.class);
    private final Set<String> whitelistedUrlPatterns;
    private final Set<String> whitelistedOAuthConsumers;
    private final boolean preAuthFilterEnabled;

    public DefaultRateLimitingProperties(WhitelistedEndpoints defaultWhitelistedEndpoints, WhitelistedOAuthConsumers defaultWhitelistedOAuthConsumers, boolean defaultPreAuthFilterEnabledFlag) {
        this.whitelistedUrlPatterns = RateLimitingProperties.sanitizeTrimmingWhitespace(defaultWhitelistedEndpoints.getEndpoints());
        logger.trace("Loaded whitelisted URL patterns: [{}]", this.whitelistedUrlPatterns);
        this.whitelistedOAuthConsumers = RateLimitingProperties.sanitizeTrimmingWhitespace(defaultWhitelistedOAuthConsumers.getConsumers());
        logger.trace("Loaded OAuth consumers: [{}]", this.whitelistedOAuthConsumers);
        this.preAuthFilterEnabled = defaultPreAuthFilterEnabledFlag;
        logger.trace("Loaded Pre Auth enabled flag: [{}]", (Object)this.preAuthFilterEnabled);
    }

    @Override
    @Nonnull
    public Set<String> getWhitelistedUrlPatterns() {
        return Collections.unmodifiableSet(this.whitelistedUrlPatterns);
    }

    @Override
    @Nonnull
    public Set<String> getWhitelistedOAuthConsumers() {
        return Collections.unmodifiableSet(this.whitelistedOAuthConsumers);
    }

    @Override
    public boolean isPreAuthFilterEnabled() {
        return this.preAuthFilterEnabled;
    }
}

