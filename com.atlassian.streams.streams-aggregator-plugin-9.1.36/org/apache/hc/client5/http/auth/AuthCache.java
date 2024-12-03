/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.client5.http.auth;

import org.apache.hc.client5.http.auth.AuthScheme;
import org.apache.hc.core5.http.HttpHost;

public interface AuthCache {
    public void put(HttpHost var1, AuthScheme var2);

    public AuthScheme get(HttpHost var1);

    public void remove(HttpHost var1);

    public void clear();
}

