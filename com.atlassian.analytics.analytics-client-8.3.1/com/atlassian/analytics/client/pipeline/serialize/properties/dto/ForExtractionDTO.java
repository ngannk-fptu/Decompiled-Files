/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.atlassian.analytics.client.pipeline.serialize.properties.dto;

import com.atlassian.analytics.client.pipeline.serialize.RequestInfo;
import java.util.Map;
import javax.annotation.Nullable;

public class ForExtractionDTO {
    private final Object event;
    private final Map<String, Object> properties;
    private final RequestInfo requestInfo;
    private final String sessionId;

    public ForExtractionDTO(Object event, Map<String, Object> properties, RequestInfo requestInfo, @Nullable String sessionId) {
        this.event = event;
        this.properties = properties;
        this.requestInfo = requestInfo;
        this.sessionId = sessionId;
    }

    public Object getEvent() {
        return this.event;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public RequestInfo getRequestInfo() {
        return this.requestInfo;
    }

    public String getSessionId() {
        return this.sessionId;
    }
}

