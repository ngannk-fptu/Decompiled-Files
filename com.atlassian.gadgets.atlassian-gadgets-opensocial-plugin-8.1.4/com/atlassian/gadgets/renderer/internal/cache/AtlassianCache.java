/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  org.apache.shindig.common.cache.Cache
 */
package com.atlassian.gadgets.renderer.internal.cache;

import com.atlassian.cache.Cache;
import java.util.Objects;

public class AtlassianCache<K, V>
implements org.apache.shindig.common.cache.Cache<K, V> {
    private final Cache<K, V> delegate;

    AtlassianCache(Cache<K, V> delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    public V getElement(K key) {
        return (V)this.delegate.get(key);
    }

    public void addElement(K key, V value) {
        this.delegate.put(key, value);
    }

    public V removeElement(K key) {
        Object existingValue = this.delegate.get(key);
        this.delegate.remove(key);
        return (V)existingValue;
    }

    public long getCapacity() {
        return -1L;
    }

    public long getSize() {
        return this.delegate.getKeys().size();
    }
}

