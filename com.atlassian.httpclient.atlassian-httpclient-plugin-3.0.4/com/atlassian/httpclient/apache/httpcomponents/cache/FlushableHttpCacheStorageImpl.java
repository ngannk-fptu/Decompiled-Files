/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import com.atlassian.httpclient.apache.httpcomponents.cache.CacheMap;
import com.atlassian.httpclient.apache.httpcomponents.cache.FlushableHttpCacheStorage;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.http.client.cache.HttpCacheEntry;
import org.apache.http.client.cache.HttpCacheUpdateCallback;
import org.apache.http.impl.client.cache.CacheConfig;

public final class FlushableHttpCacheStorageImpl
implements FlushableHttpCacheStorage {
    private final CacheMap entries;

    public FlushableHttpCacheStorageImpl(CacheConfig config) {
        this.entries = new CacheMap(config.getMaxCacheEntries());
    }

    @Override
    public synchronized void flushByUriPattern(Pattern urlPattern) {
        Iterator i = this.entries.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = i.next();
            if (!urlPattern.matcher((CharSequence)entry.getKey()).matches()) continue;
            i.remove();
        }
    }

    @Override
    public synchronized void putEntry(String url, HttpCacheEntry entry) throws IOException {
        this.entries.put(url, entry);
    }

    @Override
    public synchronized HttpCacheEntry getEntry(String url) {
        return (HttpCacheEntry)this.entries.get(url);
    }

    @Override
    public synchronized void removeEntry(String url) throws IOException {
        this.entries.remove(url);
    }

    @Override
    public synchronized void updateEntry(String url, HttpCacheUpdateCallback callback) throws IOException {
        HttpCacheEntry existingEntry = (HttpCacheEntry)this.entries.get(url);
        this.entries.put(url, callback.update(existingEntry));
    }
}

