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

public class HttpMkcol
extends BaseDavRequest {
    public HttpMkcol(URI uri) {
        super(uri);
    }

    public HttpMkcol(String uri) {
        this(URI.create(uri));
    }

    public String getMethod() {
        return "MKCOL";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 201;
    }
}

