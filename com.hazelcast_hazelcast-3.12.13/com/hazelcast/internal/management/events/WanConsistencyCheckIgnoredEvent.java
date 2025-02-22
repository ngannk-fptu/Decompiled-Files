/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanConsistencyCheckIgnoredEvent
extends AbstractWanEventBase {
    private final String reason;

    public WanConsistencyCheckIgnoredEvent(String wanReplicationName, String targetGroupName, String mapName, String reason) {
        super(wanReplicationName, targetGroupName, mapName);
        this.reason = reason;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_CONSISTENCY_CHECK_IGNORED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("reason", this.reason);
        return json;
    }
}

