/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.memoize;

import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.groovy.runtime.memoize.LRUProtectionStorage;
import org.codehaus.groovy.runtime.memoize.MemoizeCache;

public final class LRUCache
implements MemoizeCache<Object, Object> {
    private final Map<Object, Object> cache;

    public LRUCache(int maxCacheSize) {
        this.cache = Collections.synchronizedMap(new LRUProtectionStorage(maxCacheSize));
    }

    @Override
    public Object put(Object key, Object value) {
        return this.cache.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return this.cache.get(key);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void cleanUpNullReferences() {
        Map<Object, Object> map = this.cache;
        synchronized (map) {
            Iterator<Map.Entry<Object, Object>> iterator = this.cache.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Object, Object> entry = iterator.next();
                if (((SoftReference)entry.getValue()).get() != null) continue;
                iterator.remove();
            }
        }
    }
}

