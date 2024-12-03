/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 */
package org.apache.http.client.cache;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;

public interface HttpCacheInvalidator {
    public void flushInvalidatedCacheEntries(HttpHost var1, HttpRequest var2);

    public void flushInvalidatedCacheEntries(HttpHost var1, HttpRequest var2, HttpResponse var3);
}

