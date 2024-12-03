/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http;

import io.netty.handler.codec.http.DefaultHttpMessage;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessageUtil;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.internal.ObjectUtil;

public class DefaultHttpResponse
extends DefaultHttpMessage
implements HttpResponse {
    private HttpResponseStatus status;

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status) {
        this(version, status, true, false);
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders) {
        this(version, status, validateHeaders, false);
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, boolean validateHeaders, boolean singleFieldHeaders) {
        super(version, validateHeaders, singleFieldHeaders);
        this.status = (HttpResponseStatus)ObjectUtil.checkNotNull((Object)status, (String)"status");
    }

    public DefaultHttpResponse(HttpVersion version, HttpResponseStatus status, HttpHeaders headers) {
        super(version, headers);
        this.status = (HttpResponseStatus)ObjectUtil.checkNotNull((Object)status, (String)"status");
    }

    @Override
    @Deprecated
    public HttpResponseStatus getStatus() {
        return this.status();
    }

    @Override
    public HttpResponseStatus status() {
        return this.status;
    }

    @Override
    public HttpResponse setStatus(HttpResponseStatus status) {
        this.status = (HttpResponseStatus)ObjectUtil.checkNotNull((Object)status, (String)"status");
        return this;
    }

    @Override
    public HttpResponse setProtocolVersion(HttpVersion version) {
        super.setProtocolVersion(version);
        return this;
    }

    public String toString() {
        return HttpMessageUtil.appendResponse(new StringBuilder(256), this).toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + this.status.hashCode();
        result = 31 * result + super.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof DefaultHttpResponse)) {
            return false;
        }
        DefaultHttpResponse other = (DefaultHttpResponse)o;
        return this.status.equals(other.status()) && super.equals(o);
    }
}

