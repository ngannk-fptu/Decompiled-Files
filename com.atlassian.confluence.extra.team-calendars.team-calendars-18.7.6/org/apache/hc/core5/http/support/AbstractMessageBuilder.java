/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.support;

import java.util.Iterator;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.message.BasicHeader;
import org.apache.hc.core5.http.message.HeaderGroup;

public abstract class AbstractMessageBuilder<T> {
    private ProtocolVersion version;
    private HeaderGroup headerGroup;

    protected AbstractMessageBuilder() {
    }

    protected void digest(HttpMessage message) {
        if (message == null) {
            return;
        }
        this.setVersion(message.getVersion());
        this.setHeaders(message.headerIterator());
    }

    public ProtocolVersion getVersion() {
        return this.version;
    }

    public AbstractMessageBuilder<T> setVersion(ProtocolVersion version) {
        this.version = version;
        return this;
    }

    public Header[] getHeaders() {
        return this.headerGroup != null ? this.headerGroup.getHeaders() : null;
    }

    public Header[] getHeaders(String name) {
        return this.headerGroup != null ? this.headerGroup.getHeaders(name) : null;
    }

    public AbstractMessageBuilder<T> setHeaders(Header ... headers) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.setHeaders(headers);
        return this;
    }

    public AbstractMessageBuilder<T> setHeaders(Iterator<Header> it) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        } else {
            this.headerGroup.clear();
        }
        while (it.hasNext()) {
            this.headerGroup.addHeader(it.next());
        }
        return this;
    }

    public Header[] getFirstHeaders() {
        return this.headerGroup != null ? this.headerGroup.getHeaders() : null;
    }

    public Header getFirstHeader(String name) {
        return this.headerGroup != null ? this.headerGroup.getFirstHeader(name) : null;
    }

    public Header getLastHeader(String name) {
        return this.headerGroup != null ? this.headerGroup.getLastHeader(name) : null;
    }

    public AbstractMessageBuilder<T> addHeader(Header header) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.addHeader(header);
        return this;
    }

    public AbstractMessageBuilder<T> addHeader(String name, String value) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.addHeader(new BasicHeader(name, value));
        return this;
    }

    public AbstractMessageBuilder<T> removeHeader(Header header) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.removeHeader(header);
        return this;
    }

    public AbstractMessageBuilder<T> removeHeaders(String name) {
        if (name == null || this.headerGroup == null) {
            return this;
        }
        Iterator<Header> i = this.headerGroup.headerIterator();
        while (i.hasNext()) {
            Header header = i.next();
            if (!name.equalsIgnoreCase(header.getName())) continue;
            i.remove();
        }
        return this;
    }

    public AbstractMessageBuilder<T> setHeader(Header header) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.setHeader(header);
        return this;
    }

    public AbstractMessageBuilder<T> setHeader(String name, String value) {
        if (this.headerGroup == null) {
            this.headerGroup = new HeaderGroup();
        }
        this.headerGroup.setHeader(new BasicHeader(name, value));
        return this;
    }

    protected abstract T build();
}

