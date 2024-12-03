/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.wan;

public interface WanSyncStats {
    public long getDurationSecs();

    public int getPartitionsSynced();

    public int getRecordsSynced();
}

