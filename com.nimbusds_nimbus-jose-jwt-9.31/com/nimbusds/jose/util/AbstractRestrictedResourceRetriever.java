/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.ThreadSafe
 */
package com.nimbusds.jose.util;

import com.nimbusds.jose.util.RestrictedResourceRetriever;
import java.util.List;
import java.util.Map;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class AbstractRestrictedResourceRetriever
implements RestrictedResourceRetriever {
    private int connectTimeout;
    private int readTimeout;
    private int sizeLimit;
    private Map<String, List<String>> headers;

    public AbstractRestrictedResourceRetriever(int connectTimeout, int readTimeout, int sizeLimit) {
        this.setConnectTimeout(connectTimeout);
        this.setReadTimeout(readTimeout);
        this.setSizeLimit(sizeLimit);
    }

    @Override
    public int getConnectTimeout() {
        return this.connectTimeout;
    }

    @Override
    public void setConnectTimeout(int connectTimeoutMs) {
        if (connectTimeoutMs < 0) {
            throw new IllegalArgumentException("The connect timeout must not be negative");
        }
        this.connectTimeout = connectTimeoutMs;
    }

    @Override
    public int getReadTimeout() {
        return this.readTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeoutMs) {
        if (readTimeoutMs < 0) {
            throw new IllegalArgumentException("The read timeout must not be negative");
        }
        this.readTimeout = readTimeoutMs;
    }

    @Override
    public int getSizeLimit() {
        return this.sizeLimit;
    }

    @Override
    public void setSizeLimit(int sizeLimitBytes) {
        if (sizeLimitBytes < 0) {
            throw new IllegalArgumentException("The size limit must not be negative");
        }
        this.sizeLimit = sizeLimitBytes;
    }

    @Override
    public Map<String, List<String>> getHeaders() {
        return this.headers;
    }

    @Override
    public void setHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }
}

