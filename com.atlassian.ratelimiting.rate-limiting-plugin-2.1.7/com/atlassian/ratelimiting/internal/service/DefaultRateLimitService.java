/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.internal.service;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.ratelimiting.analytics.AnalyticsService;
import com.atlassian.ratelimiting.bucket.TokenBucket;
import com.atlassian.ratelimiting.bucket.TokenBucketFactory;
import com.atlassian.ratelimiting.cluster.ClusterEventService;
import com.atlassian.ratelimiting.events.RateLimitingSettingsReloadedEvent;
import com.atlassian.ratelimiting.internal.history.HistoryIntervalManager;
import com.atlassian.ratelimiting.internal.jmx.RateLimitStatisticsMXBean;
import com.atlassian.ratelimiting.internal.settings.RateLimitLightweightAccessService;
import com.atlassian.ratelimiting.node.RateLimitService;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.annotations.VisibleForTesting;
import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.management.JMException;
import javax.management.ObjectName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultRateLimitService
implements RateLimitService {
    @VisibleForTesting
    static final String MXBEAN_NAME = "com.atlassian.ratelimiting:name=RateLimitStatistics";
    private static final Logger logger = LoggerFactory.getLogger(DefaultRateLimitService.class);
    private final RateLimitLightweightAccessService systemPropertiesService;
    private final HistoryIntervalManager intervalManager;
    private final AtomicLong rejectCount = new AtomicLong();
    private final ConcurrentMap<UserKey, TokenBucket> buckets = new ConcurrentHashMap<UserKey, TokenBucket>();
    private final ClusterEventService clusterEventService;
    private final EventPublisher eventPublisher;
    private final TokenBucketFactory bucketFactory;
    private final AnalyticsService analyticsService;

    public DefaultRateLimitService(HistoryIntervalManager intervalManager, RateLimitLightweightAccessService systemPropertiesService, ClusterEventService clusterEventService, EventPublisher eventPublisher, TokenBucketFactory bucketFactory, AnalyticsService analyticsService) {
        this.intervalManager = intervalManager;
        this.systemPropertiesService = systemPropertiesService;
        this.clusterEventService = clusterEventService;
        this.eventPublisher = eventPublisher;
        this.bucketFactory = bucketFactory;
        this.analyticsService = analyticsService;
    }

    @PostConstruct
    public void onStart() {
        this.eventPublisher.register((Object)this);
        this.clusterEventService.registerRateLimitService(this);
        if (this.systemPropertiesService.isJmxEnabled()) {
            this.registerMxBean();
        }
    }

    @PreDestroy
    public void onStop() {
        this.eventPublisher.unregister((Object)this);
        if (this.systemPropertiesService.isJmxEnabled()) {
            this.unregisterMxBean();
        }
    }

    @Override
    public boolean reap() {
        logger.debug("Cleaning up full token buckets which users haven't accessed for a while. Number of stored buckets [{}]", (Object)this.buckets.size());
        boolean operationResult = this.buckets.values().removeIf(TokenBucket::isFull);
        logger.debug("Cleaning up full token buckets complete. Number of stored buckets [{}]", (Object)this.buckets.size());
        return operationResult;
    }

    @Override
    public Optional<TokenBucket> getBucket(UserKey userKey) {
        return Optional.ofNullable(this.buckets.get(userKey));
    }

    @Override
    public boolean tryRateLimitPreAuth(UserKey userKey) {
        boolean isUserRequestToBeRateLimitedPreAuth;
        TokenBucket tokenBucket = (TokenBucket)this.buckets.get(userKey);
        boolean bl = isUserRequestToBeRateLimitedPreAuth = tokenBucket != null && tokenBucket.getAvailableTokens() == 0L;
        if (isUserRequestToBeRateLimitedPreAuth) {
            this.onReject(userKey);
        }
        return isUserRequestToBeRateLimitedPreAuth;
    }

    @Override
    public boolean tryAcquire(UserKey userKey) {
        TokenBucket userBucket = Optional.ofNullable(this.buckets.get(userKey)).orElseGet(() -> this.bucketFactory.createTokenBucket(userKey));
        boolean acquired = userBucket.tryAcquire();
        this.buckets.putIfAbsent(userKey, userBucket);
        if (acquired) {
            this.onAcquire(userKey);
        } else {
            this.onReject(userKey);
        }
        return acquired;
    }

    @EventListener
    public void cleanUpStaleConfiguration(RateLimitingSettingsReloadedEvent event) {
        Instant start = Instant.now();
        AtomicInteger removed = new AtomicInteger(0);
        AtomicInteger processed = new AtomicInteger(0);
        this.buckets.forEach((userKey, bucket) -> {
            processed.incrementAndGet();
            if (!this.bucketFactory.hasCurrentSettings((UserKey)userKey, (TokenBucket)bucket) && this.buckets.remove(userKey, bucket)) {
                removed.incrementAndGet();
            }
        });
        Instant stop = Instant.now();
        logger.debug("Removed {} of {} processed buckets in [{}] after settings change", new Object[]{removed.get(), processed.get(), Duration.between(start, stop).toMillis()});
    }

    private void onAcquire(UserKey userKey) {
        logger.trace("User: [{}] has sufficient tokens, rate limiting will not be applied", (Object)userKey);
    }

    private void onReject(UserKey userKey) {
        logger.trace("User: [{}] was rate limited", (Object)userKey);
        this.rejectCount.incrementAndGet();
        this.analyticsService.incrementRejectCount();
        this.intervalManager.onReject(userKey);
    }

    private void registerMxBean() {
        try {
            ManagementFactory.getPlatformMBeanServer().registerMBean(new RateLimitStatisticsMXBeanAdapter(), new ObjectName(MXBEAN_NAME));
        }
        catch (RuntimeException | JMException e) {
            logger.warn("Could not register {}. Rate limiting details will not be available in JMX", (Object)RateLimitStatisticsMXBean.class.getName(), (Object)e);
        }
    }

    private void unregisterMxBean() {
        try {
            ManagementFactory.getPlatformMBeanServer().unregisterMBean(new ObjectName(MXBEAN_NAME));
        }
        catch (RuntimeException | JMException e) {
            logger.warn("Failed to unregister {}", (Object)RateLimitStatisticsMXBean.class.getName(), (Object)e);
        }
    }

    private class RateLimitStatisticsMXBeanAdapter
    implements RateLimitStatisticsMXBean {
        private RateLimitStatisticsMXBeanAdapter() {
        }

        @Override
        public long getRejectedRequestCount() {
            return DefaultRateLimitService.this.rejectCount.get();
        }

        @Override
        public int getUserMapSize() {
            return DefaultRateLimitService.this.buckets.size();
        }
    }
}

