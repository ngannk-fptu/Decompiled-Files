/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonObject;

public final class EventMetadata {
    private final EventType type;
    private final long timestamp;

    public EventMetadata(EventType type, long timestamp) {
        this.type = type;
        this.timestamp = timestamp;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("type", this.type.code);
        json.add("timestamp", this.timestamp);
        return json;
    }

    public static enum EventType {
        WAN_CONSISTENCY_CHECK_STARTED(1),
        WAN_CONSISTENCY_CHECK_FINISHED(2),
        WAN_SYNC_STARTED(3),
        WAN_SYNC_FINISHED_FULL(4),
        WAN_CONSISTENCY_CHECK_IGNORED(5),
        WAN_SYNC_PROGRESS_UPDATE(6),
        WAN_SYNC_FINISHED_MERKLE(7),
        WAN_CONFIGURATION_ADDED(8),
        ADD_WAN_CONFIGURATION_IGNORED(9),
        WAN_SYNC_IGNORED(10),
        WAN_CONFIGURATION_EXTENDED(11);

        private final int code;

        private EventType(int code) {
            this.code = code;
        }

        public int getCode() {
            return this.code;
        }
    }
}

