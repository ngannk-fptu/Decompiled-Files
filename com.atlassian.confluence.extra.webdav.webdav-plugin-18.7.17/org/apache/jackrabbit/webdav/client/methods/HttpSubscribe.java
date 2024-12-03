/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpResponse
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.net.URI;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.header.CodedUrlHeader;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.observation.SubscriptionInfo;

public class HttpSubscribe
extends BaseDavRequest {
    public HttpSubscribe(URI uri, SubscriptionInfo info, String subscriptionId) throws IOException {
        super(uri);
        long to;
        if (info == null) {
            throw new IllegalArgumentException("SubscriptionInfo must not be null.");
        }
        if (subscriptionId != null) {
            CodedUrlHeader h = new CodedUrlHeader("SubscriptionId", subscriptionId);
            super.setHeader(h.getHeaderName(), h.getHeaderValue());
        }
        if ((to = info.getTimeOut()) != Integer.MIN_VALUE) {
            TimeoutHeader h = new TimeoutHeader(info.getTimeOut());
            super.setHeader(h.getHeaderName(), h.getHeaderValue());
        }
        DepthHeader dh = new DepthHeader(info.isDeep());
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        super.setEntity(XmlEntity.create(info));
    }

    public HttpSubscribe(String uri, SubscriptionInfo info, String subscriptionId) throws IOException {
        this(URI.create(uri), info, subscriptionId);
    }

    public String getSubscriptionId(HttpResponse response) {
        Header sbHeader = response.getFirstHeader("SubscriptionId");
        if (sbHeader != null) {
            CodedUrlHeader cuh = new CodedUrlHeader("SubscriptionId", sbHeader.getValue());
            return cuh.getCodedUrl();
        }
        return null;
    }

    public String getMethod() {
        return "SUBSCRIBE";
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        return statusCode == 200;
    }
}

