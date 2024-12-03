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
import org.apache.jackrabbit.webdav.version.MergeInfo;

public class HttpMerge
extends BaseDavRequest {
    public HttpMerge(URI uri, MergeInfo mergeInfo) throws IOException {
        super(uri);
        super.setEntity(XmlEntity.create(mergeInfo));
    }

    public HttpMerge(String uri, MergeInfo mergeInfo) throws IOException {
        this(URI.create(uri), mergeInfo);
    }

    public String getMethod() {
        return "MERGE";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 207;
    }
}

