/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanEventBase;

abstract class AbstractWanSyncFinishedEvent
extends AbstractWanEventBase {
    private final int partitionsSynced;
    private final long recordsSynced;
    private final long durationSecs;

    AbstractWanSyncFinishedEvent(String wanReplicationName, String targetGroupName, String mapName, long durationSecs, long recordsSynced, int partitionsSynced) {
        super(wanReplicationName, targetGroupName, mapName);
        this.durationSecs = durationSecs;
        this.recordsSynced = recordsSynced;
        this.partitionsSynced = partitionsSynced;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("durationSecs", this.durationSecs);
        json.add("partitionsSynced", this.partitionsSynced);
        json.add("recordsSynced", this.recordsSynced);
        return json;
    }
}

