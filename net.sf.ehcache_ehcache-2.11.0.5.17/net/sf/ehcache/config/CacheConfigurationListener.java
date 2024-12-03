/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.config;

import net.sf.ehcache.config.CacheConfiguration;

public interface CacheConfigurationListener {
    public void timeToIdleChanged(long var1, long var3);

    public void timeToLiveChanged(long var1, long var3);

    public void diskCapacityChanged(int var1, int var2);

    public void memoryCapacityChanged(int var1, int var2);

    public void loggingChanged(boolean var1, boolean var2);

    public void registered(CacheConfiguration var1);

    public void deregistered(CacheConfiguration var1);

    public void maxBytesLocalHeapChanged(long var1, long var3);

    public void maxBytesLocalDiskChanged(long var1, long var3);

    public void maxEntriesInCacheChanged(long var1, long var3);
}

