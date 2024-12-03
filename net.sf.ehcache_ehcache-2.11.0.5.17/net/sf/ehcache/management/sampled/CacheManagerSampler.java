/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management.sampled;

import java.util.Map;
import net.sf.ehcache.util.ManagementAttribute;

public interface CacheManagerSampler {
    @ManagementAttribute
    public String getName();

    @ManagementAttribute
    public String getClusterUUID();

    @ManagementAttribute
    public String getStatus();

    public void setEnabled(boolean var1);

    @ManagementAttribute
    public boolean isEnabled();

    public void shutdown();

    public void clearAll();

    @ManagementAttribute
    public String[] getCacheNames() throws IllegalStateException;

    public Map<String, long[]> getCacheMetrics();

    public long getCacheHitRate();

    public long getCacheInMemoryHitRate();

    public long getCacheOffHeapHitRate();

    public long getCacheOnDiskHitRate();

    public long getCacheMissRate();

    public long getCacheInMemoryMissRate();

    public long getCacheOffHeapMissRate();

    public long getCacheOnDiskMissRate();

    public long getCachePutRate();

    public long getCacheUpdateRate();

    public long getCacheRemoveRate();

    public long getCacheEvictionRate();

    public long getCacheExpirationRate();

    public float getCacheAverageGetTime();

    @ManagementAttribute
    public boolean getSearchable();

    public long getCacheSearchRate();

    public long getCacheAverageSearchTime();

    public String generateActiveConfigDeclaration();

    public String generateActiveConfigDeclaration(String var1);

    @ManagementAttribute
    public boolean getTransactional();

    public long getTransactionCommittedCount();

    public long getTransactionCommitRate();

    public long getTransactionRolledBackCount();

    public long getTransactionRollbackRate();

    public long getTransactionTimedOutCount();

    @ManagementAttribute
    public boolean getHasWriteBehindWriter();

    public long getWriterQueueLength();

    @ManagementAttribute
    public int getWriterMaxQueueSize();

    @ManagementAttribute
    public long getMaxBytesLocalDisk();

    public void setMaxBytesLocalDisk(long var1);

    public void setMaxBytesLocalDiskAsString(String var1);

    @ManagementAttribute
    public String getMaxBytesLocalDiskAsString();

    @ManagementAttribute
    public long getMaxBytesLocalHeap();

    @ManagementAttribute
    public String getMaxBytesLocalHeapAsString();

    public void setMaxBytesLocalHeap(long var1);

    public void setMaxBytesLocalHeapAsString(String var1);

    @ManagementAttribute
    public long getMaxBytesLocalOffHeap();

    @ManagementAttribute
    public String getMaxBytesLocalOffHeapAsString();

    public Object[][] executeQuery(String var1);
}

