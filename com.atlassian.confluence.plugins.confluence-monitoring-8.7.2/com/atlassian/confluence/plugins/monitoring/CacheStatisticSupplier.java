/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.cache.ManagedCache
 *  com.atlassian.cluster.monitoring.spi.model.Table
 *  com.atlassian.confluence.cache.CacheStatisticsHelper
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.monitoring;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.cache.ManagedCache;
import com.atlassian.cluster.monitoring.spi.model.Table;
import com.atlassian.confluence.cache.CacheStatisticsHelper;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheStatisticSupplier
implements Supplier<Table> {
    private static final Logger log = LoggerFactory.getLogger(CacheStatisticSupplier.class);
    private static final String I18N_PREFIX = CacheStatisticSupplier.class.getCanonicalName();
    private final I18NBean i18NBean;
    private final CacheManager cacheManager;

    public CacheStatisticSupplier(CacheManager cacheManager, I18NBeanFactory i18NBeanFactory) {
        this.cacheManager = Objects.requireNonNull(cacheManager);
        this.i18NBean = i18NBeanFactory.getI18NBean();
    }

    @Override
    public Table get() {
        log.debug("Getting cache stats");
        ImmutableMap columns = ImmutableMap.builder().put((Object)Column.NAME.key, (Object)this.i18NBean.getText(Column.NAME.i18nKey)).put((Object)Column.SIZE.key, (Object)this.i18NBean.getText(Column.SIZE.i18nKey)).put((Object)Column.HIT_COUNT.key, (Object)this.i18NBean.getText(Column.HIT_COUNT.i18nKey)).put((Object)Column.MISS_COUNT.key, (Object)this.i18NBean.getText(Column.MISS_COUNT.i18nKey)).put((Object)Column.HIT_PERCENT.key, (Object)this.i18NBean.getText(Column.HIT_PERCENT.i18nKey)).build();
        LinkedHashMap<String, ImmutableList> rows = new LinkedHashMap<String, ImmutableList>();
        for (ManagedCache cache : this.cacheManager.getManagedCaches()) {
            SortedMap stats = cache.getStatistics();
            String displayableName = CacheStatisticsHelper.getDisplayableName((String)cache.getName(), (I18NBean)this.i18NBean);
            Long size = this.fetchCacheValue(stats, CacheStatisticsKey.SIZE);
            Long hitCount = this.fetchCacheValue(stats, CacheStatisticsKey.HIT_COUNT);
            Long missCount = this.fetchCacheValue(stats, CacheStatisticsKey.MISS_COUNT);
            Long hitPercentage = hitCount == null || missCount == null || hitCount + missCount == 0L ? null : Long.valueOf(100L * hitCount / (hitCount + missCount));
            rows.put(cache.getName(), ImmutableList.of((Object)displayableName, (Object)String.valueOf(size), (Object)String.valueOf(hitCount), (Object)String.valueOf(missCount), (Object)(hitPercentage == null ? "null" : hitPercentage + "%")));
        }
        return new Table((Map)columns, rows);
    }

    private Long fetchCacheValue(Map<CacheStatisticsKey, Supplier<Long>> stats, CacheStatisticsKey key) {
        Supplier<Long> longSupplier = stats.get(key);
        return longSupplier == null ? null : longSupplier.get();
    }

    private static enum Column {
        NAME("name"),
        SIZE("size"),
        HIT_COUNT("hitCount"),
        MISS_COUNT("missCount"),
        HIT_PERCENT("hitPercent");

        private final String key;
        private final String i18nKey;

        private Column(String key) {
            this.key = (String)Preconditions.checkNotNull((Object)key);
            this.i18nKey = I18N_PREFIX + "." + this.key;
        }
    }
}

