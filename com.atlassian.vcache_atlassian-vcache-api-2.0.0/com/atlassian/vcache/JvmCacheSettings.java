/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.SettingsUtils;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@PublicApi
public class JvmCacheSettings {
    private final Optional<Integer> maxEntries;
    private final Optional<Duration> defaultTtl;

    JvmCacheSettings(Optional<Integer> maxEntries, Optional<Duration> defaultTtl) {
        this.maxEntries = Objects.requireNonNull(maxEntries);
        this.defaultTtl = Objects.requireNonNull(defaultTtl);
    }

    public JvmCacheSettings override(JvmCacheSettings overrides) {
        return new JvmCacheSettings(SettingsUtils.ifPresent(overrides.getMaxEntries(), this.getMaxEntries()), SettingsUtils.ifPresent(overrides.getDefaultTtl(), this.getDefaultTtl()));
    }

    public Optional<Integer> getMaxEntries() {
        return this.maxEntries;
    }

    public Optional<Duration> getDefaultTtl() {
        return this.defaultTtl;
    }
}

