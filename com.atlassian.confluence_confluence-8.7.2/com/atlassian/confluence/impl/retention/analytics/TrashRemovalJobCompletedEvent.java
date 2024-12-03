/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.retention.analytics;

import com.atlassian.confluence.impl.retention.RemovalType;
import com.atlassian.confluence.impl.retention.analytics.TrashRemovalStatisticHolder;
import java.util.Objects;

public class TrashRemovalJobCompletedEvent {
    private final RemovalType type;
    private final TrashRemovalStatisticHolder statisticHolder;

    public TrashRemovalJobCompletedEvent(RemovalType type, TrashRemovalStatisticHolder stats) {
        this.type = Objects.requireNonNull(type);
        this.statisticHolder = Objects.requireNonNull(stats);
    }

    public RemovalType getType() {
        return this.type;
    }

    public TrashRemovalStatisticHolder getStatisticHolder() {
        return this.statisticHolder;
    }
}

