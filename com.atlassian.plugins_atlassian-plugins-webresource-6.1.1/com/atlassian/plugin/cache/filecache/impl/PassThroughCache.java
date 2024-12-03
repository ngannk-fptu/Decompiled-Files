/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.cache.filecache.impl;

import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.FileCacheImpl;
import java.io.OutputStream;

public class PassThroughCache
implements Cache {
    private static final boolean CACHE_WAS_HIT = false;

    @Override
    public boolean cache(String bucket, String key, OutputStream out, Cache.StreamProvider provider) {
        provider.write(out);
        return false;
    }

    @Override
    public boolean cacheTwo(String bucket, String key, OutputStream out1, OutputStream out2, Cache.TwoStreamProvider provider) {
        provider.write(FileCacheImpl.ensureNotNull(out1), FileCacheImpl.ensureNotNull(out2));
        return false;
    }

    @Override
    public void clear() {
    }
}

