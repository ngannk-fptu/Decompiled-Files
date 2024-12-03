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
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.PropfindInfo;

public class HttpPropfind
extends BaseDavRequest {
    public HttpPropfind(URI uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
        super(uri);
        DepthHeader dh = new DepthHeader(depth);
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        PropfindInfo info = new PropfindInfo(propfindType, names);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpPropfind(URI uri, DavPropertyNameSet names, int depth) throws IOException {
        this(uri, 0, names, depth);
    }

    public HttpPropfind(URI uri, int propfindType, int depth) throws IOException {
        this(uri, propfindType, new DavPropertyNameSet(), depth);
    }

    public HttpPropfind(String uri, int propfindType, int depth) throws IOException {
        this(URI.create(uri), propfindType, depth);
    }

    public HttpPropfind(String uri, int propfindType, DavPropertyNameSet names, int depth) throws IOException {
        this(URI.create(uri), propfindType, names, depth);
    }

    public HttpPropfind(String uri, DavPropertyNameSet names, int depth) throws IOException {
        this(URI.create(uri), names, depth);
    }

    public String getMethod() {
        return "PROPFIND";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 207;
    }
}

