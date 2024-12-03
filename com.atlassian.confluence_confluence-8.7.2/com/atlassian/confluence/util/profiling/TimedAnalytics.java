/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.util.profiling.Ticker
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.TimedAnalyticsWirer;
import com.atlassian.util.profiling.Ticker;
import java.time.Duration;

@ParametersAreNonnullByDefault
public interface TimedAnalytics {
    public static TimedAnalytics timedAnalytics() {
        return TimedAnalyticsWirer.INSTANCE;
    }

    public Ticker start(String var1);

    public Ticker startAt(String var1, Duration var2);
}

