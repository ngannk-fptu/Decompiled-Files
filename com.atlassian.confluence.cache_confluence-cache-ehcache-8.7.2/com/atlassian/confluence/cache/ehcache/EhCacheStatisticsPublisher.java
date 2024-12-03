/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.confluence.setup.settings.DarkFeaturesManager
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Status
 *  net.sf.ehcache.statistics.FlatStatistics
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.cache.ehcache;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.cache.ehcache.EhCacheManager;
import com.atlassian.confluence.cache.ehcache.EhCacheStatisticsEvent;
import com.atlassian.confluence.setup.settings.DarkFeaturesManager;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Arrays;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Status;
import net.sf.ehcache.statistics.FlatStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Deprecated
@Internal
public class EhCacheStatisticsPublisher
implements Runnable {
    private static final Logger log = LoggerFactory.getLogger(EhCacheStatisticsPublisher.class);
    private final CacheManager cacheManager;
    private final DarkFeaturesManager darkFeatureManager;
    private final EventPublisher eventPublisher;

    public EhCacheStatisticsPublisher(EhCacheManager cacheManager, DarkFeaturesManager darkFeatureManager, EventPublisher eventPublisher) {
        this(cacheManager.getDelegateEhCacheManager(), darkFeatureManager, eventPublisher);
    }

    @VisibleForTesting
    EhCacheStatisticsPublisher(CacheManager ehCacheManager, DarkFeaturesManager darkFeatureManager, EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
        this.cacheManager = (CacheManager)Preconditions.checkNotNull((Object)ehCacheManager);
        this.darkFeatureManager = (DarkFeaturesManager)Preconditions.checkNotNull((Object)darkFeatureManager);
    }

    @Override
    public void run() {
        if (this.cacheManagerIsRunning() && this.eventsEnabled()) {
            Arrays.stream(this.cacheManager.getCacheNames()).map(arg_0 -> ((CacheManager)this.cacheManager).getEhcache(arg_0)).filter(cache -> cache.getStatus() == Status.STATUS_ALIVE).map(cache -> EhCacheStatisticsEvent.create(cache.getName(), (FlatStatistics)cache.getStatistics())).forEach(arg_0 -> ((EventPublisher)this.eventPublisher).publish(arg_0));
        } else {
            log.debug("CacheManager is not alive, or dark feature is not enabled; skipping statistics events");
        }
    }

    private boolean eventsEnabled() {
        boolean isEnabled = this.darkFeatureManager.getDarkFeatures().isFeatureEnabled("ehcache.statisticsEvents.enabled");
        return isEnabled;
    }

    private boolean cacheManagerIsRunning() {
        return this.cacheManager.getStatus() == Status.STATUS_ALIVE;
    }
}

