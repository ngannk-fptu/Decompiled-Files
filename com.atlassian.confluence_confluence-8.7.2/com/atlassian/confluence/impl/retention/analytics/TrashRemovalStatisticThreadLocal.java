/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatistic;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.Nullable;

public class TrashRemovalStatisticThreadLocal {
    private static final ThreadLocal<TrashRemovalStatistic> trashRemovalStats = new ThreadLocal();

    public static void withStatistic(@Nullable TrashRemovalStatistic stats, Runnable runnable) {
        TrashRemovalStatistic currentStat = trashRemovalStats.get();
        trashRemovalStats.set(stats);
        try {
            runnable.run();
        }
        finally {
            trashRemovalStats.set(currentStat);
        }
    }

    public static Optional<TrashRemovalStatistic> getCurrentStatistic() {
        return Optional.ofNullable(trashRemovalStats.get());
    }
}

