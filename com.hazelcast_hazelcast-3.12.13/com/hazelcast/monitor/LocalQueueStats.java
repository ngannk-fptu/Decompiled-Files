/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;

public interface LocalQueueStats
extends LocalInstanceStats {
    public long getOwnedItemCount();

    public long getBackupItemCount();

    public long getMinAge();

    public long getMaxAge();

    public long getAvgAge();

    public long getOfferOperationCount();

    public long getRejectedOfferOperationCount();

    public long getPollOperationCount();

    public long getEmptyPollOperationCount();

    public long getOtherOperationsCount();

    public long getEventOperationCount();
}

