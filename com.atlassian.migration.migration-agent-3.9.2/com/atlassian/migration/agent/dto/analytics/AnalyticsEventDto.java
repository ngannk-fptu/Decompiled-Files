/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.migration.agent.dto.analytics;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.codehaus.jackson.annotate.JsonProperty;

public abstract class AnalyticsEventDto {
    @JsonProperty
    private final String action;
    @JsonProperty
    private final Map<String, Object> attributes;
    @JsonProperty
    private final long timestamp;

    AnalyticsEventDto(long timestamp, String action, Map<String, Object> attributes) {
        this.timestamp = timestamp;
        this.action = action;
        this.attributes = attributes == null ? ImmutableMap.of() : attributes;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public String getAction() {
        return this.action;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }
}

