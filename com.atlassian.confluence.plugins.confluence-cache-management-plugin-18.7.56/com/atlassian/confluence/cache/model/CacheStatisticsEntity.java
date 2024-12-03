/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.confluence.cache.CacheStatisticsHelper
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  io.atlassian.fugue.Suppliers
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.confluence.cache.model;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.confluence.cache.CacheStatisticsHelper;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import io.atlassian.fugue.Suppliers;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.xml.bind.annotation.XmlElement;

public class CacheStatisticsEntity {
    private final Map<CacheStatisticsKey, Supplier<Long>> statistics;
    private final Supplier<String> name;
    private final Supplier<String> niceName;
    private final Supplier<String> formattedSize;
    private final Supplier<String> cacheType;
    private final Supplier<Boolean> flushable;
    private final Supplier<Integer> maxSize;

    public CacheStatisticsEntity(ManagedCache managedCache, boolean clustered, FormatSettingsManager formatSettingsManager, I18NBean i18NBean, Predicate<CacheStatisticsKey> cacheStatisticFilter) {
        this.statistics = CacheStatisticsEntity.filter(managedCache.getStatistics(), cacheStatisticFilter);
        this.name = Suppliers.memoize(() -> ((ManagedCache)managedCache).getName());
        this.niceName = Suppliers.memoize(() -> CacheStatisticsHelper.getDisplayableName((String)managedCache.getName(), (I18NBean)i18NBean));
        this.formattedSize = Suppliers.memoize(() -> CacheStatisticsHelper.formatSizeInMegabytes((FormatSettingsManager)formatSettingsManager, (long)this.getSizeInBytes()));
        this.cacheType = Suppliers.memoize(() -> CacheStatisticsEntity.getCacheType(clustered, managedCache));
        this.flushable = Suppliers.memoize(() -> ((ManagedCache)managedCache).isFlushable());
        this.maxSize = Suppliers.memoize(() -> Optional.ofNullable(managedCache.currentMaxEntries()).orElse(Integer.MAX_VALUE));
    }

    private static Map<CacheStatisticsKey, Supplier<Long>> filter(SortedMap<CacheStatisticsKey, Supplier<Long>> statistics, Predicate<CacheStatisticsKey> cacheStatisticFilter) {
        return statistics.entrySet().stream().filter(entry -> cacheStatisticFilter.test((CacheStatisticsKey)entry.getKey())).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @XmlElement
    public String getType() {
        return this.cacheType.get();
    }

    private static String getCacheType(boolean clustered, ManagedCache managedCache) {
        if (clustered && managedCache != null) {
            if (managedCache.isLocal()) {
                return "local";
            }
            if (managedCache.isReplicateViaCopy()) {
                return "distributed";
            }
            return "hybrid";
        }
        return null;
    }

    @XmlElement
    public boolean getFlushable() {
        return this.flushable.get();
    }

    @XmlElement
    public long getHitCount() {
        return this.getStatistic(CacheStatisticsKey.HIT_COUNT);
    }

    private long getStatistic(CacheStatisticsKey key) {
        return this.statistics.getOrDefault(key, () -> 0L).get();
    }

    @XmlElement
    public String getNiceName() {
        return this.niceName.get();
    }

    @XmlElement
    public long getMaxSize() {
        return this.maxSize.get().intValue();
    }

    @XmlElement
    public String getName() {
        return this.name.get();
    }

    @XmlElement
    public long getSizeInBytes() {
        return this.getStatistic(CacheStatisticsKey.HEAP_SIZE);
    }

    @XmlElement
    public Long getMissCount() {
        return this.getStatistic(CacheStatisticsKey.MISS_COUNT);
    }

    @XmlElement
    public String getFormattedSizeInMegabytes() {
        return this.formattedSize.get();
    }

    @XmlElement
    public long getAccessCount() {
        return this.getHitCount() + this.getMissCount();
    }

    @XmlElement
    public Long getHitPercent() {
        return CacheStatisticsHelper.asPercentage((long)this.getHitCount(), (long)this.getAccessCount());
    }

    @XmlElement
    public long getSize() {
        return this.getStatistic(CacheStatisticsKey.SIZE);
    }

    @XmlElement
    public int getUsagePercent() {
        return CacheStatisticsHelper.calculateCapacityPercentage((long)this.getSize(), (long)this.getMaxSize());
    }

    @XmlElement
    public boolean hasContents() {
        return this.getSize() > 0L;
    }

    @XmlElement
    public Long getExpiredCount() {
        return this.getStatistic(CacheStatisticsKey.EVICTION_COUNT);
    }
}

