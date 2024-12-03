/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.Supplier
 */
package com.atlassian.confluence.impl.cache.tx;

import com.atlassian.cache.Cache;
import com.atlassian.cache.Supplier;
import java.util.Collection;

public interface TransactionAwareCache<K, V> {
    public Collection<K> getKeys();

    public V get(K var1);

    public V get(K var1, Supplier<? extends V> var2);

    public void put(K var1, V var2);

    public void remove(K var1);

    public void removeAll();

    public static <K, V> TransactionAwareCache<K, V> from(final Cache<K, V> delegate) {
        return new TransactionAwareCache<K, V>(){

            @Override
            public Collection<K> getKeys() {
                return delegate.getKeys();
            }

            @Override
            public V get(K key) {
                return delegate.get(key);
            }

            @Override
            public V get(K key, Supplier<? extends V> supplier) {
                return delegate.get(key, supplier);
            }

            @Override
            public void put(K key, V value) {
                delegate.put(key, value);
            }

            @Override
            public void remove(K key) {
                delegate.remove(key);
            }

            @Override
            public void removeAll() {
                delegate.removeAll();
            }
        };
    }
}

