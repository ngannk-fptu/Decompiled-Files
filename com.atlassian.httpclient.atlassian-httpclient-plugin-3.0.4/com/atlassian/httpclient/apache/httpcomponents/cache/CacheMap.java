/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.httpclient.apache.httpcomponents.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.http.client.cache.HttpCacheEntry;

final class CacheMap
extends LinkedHashMap<String, HttpCacheEntry> {
    private static final long serialVersionUID = -7750025207539768511L;
    private final int maxEntries;

    CacheMap(int maxEntries) {
        super(20, 0.75f, true);
        this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, HttpCacheEntry> eldest) {
        return this.size() > this.maxEntries;
    }
}

