/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;

public abstract class AbstractCacheConfigurationListener
implements CacheConfigurationListener {
    @Override
    public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
    }

    @Override
    public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
    }

    @Override
    public void diskCapacityChanged(int oldCapacity, int newCapacity) {
    }

    @Override
    public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
    }

    @Override
    public void loggingChanged(boolean oldValue, boolean newValue) {
    }

    @Override
    public void registered(CacheConfiguration config) {
    }

    @Override
    public void deregistered(CacheConfiguration config) {
    }

    @Override
    public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
    }

    @Override
    public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
    }

    @Override
    public void maxEntriesInCacheChanged(long oldCapacity, long newCapacity) {
    }
}

