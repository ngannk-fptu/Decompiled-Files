/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.management.events;

import com.hazelcast.internal.json.JsonArray;
import com.hazelcast.internal.json.JsonObject;
import com.hazelcast.internal.management.events.AbstractEventBase;
import com.hazelcast.internal.management.events.EventMetadata;
import java.util.Collection;

public class WanConfigurationExtendedEvent
extends AbstractEventBase {
    private final String wanConfigName;
    private final Collection<String> wanPublisherIds;

    public WanConfigurationExtendedEvent(String wanConfigName, Collection<String> wanPublisherIds) {
        this.wanConfigName = wanConfigName;
        this.wanPublisherIds = wanPublisherIds;
    }

    @Override
    public EventMetadata.EventType getType() {
        return EventMetadata.EventType.WAN_CONFIGURATION_EXTENDED;
    }

    @Override
    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("wanConfigName", this.wanConfigName);
        JsonArray publisherIds = new JsonArray();
        for (String publisherId : this.wanPublisherIds) {
            publisherIds.add(publisherId);
        }
        json.add("wanPublisherIds", publisherIds);
        return json;
    }
}

