/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.ClassicHttpResponse
 *  org.apache.hc.core5.http.Header
 *  org.apache.hc.core5.http.HttpEntity
 *  org.apache.hc.core5.http.ProtocolException
 *  org.apache.hc.core5.http.ProtocolVersion
 *  org.apache.hc.core5.util.Args
 */
package org.apache.hc.client5.http.impl.classic;

import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import org.apache.hc.client5.http.classic.ExecRuntime;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ProtocolException;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.util.Args;

public final class CloseableHttpResponse
implements ClassicHttpResponse {
    private final ClassicHttpResponse response;
    private final ExecRuntime execRuntime;

    static CloseableHttpResponse adapt(ClassicHttpResponse response) {
        if (response == null) {
            return null;
        }
        return response instanceof CloseableHttpResponse ? (CloseableHttpResponse)response : new CloseableHttpResponse(response, null);
    }

    CloseableHttpResponse(ClassicHttpResponse response, ExecRuntime execRuntime) {
        this.response = (ClassicHttpResponse)Args.notNull((Object)response, (String)"Response");
        this.execRuntime = execRuntime;
    }

    public int getCode() {
        return this.response.getCode();
    }

    public HttpEntity getEntity() {
        return this.response.getEntity();
    }

    public boolean containsHeader(String name) {
        return this.response.containsHeader(name);
    }

    public void setVersion(ProtocolVersion version) {
        this.response.setVersion(version);
    }

    public void setCode(int code) {
        this.response.setCode(code);
    }

    public String getReasonPhrase() {
        return this.response.getReasonPhrase();
    }

    public int countHeaders(String name) {
        return this.response.countHeaders(name);
    }

    public void setEntity(HttpEntity entity) {
        this.response.setEntity(entity);
    }

    public ProtocolVersion getVersion() {
        return this.response.getVersion();
    }

    public void setReasonPhrase(String reason) {
        this.response.setReasonPhrase(reason);
    }

    public Header[] getHeaders(String name) {
        return this.response.getHeaders(name);
    }

    public void addHeader(Header header) {
        this.response.addHeader(header);
    }

    public Locale getLocale() {
        return this.response.getLocale();
    }

    public void addHeader(String name, Object value) {
        this.response.addHeader(name, value);
    }

    public void setLocale(Locale loc) {
        this.response.setLocale(loc);
    }

    public Header getHeader(String name) throws ProtocolException {
        return this.response.getHeader(name);
    }

    public void setHeader(Header header) {
        this.response.setHeader(header);
    }

    public Header getFirstHeader(String name) {
        return this.response.getFirstHeader(name);
    }

    public void setHeader(String name, Object value) {
        this.response.setHeader(name, value);
    }

    public void setHeaders(Header ... headers) {
        this.response.setHeaders(headers);
    }

    public boolean removeHeader(Header header) {
        return this.response.removeHeader(header);
    }

    public boolean removeHeaders(String name) {
        return this.response.removeHeaders(name);
    }

    public Header getLastHeader(String name) {
        return this.response.getLastHeader(name);
    }

    public Header[] getHeaders() {
        return this.response.getHeaders();
    }

    public Iterator<Header> headerIterator() {
        return this.response.headerIterator();
    }

    public Iterator<Header> headerIterator(String name) {
        return this.response.headerIterator(name);
    }

    public void close() throws IOException {
        if (this.execRuntime != null) {
            try {
                this.response.close();
                this.execRuntime.disconnectEndpoint();
            }
            finally {
                this.execRuntime.discardEndpoint();
            }
        } else {
            this.response.close();
        }
    }

    public String toString() {
        return this.response.toString();
    }
}

