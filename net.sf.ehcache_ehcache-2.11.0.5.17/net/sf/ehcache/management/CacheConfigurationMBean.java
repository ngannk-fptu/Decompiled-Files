/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

public interface CacheConfigurationMBean {
    public String getName();

    public boolean isLoggingEnabled();

    public void setLoggingEnabled(boolean var1);

    @Deprecated
    public int getMaxElementsInMemory();

    @Deprecated
    public void setMaxElementsInMemory(int var1);

    @Deprecated
    public int getMaxElementsOnDisk();

    @Deprecated
    public void setMaxElementsOnDisk(int var1);

    public long getMaxEntriesLocalDisk();

    public long getMaxEntriesLocalHeap();

    public void setMaxEntriesLocalDisk(long var1);

    public void setMaxEntriesLocalHeap(long var1);

    public long getMaxBytesLocalDisk();

    public long getMaxBytesLocalHeap();

    public long getMaxBytesLocalOffHeap();

    public String getMemoryStoreEvictionPolicy();

    public void setMemoryStoreEvictionPolicy(String var1);

    public boolean isEternal();

    public void setEternal(boolean var1);

    public long getTimeToIdleSeconds();

    public void setTimeToIdleSeconds(long var1);

    public long getTimeToLiveSeconds();

    public void setTimeToLiveSeconds(long var1);

    public boolean isOverflowToDisk();

    public void setOverflowToDisk(boolean var1);

    public boolean isDiskPersistent();

    public void setDiskPersistent(boolean var1);

    public long getDiskExpiryThreadIntervalSeconds();

    public void setDiskExpiryThreadIntervalSeconds(long var1);

    public int getDiskSpoolBufferSizeMB();

    public void setDiskSpoolBufferSizeMB(int var1);

    public boolean isTerracottaClustered();

    public String getTerracottaConsistency();

    public boolean isOverflowToOffHeap();

    @Deprecated
    public long getMaxMemoryOffHeapInBytes();
}

