/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.core5.http.HttpHost
 */
package org.apache.hc.client5.http.auth;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.core5.http.HttpHost;

public interface AuthCache {
    public void put(HttpHost var1, AuthScheme var2);

    public AuthScheme get(HttpHost var1);

    public void remove(HttpHost var1);

    public void clear();

    default public void put(HttpHost host, String pathPrefix, AuthScheme authScheme) {
        this.put(host, authScheme);
    }

    default public AuthScheme get(HttpHost host, String pathPrefix) {
        return this.get(host);
    }

    default public void remove(HttpHost host, String pathPrefix) {
        this.remove(host);
    }
}

