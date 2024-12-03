/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanSyncStartedEvent
extends AbstractWanEventBase {
    public WanSyncStartedEvent(String wanReplicationName, String targetGroupName, String mapName) {
        super(wanReplicationName, targetGroupName, mapName);
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_SYNC_STARTED;
    }
}

