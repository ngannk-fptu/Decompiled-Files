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

public class HttpUnsubscribe
extends BaseDavRequest {
    public HttpUnsubscribe(URI uri, String subscriptionId) {
        super(uri);
        super.setHeader("SubscriptionId", subscriptionId);
    }

    public HttpUnsubscribe(String uri, String subscriptionId) {
        this(URI.create(uri), subscriptionId);
    }

    public String getMethod() {
        return "UNSUBSCRIBE";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 204;
    }
}

