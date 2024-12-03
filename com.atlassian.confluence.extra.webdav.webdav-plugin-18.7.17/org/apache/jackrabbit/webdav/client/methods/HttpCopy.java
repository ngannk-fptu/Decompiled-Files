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

public class HttpCopy
extends BaseDavRequest {
    public HttpCopy(URI uri, URI dest, boolean overwrite, boolean shallow) {
        super(uri);
        super.setHeader("Destination", dest.toASCIIString());
        if (!overwrite) {
            super.setHeader("Overwrite", "F");
        }
        if (shallow) {
            super.setHeader("Depth", "0");
        }
    }

    public HttpCopy(String uri, String dest, boolean overwrite, boolean shallow) {
        this(URI.create(uri), URI.create(dest), overwrite, shallow);
    }

    public String getMethod() {
        return "COPY";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 201 || statusCode == 204;
    }
}

