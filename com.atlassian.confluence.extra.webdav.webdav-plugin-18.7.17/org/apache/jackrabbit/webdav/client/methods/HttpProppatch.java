/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpResponse
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.property.ProppatchInfo;

public class HttpProppatch
extends BaseDavRequest {
    public HttpProppatch(URI uri, ProppatchInfo info) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(info));
    }

    public HttpProppatch(URI uri, List<? extends PropEntry> changeList) throws IOException {
        this(uri, new ProppatchInfo(changeList));
    }

    public HttpProppatch(URI uri, DavPropertySet setProperties, DavPropertyNameSet removeProperties) throws IOException {
        this(uri, new ProppatchInfo(setProperties, removeProperties));
    }

    public HttpProppatch(String uri, List<? extends PropEntry> changeList) throws IOException {
        this(URI.create(uri), new ProppatchInfo(changeList));
    }

    public HttpProppatch(String uri, DavPropertySet setProperties, DavPropertyNameSet removeProperties) throws IOException {
        this(URI.create(uri), new ProppatchInfo(setProperties, removeProperties));
    }

    public String getMethod() {
        return "PROPPATCH";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 207;
    }
}

