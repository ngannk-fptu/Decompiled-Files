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
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.ordering.OrderPatch;

public class HttpOrderpatch
extends BaseDavRequest {
    public HttpOrderpatch(URI uri, OrderPatch info) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpOrderpatch(String uri, OrderPatch info) throws IOException {
        this(URI.create(uri), info);
    }

    public String getMethod() {
        return "ORDERPATCH";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200;
    }
}

