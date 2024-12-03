/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface HttpCacheInvalidator {
    public void flushInvalidatedCacheEntries(HttpHost var1, HttpRequest var2);

    public void flushInvalidatedCacheEntries(HttpHost var1, HttpRequest var2, HttpResponse var3);
}

