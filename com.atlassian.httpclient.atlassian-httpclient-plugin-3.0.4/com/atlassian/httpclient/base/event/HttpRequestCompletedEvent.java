/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.Analytics
 */
package com.atlassian.httpclient.base.event;

import com.atlassian.analytics.api.annotations.Analytics;
import com.atlassian.httpclient.base.event.AbstractHttpRequestEvent;
import java.util.Map;

@Analytics(value="httpclient.requestcompleted")
public final class HttpRequestCompletedEvent
extends AbstractHttpRequestEvent {
    public HttpRequestCompletedEvent(String url, String httpMethod, int statusCode, long requestDuration, Map<String, String> properties) {
        super(url, httpMethod, statusCode, requestDuration, properties);
    }
}

