/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.bind.RebindInfo;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;

public class HttpRebind
extends BaseDavRequest {
    public HttpRebind(URI uri, RebindInfo info) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpRebind(String uri, RebindInfo info) throws IOException {
        this(URI.create(uri), info);
    }

    public String getMethod() {
        return "REBIND";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200 || statusCode == 201;
    }
}

