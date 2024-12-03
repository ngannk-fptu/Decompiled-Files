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

public class HttpCheckout
extends BaseDavRequest {
    public HttpCheckout(URI uri) {
        super(uri);
    }

    public HttpCheckout(String uri) {
        this(URI.create(uri));
    }

    public String getMethod() {
        return "CHECKOUT";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200;
    }
}

