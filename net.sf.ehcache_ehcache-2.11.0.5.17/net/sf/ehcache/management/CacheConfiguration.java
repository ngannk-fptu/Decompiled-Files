/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.io.Serializable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import net.sf.ehcache.management.CacheConfigurationMBean;

public class CacheConfiguration
implements CacheConfigurationMBean,
Serializable {
    private static final long serialVersionUID = -8944774509593267228L;
    private final transient net.sf.ehcache.config.CacheConfiguration cacheConfiguration;
    private final ObjectName objectName;

    public CacheConfiguration(Ehcache cache) {
        this.cacheConfiguration = cache.getCacheConfiguration();
        this.objectName = CacheConfiguration.createObjectName(cache.getCacheManager().toString(), cache.getName());
    }

    static ObjectName createObjectName(String cacheManagerName, String cacheName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheConfiguration,CacheManager=" + cacheManagerName + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName));
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    @Override
    public String getName() {
        return this.cacheConfiguration.getName();
    }

    @Override
    public boolean isLoggingEnabled() {
        return this.cacheConfiguration.getLogging();
    }

    @Override
    public void setLoggingEnabled(boolean enable) {
        this.cacheConfiguration.setLogging(enable);
    }

    @Override
    @Deprecated
    public int getMaxElementsInMemory() {
        return this.cacheConfiguration.getMaxElementsInMemory();
    }

    @Override
    @Deprecated
    public void setMaxElementsInMemory(int maxElements) {
        this.cacheConfiguration.setMaxElementsInMemory(maxElements);
    }

    @Override
    @Deprecated
    public int getMaxElementsOnDisk() {
        return this.cacheConfiguration.getMaxElementsOnDisk();
    }

    @Override
    @Deprecated
    public void setMaxElementsOnDisk(int maxElements) {
        this.cacheConfiguration.setMaxElementsOnDisk(maxElements);
    }

    @Override
    public String getMemoryStoreEvictionPolicy() {
        return this.cacheConfiguration.getMemoryStoreEvictionPolicy().toString();
    }

    @Override
    public void setMemoryStoreEvictionPolicy(String memoryStoreEvictionPolicy) {
        this.cacheConfiguration.setMemoryStoreEvictionPolicy(memoryStoreEvictionPolicy);
    }

    @Override
    public boolean isEternal() {
        return this.cacheConfiguration.isEternal();
    }

    @Override
    public void setEternal(boolean eternal) {
        this.cacheConfiguration.setEternal(eternal);
    }

    @Override
    public long getTimeToIdleSeconds() {
        return this.cacheConfiguration.getTimeToIdleSeconds();
    }

    @Override
    public void setTimeToIdleSeconds(long tti) {
        this.cacheConfiguration.setTimeToIdleSeconds(tti);
    }

    @Override
    public long getTimeToLiveSeconds() {
        return this.cacheConfiguration.getTimeToLiveSeconds();
    }

    @Override
    public void setTimeToLiveSeconds(long ttl) {
        this.cacheConfiguration.setTimeToLiveSeconds(ttl);
    }

    @Override
    public boolean isOverflowToDisk() {
        return this.cacheConfiguration.isOverflowToDisk();
    }

    @Override
    public void setOverflowToDisk(boolean overflowToDisk) {
        this.cacheConfiguration.setOverflowToDisk(overflowToDisk);
    }

    @Override
    public boolean isDiskPersistent() {
        return this.cacheConfiguration.isDiskPersistent();
    }

    @Override
    public void setDiskPersistent(boolean diskPersistent) {
        this.cacheConfiguration.setDiskPersistent(diskPersistent);
    }

    @Override
    public int getDiskSpoolBufferSizeMB() {
        return this.cacheConfiguration.getDiskSpoolBufferSizeMB();
    }

    @Override
    public void setDiskSpoolBufferSizeMB(int diskSpoolBufferSizeMB) {
        this.cacheConfiguration.setDiskSpoolBufferSizeMB(diskSpoolBufferSizeMB);
    }

    @Override
    public long getDiskExpiryThreadIntervalSeconds() {
        return this.cacheConfiguration.getDiskExpiryThreadIntervalSeconds();
    }

    @Override
    public final void setDiskExpiryThreadIntervalSeconds(long diskExpiryThreadIntervalSeconds) {
        this.cacheConfiguration.setDiskExpiryThreadIntervalSeconds(diskExpiryThreadIntervalSeconds);
    }

    @Override
    public boolean isTerracottaClustered() {
        return this.cacheConfiguration.isTerracottaClustered();
    }

    @Override
    public String getTerracottaConsistency() {
        TerracottaConfiguration.Consistency consistency = this.cacheConfiguration.getTerracottaConsistency();
        return consistency != null ? consistency.name() : "na";
    }

    ObjectName getObjectName() {
        return this.objectName;
    }

    @Override
    public boolean isOverflowToOffHeap() {
        return this.cacheConfiguration.isOverflowToOffHeap();
    }

    @Override
    @Deprecated
    public long getMaxMemoryOffHeapInBytes() {
        return this.cacheConfiguration.getMaxMemoryOffHeapInBytes();
    }

    @Override
    public long getMaxEntriesLocalDisk() {
        return this.cacheConfiguration.getMaxEntriesLocalDisk();
    }

    @Override
    public long getMaxEntriesLocalHeap() {
        return this.cacheConfiguration.getMaxEntriesLocalHeap();
    }

    @Override
    public void setMaxEntriesLocalDisk(long maxEntries) {
        this.cacheConfiguration.setMaxEntriesLocalDisk(maxEntries);
    }

    @Override
    public void setMaxEntriesLocalHeap(long maxEntries) {
        this.cacheConfiguration.setMaxEntriesLocalHeap(maxEntries);
    }

    @Override
    public long getMaxBytesLocalDisk() {
        return this.cacheConfiguration.getMaxBytesLocalDisk();
    }

    @Override
    public long getMaxBytesLocalHeap() {
        return this.cacheConfiguration.getMaxBytesLocalHeap();
    }

    @Override
    public long getMaxBytesLocalOffHeap() {
        return this.cacheConfiguration.getMaxBytesLocalOffHeap();
    }
}

