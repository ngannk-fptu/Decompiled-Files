/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.EventMetadata;

public interface Event {
    public EventMetadata.EventType getType();

    public long getTimestamp();

    public JsonObject toJson();
}

