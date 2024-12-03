/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorage;

public interface ConcurrentCacheStorage
extends CacheStorage {
    public boolean isConcurrent();
}

