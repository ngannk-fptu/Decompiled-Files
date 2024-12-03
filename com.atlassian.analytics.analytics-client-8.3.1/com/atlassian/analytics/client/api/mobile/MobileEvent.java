/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.analytics.client.api.mobile;

import com.atlassian.analytics.client.api.ClientEvent;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

public class MobileEvent
implements ClientEvent {
    private final String name;
    private final Map<String, Object> properties;
    private final long clientTime;

    @JsonCreator
    public MobileEvent(@JsonProperty(value="name") String name, @JsonProperty(value="properties") Map<String, Object> properties, @JsonProperty(value="clientTime") long clientTime) {
        this.name = name;
        this.properties = properties;
        this.clientTime = clientTime;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Map<String, Object> getProperties() {
        return this.properties;
    }

    @Override
    public long getClientTime() {
        return this.clientTime;
    }
}

