/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanSyncProgressUpdateEvent
extends AbstractWanEventBase {
    private final int partitionsToSync;
    private final int partitionsSynced;

    public WanSyncProgressUpdateEvent(String wanReplicationName, String targetGroupName, String mapName, int partitionsToSync, int partitionsSynced) {
        super(wanReplicationName, targetGroupName, mapName);
        this.partitionsToSync = partitionsToSync;
        this.partitionsSynced = partitionsSynced;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_SYNC_PROGRESS_UPDATE;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("partitionsToSync", this.partitionsToSync);
        json.add("partitionsSynced", this.partitionsSynced);
        return json;
    }
}

