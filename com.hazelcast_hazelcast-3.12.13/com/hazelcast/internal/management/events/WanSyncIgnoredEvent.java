/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public final class WanSyncIgnoredEvent
extends AbstractWanEventBase {
    private final String reason;

    private WanSyncIgnoredEvent(String wanReplicationName, String targetGroupName, String mapName, String reason) {
        super(wanReplicationName, targetGroupName, mapName);
        this.reason = reason;
    }

    public static WanSyncIgnoredEvent enterpriseOnly(String wanReplicationName, String targetGroupName, String mapName) {
        return new WanSyncIgnoredEvent(wanReplicationName, targetGroupName, mapName, "WAN sync is supported for enterprise clusters only.");
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_SYNC_IGNORED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = super.toJson();
        json.add("reason", this.reason);
        return json;
    }
}

