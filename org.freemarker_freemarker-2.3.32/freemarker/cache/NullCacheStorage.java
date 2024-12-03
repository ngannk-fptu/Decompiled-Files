/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorageWithGetSize;
import freemarker.cache.ConcurrentCacheStorage;

public class NullCacheStorage
implements ConcurrentCacheStorage,
CacheStorageWithGetSize {
    public static final NullCacheStorage INSTANCE = new NullCacheStorage();

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    public Object get(Object key) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {
    }

    @Override
    public void remove(Object key) {
    }

    @Override
    public void clear() {
    }

    @Override
    public int getSize() {
        return 0;
    }
}

