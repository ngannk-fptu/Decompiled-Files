/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.impl.cache.CacheCompactorSupport
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Stopwatch
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.Status
 *  net.sf.ehcache.statistics.StatisticsGateway
 *  net.sf.ehcache.statistics.extended.ExtendedStatistics
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.cache.ehcache.EhCacheManager;
import com.atlassian.confluence.impl.cache.CacheCompactorSupport;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.statistics.StatisticsGateway;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class EhCacheCompactor
extends CacheCompactorSupport {
    private static final Logger log = LoggerFactory.getLogger(EhCacheCompactor.class);
    public static final boolean STATS_COMPACTION_ENABLED = Boolean.getBoolean("ehcache.stats.compaction");
    private static final long ELAPSED_MILLIS_WARN_THRESHOLD = 1000L;
    private final CacheManager cacheManager;
    private final DarkFeaturesManager darkFeatureManager;

    public EhCacheCompactor(EhCacheManager cacheManager, DarkFeaturesManager darkFeatureManager) {
        this.cacheManager = (CacheManager)Preconditions.checkNotNull((Object)cacheManager.getDelegateEhCacheManager());
        this.darkFeatureManager = (DarkFeaturesManager)Preconditions.checkNotNull((Object)darkFeatureManager);
        log.info("Ehcache stats compaction is {}", (Object)(STATS_COMPACTION_ENABLED ? "ENABLED" : "DISABLED"));
    }

    public void compact() {
        if (this.cacheManagerIsRunning() && this.compactionEnabled()) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            int numberOfCaches = this.performEviction();
            long elapsedMillis = stopwatch.elapsed(TimeUnit.MILLISECONDS);
            if (elapsedMillis > 1000L) {
                log.warn("Completed compaction on {} caches in {} ms", (Object)numberOfCaches, (Object)elapsedMillis);
            } else {
                log.debug("Completed compaction on {} caches in {} ms", (Object)numberOfCaches, (Object)elapsedMillis);
            }
        } else {
            log.debug("CacheManager is not alive, or dark feature is not enabled; skipping compaction");
        }
    }

    private int performEviction() {
        String[] cacheNames = this.cacheManager.getCacheNames();
        log.debug("Starting compaction for all {} caches", (Object)cacheNames.length);
        for (String cacheName : cacheNames) {
            this.evictExpiredElements(this.cacheManager.getEhcache(cacheName));
        }
        return cacheNames.length;
    }

    private boolean compactionEnabled() {
        boolean isDisabled = this.darkFeatureManager.getDarkFeatures().isFeatureEnabled("ehcache.compactionJob.disabled");
        return !isDisabled;
    }

    private boolean cacheManagerIsRunning() {
        return this.cacheManager.getStatus() == Status.STATUS_ALIVE;
    }

    private void evictExpiredElements(Ehcache cache) {
        if (log.isDebugEnabled()) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            int noOfElementsBefore = cache.getSize();
            cache.evictExpiredElements();
            int noOfElementsAfter = cache.getSize();
            log.debug("Evicted elements from cache [{}] in {} ms ({} elements before, {} after)", new Object[]{cache.getName(), stopwatch.elapsed(TimeUnit.MILLISECONDS), noOfElementsBefore, noOfElementsAfter});
        } else {
            cache.evictExpiredElements();
        }
        if (STATS_COMPACTION_ENABLED) {
            StatisticsGateway statisticsGateway = cache.getStatistics();
            ExtendedStatistics extended = statisticsGateway.getExtended();
            EhCacheCompactor.flushStats(extended, "standardOperations");
            EhCacheCompactor.flushStats(extended, "customOperations");
            EhCacheCompactor.flushStats(extended, "customPassthrus");
        }
    }

    public static void flushStats(ExtendedStatistics extendedStatistics, String statsName) {
        try {
            Field f = extendedStatistics.getClass().getDeclaredField(statsName);
            f.setAccessible(true);
            ConcurrentMap statsHolder = (ConcurrentMap)f.get(extendedStatistics);
            log.debug("Flushing [{}] operations from {}", (Object)statsHolder.size(), (Object)statsName);
            statsHolder.clear();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

