/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.header.PollTimeoutHeader;

public class HttpPoll
extends BaseDavRequest {
    public HttpPoll(URI uri, String subscriptionId, long timeout) {
        super(uri);
        super.setHeader("SubscriptionId", subscriptionId);
        if (timeout > 0L) {
            PollTimeoutHeader th = new PollTimeoutHeader(timeout);
            super.setHeader(th.getHeaderName(), th.getHeaderValue());
        }
    }

    public HttpPoll(String uri, String subscriptionId, long timeout) {
        this(URI.create(uri), subscriptionId, timeout);
    }

    public String getMethod() {
        return "POLL";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200;
    }
}

