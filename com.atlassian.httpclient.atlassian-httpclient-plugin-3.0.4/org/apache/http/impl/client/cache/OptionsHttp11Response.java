/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.impl.client.cache;

import java.util.Locale;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;

@Contract(threading=ThreadingBehavior.IMMUTABLE)
final class OptionsHttp11Response
extends AbstractHttpMessage
implements HttpResponse {
    private final StatusLine statusLine = new BasicStatusLine(HttpVersion.HTTP_1_1, 501, "");
    private final ProtocolVersion version = HttpVersion.HTTP_1_1;

    OptionsHttp11Response() {
    }

    @Override
    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    @Override
    public void setStatusLine(StatusLine statusline) {
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code) {
    }

    @Override
    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
    }

    @Override
    public void setStatusCode(int code) throws IllegalStateException {
    }

    @Override
    public void setReasonPhrase(String reason) throws IllegalStateException {
    }

    @Override
    public HttpEntity getEntity() {
        return null;
    }

    @Override
    public void setEntity(HttpEntity entity) {
    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public void setLocale(Locale loc) {
    }

    @Override
    public ProtocolVersion getProtocolVersion() {
        return this.version;
    }

    @Override
    public boolean containsHeader(String name) {
        return this.headergroup.containsHeader(name);
    }

    @Override
    public Header[] getHeaders(String name) {
        return this.headergroup.getHeaders(name);
    }

    @Override
    public Header getFirstHeader(String name) {
        return this.headergroup.getFirstHeader(name);
    }

    @Override
    public Header getLastHeader(String name) {
        return this.headergroup.getLastHeader(name);
    }

    @Override
    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }

    @Override
    public void addHeader(Header header) {
    }

    @Override
    public void addHeader(String name, String value) {
    }

    @Override
    public void setHeader(Header header) {
    }

    @Override
    public void setHeader(String name, String value) {
    }

    @Override
    public void setHeaders(Header[] headers) {
    }

    @Override
    public void removeHeader(Header header) {
    }

    @Override
    public void removeHeaders(String name) {
    }

    @Override
    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }

    @Override
    public HeaderIterator headerIterator(String name) {
        return this.headergroup.iterator(name);
    }

    @Override
    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }

    @Override
    public void setParams(HttpParams params) {
    }
}

