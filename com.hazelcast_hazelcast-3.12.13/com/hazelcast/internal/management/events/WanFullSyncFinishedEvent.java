/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.management.events.AbstractWanSyncFinishedEvent;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanFullSyncFinishedEvent
extends AbstractWanSyncFinishedEvent {
    public WanFullSyncFinishedEvent(String wanReplicationName, String targetGroupName, String mapName, long durationSecs, long recordsSynced, int partitionsSynced) {
        super(wanReplicationName, targetGroupName, mapName, durationSecs, recordsSynced, partitionsSynced);
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_SYNC_FINISHED_FULL;
    }
}

