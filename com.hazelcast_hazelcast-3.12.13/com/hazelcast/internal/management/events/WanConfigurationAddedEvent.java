/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public class WanConfigurationAddedEvent
extends AbstractEventBase {
    private final String wanConfigName;

    public WanConfigurationAddedEvent(String wanConfigName) {
        this.wanConfigName = wanConfigName;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_CONFIGURATION_ADDED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("wanConfigName", this.wanConfigName);
        return json;
    }
}

