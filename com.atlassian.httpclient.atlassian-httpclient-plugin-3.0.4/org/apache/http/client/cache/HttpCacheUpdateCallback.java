/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;
import org.apache.http.client.cache.HttpCacheEntry;

public interface HttpCacheUpdateCallback {
    public HttpCacheEntry update(HttpCacheEntry var1) throws IOException;
}

