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
import org.apache.jackrabbit.webdav.version.UpdateInfo;

public class HttpUpdate
extends BaseDavRequest {
    public HttpUpdate(URI uri, UpdateInfo updateInfo) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(updateInfo));
    }

    public HttpUpdate(String uri, UpdateInfo updateInfo) throws IOException {
        this(URI.create(uri), updateInfo);
    }

    public String getMethod() {
        return "UPDATE";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 207;
    }
}

