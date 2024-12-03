/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dao;

import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import java.util.Optional;

public interface SystemRateLimitingSettingsDao {
    public void initializeDbIfNeeded(SystemRateLimitingSettings var1);

    public SystemRateLimitingSettings saveOrUpdate(SystemRateLimitingSettings var1);

    public SystemRateLimitingSettings getSystemSettings();

    public Optional<Long> getLatestSystemSettingsVersion();
}

