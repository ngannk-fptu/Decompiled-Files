/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatistic;

public class TrashRemovalStatisticHolder {
    private final TrashRemovalStatistic globalStats;
    private final TrashRemovalStatistic spaceStats;

    public TrashRemovalStatisticHolder() {
        this(new TrashRemovalStatistic(), new TrashRemovalStatistic());
    }

    public TrashRemovalStatisticHolder(TrashRemovalStatistic globalStats, TrashRemovalStatistic spaceStats) {
        this.globalStats = globalStats;
        this.spaceStats = spaceStats;
    }

    public TrashRemovalStatistic getGlobalStats() {
        return this.globalStats;
    }

    public TrashRemovalStatistic getSpaceStats() {
        return this.spaceStats;
    }
}

