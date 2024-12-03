/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.restapi.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.rest.endpoint.hit")
public class RestEndpointAnalyticsEvent {
    private final String method;
    private final String path;

    public RestEndpointAnalyticsEvent(String method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getMethod() {
        return this.method;
    }

    public String getPath() {
        return this.path;
    }
}

