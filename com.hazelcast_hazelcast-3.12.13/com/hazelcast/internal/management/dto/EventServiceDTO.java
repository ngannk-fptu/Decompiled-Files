/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.dto;

import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.JsonSerializable;
import com.hazelcast.spi.EventService;
import com.hazelcast.util.JsonUtil;

public class EventServiceDTO
implements JsonSerializable {
    public int eventThreadCount;
    public int eventQueueCapacity;
    public int eventQueueSize;

    public EventServiceDTO() {
    }

    public EventServiceDTO(EventService es) {
        this.eventThreadCount = es.getEventThreadCount();
        this.eventQueueCapacity = es.getEventQueueCapacity();
        this.eventQueueSize = es.getEventQueueSize();
    }

    @Override
    public JsonObject toJson() {
        JsonObject root = new JsonObject();
        root.add("eventThreadCount", this.eventThreadCount);
        root.add("eventQueueCapacity", this.eventQueueCapacity);
        root.add("eventQueueSize", this.eventQueueSize);
        return root;
    }

    @Override
    public void fromJson(JsonObject json) {
        this.eventThreadCount = JsonUtil.getInt(json, "eventThreadCount", -1);
        this.eventQueueCapacity = JsonUtil.getInt(json, "eventQueueCapacity", -1);
        this.eventQueueSize = JsonUtil.getInt(json, "eventQueueSize", -1);
    }
}

