/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.ExternalCacheSettings;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@PublicApi
public class ExternalCacheSettingsBuilder {
    private Optional<Duration> defaultTtl = Optional.empty();
    private Optional<Integer> entryCountHint = Optional.empty();
    private Optional<ChangeRate> dataChangeRateHint = Optional.empty();
    private Optional<ChangeRate> entryGrowthRateHint = Optional.empty();

    public ExternalCacheSettingsBuilder() {
    }

    public ExternalCacheSettingsBuilder(ExternalCacheSettings settings) {
        this.defaultTtl = Objects.requireNonNull(settings.getDefaultTtl());
        this.entryCountHint = Objects.requireNonNull(settings.getEntryCountHint());
        this.dataChangeRateHint = Objects.requireNonNull(settings.getDataChangeRateHint());
        this.entryGrowthRateHint = Objects.requireNonNull(settings.getEntryGrowthRateHint());
    }

    public ExternalCacheSettings build() {
        return new ExternalCacheSettings(this.defaultTtl, this.entryCountHint, this.dataChangeRateHint, this.entryGrowthRateHint);
    }

    public ExternalCacheSettingsBuilder defaultTtl(Duration ttl) {
        if (ttl.isNegative() || ttl.isZero()) {
            throw new IllegalArgumentException("ttl must be greater than zero, passed: " + ttl);
        }
        this.defaultTtl = Optional.of(ttl);
        return this;
    }

    public ExternalCacheSettingsBuilder entryCountHint(int hint) {
        if (hint < 0) {
            throw new IllegalArgumentException("maxEntries cannot be negative, passed: " + hint);
        }
        this.entryCountHint = Optional.of(hint);
        return this;
    }

    public ExternalCacheSettingsBuilder dataChangeRateHint(ChangeRate hint) {
        this.dataChangeRateHint = Optional.of(hint);
        return this;
    }

    public ExternalCacheSettingsBuilder entryGrowthRateHint(ChangeRate hint) {
        this.entryGrowthRateHint = Optional.of(hint);
        return this;
    }
}

