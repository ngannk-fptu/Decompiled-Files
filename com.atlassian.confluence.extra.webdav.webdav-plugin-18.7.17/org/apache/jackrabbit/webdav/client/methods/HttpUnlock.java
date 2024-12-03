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
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;

public class HttpUnlock
extends BaseDavRequest {
    public HttpUnlock(URI uri, String lockToken) {
        super(uri);
        CodedUrlHeader lth = new CodedUrlHeader("Lock-Token", lockToken);
        super.setHeader(lth.getHeaderName(), lth.getHeaderValue());
    }

    public HttpUnlock(String uri, String lockToken) {
        this(URI.create(uri), lockToken);
    }

    public String getMethod() {
        return "UNLOCK";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200 || statusCode == 204;
    }
}

