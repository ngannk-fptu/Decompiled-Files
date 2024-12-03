/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.cache.CacheStatistics
 *  com.atlassian.confluence.cache.CacheStatisticsHelper
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.statistics.StatisticsGateway
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsHelper;
import com.atlassian.confluence.cache.ehcache.EhCacheSettingsDefaultsProvider;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.statistics.StatisticsGateway;

@Deprecated
class EhCacheStatistics
implements CacheStatistics {
    private final String cacheName;
    private final long hitCount;
    private final long notFoundCount;
    private final long expiredCount;
    private final long size;
    private final String niceName;
    private final long maxSize;
    private final long localHeapSizeInBytes;
    private final String formattedLocalHeapSizeInMegabytes;

    public EhCacheStatistics(Ehcache cache, I18NBean i18nBean, EhCacheSettingsDefaultsProvider settingsProvider, FormatSettingsManager formatSettingsManager) throws CacheException {
        StatisticsGateway statistics = cache.getStatistics();
        this.cacheName = cache.getName();
        this.niceName = CacheStatisticsHelper.getDisplayableName((String)cache.getName(), (I18NBean)i18nBean);
        this.hitCount = statistics.cacheHitCount();
        this.notFoundCount = statistics.cacheMissCount();
        this.expiredCount = statistics.cacheEvictedCount();
        this.size = cache.getSize();
        this.maxSize = cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        if (settingsProvider.isReportBytesLocalHeap(this.cacheName)) {
            this.localHeapSizeInBytes = statistics.getLocalHeapSizeInBytes();
            this.formattedLocalHeapSizeInMegabytes = CacheStatisticsHelper.formatSizeInMegabytes((FormatSettingsManager)formatSettingsManager, (long)this.localHeapSizeInBytes);
        } else {
            this.localHeapSizeInBytes = 0L;
            this.formattedLocalHeapSizeInMegabytes = i18nBean.getText("cache.size.unknown");
        }
    }

    public long getSizeInBytes() {
        return this.localHeapSizeInBytes;
    }

    public boolean isNearCache() {
        return false;
    }

    public String getFormattedSizeInMegabytes() {
        return this.formattedLocalHeapSizeInMegabytes;
    }

    public boolean hasContents() {
        return this.size > 0L;
    }

    public long getHitCount() {
        return this.hitCount;
    }

    public long getExpiredCount() {
        return this.expiredCount;
    }

    public long getAccessCount() {
        return this.hitCount + this.notFoundCount + this.expiredCount;
    }

    public long getMissCount() {
        return this.notFoundCount + this.expiredCount;
    }

    public int getHitPercent() {
        return CacheStatisticsHelper.asPercentage((long)this.hitCount, (long)this.getAccessCount());
    }

    public long getSize() {
        return this.size;
    }

    public long getMaxSize() {
        return this.maxSize;
    }

    public String getName() {
        return this.cacheName;
    }

    public int getUsagePercent() {
        if (this.getAccessCount() == 0L) {
            return 0;
        }
        return CacheStatisticsHelper.calculateCapacityPercentage((long)this.getSize(), (long)this.getMaxSize());
    }

    public String getNiceName() {
        return this.niceName;
    }
}

