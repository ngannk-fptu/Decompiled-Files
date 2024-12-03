/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http;

import com.atlassian.confluence.util.http.Authenticator;
import java.util.HashMap;
import java.util.Map;

@Deprecated(forRemoval=true)
public class HttpRequest {
    private String url;
    private int maximumSize;
    private long maximumCacheAgeInMillis;
    private Authenticator authenticator;
    private Map<String, String> headers = new HashMap<String, String>();

    public String getUrl() {
        return this.url;
    }

    public int getMaximumSize() {
        return this.maximumSize;
    }

    public long getMaximumCacheAgeInMillis() {
        return this.maximumCacheAgeInMillis;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setMaximumSize(int maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void setMaximumCacheAgeInMillis(long maximumCacheAgeInMillis) {
        this.maximumCacheAgeInMillis = maximumCacheAgeInMillis;
    }

    public Authenticator getAuthenticator() {
        return this.authenticator;
    }

    public void setAuthenticator(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }

    public Iterable<Map.Entry<String, String>> getHeaders() {
        return this.headers.entrySet();
    }
}

