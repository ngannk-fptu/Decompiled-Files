/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.rememberme;

import java.time.Duration;

public interface CrowdSpecificRememberMeSettings {
    public static final boolean DEFAULT_ENABLED = true;
    public static final Duration DEFAULT_EXPIRATION_DURATION = Duration.ofDays(30L);

    public boolean isEnabled();

    public Duration getExpirationDuration();
}

