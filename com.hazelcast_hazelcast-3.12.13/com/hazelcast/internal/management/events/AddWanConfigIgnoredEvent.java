/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractEventBase;
import com.hazelcast.internal.management.events.EventMetadata;

public final class AddWanConfigIgnoredEvent
extends AbstractEventBase {
    private final String wanConfigName;
    private final String reason;

    private AddWanConfigIgnoredEvent(String wanConfigName, String reason) {
        this.wanConfigName = wanConfigName;
        this.reason = reason;
    }

    public static AddWanConfigIgnoredEvent alreadyExists(String wanConfigName) {
        return new AddWanConfigIgnoredEvent(wanConfigName, "A WAN replication config already exists with the given name.");
    }

    public static AddWanConfigIgnoredEvent enterpriseOnly(String wanConfigName) {
        return new AddWanConfigIgnoredEvent(wanConfigName, "Adding new WAN replication config is supported for enterprise clusters only.");
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.ADD_WAN_CONFIGURATION_IGNORED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("wanConfigName", this.wanConfigName);
        json.add("reason", this.reason);
        return json;
    }
}

