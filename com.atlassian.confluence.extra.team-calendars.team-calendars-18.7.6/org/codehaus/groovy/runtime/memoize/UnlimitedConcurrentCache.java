/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.memoize;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.codehaus.groovy.runtime.memoize.MemoizeCache;

public final class UnlimitedConcurrentCache
implements MemoizeCache<Object, Object> {
    private final ConcurrentHashMap<Object, Object> cache = new ConcurrentHashMap();

    @Override
    public Object put(Object key, Object value) {
        return this.cache.put(key, value);
    }

    @Override
    public Object get(Object key) {
        return this.cache.get(key);
    }

    @Override
    public void cleanUpNullReferences() {
        for (Map.Entry<Object, Object> entry : this.cache.entrySet()) {
            Object entryVal = entry.getValue();
            if (entryVal == null || ((SoftReference)entryVal).get() != null) continue;
            this.cache.remove(entry.getKey(), entryVal);
        }
    }
}

