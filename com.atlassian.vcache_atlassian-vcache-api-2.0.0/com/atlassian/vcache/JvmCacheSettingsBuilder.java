/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.JvmCacheSettings;
import java.time.Duration;
import java.util.Optional;

@PublicApi
public class JvmCacheSettingsBuilder {
    private Optional<Integer> maxEntries = Optional.empty();
    private Optional<Duration> defaultTtl = Optional.empty();

    public JvmCacheSettingsBuilder() {
    }

    public JvmCacheSettingsBuilder(JvmCacheSettings settings) {
        this.maxEntries = settings.getMaxEntries();
        this.defaultTtl = settings.getDefaultTtl();
    }

    public JvmCacheSettings build() {
        return new JvmCacheSettings(this.maxEntries, this.defaultTtl);
    }

    public JvmCacheSettingsBuilder maxEntries(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("maxEntries must not be negative, passed: " + max);
        }
        this.maxEntries = Optional.of(max);
        return this;
    }

    public JvmCacheSettingsBuilder defaultTtl(Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be greater than zero, passed: " + ttl);
        }
        this.defaultTtl = Optional.of(ttl);
        return this;
    }
}

