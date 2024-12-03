/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import org.apache.http.client.cache.CacheResponseStatus;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

public class HttpCacheContext
extends HttpClientContext {
    public static final String CACHE_RESPONSE_STATUS = "http.cache.response.status";

    public static HttpCacheContext adapt(HttpContext context) {
        if (context instanceof HttpCacheContext) {
            return (HttpCacheContext)context;
        }
        return new HttpCacheContext(context);
    }

    public static HttpCacheContext create() {
        return new HttpCacheContext(new BasicHttpContext());
    }

    public HttpCacheContext(HttpContext context) {
        super(context);
    }

    public HttpCacheContext() {
    }

    public CacheResponseStatus getCacheResponseStatus() {
        return this.getAttribute(CACHE_RESPONSE_STATUS, CacheResponseStatus.class);
    }
}

