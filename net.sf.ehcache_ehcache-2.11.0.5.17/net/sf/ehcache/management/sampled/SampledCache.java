/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management.sampled;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.hibernate.management.impl.BaseEmitterBean;
import net.sf.ehcache.management.sampled.CacheSamplerImpl;
import net.sf.ehcache.management.sampled.SampledCacheMBean;
import net.sf.ehcache.management.sampled.Utils;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.SampledRateCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampledCache
extends BaseEmitterBean
implements SampledCacheMBean,
PropertyChangeListener {
    private static final Logger LOG = LoggerFactory.getLogger(SampledCache.class);
    private static final MBeanNotificationInfo[] NOTIFICATION_INFO;
    private final CacheSamplerImpl sampledCacheDelegate;
    private final String immutableCacheName;

    public SampledCache(Ehcache cache) throws NotCompliantMBeanException {
        super(SampledCacheMBean.class);
        this.immutableCacheName = cache.getName();
        cache.addPropertyChangeListener(this);
        this.sampledCacheDelegate = new CacheSamplerImpl(cache);
    }

    String getImmutableCacheName() {
        return this.immutableCacheName;
    }

    @Override
    public boolean isEnabled() {
        return this.sampledCacheDelegate.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.sampledCacheDelegate.setEnabled(enabled);
    }

    @Override
    public boolean isClusterBulkLoadEnabled() {
        return this.sampledCacheDelegate.isClusterBulkLoadEnabled();
    }

    @Override
    public boolean isNodeBulkLoadEnabled() {
        return this.sampledCacheDelegate.isNodeBulkLoadEnabled();
    }

    @Override
    public void setNodeBulkLoadEnabled(boolean bulkLoadEnabled) {
        this.sampledCacheDelegate.setNodeBulkLoadEnabled(bulkLoadEnabled);
    }

    @Override
    public void flush() {
        this.sampledCacheDelegate.flush();
        this.sendNotification("CacheFlushed", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public String getCacheName() {
        return this.sampledCacheDelegate.getCacheName();
    }

    @Override
    public String getStatus() {
        return this.sampledCacheDelegate.getStatus();
    }

    @Override
    public void removeAll() {
        this.sampledCacheDelegate.removeAll();
        this.sendNotification("CacheCleared", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public long getAverageGetTimeNanosMostRecentSample() {
        return this.sampledCacheDelegate.getAverageGetTimeNanosMostRecentSample();
    }

    @Override
    public long getCacheEvictionRate() {
        return this.sampledCacheDelegate.getCacheEvictionRate();
    }

    @Override
    public long getCacheElementEvictedMostRecentSample() {
        return this.sampledCacheDelegate.getCacheElementEvictedMostRecentSample();
    }

    @Override
    public long getCacheExpirationRate() {
        return this.sampledCacheDelegate.getCacheExpirationRate();
    }

    @Override
    public long getCacheElementExpiredMostRecentSample() {
        return this.sampledCacheDelegate.getCacheElementExpiredMostRecentSample();
    }

    @Override
    public long getCachePutRate() {
        return this.sampledCacheDelegate.getCachePutRate();
    }

    @Override
    public long getCacheElementPutMostRecentSample() {
        return this.sampledCacheDelegate.getCacheElementPutMostRecentSample();
    }

    @Override
    public long getCacheRemoveRate() {
        return this.sampledCacheDelegate.getCacheRemoveRate();
    }

    @Override
    public long getCacheElementRemovedMostRecentSample() {
        return this.sampledCacheDelegate.getCacheElementRemovedMostRecentSample();
    }

    @Override
    public long getCacheUpdateRate() {
        return this.getCacheElementUpdatedMostRecentSample();
    }

    @Override
    public long getCacheElementUpdatedMostRecentSample() {
        return this.sampledCacheDelegate.getCacheElementUpdatedMostRecentSample();
    }

    @Override
    public long getCacheInMemoryHitRate() {
        return this.getCacheHitInMemoryMostRecentSample();
    }

    @Override
    public long getCacheHitInMemoryMostRecentSample() {
        return this.sampledCacheDelegate.getCacheHitInMemoryMostRecentSample();
    }

    @Override
    public long getCacheOffHeapHitRate() {
        return this.sampledCacheDelegate.getCacheOffHeapHitRate();
    }

    @Override
    public long getCacheHitOffHeapMostRecentSample() {
        return this.sampledCacheDelegate.getCacheHitOffHeapMostRecentSample();
    }

    @Override
    public long getCacheHitRate() {
        return this.sampledCacheDelegate.getCacheHitRate();
    }

    @Override
    public long getCacheHitMostRecentSample() {
        return this.sampledCacheDelegate.getCacheHitMostRecentSample();
    }

    @Override
    public long getCacheOnDiskHitRate() {
        return this.sampledCacheDelegate.getCacheOnDiskHitRate();
    }

    @Override
    public long getCacheHitOnDiskMostRecentSample() {
        return this.sampledCacheDelegate.getCacheHitOnDiskMostRecentSample();
    }

    @Override
    public long getCacheMissExpiredMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissExpiredMostRecentSample();
    }

    @Override
    public long getCacheMissRate() {
        return this.sampledCacheDelegate.getCacheMissRate();
    }

    @Override
    public long getCacheMissMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissMostRecentSample();
    }

    @Override
    public long getCacheInMemoryMissRate() {
        return this.sampledCacheDelegate.getCacheInMemoryMissRate();
    }

    @Override
    public long getCacheMissInMemoryMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissInMemoryMostRecentSample();
    }

    @Override
    public long getCacheOffHeapMissRate() {
        return this.sampledCacheDelegate.getCacheOffHeapMissRate();
    }

    @Override
    public long getCacheMissOffHeapMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissOffHeapMostRecentSample();
    }

    @Override
    public long getCacheOnDiskMissRate() {
        return this.sampledCacheDelegate.getCacheOnDiskMissRate();
    }

    @Override
    public long getCacheMissOnDiskMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissOnDiskMostRecentSample();
    }

    @Override
    public long getCacheMissNotFoundMostRecentSample() {
        return this.sampledCacheDelegate.getCacheMissNotFoundMostRecentSample();
    }

    @Override
    public boolean isTerracottaClustered() {
        return this.sampledCacheDelegate.isTerracottaClustered();
    }

    @Override
    public String getTerracottaConsistency() {
        return this.sampledCacheDelegate.getTerracottaConsistency();
    }

    @Override
    @Deprecated
    public void setNodeCoherent(boolean coherent) {
        boolean isNodeCoherent = this.isNodeCoherent();
        if (coherent != isNodeCoherent) {
            if (!coherent && this.getTransactional()) {
                LOG.warn("a transactional cache cannot be incoherent");
                return;
            }
            try {
                this.sampledCacheDelegate.getCache().setNodeCoherent(coherent);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    @Deprecated
    public boolean isClusterCoherent() {
        try {
            return this.sampledCacheDelegate.getCache().isClusterCoherent();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    @Deprecated
    public boolean isNodeCoherent() {
        try {
            return this.sampledCacheDelegate.getCache().isNodeCoherent();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getAverageGetTimeNanos() {
        return this.sampledCacheDelegate.getAverageGetTimeNanos();
    }

    @Override
    public Long getMaxGetTimeNanos() {
        return this.sampledCacheDelegate.getMaxGetTimeNanos();
    }

    @Override
    public Long getMinGetTimeNanos() {
        return this.sampledCacheDelegate.getMinGetTimeNanos();
    }

    @Override
    public long getXaCommitCount() {
        return this.sampledCacheDelegate.getXaCommitCount();
    }

    @Override
    public long getXaRollbackCount() {
        return this.sampledCacheDelegate.getXaRollbackCount();
    }

    @Override
    public long getXaRecoveredCount() {
        return this.sampledCacheDelegate.getXaRecoveredCount();
    }

    @Override
    public boolean getHasWriteBehindWriter() {
        return this.sampledCacheDelegate.getHasWriteBehindWriter();
    }

    @Override
    public long getWriterQueueLength() {
        return this.sampledCacheDelegate.getWriterQueueLength();
    }

    @Override
    public int getWriterMaxQueueSize() {
        return this.sampledCacheDelegate.getWriterMaxQueueSize();
    }

    @Override
    public int getWriterConcurrency() {
        return this.sampledCacheDelegate.getWriterConcurrency();
    }

    @Override
    public long getCacheHitCount() {
        return this.sampledCacheDelegate.getCacheHitCount();
    }

    @Override
    public long getCacheMissCount() {
        return this.sampledCacheDelegate.getCacheMissCount();
    }

    @Override
    public long getInMemoryMissCount() {
        return this.sampledCacheDelegate.getInMemoryMissCount();
    }

    @Override
    public long getOffHeapMissCount() {
        return this.sampledCacheDelegate.getOffHeapMissCount();
    }

    @Override
    public long getOnDiskMissCount() {
        return this.sampledCacheDelegate.getOnDiskMissCount();
    }

    @Override
    public long getCacheMissCountExpired() {
        return this.sampledCacheDelegate.getCacheMissCountExpired();
    }

    @Override
    public long getDiskExpiryThreadIntervalSeconds() {
        return this.sampledCacheDelegate.getDiskExpiryThreadIntervalSeconds();
    }

    @Override
    public void setDiskExpiryThreadIntervalSeconds(long seconds) {
        this.sampledCacheDelegate.setDiskExpiryThreadIntervalSeconds(seconds);
    }

    @Override
    public long getMaxEntriesLocalHeap() {
        return this.sampledCacheDelegate.getMaxEntriesLocalHeap();
    }

    @Override
    public void setMaxEntriesLocalHeap(long maxEntries) {
        this.sampledCacheDelegate.setMaxEntriesLocalHeap(maxEntries);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public long getMaxBytesLocalHeap() {
        return this.sampledCacheDelegate.getMaxBytesLocalHeap();
    }

    @Override
    public void setMaxBytesLocalHeap(long maxBytes) {
        this.sampledCacheDelegate.setMaxBytesLocalHeap(maxBytes);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public void setMaxBytesLocalHeapAsString(String maxBytes) {
        this.sampledCacheDelegate.setMaxBytesLocalHeapAsString(maxBytes);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public String getMaxBytesLocalHeapAsString() {
        return this.sampledCacheDelegate.getMaxBytesLocalHeapAsString();
    }

    @Override
    public int getMaxElementsInMemory() {
        return this.sampledCacheDelegate.getCache().getCacheConfiguration().getMaxElementsInMemory();
    }

    @Override
    public void setMaxElementsInMemory(int maxElements) {
        if (this.getMaxElementsInMemory() != maxElements) {
            try {
                this.sampledCacheDelegate.getCache().getCacheConfiguration().setMaxElementsInMemory(maxElements);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getMaxEntriesLocalDisk() {
        return this.sampledCacheDelegate.getMaxEntriesLocalDisk();
    }

    @Override
    public long getMaxEntriesInCache() {
        return this.sampledCacheDelegate.getMaxEntriesInCache();
    }

    @Override
    public void setMaxEntriesLocalDisk(long maxEntries) {
        this.sampledCacheDelegate.setMaxEntriesLocalDisk(maxEntries);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public void setMaxBytesLocalDisk(long maxBytes) {
        this.sampledCacheDelegate.setMaxBytesLocalDisk(maxBytes);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public void setMaxBytesLocalDiskAsString(String maxBytes) {
        this.sampledCacheDelegate.setMaxBytesLocalDiskAsString(maxBytes);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public String getMaxBytesLocalDiskAsString() {
        return this.sampledCacheDelegate.getMaxBytesLocalDiskAsString();
    }

    @Override
    public int getMaxElementsOnDisk() {
        return this.sampledCacheDelegate.getMaxElementsOnDisk();
    }

    @Override
    public void setMaxElementsOnDisk(int maxElements) {
        this.sampledCacheDelegate.setMaxElementsOnDisk(maxElements);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public void setMaxEntriesInCache(long maxEntries) {
        this.sampledCacheDelegate.setMaxEntriesInCache(maxEntries);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public long getMaxBytesLocalDisk() {
        return this.sampledCacheDelegate.getMaxBytesLocalDisk();
    }

    @Override
    public long getMaxBytesLocalOffHeap() {
        return this.sampledCacheDelegate.getMaxBytesLocalOffHeap();
    }

    @Override
    public String getMaxBytesLocalOffHeapAsString() {
        return this.sampledCacheDelegate.getMaxBytesLocalOffHeapAsString();
    }

    @Override
    public String getMemoryStoreEvictionPolicy() {
        return this.sampledCacheDelegate.getMemoryStoreEvictionPolicy();
    }

    @Override
    public void setMemoryStoreEvictionPolicy(String evictionPolicy) {
        this.sampledCacheDelegate.setMemoryStoreEvictionPolicy(evictionPolicy);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public long getTimeToIdleSeconds() {
        return this.sampledCacheDelegate.getTimeToIdleSeconds();
    }

    @Override
    public void setTimeToIdleSeconds(long tti) {
        this.sampledCacheDelegate.setTimeToIdleSeconds(tti);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public long getTimeToLiveSeconds() {
        return this.sampledCacheDelegate.getTimeToLiveSeconds();
    }

    @Override
    public void setTimeToLiveSeconds(long ttl) {
        this.sampledCacheDelegate.setTimeToLiveSeconds(ttl);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public boolean isOverflowToOffHeap() {
        return this.sampledCacheDelegate.isOverflowToOffHeap();
    }

    @Override
    public boolean isDiskPersistent() {
        return this.sampledCacheDelegate.isDiskPersistent();
    }

    @Override
    public void setDiskPersistent(boolean diskPersistent) {
        this.sampledCacheDelegate.setDiskPersistent(diskPersistent);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public String getPersistenceStrategy() {
        return this.sampledCacheDelegate.getPersistenceStrategy();
    }

    @Override
    public boolean isEternal() {
        return this.sampledCacheDelegate.isEternal();
    }

    @Override
    public void setEternal(boolean eternal) {
        this.sampledCacheDelegate.setEternal(eternal);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public boolean isOverflowToDisk() {
        return this.sampledCacheDelegate.isOverflowToDisk();
    }

    @Override
    public void setOverflowToDisk(boolean overflowToDisk) {
        this.sampledCacheDelegate.setOverflowToDisk(overflowToDisk);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public boolean isLoggingEnabled() {
        return this.sampledCacheDelegate.isLoggingEnabled();
    }

    @Override
    public void setLoggingEnabled(boolean enabled) {
        this.sampledCacheDelegate.setLoggingEnabled(enabled);
        this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
    }

    @Override
    public boolean isPinned() {
        return this.sampledCacheDelegate.isPinned();
    }

    @Override
    public String getPinnedToStore() {
        return this.sampledCacheDelegate.getPinnedToStore();
    }

    @Override
    public long getEvictedCount() {
        return this.sampledCacheDelegate.getEvictedCount();
    }

    @Override
    public long getExpiredCount() {
        return this.sampledCacheDelegate.getExpiredCount();
    }

    @Override
    public long getInMemoryHitCount() {
        return this.sampledCacheDelegate.getInMemoryHitCount();
    }

    @Override
    @Deprecated
    public long getInMemorySize() {
        return this.getLocalHeapSize();
    }

    @Override
    public long getOffHeapHitCount() {
        return this.sampledCacheDelegate.getOffHeapHitCount();
    }

    @Override
    @Deprecated
    public long getOffHeapSize() {
        return this.sampledCacheDelegate.getOffHeapSize();
    }

    @Override
    public long getOnDiskHitCount() {
        return this.sampledCacheDelegate.getOnDiskHitCount();
    }

    @Override
    @Deprecated
    public long getOnDiskSize() {
        return this.sampledCacheDelegate.getOnDiskSize();
    }

    @Override
    public long getLocalDiskSize() {
        return this.sampledCacheDelegate.getLocalDiskSize();
    }

    @Override
    public long getLocalHeapSize() {
        return this.sampledCacheDelegate.getLocalHeapSize();
    }

    @Override
    public long getLocalOffHeapSize() {
        return this.sampledCacheDelegate.getLocalOffHeapSize();
    }

    @Override
    public long getLocalDiskSizeInBytes() {
        return this.sampledCacheDelegate.getLocalDiskSizeInBytes();
    }

    @Override
    public long getLocalHeapSizeInBytes() {
        return this.sampledCacheDelegate.getLocalHeapSizeInBytes();
    }

    @Override
    public long getLocalOffHeapSizeInBytes() {
        return this.sampledCacheDelegate.getLocalOffHeapSizeInBytes();
    }

    @Override
    public long getPutCount() {
        return this.sampledCacheDelegate.getPutCount();
    }

    @Override
    public long getRemovedCount() {
        return this.sampledCacheDelegate.getRemovedCount();
    }

    @Override
    public long getSize() {
        return this.sampledCacheDelegate.getSize();
    }

    @Override
    public long getUpdateCount() {
        return this.sampledCacheDelegate.getUpdateCount();
    }

    @Override
    public long getReplaceOneArgSuccessCount() {
        return this.sampledCacheDelegate.getReplaceOneArgSuccessCount();
    }

    @Override
    public long getReplaceOneArgSuccessRate() {
        return this.sampledCacheDelegate.getReplaceOneArgSuccessRate();
    }

    @Override
    public long getReplaceOneArgMissCount() {
        return this.sampledCacheDelegate.getReplaceOneArgMissCount();
    }

    @Override
    public long getReplaceOneArgMissRate() {
        return this.sampledCacheDelegate.getReplaceOneArgMissRate();
    }

    @Override
    public long getReplaceTwoArgSuccessCount() {
        return this.sampledCacheDelegate.getReplaceTwoArgSuccessCount();
    }

    @Override
    public long getReplaceTwoArgSuccessRate() {
        return this.sampledCacheDelegate.getReplaceTwoArgSuccessRate();
    }

    @Override
    public long getReplaceTwoArgMissCount() {
        return this.sampledCacheDelegate.getReplaceTwoArgMissCount();
    }

    @Override
    public long getReplaceTwoArgMissRate() {
        return this.sampledCacheDelegate.getReplaceTwoArgMissRate();
    }

    @Override
    public long getPutIfAbsentSuccessCount() {
        return this.sampledCacheDelegate.getPutIfAbsentSuccessCount();
    }

    @Override
    public long getPutIfAbsentSuccessRate() {
        return this.sampledCacheDelegate.getPutIfAbsentSuccessRate();
    }

    @Override
    public long getPutIfAbsentMissCount() {
        return this.sampledCacheDelegate.getPutIfAbsentMissCount();
    }

    @Override
    public long getPutIfAbsentMissRate() {
        return this.sampledCacheDelegate.getPutIfAbsentMissRate();
    }

    @Override
    public long getRemoveElementSuccessCount() {
        return this.sampledCacheDelegate.getRemoveElementSuccessCount();
    }

    @Override
    public long getRemoveElementSuccessRate() {
        return this.sampledCacheDelegate.getRemoveElementSuccessRate();
    }

    @Override
    public long getRemoveElementMissCount() {
        return this.sampledCacheDelegate.getRemoveElementMissCount();
    }

    @Override
    public long getRemoveElementMissRate() {
        return this.sampledCacheDelegate.getRemoveElementMissRate();
    }

    public Map<String, Object> getCacheAttributes() {
        HashMap<String, Object> result = new HashMap<String, Object>();
        result.put("Enabled", this.isEnabled());
        result.put("TerracottaClustered", this.isTerracottaClustered());
        result.put("LoggingEnabled", this.isLoggingEnabled());
        result.put("TimeToIdleSeconds", this.getTimeToIdleSeconds());
        result.put("TimeToLiveSeconds", this.getTimeToLiveSeconds());
        result.put("MaxEntriesLocalHeap", this.getMaxEntriesLocalHeap());
        result.put("MaxEntriesLocalDisk", this.getMaxEntriesLocalDisk());
        result.put("MaxBytesLocalHeapAsString", this.getMaxBytesLocalHeapAsString());
        result.put("MaxBytesLocalOffHeapAsString", this.getMaxBytesLocalOffHeapAsString());
        result.put("MaxBytesLocalDiskAsString", this.getMaxBytesLocalDiskAsString());
        result.put("MaxBytesLocalHeap", this.getMaxBytesLocalHeap());
        result.put("MaxBytesLocalOffHeap", this.getMaxBytesLocalOffHeap());
        result.put("MaxBytesLocalDisk", this.getMaxBytesLocalDisk());
        result.put("MaxEntriesInCache", this.getMaxEntriesInCache());
        result.put("DiskPersistent", this.isDiskPersistent());
        result.put("PersistenceStrategy", this.getPersistenceStrategy());
        result.put("Eternal", this.isEternal());
        result.put("OverflowToDisk", this.isOverflowToDisk());
        result.put("OverflowToOffHeap", this.isOverflowToOffHeap());
        result.put("DiskExpiryThreadIntervalSeconds", this.getDiskExpiryThreadIntervalSeconds());
        result.put("MemoryStoreEvictionPolicy", this.getMemoryStoreEvictionPolicy());
        result.put("TerracottaConsistency", this.getTerracottaConsistency());
        if (this.isTerracottaClustered()) {
            result.put("NodeBulkLoadEnabled", this.isNodeBulkLoadEnabled());
            result.put("NodeCoherent", this.isNodeCoherent());
            result.put("ClusterBulkLoadEnabled", this.isClusterBulkLoadEnabled());
            result.put("ClusterCoherent", this.isClusterCoherent());
        }
        result.put("WriterConcurrency", this.getWriterConcurrency());
        result.put("Transactional", this.getTransactional());
        result.put("PinnedToStore", this.getPinnedToStore());
        return result;
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        return NOTIFICATION_INFO;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        try {
            this.sendNotification("CacheChanged", this.getCacheAttributes(), this.getImmutableCacheName());
        }
        catch (RuntimeException e) {
            LOG.warn("Failed to send JMX notification for {} {} -> {} due to {}", new Object[]{evt.getPropertyName(), evt.getOldValue(), evt.getNewValue(), e.getMessage()});
        }
    }

    @Override
    protected void doDispose() {
        this.sampledCacheDelegate.dispose();
    }

    @Override
    public long getSearchesPerSecond() {
        return this.sampledCacheDelegate.getSearchesPerSecond();
    }

    @Override
    public boolean getTransactional() {
        return this.sampledCacheDelegate.getTransactional();
    }

    @Override
    public boolean getSearchable() {
        return this.sampledCacheDelegate.getSearchable();
    }

    @Override
    public Map<String, String> getSearchAttributes() {
        return this.sampledCacheDelegate.getSearchAttributes();
    }

    @Override
    public long getCacheSearchRate() {
        return this.sampledCacheDelegate.getCacheSearchRate();
    }

    public long getCacheAverageSearchTimeNanos() {
        return this.sampledCacheDelegate.getAverageSearchTime();
    }

    @Override
    public long getTransactionCommitRate() {
        return this.getCacheXaCommitsMostRecentSample();
    }

    @Override
    public long getCacheXaCommitsMostRecentSample() {
        return this.sampledCacheDelegate.getCacheXaCommitsMostRecentSample();
    }

    @Override
    public long getTransactionRollbackRate() {
        return this.getCacheXaRollbacksMostRecentSample();
    }

    @Override
    public long getCacheXaRollbacksMostRecentSample() {
        return this.sampledCacheDelegate.getCacheXaRollbacksMostRecentSample();
    }

    @Override
    public boolean isLocalHeapCountBased() {
        return this.sampledCacheDelegate.isLocalHeapCountBased();
    }

    @Override
    public int getCacheHitRatio() {
        return this.sampledCacheDelegate.getCacheHitRatio();
    }

    @Override
    public int getCacheHitRatioMostRecentSample() {
        return this.sampledCacheDelegate.getCacheHitRatioMostRecentSample();
    }

    @Override
    public long getAverageSearchTimeNanos() {
        return this.sampledCacheDelegate.getAverageSearchTimeNanos();
    }

    @Override
    public SampledCounter getCacheHitSample() {
        return this.sampledCacheDelegate.getCacheHitSample();
    }

    @Override
    public SampledCounter getCacheHitRatioSample() {
        return this.sampledCacheDelegate.getCacheHitRatioSample();
    }

    @Override
    public SampledCounter getCacheHitInMemorySample() {
        return this.sampledCacheDelegate.getCacheHitInMemorySample();
    }

    @Override
    public SampledCounter getCacheHitOffHeapSample() {
        return this.sampledCacheDelegate.getCacheHitOffHeapSample();
    }

    @Override
    public SampledCounter getCacheHitOnDiskSample() {
        return this.sampledCacheDelegate.getCacheHitOnDiskSample();
    }

    @Override
    public SampledCounter getCacheMissSample() {
        return this.sampledCacheDelegate.getCacheMissSample();
    }

    @Override
    public SampledCounter getCacheMissInMemorySample() {
        return this.sampledCacheDelegate.getCacheMissInMemorySample();
    }

    @Override
    public SampledCounter getCacheMissOffHeapSample() {
        return this.sampledCacheDelegate.getCacheMissOffHeapSample();
    }

    @Override
    public SampledCounter getCacheMissOnDiskSample() {
        return this.sampledCacheDelegate.getCacheMissOnDiskSample();
    }

    @Override
    public SampledCounter getCacheMissExpiredSample() {
        return this.sampledCacheDelegate.getCacheMissExpiredSample();
    }

    @Override
    public SampledCounter getCacheMissNotFoundSample() {
        return this.sampledCacheDelegate.getCacheMissNotFoundSample();
    }

    @Override
    public SampledCounter getCacheElementEvictedSample() {
        return this.sampledCacheDelegate.getCacheElementEvictedSample();
    }

    @Override
    public SampledCounter getCacheElementRemovedSample() {
        return this.sampledCacheDelegate.getCacheElementRemovedSample();
    }

    @Override
    public SampledCounter getCacheElementExpiredSample() {
        return this.sampledCacheDelegate.getCacheElementExpiredSample();
    }

    @Override
    public SampledCounter getCacheElementPutSample() {
        return this.sampledCacheDelegate.getCacheElementPutSample();
    }

    @Override
    public SampledCounter getCacheElementUpdatedSample() {
        return this.sampledCacheDelegate.getCacheElementUpdatedSample();
    }

    @Override
    public SampledRateCounter getAverageGetTimeSample() {
        return this.sampledCacheDelegate.getAverageGetTimeSample();
    }

    @Override
    public SampledRateCounter getAverageSearchTimeSample() {
        return this.sampledCacheDelegate.getAverageSearchTimeSample();
    }

    @Override
    public SampledCounter getSearchesPerSecondSample() {
        return this.sampledCacheDelegate.getSearchesPerSecondSample();
    }

    @Override
    public SampledCounter getCacheXaCommitsSample() {
        return this.sampledCacheDelegate.getCacheXaCommitsSample();
    }

    @Override
    public SampledCounter getCacheXaRollbacksSample() {
        return this.sampledCacheDelegate.getCacheXaRollbacksSample();
    }

    @Override
    public long getAverageSearchTime() {
        return this.sampledCacheDelegate.getAverageSearchTime();
    }

    @Override
    public long getAverageGetTime() {
        return this.sampledCacheDelegate.getAverageGetTime();
    }

    @Override
    public SampledCounter getSizeSample() {
        return this.sampledCacheDelegate.getSizeSample();
    }

    @Override
    public SampledCounter getLocalHeapSizeSample() {
        return this.sampledCacheDelegate.getLocalHeapSizeSample();
    }

    @Override
    public SampledCounter getLocalHeapSizeInBytesSample() {
        return this.sampledCacheDelegate.getLocalHeapSizeInBytesSample();
    }

    @Override
    public SampledCounter getLocalOffHeapSizeSample() {
        return this.sampledCacheDelegate.getLocalOffHeapSizeSample();
    }

    @Override
    public SampledCounter getLocalOffHeapSizeInBytesSample() {
        return this.sampledCacheDelegate.getLocalOffHeapSizeInBytesSample();
    }

    @Override
    public SampledCounter getLocalDiskSizeSample() {
        return this.sampledCacheDelegate.getLocalDiskSizeSample();
    }

    @Override
    public SampledCounter getLocalDiskSizeInBytesSample() {
        return this.sampledCacheDelegate.getLocalDiskSizeInBytesSample();
    }

    @Override
    public SampledCounter getRemoteSizeSample() {
        return this.sampledCacheDelegate.getRemoteSizeSample();
    }

    @Override
    public SampledCounter getWriterQueueLengthSample() {
        return this.sampledCacheDelegate.getWriterQueueLengthSample();
    }

    @Override
    public SampledCounter getCacheClusterOfflineSample() {
        return this.sampledCacheDelegate.getCacheClusterOfflineSample();
    }

    @Override
    public SampledCounter getCacheClusterOnlineSample() {
        return this.sampledCacheDelegate.getCacheClusterOnlineSample();
    }

    @Override
    public SampledCounter getCacheClusterRejoinSample() {
        return this.sampledCacheDelegate.getCacheClusterRejoinSample();
    }

    @Override
    public long getCacheClusterOfflineCount() {
        return this.sampledCacheDelegate.getCacheClusterOfflineCount();
    }

    @Override
    public long getCacheClusterRejoinCount() {
        return this.sampledCacheDelegate.getCacheClusterRejoinCount();
    }

    @Override
    public long getCacheClusterOnlineCount() {
        return this.sampledCacheDelegate.getCacheClusterOnlineCount();
    }

    @Override
    public long getCacheClusterOfflineMostRecentSample() {
        return this.sampledCacheDelegate.getCacheClusterOfflineMostRecentSample();
    }

    @Override
    public long getCacheClusterRejoinMostRecentSample() {
        return this.sampledCacheDelegate.getCacheClusterRejoinMostRecentSample();
    }

    @Override
    public long getCacheClusterOnlineMostRecentSample() {
        return this.sampledCacheDelegate.getCacheClusterOnlineMostRecentSample();
    }

    @Override
    public long getMostRecentRejoinTimeStampMillis() {
        return this.sampledCacheDelegate.getMostRecentRejoinTimeStampMillis();
    }

    @Override
    public SampledCounter getMostRecentRejoinTimestampMillisSample() {
        return this.sampledCacheDelegate.getMostRecentRejoinTimestampMillisSample();
    }

    @Override
    public SampledCounter getNonStopSuccessSample() {
        return this.sampledCacheDelegate.getNonStopSuccessSample();
    }

    @Override
    public SampledCounter getNonStopFailureSample() {
        return this.sampledCacheDelegate.getNonStopFailureSample();
    }

    @Override
    public SampledCounter getNonStopRejoinTimeoutSample() {
        return this.sampledCacheDelegate.getNonStopRejoinTimeoutSample();
    }

    @Override
    public SampledCounter getNonStopTimeoutSample() {
        return this.sampledCacheDelegate.getNonStopTimeoutSample();
    }

    @Override
    public long getNonStopSuccessCount() {
        return this.sampledCacheDelegate.getNonStopSuccessCount();
    }

    @Override
    public long getNonStopFailureCount() {
        return this.sampledCacheDelegate.getNonStopFailureCount();
    }

    @Override
    public long getNonStopRejoinTimeoutCount() {
        return this.sampledCacheDelegate.getNonStopRejoinTimeoutCount();
    }

    @Override
    public long getNonStopTimeoutCount() {
        return this.sampledCacheDelegate.getNonStopTimeoutCount();
    }

    @Override
    public long getNonStopSuccessMostRecentSample() {
        return this.sampledCacheDelegate.getNonStopSuccessMostRecentSample();
    }

    @Override
    public long getNonStopFailureMostRecentSample() {
        return this.sampledCacheDelegate.getNonStopFailureMostRecentSample();
    }

    @Override
    public long getNonStopRejoinTimeoutMostRecentSample() {
        return this.sampledCacheDelegate.getNonStopRejoinTimeoutMostRecentSample();
    }

    @Override
    public long getNonStopTimeoutMostRecentSample() {
        return this.sampledCacheDelegate.getNonStopTimeoutMostRecentSample();
    }

    @Override
    public long getNonStopSuccessRate() {
        return this.getNonStopSuccessMostRecentSample();
    }

    @Override
    public long getNonStopFailureRate() {
        return this.getNonStopFailureMostRecentSample();
    }

    @Override
    public long getNonStopRejoinTimeoutRate() {
        return this.getNonStopRejoinTimeoutMostRecentSample();
    }

    @Override
    public long getNonStopTimeoutRate() {
        return this.getNonStopTimeoutMostRecentSample();
    }

    @Override
    public SampledCounter getReplaceOneArgSuccessSample() {
        return this.sampledCacheDelegate.getReplaceOneArgSuccessSample();
    }

    @Override
    public SampledCounter getReplaceOneArgMissSample() {
        return this.sampledCacheDelegate.getReplaceOneArgMissSample();
    }

    @Override
    public SampledCounter getReplaceTwoArgSuccessSample() {
        return this.sampledCacheDelegate.getReplaceTwoArgSuccessSample();
    }

    @Override
    public SampledCounter getReplaceTwoArgMissSample() {
        return this.sampledCacheDelegate.getReplaceTwoArgMissSample();
    }

    @Override
    public SampledCounter getPutIfAbsentSuccessSample() {
        return this.sampledCacheDelegate.getPutIfAbsentSuccessSample();
    }

    @Override
    public SampledCounter getPutIfAbsentMissSample() {
        return this.sampledCacheDelegate.getPutIfAbsentMissSample();
    }

    @Override
    public SampledCounter getRemoveElementSuccessSample() {
        return this.sampledCacheDelegate.getRemoveElementSuccessSample();
    }

    @Override
    public SampledCounter getRemoveElementMissSample() {
        return this.sampledCacheDelegate.getRemoveElementMissSample();
    }

    @Override
    public int getNonstopTimeoutRatio() {
        return this.sampledCacheDelegate.getNonstopTimeoutRatio();
    }

    static {
        String[] notifTypes = new String[]{"CacheEnabled", "CacheChanged", "CacheFlushed"};
        String name = Notification.class.getName();
        String description = "Ehcache SampledCache Event";
        NOTIFICATION_INFO = new MBeanNotificationInfo[]{new MBeanNotificationInfo(notifTypes, name, "Ehcache SampledCache Event")};
    }
}

