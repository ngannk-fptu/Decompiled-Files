/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.cache.Cache
 *  com.google.common.cache.CacheBuilder
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service;

import com.addonengine.addons.analytics.service.EstimationService;
import com.addonengine.addons.analytics.service.ReportTimingEstimation;
import com.addonengine.addons.analytics.store.EventRepository;
import com.addonengine.addons.analytics.store.server.TimedEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u0010\u000f\u001a\u00020\nH\u0002J\u0018\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0013H\u0016R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0018\u0010\u000b\u001a\n \r*\u0004\u0018\u00010\f0\fX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u000e\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/service/EstimationServiceImpl;", "Lcom/addonengine/addons/analytics/service/EstimationService;", "eventRepository", "Lcom/addonengine/addons/analytics/store/EventRepository;", "(Lcom/addonengine/addons/analytics/store/EventRepository;)V", "CACHE_KEY", "Ljava/lang/Object;", "densityCache", "Lcom/google/common/cache/Cache;", "", "", "slownessThreshold", "", "kotlin.jvm.PlatformType", "Ljava/lang/Long;", "computeDensityFromDatabase", "estimateReportTiming", "Lcom/addonengine/addons/analytics/service/ReportTimingEstimation;", "from", "Ljava/time/OffsetDateTime;", "to", "analytics"})
public final class EstimationServiceImpl
implements EstimationService {
    @NotNull
    private final EventRepository eventRepository;
    private final Long slownessThreshold;
    @NotNull
    private final Object CACHE_KEY;
    @NotNull
    private final Cache<Object, Double> densityCache;

    @Autowired
    public EstimationServiceImpl(@NotNull EventRepository eventRepository) {
        Intrinsics.checkNotNullParameter((Object)eventRepository, (String)"eventRepository");
        this.eventRepository = eventRepository;
        this.slownessThreshold = Long.getLong("confluence.analytics.slow.report.estimation.threshold", 5000000L);
        this.CACHE_KEY = new Object();
        Cache cache = CacheBuilder.newBuilder().expireAfterWrite(1L, TimeUnit.HOURS).build();
        Intrinsics.checkNotNullExpressionValue((Object)cache, (String)"build(...)");
        this.densityCache = cache;
    }

    @Override
    @NotNull
    public ReportTimingEstimation estimateReportTiming(@NotNull OffsetDateTime from, @NotNull OffsetDateTime to) {
        Intrinsics.checkNotNullParameter((Object)from, (String)"from");
        Intrinsics.checkNotNullParameter((Object)to, (String)"to");
        if (from.isAfter(to)) {
            Long l = this.slownessThreshold;
            Intrinsics.checkNotNullExpressionValue((Object)l, (String)"slownessThreshold");
            return new ReportTimingEstimation(0L, ((Number)l).longValue());
        }
        long requestedTimeSpanMs = Duration.between(from, to).toMillis();
        Double cachedDensity = (Double)this.densityCache.getIfPresent(this.CACHE_KEY);
        if (cachedDensity != null) {
            long estimation = Math.round(cachedDensity * (double)requestedTimeSpanMs);
            Long l = this.slownessThreshold;
            Intrinsics.checkNotNullExpressionValue((Object)l, (String)"slownessThreshold");
            return new ReportTimingEstimation(estimation, ((Number)l).longValue());
        }
        double computedDensity = this.computeDensityFromDatabase();
        this.densityCache.put(this.CACHE_KEY, (Object)computedDensity);
        long estimation = Math.round(computedDensity * (double)requestedTimeSpanMs);
        Long l = this.slownessThreshold;
        Intrinsics.checkNotNullExpressionValue((Object)l, (String)"slownessThreshold");
        return new ReportTimingEstimation(estimation, ((Number)l).longValue());
    }

    private final double computeDensityFromDatabase() {
        TimedEvent earliestEvent = this.eventRepository.getEarliestEvent();
        TimedEvent latestEvent = this.eventRepository.getLatestEvent();
        if (earliestEvent == null || latestEvent == null) {
            return 0.0;
        }
        long timeDifferenceMs = latestEvent.getTimestamp() - earliestEvent.getTimestamp();
        long idDifference = latestEvent.getId() - earliestEvent.getId();
        if (timeDifferenceMs <= 0L || idDifference <= 0L) {
            return 0.0;
        }
        return (double)idDifference / (double)timeDifferenceMs;
    }
}

