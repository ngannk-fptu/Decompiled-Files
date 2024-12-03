/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractEventBase;

abstract class AbstractWanEventBase
extends AbstractEventBase {
    protected final String wanReplicationName;
    protected final String targetGroupName;
    protected final String mapName;

    protected AbstractWanEventBase(String wanReplicationName, String targetGroupName, String mapName) {
        this.wanReplicationName = wanReplicationName;
        this.targetGroupName = targetGroupName;
        this.mapName = mapName;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("wanReplicationName", this.wanReplicationName);
        json.add("targetGroupName", this.targetGroupName);
        json.add("mapName", this.mapName);
        return json;
    }
}

