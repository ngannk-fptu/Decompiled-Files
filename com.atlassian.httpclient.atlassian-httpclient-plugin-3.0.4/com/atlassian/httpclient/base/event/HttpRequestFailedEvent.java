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

@Analytics(value="httpclient.requestfailed")
public final class HttpRequestFailedEvent
extends AbstractHttpRequestEvent {
    public HttpRequestFailedEvent(String url, String httpMethod, int statusCode, long elapsed, Map<String, String> properties) {
        super(url, httpMethod, statusCode, elapsed, properties);
    }

    public HttpRequestFailedEvent(String url, String httpMethod, String error, long elapsed, Map<String, String> properties) {
        super(url, httpMethod, error, elapsed, properties);
    }
}

