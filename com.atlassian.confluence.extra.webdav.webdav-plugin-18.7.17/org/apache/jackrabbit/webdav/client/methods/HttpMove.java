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

public class HttpMove
extends BaseDavRequest {
    public HttpMove(URI uri, URI dest, boolean overwrite) {
        super(uri);
        super.setHeader("Destination", dest.toASCIIString());
        if (!overwrite) {
            super.setHeader("Overwrite", "F");
        }
    }

    public HttpMove(String uri, String dest, boolean overwrite) {
        this(URI.create(uri), URI.create(dest), overwrite);
    }

    public String getMethod() {
        return "MOVE";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 201 || statusCode == 204;
    }
}

