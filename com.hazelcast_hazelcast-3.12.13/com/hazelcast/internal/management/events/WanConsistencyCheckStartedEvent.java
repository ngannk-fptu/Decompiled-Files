/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.management.events.AbstractWanEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanConsistencyCheckStartedEvent
extends AbstractWanEventBase {
    public WanConsistencyCheckStartedEvent(String wanReplicationName, String targetGroupName, String mapName) {
        super(wanReplicationName, targetGroupName, mapName);
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_CONSISTENCY_CHECK_STARTED;
    }
}

