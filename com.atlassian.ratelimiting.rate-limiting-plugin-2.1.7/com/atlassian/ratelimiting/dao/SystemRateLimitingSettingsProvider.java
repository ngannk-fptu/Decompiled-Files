/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;

public interface SystemRateLimitingSettingsProvider {
    public SystemRateLimitingSettings getSystemSettings();

    public boolean tryReloadCache();

    public void forceReloadCache();
}

