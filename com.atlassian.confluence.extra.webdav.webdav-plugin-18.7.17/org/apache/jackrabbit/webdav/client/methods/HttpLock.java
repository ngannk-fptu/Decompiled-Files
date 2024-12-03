/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HttpResponse
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.jackrabbit.webdav.client.methods.BaseDavRequest;
import org.apache.jackrabbit.webdav.client.methods.XmlEntity;
import org.apache.jackrabbit.webdav.header.DepthHeader;
import org.apache.jackrabbit.webdav.header.IfHeader;
import org.apache.jackrabbit.webdav.header.TimeoutHeader;
import org.apache.jackrabbit.webdav.lock.LockInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpLock
extends BaseDavRequest {
    private static final Logger LOG = LoggerFactory.getLogger(HttpLock.class);
    private final boolean isRefresh;

    public HttpLock(URI uri, LockInfo lockInfo) throws IOException {
        super(uri);
        TimeoutHeader th = new TimeoutHeader(lockInfo.getTimeout());
        super.setHeader(th.getHeaderName(), th.getHeaderValue());
        DepthHeader dh = new DepthHeader(lockInfo.isDeep());
        super.setHeader(dh.getHeaderName(), dh.getHeaderValue());
        super.setEntity(XmlEntity.create(lockInfo));
        this.isRefresh = false;
    }

    public HttpLock(String uri, LockInfo lockInfo) throws IOException {
        this(URI.create(uri), lockInfo);
    }

    public HttpLock(URI uri, long timeout, String[] lockTokens) {
        super(uri);
        TimeoutHeader th = new TimeoutHeader(timeout);
        super.setHeader(th.getHeaderName(), th.getHeaderValue());
        IfHeader ifh = new IfHeader(lockTokens);
        super.setHeader(ifh.getHeaderName(), ifh.getHeaderValue());
        this.isRefresh = true;
    }

    public HttpLock(String uri, long timeout, String[] lockTokens) {
        this(URI.create(uri), timeout, lockTokens);
    }

    public String getMethod() {
        return "LOCK";
    }

    public String getLockToken(HttpResponse response) {
        Header[] ltHeader = response.getHeaders("Lock-Token");
        if (ltHeader == null || ltHeader.length == 0) {
            return null;
        }
        if (ltHeader.length != 1) {
            LOG.debug("Multiple 'Lock-Token' header fields in response for " + this.getURI() + ": " + Arrays.asList(ltHeader));
            return null;
        }
        String v = ltHeader[0].getValue().trim();
        if (!v.startsWith("<") || !v.endsWith(">")) {
            LOG.debug("Invalid 'Lock-Token' header field in response for " + this.getURI() + ": " + Arrays.asList(ltHeader));
            return null;
        }
        return v.substring(1, v.length() - 1);
    }

    @Override
    public boolean succeeded(HttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        boolean lockTokenHeaderOk = this.isRefresh || null != this.getLockToken(response);
        return lockTokenHeaderOk && (statusCode == 200 || statusCode == 201);
    }
}

