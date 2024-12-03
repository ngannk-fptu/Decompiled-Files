/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.configuration;

import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public interface SystemPropertiesService {
    public RateLimitingMode getRateLimitingMode();

    public TokenBucketSettings getDefaultRateLimitSettings();

    public void updateSystemRateLimitSettings(SystemRateLimitingSettings var1);

    public boolean isJmxEnabled();

    public SystemRateLimitingSettings getSystemSettings();

    public SystemJobControlSettings updateSystemJobControlSettings(SystemJobControlSettings var1);

    public void initializeData();
}

