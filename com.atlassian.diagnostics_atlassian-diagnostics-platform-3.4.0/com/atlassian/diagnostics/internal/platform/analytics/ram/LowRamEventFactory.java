/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.diagnostics.internal.platform.analytics.ram;

import com.atlassian.diagnostics.internal.platform.analytics.EventFactory;
import com.atlassian.diagnostics.internal.platform.analytics.ram.LowRamAnalyticsEvent;
import javax.annotation.Nonnull;

public abstract class LowRamEventFactory
implements EventFactory {
    public LowRamAnalyticsEvent create(@Nonnull Long freeMemoryInMegabytes, @Nonnull Long totalMemoryInMegabytes, @Nonnull Long minMemoryThresholdInMegabytes) {
        return new LowRamAnalyticsEvent(freeMemoryInMegabytes, totalMemoryInMegabytes, minMemoryThresholdInMegabytes);
    }

    public static LowRamEventFactory defaultFactory() {
        return new LowRamEventFactory(){

            @Override
            public LowRamAnalyticsEvent create(@Nonnull Long freeMemoryInMegabytes, @Nonnull Long totalMemoryInMegabytes, @Nonnull Long minMemoryThresholdInMegabytes) {
                return super.create(freeMemoryInMegabytes, totalMemoryInMegabytes, minMemoryThresholdInMegabytes);
            }
        };
    }
}

