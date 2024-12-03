/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.CacheStorageWithGetSize;
import freemarker.cache.ConcurrentCacheStorage;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StrongCacheStorage
implements ConcurrentCacheStorage,
CacheStorageWithGetSize {
    private final Map map = new ConcurrentHashMap();

    @Override
    public boolean isConcurrent() {
        return true;
    }

    @Override
    public Object get(Object key) {
        return this.map.get(key);
    }

    @Override
    public void put(Object key, Object value) {
        this.map.put(key, value);
    }

    @Override
    public void remove(Object key) {
        this.map.remove(key);
    }

    @Override
    public int getSize() {
        return this.map.size();
    }

    @Override
    public void clear() {
        this.map.clear();
    }
}

