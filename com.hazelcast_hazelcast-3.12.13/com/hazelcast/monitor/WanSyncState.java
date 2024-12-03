/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.monitor;

import com.hazelcast.monitor.LocalInstanceStats;
import com.hazelcast.wan.WanSyncStatus;

public interface WanSyncState
extends LocalInstanceStats {
    public WanSyncStatus getStatus();

    public int getSyncedPartitionCount();

    public String getActiveWanConfigName();

    public String getActivePublisherName();
}

