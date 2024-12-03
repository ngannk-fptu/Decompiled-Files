/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanConsistencyCheckFinishedEvent
extends AbstractWanEventBase {
    private final int diffCount;
    private final int checkedCount;
    private final int entriesToSync;

    public WanConsistencyCheckFinishedEvent(String wanReplicationName, String targetGroupName, String mapName, int diffCount, int checkedCount, int entriesToSync) {
        super(wanReplicationName, targetGroupName, mapName);
        this.diffCount = diffCount;
        this.checkedCount = checkedCount;
        this.entriesToSync = entriesToSync;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_CONSISTENCY_CHECK_FINISHED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("diffCount", this.diffCount);
        json.add("checkedCount", this.checkedCount);
        json.add("entriesToSync", this.entriesToSync);
        return json;
    }
}

