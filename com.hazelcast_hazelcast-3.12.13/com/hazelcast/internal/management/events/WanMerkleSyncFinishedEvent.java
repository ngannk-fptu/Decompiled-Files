/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanSyncFinishedEvent;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanMerkleSyncFinishedEvent
extends AbstractWanSyncFinishedEvent {
    private final int nodesSynced;
    private final int minLeafEntryCount;
    private final int maxLeafEntryCount;
    private final double avgEntriesPerLeaf;
    private final double stdDevEntriesPerLeaf;

    public WanMerkleSyncFinishedEvent(String wanReplicationName, String targetGroupName, String mapName, long durationSecs, int partitionsSynced, int nodesSynced, long recordsSynced, int minLeafEntryCount, int maxLeafEntryCount, double avgEntriesPerLeaf, double stdDevEntriesPerLeaf) {
        super(wanReplicationName, targetGroupName, mapName, durationSecs, recordsSynced, partitionsSynced);
        this.nodesSynced = nodesSynced;
        this.minLeafEntryCount = minLeafEntryCount;
        this.maxLeafEntryCount = maxLeafEntryCount;
        this.avgEntriesPerLeaf = avgEntriesPerLeaf;
        this.stdDevEntriesPerLeaf = stdDevEntriesPerLeaf;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_SYNC_FINISHED_MERKLE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("nodesSynced", this.nodesSynced);
        json.add("minLeafEntryCount", this.minLeafEntryCount);
        json.add("maxLeafEntryCount", this.maxLeafEntryCount);
        json.add("avgEntriesPerLeaf", this.avgEntriesPerLeaf);
        json.add("stdDevEntriesPerLeaf", this.stdDevEntriesPerLeaf);
        return json;
    }
}

