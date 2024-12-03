/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.impl.startup;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import java.util.Map;

@AsynchronousPreferred
@EventName(value="confluence.start")
public class ConfluenceStartAnalyticsEvent {
    private final Map<String, Object> properties;

    public ConfluenceStartAnalyticsEvent(Map<String, Object> properties) {
        this.properties = properties;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }
}

