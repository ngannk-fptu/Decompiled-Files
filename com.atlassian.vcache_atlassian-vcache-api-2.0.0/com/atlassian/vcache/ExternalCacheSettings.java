/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.vcache;

import com.atlassian.annotations.PublicApi;
import com.atlassian.vcache.ChangeRate;
import com.atlassian.vcache.SettingsUtils;
import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

@PublicApi
public class ExternalCacheSettings {
    private final Optional<Duration> defaultTtl;
    private final Optional<Integer> entryCountHint;
    private final Optional<ChangeRate> dataChangeRateHint;
    private final Optional<ChangeRate> entryGrowthRateHint;

    ExternalCacheSettings(Optional<Duration> defaultTtl, Optional<Integer> entryCountHint, Optional<ChangeRate> dataChangeRateHint, Optional<ChangeRate> entryGrowthRateHint) {
        this.defaultTtl = Objects.requireNonNull(defaultTtl);
        this.entryCountHint = Objects.requireNonNull(entryCountHint);
        this.dataChangeRateHint = Objects.requireNonNull(dataChangeRateHint);
        this.entryGrowthRateHint = Objects.requireNonNull(entryGrowthRateHint);
    }

    public ExternalCacheSettings override(ExternalCacheSettings overrides) {
        return new ExternalCacheSettings(SettingsUtils.ifPresent(overrides.getDefaultTtl(), this.getDefaultTtl()), SettingsUtils.ifPresent(overrides.getEntryCountHint(), this.getEntryCountHint()), SettingsUtils.ifPresent(overrides.getDataChangeRateHint(), this.getDataChangeRateHint()), SettingsUtils.ifPresent(overrides.getEntryGrowthRateHint(), this.getEntryGrowthRateHint()));
    }

    public Optional<Duration> getDefaultTtl() {
        return this.defaultTtl;
    }

    public Optional<Integer> getEntryCountHint() {
        return this.entryCountHint;
    }

    public Optional<ChangeRate> getDataChangeRateHint() {
        return this.dataChangeRateHint;
    }

    public Optional<ChangeRate> getEntryGrowthRateHint() {
        return this.entryGrowthRateHint;
    }
}

