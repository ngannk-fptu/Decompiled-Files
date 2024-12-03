/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.dao;

import java.util.Optional;

public interface RateLimitingSettingsVersionDao {
    public void incrementDefaultSettingsVersion();

    public void incrementUserSettingsVersion();

    public Optional<Long> getLatestUserSettingsVersion();

    public Optional<Long> getLatestSystemSettingsVersion();
}

