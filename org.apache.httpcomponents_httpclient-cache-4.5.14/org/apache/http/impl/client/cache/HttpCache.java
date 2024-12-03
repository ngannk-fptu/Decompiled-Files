/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpHost
 *  org.apache.http.HttpRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.methods.CloseableHttpResponse
 */
package org.apache.http.impl.client.cache;

import java.io.IOException;
import java.util.Date;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.cache.Variant;

interface HttpCache {
    public void flushCacheEntriesFor(HttpHost var1, HttpRequest var2) throws IOException;

    public void flushInvalidatedCacheEntriesFor(HttpHost var1, HttpRequest var2) throws IOException;

    public void flushInvalidatedCacheEntriesFor(HttpHost var1, HttpRequest var2, HttpResponse var3);

    public HttpCacheEntry getCacheEntry(HttpHost var1, HttpRequest var2) throws IOException;

    public Map<String, Variant> getVariantCacheEntriesWithEtags(HttpHost var1, HttpRequest var2) throws IOException;

    public HttpResponse cacheAndReturnResponse(HttpHost var1, HttpRequest var2, HttpResponse var3, Date var4, Date var5) throws IOException;

    public CloseableHttpResponse cacheAndReturnResponse(HttpHost var1, HttpRequest var2, CloseableHttpResponse var3, Date var4, Date var5) throws IOException;

    public HttpCacheEntry updateCacheEntry(HttpHost var1, HttpRequest var2, HttpCacheEntry var3, HttpResponse var4, Date var5, Date var6) throws IOException;

    public HttpCacheEntry updateVariantCacheEntry(HttpHost var1, HttpRequest var2, HttpCacheEntry var3, HttpResponse var4, Date var5, Date var6, String var7) throws IOException;

    public void reuseVariantEntryFor(HttpHost var1, HttpRequest var2, Variant var3) throws IOException;
}

