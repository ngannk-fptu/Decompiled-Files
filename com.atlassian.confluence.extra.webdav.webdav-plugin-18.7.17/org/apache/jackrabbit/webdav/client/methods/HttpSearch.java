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
import org.apache.jackrabbit.webdav.search.SearchInfo;

public class HttpSearch
extends BaseDavRequest {
    public HttpSearch(URI uri, SearchInfo searchInfo) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(searchInfo));
    }

    public HttpSearch(String uri, SearchInfo searchInfo) throws IOException {
        this(URI.create(uri), searchInfo);
    }

    public String getMethod() {
        return "SEARCH";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 207;
    }
}

