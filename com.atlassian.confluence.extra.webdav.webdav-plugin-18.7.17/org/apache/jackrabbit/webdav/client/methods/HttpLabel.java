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
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.version.LabelInfo;

public class HttpLabel
extends BaseDavRequest {
    public HttpLabel(URI uri, LabelInfo labelInfo) throws IOException {
        super(uri);
        DepthHeader dh = new DepthHeader(labelInfo.getDepth());
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        super.setEntity(XmlEntity.create(labelInfo));
    }

    public HttpLabel(String uri, LabelInfo labelInfo) throws IOException {
        this(URI.create(uri), labelInfo);
    }

    public String getMethod() {
        return "LABEL";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200;
    }
}

