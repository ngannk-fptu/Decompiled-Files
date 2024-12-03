/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store.cachingtier;

import java.util.Map;
import java.util.Set;
import net.sf.ehcache.store.Policy;

public interface HeapCacheBackEnd<K, V> {
    public boolean hasSpace();

    public V get(K var1);

    public V putIfAbsent(K var1, V var2);

    public boolean remove(K var1, V var2);

    public boolean replace(K var1, V var2, V var3);

    public V remove(K var1);

    public void clear(boolean var1);

    @Deprecated
    public int size();

    @Deprecated
    public Set<Map.Entry<K, V>> entrySet();

    public void registerEvictionCallback(EvictionCallback<K, V> var1);

    @Deprecated
    public void recalculateSize(K var1);

    @Deprecated
    public Policy getPolicy();

    @Deprecated
    public void setPolicy(Policy var1);

    public static interface EvictionCallback<K, V> {
        public void evicted(K var1, V var2);
    }
}

