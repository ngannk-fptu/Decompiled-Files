/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management.sampled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheOperationOutcomes;
import net.sf.ehcache.CacheStoreHelper;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.CacheConfigurationListener;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.management.sampled.CacheSampler;
import net.sf.ehcache.management.sampled.SampledCounterProxy;
import net.sf.ehcache.management.sampled.SampledRateCounterProxy;
import net.sf.ehcache.management.sampled.Utils;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.util.CacheTransactionHelper;
import net.sf.ehcache.util.counter.sampled.SampledCounter;
import net.sf.ehcache.util.counter.sampled.SampledRateCounter;
import net.sf.ehcache.util.counter.sampled.TimeStampedCounterValue;
import net.sf.ehcache.writer.writebehind.WriteBehindManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.statistics.archive.Timestamped;

public class CacheSamplerImpl
implements CacheSampler,
CacheConfigurationListener {
    private static final double ONE_HUNDRED = 100.0;
    private static final int PERCENTAGE_DIVISOR = 100;
    private static final Logger LOG = LoggerFactory.getLogger(CacheSamplerImpl.class);
    private final Ehcache cache;

    public CacheSamplerImpl(Ehcache cache) {
        this.cache = cache;
        cache.getCacheConfiguration().addConfigurationListener(this);
    }

    @Override
    public boolean isEnabled() {
        return !this.cache.isDisabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        try {
            this.cache.setDisabled(!enabled);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public boolean isClusterBulkLoadEnabled() {
        try {
            return this.cache.isClusterBulkLoadEnabled();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public boolean isNodeBulkLoadEnabled() {
        return this.cache.getCacheConfiguration().isTerracottaClustered() && this.cache.isNodeBulkLoadEnabled();
    }

    @Override
    public void setNodeBulkLoadEnabled(boolean bulkLoadEnabled) {
        if (bulkLoadEnabled && this.getTransactional()) {
            LOG.warn("a transactional cache cannot be put into bulk-load mode");
            return;
        }
        this.cache.setNodeBulkLoadEnabled(bulkLoadEnabled);
    }

    @Override
    public void flush() {
        try {
            this.cache.flush();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public String getCacheName() {
        return this.cache.getName();
    }

    @Override
    public String getStatus() {
        return this.cache.getStatus().toString();
    }

    @Override
    public void removeAll() {
        Store store;
        if (this.cache instanceof Cache && (store = new CacheStoreHelper((Cache)this.cache).getStore()) instanceof TerracottaStore) {
            ((TerracottaStore)store).quickClear();
            this.cache.getCacheEventNotificationService().notifyRemoveAll(false);
            PinningConfiguration pinningConfiguration = this.cache.getCacheConfiguration().getPinningConfiguration();
            if (pinningConfiguration != null && PinningConfiguration.Store.INCACHE.equals((Object)pinningConfiguration.getStore())) {
                LOG.warn("Data availability impacted:\n****************************************************************************************\n************************** removeAll called on a pinned cache **************************\n****************************************************************************************");
            }
            return;
        }
        CacheTransactionHelper.beginTransactionIfNeeded(this.cache);
        try {
            this.cache.removeAll();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
        finally {
            try {
                CacheTransactionHelper.commitTransactionIfNeeded(this.cache);
            }
            catch (RuntimeException e2) {
                throw Utils.newPlainException(e2);
            }
        }
    }

    @Override
    public long getAverageGetTimeNanosMostRecentSample() {
        return this.getAverageGetTimeNanos();
    }

    @Override
    public long getCacheEvictionRate() {
        return this.cache.getStatistics().cacheEvictionOperation().rate().value().longValue();
    }

    @Override
    public long getCacheElementEvictedMostRecentSample() {
        return this.getCacheEvictionRate();
    }

    @Override
    public long getCacheExpirationRate() {
        return this.cache.getStatistics().cacheExpiredOperation().rate().value().longValue();
    }

    @Override
    public long getCacheElementExpiredMostRecentSample() {
        return this.getCacheExpirationRate();
    }

    @Override
    public long getCachePutRate() {
        return this.cache.getStatistics().cachePutOperation().rate().value().longValue();
    }

    @Override
    public long getCacheElementPutMostRecentSample() {
        return this.getCachePutRate();
    }

    @Override
    public long getCacheRemoveRate() {
        return this.cache.getStatistics().cacheRemoveOperation().rate().value().longValue();
    }

    @Override
    public long getCacheElementRemovedMostRecentSample() {
        return this.getCacheRemoveRate();
    }

    @Override
    public long getCacheUpdateRate() {
        return this.cache.getStatistics().cachePutReplacedOperation().rate().value().longValue();
    }

    @Override
    public long getCacheElementUpdatedMostRecentSample() {
        return this.getCacheUpdateRate();
    }

    @Override
    public long getCacheInMemoryHitRate() {
        return this.cache.getStatistics().localHeapHitOperation().rate().value().longValue();
    }

    @Override
    public long getCacheHitInMemoryMostRecentSample() {
        return this.getCacheInMemoryHitRate();
    }

    @Override
    public long getCacheOffHeapHitRate() {
        return this.cache.getStatistics().localOffHeapHitOperation().rate().value().longValue();
    }

    @Override
    public long getCacheHitOffHeapMostRecentSample() {
        return this.getCacheOffHeapHitRate();
    }

    @Override
    public long getCacheHitRate() {
        return this.cache.getStatistics().cacheHitOperation().rate().value().longValue();
    }

    @Override
    public long getCacheHitMostRecentSample() {
        return this.getCacheHitRate();
    }

    @Override
    public long getCacheOnDiskHitRate() {
        return this.cache.getStatistics().localDiskHitOperation().rate().value().longValue();
    }

    @Override
    public long getCacheHitOnDiskMostRecentSample() {
        return this.getCacheOnDiskHitRate();
    }

    @Override
    public long getCacheMissExpiredMostRecentSample() {
        return this.cache.getStatistics().cacheMissExpiredOperation().rate().value().longValue();
    }

    @Override
    public long getCacheMissRate() {
        return this.cache.getStatistics().cacheMissOperation().rate().value().longValue();
    }

    @Override
    public long getCacheMissMostRecentSample() {
        return this.getCacheMissRate();
    }

    @Override
    public long getCacheInMemoryMissRate() {
        return this.cache.getStatistics().localHeapMissOperation().rate().value().longValue();
    }

    @Override
    public long getCacheMissInMemoryMostRecentSample() {
        return this.getCacheInMemoryMissRate();
    }

    @Override
    public long getCacheOffHeapMissRate() {
        return this.cache.getStatistics().localOffHeapMissOperation().rate().value().longValue();
    }

    @Override
    public long getCacheMissOffHeapMostRecentSample() {
        return this.getCacheOffHeapMissRate();
    }

    @Override
    public long getCacheOnDiskMissRate() {
        return this.cache.getStatistics().localDiskMissOperation().rate().value().longValue();
    }

    @Override
    public long getCacheMissOnDiskMostRecentSample() {
        return this.getCacheOnDiskMissRate();
    }

    @Override
    public long getCacheMissNotFoundMostRecentSample() {
        return this.cache.getStatistics().cacheMissNotFoundOperation().rate().value().longValue();
    }

    @Override
    public void dispose() {
        this.cache.getCacheConfiguration().removeConfigurationListener(this);
    }

    @Override
    public boolean isTerracottaClustered() {
        return this.cache.getCacheConfiguration().isTerracottaClustered();
    }

    @Override
    public String getTerracottaConsistency() {
        TerracottaConfiguration.Consistency consistency = this.cache.getCacheConfiguration().getTerracottaConsistency();
        return consistency != null ? consistency.name() : "na";
    }

    @Override
    public long getAverageGetTime() {
        try {
            return this.cache.getStatistics().cacheGetOperation().latency().average().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public Long getMaxGetTimeNanos() {
        try {
            return this.cache.getStatistics().cacheGetOperation().latency().maximum().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public Long getMinGetTimeNanos() {
        try {
            return this.cache.getStatistics().cacheGetOperation().latency().minimum().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getXaCommitCount() {
        try {
            return this.cache.getStatistics().xaCommitCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getXaRollbackCount() {
        try {
            return this.cache.getStatistics().xaRollbackCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getXaRecoveredCount() {
        try {
            return this.cache.getStatistics().xaRecoveryCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public boolean getHasWriteBehindWriter() {
        return this.cache.getWriterManager() instanceof WriteBehindManager && this.cache.getRegisteredCacheWriter() != null;
    }

    @Override
    public long getWriterQueueLength() {
        try {
            return this.cache.getStatistics().getWriterQueueLength();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public int getWriterMaxQueueSize() {
        return this.cache.getCacheConfiguration().getCacheWriterConfiguration().getWriteBehindMaxQueueSize();
    }

    @Override
    public int getWriterConcurrency() {
        return this.cache.getCacheConfiguration().getCacheWriterConfiguration().getWriteBehindConcurrency();
    }

    @Override
    public long getCacheHitCount() {
        try {
            return this.cache.getStatistics().cacheHitCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getCacheMissCount() {
        try {
            return this.cache.getStatistics().cacheMissCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getInMemoryMissCount() {
        try {
            return this.cache.getStatistics().localHeapMissCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getOffHeapMissCount() {
        try {
            return this.cache.getStatistics().localOffHeapMissCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getOnDiskMissCount() {
        try {
            return this.cache.getStatistics().localDiskMissCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getCacheMissCountExpired() {
        try {
            return this.cache.getStatistics().cacheMissExpiredCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getDiskExpiryThreadIntervalSeconds() {
        return this.cache.getCacheConfiguration().getDiskExpiryThreadIntervalSeconds();
    }

    @Override
    public void setDiskExpiryThreadIntervalSeconds(long seconds) {
        if (this.getDiskExpiryThreadIntervalSeconds() != seconds) {
            try {
                this.cache.getCacheConfiguration().setDiskExpiryThreadIntervalSeconds(seconds);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getMaxEntriesLocalHeap() {
        return this.cache.getCacheConfiguration().getMaxEntriesLocalHeap();
    }

    @Override
    public void setMaxEntriesLocalHeap(long maxEntries) {
        if (this.getMaxEntriesLocalHeap() != maxEntries) {
            try {
                this.cache.getCacheConfiguration().setMaxEntriesLocalHeap(maxEntries);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getMaxBytesLocalHeap() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalHeap();
    }

    @Override
    public void setMaxBytesLocalHeap(long maxBytes) {
        try {
            long heapPoolSize;
            if (this.cache.getCacheManager().getConfiguration().isMaxBytesLocalHeapSet() && maxBytes > (heapPoolSize = this.cache.getCacheManager().getConfiguration().getMaxBytesLocalHeap())) {
                throw new IllegalArgumentException("Requested maxBytesLocalHeap (" + maxBytes + ") greater than available CacheManager heap pool size (" + heapPoolSize + ")");
            }
            this.cache.getCacheConfiguration().setMaxBytesLocalHeap(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public void setMaxBytesLocalHeapAsString(String maxBytes) {
        try {
            this.cache.getCacheConfiguration().setMaxBytesLocalHeap(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
        if (this.cache.getCacheConfiguration().isMaxBytesLocalHeapPercentageSet()) {
            long cacheAssignedMem = this.cache.getCacheManager().getConfiguration().getMaxBytesLocalHeap() * (long)this.cache.getCacheConfiguration().getMaxBytesLocalHeapPercentage().intValue() / 100L;
            this.setMaxBytesLocalHeap(cacheAssignedMem);
        }
    }

    @Override
    public String getMaxBytesLocalHeapAsString() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalHeapAsString();
    }

    @Override
    public long getMaxEntriesLocalDisk() {
        return this.cache.getCacheConfiguration().getMaxEntriesLocalDisk();
    }

    @Override
    public void setMaxEntriesLocalDisk(long maxEntries) {
        if (this.getMaxEntriesLocalDisk() != maxEntries) {
            try {
                this.cache.getCacheConfiguration().setMaxEntriesLocalDisk(maxEntries);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public void setMaxBytesLocalDisk(long maxBytes) {
        try {
            long diskPoolSize;
            if (this.cache.getCacheManager().getConfiguration().isMaxBytesLocalDiskSet() && maxBytes > (diskPoolSize = this.cache.getCacheManager().getConfiguration().getMaxBytesLocalDisk())) {
                throw new IllegalArgumentException("Requested maxBytesLocalDisk (" + maxBytes + ") greater than available CacheManager disk pool size (" + diskPoolSize + ")");
            }
            this.cache.getCacheConfiguration().setMaxBytesLocalDisk(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public void setMaxBytesLocalDiskAsString(String maxBytes) {
        try {
            this.cache.getCacheConfiguration().setMaxBytesLocalDisk(maxBytes);
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
        if (this.cache.getCacheConfiguration().isMaxBytesLocalDiskPercentageSet()) {
            long cacheAssignedMem = this.cache.getCacheManager().getConfiguration().getMaxBytesLocalDisk() * (long)this.cache.getCacheConfiguration().getMaxBytesLocalDiskPercentage().intValue() / 100L;
            this.setMaxBytesLocalDisk(cacheAssignedMem);
        }
    }

    @Override
    public String getMaxBytesLocalDiskAsString() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalDiskAsString();
    }

    @Override
    public int getMaxElementsOnDisk() {
        return this.cache.getCacheConfiguration().getMaxElementsOnDisk();
    }

    @Override
    public long getMaxEntriesInCache() {
        return this.cache.getCacheConfiguration().getMaxEntriesInCache();
    }

    @Override
    public void setMaxElementsOnDisk(int maxElements) {
        if (this.getMaxElementsOnDisk() != maxElements) {
            try {
                this.cache.getCacheConfiguration().setMaxElementsOnDisk(maxElements);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public void setMaxEntriesInCache(long maxEntries) {
        if (this.getMaxEntriesInCache() != maxEntries) {
            try {
                this.cache.getCacheConfiguration().setMaxEntriesInCache(maxEntries);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getMaxBytesLocalDisk() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalDisk();
    }

    @Override
    public long getMaxBytesLocalOffHeap() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalOffHeap();
    }

    @Override
    public String getMaxBytesLocalOffHeapAsString() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalOffHeapAsString();
    }

    @Override
    public String getMemoryStoreEvictionPolicy() {
        return this.cache.getCacheConfiguration().getMemoryStoreEvictionPolicy().toString();
    }

    @Override
    public void setMemoryStoreEvictionPolicy(String evictionPolicy) {
        if (!this.getMemoryStoreEvictionPolicy().equals(evictionPolicy)) {
            try {
                this.cache.getCacheConfiguration().setMemoryStoreEvictionPolicy(evictionPolicy);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getTimeToIdleSeconds() {
        return this.cache.getCacheConfiguration().getTimeToIdleSeconds();
    }

    @Override
    public void setTimeToIdleSeconds(long tti) {
        if (this.getTimeToIdleSeconds() != tti) {
            try {
                this.cache.getCacheConfiguration().setTimeToIdleSeconds(tti);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public long getTimeToLiveSeconds() {
        return this.cache.getCacheConfiguration().getTimeToLiveSeconds();
    }

    @Override
    public void setTimeToLiveSeconds(long ttl) {
        if (this.getTimeToLiveSeconds() != ttl) {
            try {
                this.cache.getCacheConfiguration().setTimeToLiveSeconds(ttl);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public boolean isDiskPersistent() {
        return this.cache.getCacheConfiguration().isDiskPersistent();
    }

    @Override
    public String getPersistenceStrategy() {
        PersistenceConfiguration pc = this.cache.getCacheConfiguration().getPersistenceConfiguration();
        return pc != null ? pc.getStrategy().name() : "";
    }

    @Override
    public void setDiskPersistent(boolean diskPersistent) {
        if (this.isDiskPersistent() != diskPersistent) {
            try {
                this.cache.getCacheConfiguration().setDiskPersistent(diskPersistent);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public boolean isOverflowToOffHeap() {
        return this.cache.getCacheConfiguration().isOverflowToOffHeap();
    }

    @Override
    public boolean isEternal() {
        return this.cache.getCacheConfiguration().isEternal();
    }

    @Override
    public void setEternal(boolean eternal) {
        if (this.isEternal() != eternal) {
            try {
                this.cache.getCacheConfiguration().setEternal(eternal);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public boolean isOverflowToDisk() {
        return this.cache.getCacheConfiguration().isOverflowToDisk();
    }

    @Override
    public void setOverflowToDisk(boolean overflowToDisk) {
        if (this.isOverflowToDisk() != overflowToDisk) {
            try {
                this.cache.getCacheConfiguration().setOverflowToDisk(overflowToDisk);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public boolean isLoggingEnabled() {
        return this.cache.getCacheConfiguration().getLogging();
    }

    @Override
    public void setLoggingEnabled(boolean enabled) {
        if (this.isLoggingEnabled() != enabled) {
            try {
                this.cache.getCacheConfiguration().setLogging(enabled);
            }
            catch (RuntimeException e) {
                throw Utils.newPlainException(e);
            }
        }
    }

    @Override
    public boolean isPinned() {
        return this.cache.getCacheConfiguration().getPinningConfiguration() != null;
    }

    @Override
    public String getPinnedToStore() {
        PinningConfiguration pinningConfig = this.cache.getCacheConfiguration().getPinningConfiguration();
        return pinningConfig != null ? pinningConfig.getStore().name() : "na";
    }

    @Override
    public long getEvictedCount() {
        try {
            return this.cache.getStatistics().cacheEvictedCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getExpiredCount() {
        try {
            return this.cache.getStatistics().cacheExpiredCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getInMemoryHitCount() {
        try {
            return this.cache.getStatistics().localHeapHitCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getOffHeapHitCount() {
        try {
            return this.cache.getStatistics().localOffHeapHitCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    @Deprecated
    public long getOffHeapSize() {
        return this.getLocalOffHeapSize();
    }

    @Override
    public long getOnDiskHitCount() {
        try {
            return this.cache.getStatistics().localDiskHitCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    @Deprecated
    public long getOnDiskSize() {
        return this.getLocalDiskSize();
    }

    @Override
    public long getLocalDiskSize() {
        try {
            return this.cache.getStatistics().getLocalDiskSize();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getLocalHeapSize() {
        try {
            return this.cache.getStatistics().getLocalHeapSize();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getLocalOffHeapSize() {
        try {
            return this.cache.getStatistics().getLocalOffHeapSize();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getLocalDiskSizeInBytes() {
        try {
            return this.cache.getStatistics().getLocalDiskSizeInBytes();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getLocalHeapSizeInBytes() {
        try {
            return this.cache.getStatistics().getLocalHeapSizeInBytes();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getLocalOffHeapSizeInBytes() {
        try {
            return this.cache.getStatistics().getLocalOffHeapSizeInBytes();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getPutCount() {
        try {
            return this.cache.getStatistics().cachePutCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getRemovedCount() {
        try {
            return this.cache.getStatistics().cacheRemoveCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getSize() {
        if (this.cache.getCacheConfiguration().isTerracottaClustered()) {
            return this.getRemoteSizeSample().getMostRecentSample().getCounterValue();
        }
        CacheTransactionHelper.beginTransactionIfNeeded(this.cache);
        try {
            long l = this.cache.getStatistics().getSize();
            return l;
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
        finally {
            try {
                CacheTransactionHelper.commitTransactionIfNeeded(this.cache);
            }
            catch (RuntimeException re) {
                throw Utils.newPlainException(re);
            }
        }
    }

    @Override
    public long getInMemorySize() {
        return this.getLocalHeapSize();
    }

    @Override
    public long getUpdateCount() {
        try {
            return this.cache.getStatistics().cachePutUpdatedCount();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceOneArgSuccessCount() {
        try {
            return this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceOneArgSuccessRate() {
        try {
            return this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.SUCCESS).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceOneArgMissCount() {
        try {
            return this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.FAILURE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceOneArgMissRate() {
        try {
            return this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.FAILURE).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceTwoArgSuccessCount() {
        try {
            return this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceTwoArgSuccessRate() {
        try {
            return this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.SUCCESS).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceTwoArgMissCount() {
        try {
            return this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getReplaceTwoArgMissRate() {
        try {
            return this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.FAILURE).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getPutIfAbsentSuccessCount() {
        try {
            return this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getPutIfAbsentSuccessRate() {
        try {
            return this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.SUCCESS).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getPutIfAbsentMissCount() {
        try {
            return this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.FAILURE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getPutIfAbsentMissRate() {
        try {
            return this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.FAILURE).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getRemoveElementSuccessCount() {
        try {
            return this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getRemoveElementSuccessRate() {
        try {
            return this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.SUCCESS).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getRemoveElementMissCount() {
        try {
            return this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.FAILURE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getRemoveElementMissRate() {
        try {
            return this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.FAILURE).rate().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public void deregistered(CacheConfiguration config) {
    }

    @Override
    public void maxBytesLocalHeapChanged(long oldValue, long newValue) {
        if (oldValue != newValue) {
            this.setMaxBytesLocalHeap(newValue);
        }
    }

    @Override
    public void maxBytesLocalDiskChanged(long oldValue, long newValue) {
        if (oldValue != newValue) {
            this.setMaxBytesLocalDisk(newValue);
        }
    }

    @Override
    public void diskCapacityChanged(int oldCapacity, int newCapacity) {
        if (oldCapacity != newCapacity) {
            this.setMaxElementsOnDisk(newCapacity);
        }
    }

    @Override
    public void maxEntriesInCacheChanged(long oldCapacity, long newCapacity) {
        if (oldCapacity != newCapacity) {
            this.setMaxEntriesInCache(newCapacity);
        }
    }

    @Override
    public void loggingChanged(boolean oldValue, boolean newValue) {
        if (oldValue != newValue) {
            this.setLoggingEnabled(newValue);
        }
    }

    @Override
    public void memoryCapacityChanged(int oldCapacity, int newCapacity) {
        if (oldCapacity != newCapacity) {
            this.setMaxEntriesLocalHeap(newCapacity);
        }
    }

    @Override
    public void registered(CacheConfiguration config) {
    }

    @Override
    public void timeToIdleChanged(long oldTimeToIdle, long newTimeToIdle) {
        if (oldTimeToIdle != newTimeToIdle) {
            this.setTimeToIdleSeconds(newTimeToIdle);
        }
    }

    @Override
    public void timeToLiveChanged(long oldTimeToLive, long newTimeToLive) {
        if (oldTimeToLive != newTimeToLive) {
            this.setTimeToLiveSeconds(newTimeToLive);
        }
    }

    @Override
    public long getAverageSearchTime() {
        return this.cache.getStatistics().cacheSearchOperation().latency().average().value().longValue();
    }

    @Override
    public long getSearchesPerSecond() {
        return this.getCacheSearchRate();
    }

    @Override
    public boolean getTransactional() {
        return this.cache.getCacheConfiguration().getTransactionalMode().isTransactional();
    }

    @Override
    public boolean getSearchable() {
        return this.cache.getCacheConfiguration().getSearchable() != null;
    }

    @Override
    public Map<String, String> getSearchAttributes() {
        HashMap<String, String> result = new HashMap<String, String>();
        if (this.cache != null && this.cache.getCacheConfiguration().getSearchable() != null) {
            HashMap<String, Attribute> attrMap = new HashMap<String, Attribute>();
            for (Attribute attr : this.cache.getSearchAttributes()) {
                attrMap.put(attr.getAttributeName(), attr);
            }
            for (SearchAttribute sa : this.cache.getCacheConfiguration().getSearchAttributes().values()) {
                String saName = sa.getName();
                String typeName = sa.getTypeName();
                if (!attrMap.containsKey(saName) || typeName == null) continue;
                result.put(saName, typeName);
            }
        }
        return result;
    }

    @Override
    public long getCacheSearchRate() {
        return this.cache.getStatistics().cacheSearchOperation().rate().value().longValue();
    }

    @Override
    public long getTransactionCommitRate() {
        return this.cache.getStatistics().xaRecoveryOperation().rate().value().longValue();
    }

    @Override
    public long getCacheXaCommitsMostRecentSample() {
        return this.getTransactionCommitRate();
    }

    @Override
    public long getTransactionRollbackRate() {
        return this.cache.getStatistics().xaRollbackOperation().rate().value().longValue();
    }

    @Override
    public long getCacheXaRollbacksMostRecentSample() {
        return this.getTransactionRollbackRate();
    }

    @Override
    public boolean isLocalHeapCountBased() {
        return this.cache.getCacheConfiguration().getMaxBytesLocalHeap() <= 0L && (this.cache.getCacheManager() == null || !this.cache.getCacheManager().getConfiguration().isMaxBytesLocalHeapSet());
    }

    Ehcache getCache() {
        return this.cache;
    }

    @Override
    public int getCacheHitRatio() {
        return (int)(this.cache.getStatistics().getExtended().cacheHitRatio().value() * 100.0);
    }

    @Override
    public int getCacheHitRatioMostRecentSample() {
        return this.getCacheHitRatio();
    }

    @Override
    public SampledCounter getCacheHitRatioSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().cacheHitRatio()){

            @Override
            public TimeStampedCounterValue getMostRecentSample() {
                return new TimeStampedCounterValue(System.currentTimeMillis(), this.getValue());
            }

            @Override
            public TimeStampedCounterValue[] getAllSampleValues() {
                ArrayList<TimeStampedCounterValue> arr = new ArrayList<TimeStampedCounterValue>();
                for (Timestamped ts : this.rate.history()) {
                    arr.add(new TimeStampedCounterValue(ts.getTimestamp(), (int)((Double)ts.getSample() * 100.0)));
                }
                return this.sortAndPresent(arr);
            }

            @Override
            public long getValue() {
                return (long)((Double)this.rate.value() * 100.0);
            }
        };
    }

    @Override
    public long getAverageGetTimeNanos() {
        return this.cache.getStatistics().cacheGetOperation().latency().average().value().longValue();
    }

    @Override
    public SampledCounter getCacheHitSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheHitOperation().rate());
    }

    @Override
    public SampledCounter getCacheHitInMemorySample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localHeapHitOperation().rate());
    }

    @Override
    public SampledCounter getCacheHitOffHeapSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localOffHeapHitOperation().rate());
    }

    @Override
    public SampledCounter getCacheHitOnDiskSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localDiskHitOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheMissOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissInMemorySample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localHeapMissOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissOffHeapSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localOffHeapMissOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissOnDiskSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().localDiskMissOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissExpiredSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheMissExpiredOperation().rate());
    }

    @Override
    public SampledCounter getCacheMissNotFoundSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheMissNotFoundOperation().rate());
    }

    @Override
    public SampledCounter getCacheElementEvictedSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheEvictionOperation().rate());
    }

    @Override
    public SampledCounter getCacheElementRemovedSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheRemoveOperation().rate());
    }

    @Override
    public SampledCounter getCacheElementExpiredSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cacheExpiredOperation().rate());
    }

    @Override
    public SampledCounter getCacheElementPutSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cachePutOperation().rate());
    }

    @Override
    public SampledCounter getCacheElementUpdatedSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().cachePutReplacedOperation().rate());
    }

    @Override
    public SampledRateCounter getAverageGetTimeSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().cacheGetOperation().latency().average());
    }

    @Override
    public SampledRateCounter getAverageSearchTimeSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().cacheSearchOperation().latency().average());
    }

    @Override
    public SampledCounter getSearchesPerSecondSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().cacheSearchOperation().rate());
    }

    @Override
    public SampledCounter getCacheXaCommitsSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().xaCommitSuccessOperation().rate());
    }

    @Override
    public SampledCounter getCacheXaRollbacksSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().xaRollbackOperation().rate());
    }

    @Override
    public SampledCounter getSizeSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().size());
    }

    @Override
    public SampledCounter getLocalHeapSizeSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localHeapSize());
    }

    @Override
    public SampledCounter getLocalHeapSizeInBytesSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localHeapSizeInBytes());
    }

    @Override
    public SampledCounter getLocalOffHeapSizeSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localOffHeapSize());
    }

    @Override
    public SampledCounter getLocalOffHeapSizeInBytesSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localOffHeapSizeInBytes());
    }

    @Override
    public SampledCounter getLocalDiskSizeSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localDiskSize());
    }

    @Override
    public SampledCounter getLocalDiskSizeInBytesSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().localDiskSizeInBytes());
    }

    @Override
    public SampledCounter getRemoteSizeSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().remoteSize());
    }

    @Override
    public SampledCounter getWriterQueueLengthSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().writerQueueLength());
    }

    @Override
    public long getAverageSearchTimeNanos() {
        return this.getAverageSearchTime();
    }

    @Override
    public long getCacheClusterOfflineCount() {
        try {
            return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.OFFLINE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getCacheClusterRejoinCount() {
        try {
            return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.REJOINED).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getCacheClusterOnlineCount() {
        try {
            return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.ONLINE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getCacheClusterOfflineMostRecentSample() {
        return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.OFFLINE).rate().value().longValue();
    }

    @Override
    public long getCacheClusterRejoinMostRecentSample() {
        return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.REJOINED).rate().value().longValue();
    }

    @Override
    public long getCacheClusterOnlineMostRecentSample() {
        return this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.ONLINE).rate().value().longValue();
    }

    @Override
    public SampledCounter getCacheClusterOfflineSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.OFFLINE).rate());
    }

    @Override
    public SampledCounter getCacheClusterOnlineSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.ONLINE).rate());
    }

    @Override
    public SampledCounter getCacheClusterRejoinSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().clusterEvent().component(CacheOperationOutcomes.ClusterEventOutcomes.REJOINED).rate());
    }

    @Override
    public long getMostRecentRejoinTimeStampMillis() {
        try {
            return this.cache.getStatistics().getExtended().mostRecentRejoinTimeStampMillis().value().longValue();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public SampledCounter getMostRecentRejoinTimestampMillisSample() {
        return new SampledRateCounterProxy<Number>(this.cache.getStatistics().getExtended().mostRecentRejoinTimeStampMillis());
    }

    @Override
    public long getNonStopSuccessCount() {
        try {
            return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getNonStopFailureCount() {
        try {
            return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.FAILURE).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getNonStopRejoinTimeoutCount() {
        try {
            return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getNonStopTimeoutCount() {
        try {
            return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT).count().value();
        }
        catch (RuntimeException e) {
            throw Utils.newPlainException(e);
        }
    }

    @Override
    public long getNonStopSuccessMostRecentSample() {
        return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS).rate().value().longValue();
    }

    @Override
    public long getNonStopFailureMostRecentSample() {
        return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.FAILURE).rate().value().longValue();
    }

    @Override
    public long getNonStopRejoinTimeoutMostRecentSample() {
        return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT).rate().value().longValue();
    }

    @Override
    public long getNonStopTimeoutMostRecentSample() {
        return this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT).rate().value().longValue();
    }

    @Override
    public SampledCounter getNonStopSuccessSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.SUCCESS).rate());
    }

    @Override
    public SampledCounter getNonStopFailureSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.FAILURE).rate());
    }

    @Override
    public SampledCounter getNonStopRejoinTimeoutSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.REJOIN_TIMEOUT).rate());
    }

    @Override
    public SampledCounter getNonStopTimeoutSample() {
        return new SampledRateCounterProxy<Double>(this.cache.getStatistics().getExtended().nonstop().component(CacheOperationOutcomes.NonStopOperationOutcomes.TIMEOUT).rate());
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
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.SUCCESS).rate());
    }

    @Override
    public SampledCounter getReplaceOneArgMissSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().replaceOneArg().component(CacheOperationOutcomes.ReplaceOneArgOutcome.FAILURE).rate());
    }

    @Override
    public SampledCounter getReplaceTwoArgSuccessSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.SUCCESS).rate());
    }

    @Override
    public SampledCounter getReplaceTwoArgMissSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().replaceTwoArg().component(CacheOperationOutcomes.ReplaceTwoArgOutcome.FAILURE).rate());
    }

    @Override
    public SampledCounter getPutIfAbsentSuccessSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.SUCCESS).rate());
    }

    @Override
    public SampledCounter getPutIfAbsentMissSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().putIfAbsent().component(CacheOperationOutcomes.PutIfAbsentOutcome.FAILURE).rate());
    }

    @Override
    public SampledCounter getRemoveElementSuccessSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.SUCCESS).rate());
    }

    @Override
    public SampledCounter getRemoveElementMissSample() {
        return new SampledCounterProxy<Double>(this.cache.getStatistics().getExtended().removeElement().component(CacheOperationOutcomes.RemoveElementOutcome.FAILURE).rate());
    }

    @Override
    public int getNonstopTimeoutRatio() {
        return (int)(this.cache.getStatistics().getExtended().nonstopTimeoutRatio().value() * 100.0);
    }
}

