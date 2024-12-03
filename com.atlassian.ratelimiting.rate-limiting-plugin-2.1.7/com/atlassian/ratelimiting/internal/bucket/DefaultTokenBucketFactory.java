/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.bucket;

import com.atlassian.ratelimiting.bucket.Configurable;
import com.atlassian.ratelimiting.bucket.ProhibitedTokenBucket;
import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.bucket.TokenBucketFactory;
import com.atlassian.ratelimiting.bucket.UnlimitedTokenBucket;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.ratelimiting.internal.bucket.Bucket4jTokenBucket;
import com.atlassian.ratelimiting.internal.settings.RateLimitLightweightAccessService;
import com.atlassian.sal.api.user.UserKey;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultTokenBucketFactory
implements TokenBucketFactory {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTokenBucketFactory.class);
    private static final TokenBucket UNLIMITED_TOKEN_BUCKET = new UnlimitedTokenBucket();
    private static final TokenBucket PROHIBITED_TOKEN_BUCKET = new ProhibitedTokenBucket();
    private final RateLimitLightweightAccessService rateLimitSettingsService;

    public DefaultTokenBucketFactory(RateLimitLightweightAccessService rateLimitSettingsService) {
        this.rateLimitSettingsService = rateLimitSettingsService;
    }

    @Override
    public TokenBucket createTokenBucket(UserKey userKey) {
        Optional<UserRateLimitSettings> userSettings = this.rateLimitSettingsService.getUserSettings(userKey);
        logger.trace("Got rate limiting settings for userKey: [{}]: [{}]", (Object)userKey, userSettings);
        return userSettings.map(this::createBucketForUserSettings).orElseGet(this::createSystemDefaultBucket);
    }

    private TokenBucket createBucketForUserSettings(UserRateLimitSettings userRateLimitSettings) {
        UserKey userKey = userRateLimitSettings.getUserKey();
        if (userRateLimitSettings.isWhitelisted()) {
            logger.trace("User with id {} is whitelisted", (Object)userKey);
            return UNLIMITED_TOKEN_BUCKET;
        }
        if (userRateLimitSettings.isBlacklisted()) {
            logger.trace("User with id {} is blacklisted", (Object)userKey);
            return PROHIBITED_TOKEN_BUCKET;
        }
        return new Bucket4jTokenBucket(userRateLimitSettings.getBucketSettings());
    }

    private TokenBucket createBucketForBucketSettings(TokenBucketSettings tokenBucketSettings) {
        if (tokenBucketSettings.isWhitelisted()) {
            return UNLIMITED_TOKEN_BUCKET;
        }
        if (tokenBucketSettings.isBlacklisted()) {
            return PROHIBITED_TOKEN_BUCKET;
        }
        return new Bucket4jTokenBucket(tokenBucketSettings);
    }

    private TokenBucket createSystemDefaultBucket() {
        TokenBucketSettings systemDefaultSettings = this.rateLimitSettingsService.getSystemDefaultBucketSettings();
        return this.createBucketForBucketSettings(systemDefaultSettings);
    }

    @Override
    public boolean hasCurrentSettings(UserKey userKey, TokenBucket bucket) {
        Optional<UserRateLimitSettings> userSettings = this.rateLimitSettingsService.getUserSettings(userKey);
        if (userSettings.isPresent()) {
            return this.hasSameSettings(bucket, userSettings.get());
        }
        return this.hasSameSettings(bucket, this.rateLimitSettingsService.getSystemDefaultBucketSettings());
    }

    private boolean hasSameSettings(TokenBucket bucket, UserRateLimitSettings userRateLimitSettings) {
        if (bucket instanceof ProhibitedTokenBucket && this.isProhibitedBucketSettings(userRateLimitSettings)) {
            return true;
        }
        if (bucket instanceof UnlimitedTokenBucket && this.isUnlimitedBucketSettings(userRateLimitSettings)) {
            return true;
        }
        if (bucket instanceof Configurable && this.isDetailedSettings(userRateLimitSettings)) {
            TokenBucketSettings settingsFromBucket = ((Configurable)((Object)bucket)).getSettings();
            Optional<TokenBucketSettings> currentSettings = userRateLimitSettings.getSettings();
            return currentSettings.map(s -> s.equals(settingsFromBucket)).orElse(false);
        }
        return false;
    }

    private boolean isDetailedSettings(UserRateLimitSettings userRateLimitSettings) {
        return !userRateLimitSettings.isWhitelisted() && !userRateLimitSettings.isBlacklisted();
    }

    private boolean isProhibitedBucketSettings(UserRateLimitSettings userRateLimitSettings) {
        return userRateLimitSettings.isBlacklisted();
    }

    private boolean isUnlimitedBucketSettings(UserRateLimitSettings userRateLimitSettings) {
        return userRateLimitSettings.isWhitelisted();
    }

    private boolean hasSameSettings(TokenBucket bucket, TokenBucketSettings tokenBucketSettings) {
        if (bucket instanceof ProhibitedTokenBucket && this.isProhibitedBucketSettings(tokenBucketSettings)) {
            return true;
        }
        if (bucket instanceof UnlimitedTokenBucket && this.isUnlimitedBucketSettings(tokenBucketSettings)) {
            return true;
        }
        if (bucket instanceof Configurable && tokenBucketSettings.isCustomSettings()) {
            return ((Configurable)((Object)bucket)).getSettings().equals(tokenBucketSettings);
        }
        return false;
    }

    private boolean isUnlimitedBucketSettings(TokenBucketSettings settings) {
        return settings.isWhitelisted();
    }

    private boolean isProhibitedBucketSettings(TokenBucketSettings settings) {
        return settings.isBlacklisted();
    }
}

