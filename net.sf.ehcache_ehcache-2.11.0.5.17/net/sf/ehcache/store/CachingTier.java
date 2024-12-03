/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import java.util.concurrent.Callable;
import net.sf.ehcache.store.Policy;

public interface CachingTier<K, V> {
    public boolean loadOnPut();

    public V get(K var1, Callable<V> var2, boolean var3);

    public V remove(K var1);

    public void clear();

    public void clearAndNotify();

    public void addListener(Listener<K, V> var1);

    @Deprecated
    public int getInMemorySize();

    @Deprecated
    public int getOffHeapSize();

    @Deprecated
    public boolean contains(K var1);

    @Deprecated
    public long getInMemorySizeInBytes();

    @Deprecated
    public long getOffHeapSizeInBytes();

    @Deprecated
    public long getOnDiskSizeInBytes();

    @Deprecated
    public void recalculateSize(K var1);

    @Deprecated
    public Policy getEvictionPolicy();

    @Deprecated
    public void setEvictionPolicy(Policy var1);

    public static interface Listener<K, V> {
        public void evicted(K var1, V var2);
    }
}

