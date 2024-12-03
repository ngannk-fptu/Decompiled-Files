/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.store;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;

public abstract class MemoryLimitedCacheLoader
implements BootstrapCacheLoader,
Cloneable {
    protected boolean isInMemoryLimitReached(Ehcache cache, int loadedElements) {
        boolean overflowToOffHeap = cache.getCacheConfiguration().isOverflowToOffHeap();
        long maxElementsInMem = cache.getCacheConfiguration().getMaxEntriesLocalHeap() == 0L ? Integer.MAX_VALUE : cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        long maxBytesInMem = overflowToOffHeap ? cache.getCacheConfiguration().getMaxBytesLocalOffHeap() : cache.getCacheConfiguration().getMaxBytesLocalHeap();
        if (maxBytesInMem != 0L) {
            long inMemoryCount;
            long l = inMemoryCount = overflowToOffHeap ? cache.getStatistics().getLocalOffHeapSize() : cache.getStatistics().getLocalHeapSize();
            if (inMemoryCount == 0L) {
                return false;
            }
            long inMemorySizeInBytes = overflowToOffHeap ? cache.getStatistics().getLocalOffHeapSizeInBytes() : cache.getStatistics().getLocalHeapSizeInBytes();
            long avgSize = inMemorySizeInBytes / inMemoryCount;
            return inMemorySizeInBytes + avgSize * 2L >= maxBytesInMem;
        }
        return (long)loadedElements >= maxElementsInMem;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

