/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.Event;
import java.util.List;

public class EventBatch {
    private final String cluster;
    private final String address;
    private final List<Event> events;

    public EventBatch(String cluster, String address, List<Event> events) {
        this.cluster = cluster;
        this.address = address;
        this.events = events;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("cluster", this.cluster);
        json.add("address", this.address);
        JsonArray eventJsonArray = new JsonArray();
        for (Event event : this.events) {
            JsonObject metadataJson = new JsonObject();
            metadataJson.add("type", event.getType().getCode());
            metadataJson.add("timestamp", event.getTimestamp());
            JsonObject eventJson = new JsonObject();
            eventJson.add("metadata", metadataJson);
            eventJson.add("data", event.toJson());
            eventJsonArray.add(eventJson);
        }
        json.add("events", eventJsonArray);
        return json;
    }
}

