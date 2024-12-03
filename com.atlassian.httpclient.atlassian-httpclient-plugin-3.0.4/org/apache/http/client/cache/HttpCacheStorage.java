/*
 * Decompiled with CFR 0.152.
 */
package org.apache.http.client.cache;

import java.io.IOException;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.client.cache.HttpCacheUpdateException;

public interface HttpCacheStorage {
    public void putEntry(String var1, HttpCacheEntry var2) throws IOException;

    public HttpCacheEntry getEntry(String var1) throws IOException;

    public void removeEntry(String var1) throws IOException;

    public void updateEntry(String var1, HttpCacheUpdateCallback var2) throws IOException, HttpCacheUpdateException;
}

