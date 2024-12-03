/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.client.report;

import java.util.Map;

public class EventReportItem {
    private final String name;
    private final long time;
    private final String user;
    private final String requestCorrelationId;
    private final Map<String, Object> properties;
    private final boolean removed;

    public EventReportItem(String name, long time, String user, String requestCorrelationId, Map<String, Object> properties, boolean removed) {
        this.name = name;
        this.time = time;
        this.user = user;
        this.requestCorrelationId = requestCorrelationId;
        this.properties = properties;
        this.removed = removed;
    }

    public String getName() {
        return this.name;
    }

    public long getTime() {
        return this.time;
    }

    public String getUser() {
        return this.user;
    }

    public String getRequestCorrelationId() {
        return this.requestCorrelationId;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public boolean isRemoved() {
        return this.removed;
    }
}

