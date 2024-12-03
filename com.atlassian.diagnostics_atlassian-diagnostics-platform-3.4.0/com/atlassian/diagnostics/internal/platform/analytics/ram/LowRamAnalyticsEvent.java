/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.analytics.ram;

import javax.annotation.Nonnull;

public class LowRamAnalyticsEvent {
    private final Long freeMemoryInMb;
    private final Long totalMemoryInMb;
    private final Long minMemoryThresholdInMb;

    protected LowRamAnalyticsEvent(@Nonnull Long freeMemoryInMegabytes, @Nonnull Long totalMemoryInMegabytes, @Nonnull Long minMemoryThresholdInMegabytes) {
        this.freeMemoryInMb = freeMemoryInMegabytes;
        this.totalMemoryInMb = totalMemoryInMegabytes;
        this.minMemoryThresholdInMb = minMemoryThresholdInMegabytes;
    }

    public Long getFreeMemoryInMb() {
        return this.freeMemoryInMb;
    }

    public Long getTotalMemoryInMb() {
        return this.totalMemoryInMb;
    }

    public Long getMinMemoryThresholdInMb() {
        return this.minMemoryThresholdInMb;
    }
}

