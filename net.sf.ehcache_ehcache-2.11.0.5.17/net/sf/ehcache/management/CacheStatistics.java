/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.management;

import java.io.Serializable;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import net.sf.ehcache.management.CacheStatisticsMBean;
import net.sf.ehcache.statistics.StatisticsGateway;

public class CacheStatistics
implements CacheStatisticsMBean,
Serializable {
    private static final long serialVersionUID = 8085302752781762030L;
    private transient Ehcache ehcache;
    private StatisticsGateway statistics;
    private final ObjectName objectName;

    public CacheStatistics(Ehcache ehcache) {
        this.ehcache = ehcache;
        this.statistics = ehcache.getStatistics();
        this.objectName = CacheStatistics.createObjectName(ehcache.getCacheManager().getName(), ehcache.getName());
    }

    static ObjectName createObjectName(String cacheManagerName, String cacheName) {
        ObjectName objectName;
        try {
            objectName = new ObjectName("net.sf.ehcache:type=CacheStatistics,CacheManager=" + cacheManagerName + ",name=" + EhcacheHibernateMbeanNames.mbeanSafe(cacheName));
        }
        catch (MalformedObjectNameException e) {
            throw new CacheException(e);
        }
        return objectName;
    }

    @Override
    public String getAssociatedCacheName() {
        if (this.statistics == null) {
            return null;
        }
        return this.statistics.getAssociatedCacheName();
    }

    @Override
    public long getInMemoryHits() {
        return this.statistics.localHeapHitCount();
    }

    @Override
    public long getOffHeapHits() {
        return this.statistics.localOffHeapHitCount();
    }

    @Override
    public long getOnDiskHits() {
        return this.statistics.localDiskHitCount();
    }

    @Override
    public long getCacheMisses() {
        return this.statistics.cacheMissCount();
    }

    @Override
    public long getInMemoryMisses() {
        return this.statistics.localHeapMissCount();
    }

    @Override
    public long getOffHeapMisses() {
        return this.statistics.localOffHeapMissCount();
    }

    @Override
    public long getOnDiskMisses() {
        return this.statistics.localDiskMissCount();
    }

    @Override
    public long getObjectCount() {
        return this.statistics.getSize();
    }

    @Override
    public long getWriterQueueLength() {
        return this.statistics.getWriterQueueLength();
    }

    @Override
    public int getWriterMaxQueueSize() {
        return this.ehcache.getCacheConfiguration().getCacheWriterConfiguration().getWriteBehindMaxQueueSize();
    }

    @Override
    public long getMemoryStoreObjectCount() {
        return this.statistics.getLocalHeapSize();
    }

    @Override
    public long getOffHeapStoreObjectCount() {
        return this.statistics.getLocalOffHeapSize();
    }

    @Override
    public long getDiskStoreObjectCount() {
        return this.statistics.getLocalDiskSize();
    }

    ObjectName getObjectName() {
        return this.objectName;
    }

    public Ehcache getEhcache() {
        return this.ehcache;
    }

    private static double getPercentage(long number, long total) {
        if (total == 0L) {
            return 0.0;
        }
        return (double)number / (double)total;
    }

    @Override
    public double getCacheHitPercentage() {
        long hits = this.statistics.cacheHitCount();
        long misses = this.statistics.cacheMissCount();
        long total = hits + misses;
        return CacheStatistics.getPercentage(hits, total);
    }

    @Override
    public double getCacheMissPercentage() {
        long hits = this.statistics.cacheHitCount();
        long misses = this.statistics.cacheMissCount();
        long total = hits + misses;
        return CacheStatistics.getPercentage(misses, total);
    }

    @Override
    public double getInMemoryHitPercentage() {
        long hits = this.statistics.localHeapHitCount();
        long misses = this.statistics.localHeapMissCount();
        long total = hits + misses;
        return CacheStatistics.getPercentage(hits, total);
    }

    @Override
    public double getOffHeapHitPercentage() {
        long hits = this.statistics.localOffHeapHitCount();
        long misses = this.statistics.localOffHeapMissCount();
        long total = hits + misses;
        return CacheStatistics.getPercentage(hits, total);
    }

    @Override
    public double getOnDiskHitPercentage() {
        long hits = this.statistics.localDiskHitCount();
        long misses = this.statistics.localDiskMissCount();
        long total = hits + misses;
        return CacheStatistics.getPercentage(hits, total);
    }

    @Override
    public long getCacheHits() {
        return this.statistics.cacheHitCount();
    }
}

