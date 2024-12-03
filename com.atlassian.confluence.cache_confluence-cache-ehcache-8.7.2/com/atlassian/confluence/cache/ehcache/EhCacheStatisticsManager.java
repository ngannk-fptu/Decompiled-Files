/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheStatisticsKey
 *  com.atlassian.confluence.cache.CacheStatistics
 *  com.atlassian.confluence.cache.CacheStatisticsCapability
 *  com.atlassian.confluence.cache.CacheStatisticsManager
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  org.dom4j.DocumentException
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.cache.CacheStatisticsKey;
import com.atlassian.confluence.cache.CacheStatistics;
import com.atlassian.confluence.cache.CacheStatisticsCapability;
import com.atlassian.confluence.cache.CacheStatisticsManager;
import com.atlassian.confluence.cache.ehcache.EhCacheSettingsDefaultsProvider;
import com.atlassian.confluence.cache.ehcache.EhCacheStatistics;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import org.dom4j.DocumentException;

public class EhCacheStatisticsManager
implements CacheStatisticsManager {
    private final I18NBeanFactory i18NBeanFactory;
    private final CacheManager cacheManager;
    private final EhCacheSettingsDefaultsProvider settingsProvider;
    private final FormatSettingsManager formatSettingsManager;
    private static final Comparator<CacheStatistics> CACHE_NICE_NAME_COMPARATOR = (o1, o2) -> {
        String name1 = o1.getNiceName() != null ? o1.getNiceName() : o1.getName();
        String name2 = o2.getNiceName() != null ? o2.getNiceName() : o2.getName();
        return name1.compareTo(name2);
    };

    public EhCacheStatisticsManager(I18NBeanFactory i18NBeanFactory, CacheManager ehCacheManager, EhCacheSettingsDefaultsProvider settingsProvider, FormatSettingsManager formatSettingsManager) throws DocumentException {
        this.i18NBeanFactory = Objects.requireNonNull(i18NBeanFactory);
        this.cacheManager = Objects.requireNonNull(ehCacheManager);
        this.settingsProvider = Objects.requireNonNull(settingsProvider);
        this.formatSettingsManager = Objects.requireNonNull(formatSettingsManager);
    }

    public List<CacheStatistics> getLocalCacheStatistics() {
        ArrayList<CacheStatistics> cacheStatistics = new ArrayList<CacheStatistics>();
        I18NBean i18nBean = this.i18NBeanFactory.getI18NBean();
        for (String cacheName : this.cacheManager.getCacheNames()) {
            cacheStatistics.add(new EhCacheStatistics(this.cacheManager.getEhcache(cacheName), i18nBean, this.settingsProvider, this.formatSettingsManager));
        }
        Collections.sort(cacheStatistics, CACHE_NICE_NAME_COMPARATOR);
        return cacheStatistics;
    }

    public CacheStatistics getLocalCacheStatistics(String cacheName) {
        Ehcache ehcache = this.cacheManager.getEhcache(cacheName);
        if (ehcache != null) {
            return new EhCacheStatistics(ehcache, this.i18NBeanFactory.getI18NBean(), this.settingsProvider, this.formatSettingsManager);
        }
        return null;
    }

    public Set<CacheStatisticsCapability> getCapabilities() {
        return Collections.singleton(CacheStatisticsCapability.CACHE_MISSES);
    }

    public Predicate<CacheStatisticsKey> getCacheStatisticFilter(String cacheName) {
        return key -> CacheStatisticsKey.HEAP_SIZE != key || this.settingsProvider.isReportBytesLocalHeap(cacheName);
    }
}

