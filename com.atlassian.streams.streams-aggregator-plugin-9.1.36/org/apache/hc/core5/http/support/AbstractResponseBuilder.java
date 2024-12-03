/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.support;

import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.ProtocolVersion;
import org.apache.hc.core5.http.support.AbstractMessageBuilder;

public abstract class AbstractResponseBuilder<T>
extends AbstractMessageBuilder<T> {
    private int status;

    protected AbstractResponseBuilder(int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public AbstractResponseBuilder<T> setVersion(ProtocolVersion version) {
        super.setVersion(version);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> setHeaders(Header ... headers) {
        super.setHeaders(headers);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> addHeader(Header header) {
        super.addHeader(header);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> addHeader(String name, String value) {
        super.addHeader(name, value);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> removeHeader(Header header) {
        super.removeHeader(header);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> removeHeaders(String name) {
        super.removeHeaders(name);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> setHeader(Header header) {
        super.setHeader(header);
        return this;
    }

    @Override
    public AbstractResponseBuilder<T> setHeader(String name, String value) {
        super.setHeader(name, value);
        return this;
    }

    @Override
    protected abstract T build();
}

