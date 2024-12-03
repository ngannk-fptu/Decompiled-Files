/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.auth.AuthScheme
 *  org.apache.http.client.AuthCache
 */
package com.atlassian.sal.core.net;

import com.atlassian.sal.core.util.Assert;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScheme;
import org.apache.http.client.AuthCache;

class AllPortsAuthCache
implements AuthCache {
    private final Map<HttpHost, AuthScheme> map = new ConcurrentHashMap<HttpHost, AuthScheme>();

    AllPortsAuthCache() {
    }

    public AuthScheme get(HttpHost host) {
        Assert.notNull(host, "HTTP host");
        AuthScheme authScheme = this.map.get(host);
        if (authScheme != null) {
            return authScheme;
        }
        return this.map.get(new HttpHost(host.getHostName()));
    }

    public void put(HttpHost host, AuthScheme authScheme) {
        Assert.notNull(host, "HTTP host");
        this.map.put(host, authScheme);
    }

    public void remove(HttpHost host) {
        Assert.notNull(host, "HTTP host");
        this.map.remove(host);
    }

    public void clear() {
        this.map.clear();
    }

    public String toString() {
        return this.map.toString();
    }
}

