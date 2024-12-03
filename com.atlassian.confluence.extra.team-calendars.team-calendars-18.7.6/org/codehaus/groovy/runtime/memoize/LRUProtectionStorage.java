/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.memoize;

import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.groovy.runtime.memoize.ProtectionStorage;

final class LRUProtectionStorage
extends LinkedHashMap<Object, Object>
implements ProtectionStorage {
    private static final long serialVersionUID = 1L;
    private final int maxSize;

    public LRUProtectionStorage(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Object, Object> eldest) {
        return this.size() > this.maxSize;
    }

    @Override
    public synchronized void touch(Object key, Object value) {
        this.remove(key);
        this.put(key, value);
    }

    @Override
    public synchronized Object get(Object key) {
        Object value = this.remove(key);
        if (value != null) {
            this.put(key, value);
        }
        return value;
    }

    @Override
    public Object clone() {
        return super.clone();
    }
}

