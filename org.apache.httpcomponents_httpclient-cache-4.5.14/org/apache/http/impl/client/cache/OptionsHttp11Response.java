/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.Header
 *  org.apache.http.HeaderIterator
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.HttpVersion
 *  org.apache.http.ProtocolVersion
 *  org.apache.http.StatusLine
 *  org.apache.http.annotation.Contract
 *  org.apache.http.annotation.ThreadingBehavior
 *  org.apache.http.message.AbstractHttpMessage
 *  org.apache.http.message.BasicStatusLine
 *  org.apache.http.params.BasicHttpParams
 *  org.apache.http.params.HttpParams
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
    private final StatusLine statusLine = new BasicStatusLine((ProtocolVersion)HttpVersion.HTTP_1_1, 501, "");
    private final ProtocolVersion version = HttpVersion.HTTP_1_1;

    OptionsHttp11Response() {
    }

    public StatusLine getStatusLine() {
        return this.statusLine;
    }

    public void setStatusLine(StatusLine statusline) {
    }

    public void setStatusLine(ProtocolVersion ver, int code) {
    }

    public void setStatusLine(ProtocolVersion ver, int code, String reason) {
    }

    public void setStatusCode(int code) throws IllegalStateException {
    }

    public void setReasonPhrase(String reason) throws IllegalStateException {
    }

    public HttpEntity getEntity() {
        return null;
    }

    public void setEntity(HttpEntity entity) {
    }

    public Locale getLocale() {
        return null;
    }

    public void setLocale(Locale loc) {
    }

    public ProtocolVersion getProtocolVersion() {
        return this.version;
    }

    public boolean containsHeader(String name) {
        return this.headergroup.containsHeader(name);
    }

    public Header[] getHeaders(String name) {
        return this.headergroup.getHeaders(name);
    }

    public Header getFirstHeader(String name) {
        return this.headergroup.getFirstHeader(name);
    }

    public Header getLastHeader(String name) {
        return this.headergroup.getLastHeader(name);
    }

    public Header[] getAllHeaders() {
        return this.headergroup.getAllHeaders();
    }

    public void addHeader(Header header) {
    }

    public void addHeader(String name, String value) {
    }

    public void setHeader(Header header) {
    }

    public void setHeader(String name, String value) {
    }

    public void setHeaders(Header[] headers) {
    }

    public void removeHeader(Header header) {
    }

    public void removeHeaders(String name) {
    }

    public HeaderIterator headerIterator() {
        return this.headergroup.iterator();
    }

    public HeaderIterator headerIterator(String name) {
        return this.headergroup.iterator(name);
    }

    public HttpParams getParams() {
        if (this.params == null) {
            this.params = new BasicHttpParams();
        }
        return this.params;
    }

    public void setParams(HttpParams params) {
    }
}

