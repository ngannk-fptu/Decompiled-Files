/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.events.MauEvent$Builder
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.confluence.api.model.event.mau.MauApplicationKey
 *  com.atlassian.confluence.api.service.event.mau.MauEventService
 *  com.atlassian.event.api.EventPublisher
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Sets
 *  org.checkerframework.checker.nullness.qual.Nullable
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.api.impl.service.event.mau;

import com.atlassian.analytics.api.events.MauEvent;
import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.api.model.event.mau.MauApplicationKey;
import com.atlassian.confluence.api.service.event.mau.MauEventService;
import com.atlassian.confluence.cache.CoreCache;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Sets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MauEventServiceImpl
implements MauEventService {
    private static final Duration DEFAULT_INTERVAL = Duration.ofMinutes(60L);
    private static final Duration INTERVAL = Duration.ofMinutes(Long.getLong("confluence.mau.event.interval.minutes", DEFAULT_INTERVAL.toMinutes()));
    private static final Logger log = LoggerFactory.getLogger(MauEventServiceImpl.class);
    private final EventPublisher eventPublisher;
    private final Clock clock;
    private final PermissionManager permissionManager;
    private final ConfluenceAccessManager accessManager;
    private final Cache<String, Instant> lastSentCache;
    private final ThreadLocal<Set<MauApplicationKey>> perRequestApplications = ThreadLocal.withInitial(() -> Sets.newHashSet((Object[])new MauApplicationKey[]{MauApplicationKey.APP_CONFLUENCE}));

    public MauEventServiceImpl(EventPublisher eventPublisher, Clock clock, CacheFactory cacheFactory, PermissionManager permissionManager, ConfluenceAccessManager accessManager) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.clock = Objects.requireNonNull(clock);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.accessManager = Objects.requireNonNull(accessManager);
        this.lastSentCache = CoreCache.MAU_LAST_SENT_TIME_BY_USER.resolve(cacheName -> cacheFactory.getCache(cacheName, null, new CacheSettingsBuilder().expireAfterWrite(DEFAULT_INTERVAL.toMillis(), TimeUnit.MILLISECONDS).build()));
    }

    public void addApplicationActivity(MauApplicationKey appKey) {
        this.perRequestApplications.get().add(appKey);
    }

    public void clearApplicationActivities() {
        this.perRequestApplications.remove();
    }

    public void sendMauEvents() {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (MauEventServiceImpl.isAnonymous(user) || !this.accessManager.getUserAccessStatus(user).canUseConfluence() || this.permissionManager.isSystemAdministrator(user)) {
            return;
        }
        Instant currentTime = this.clock.instant();
        for (MauApplicationKey appKey : this.getMauProducts()) {
            String cacheKey = MauEventServiceImpl.cacheKey(user, appKey);
            Instant lastSentTime = (Instant)this.lastSentCache.get((Object)cacheKey);
            if (!ConfluenceSystemProperties.isDevMode() && lastSentTime != null && !currentTime.isAfter(lastSentTime.plus(INTERVAL))) continue;
            this.lastSentCache.put((Object)cacheKey, (Object)currentTime);
            this.eventPublisher.publish((Object)new MauEvent.Builder().application(appKey.getKey()).build(Optional.ofNullable(user.getEmail()).orElse("NONE")));
            log.debug("Sent MAU event for product: {}", (Object)appKey.getKey());
        }
    }

    @VisibleForTesting
    Set<MauApplicationKey> getMauProducts() {
        return this.perRequestApplications.get();
    }

    private static boolean isAnonymous(@Nullable ConfluenceUser user) {
        return user == null;
    }

    private static String cacheKey(ConfluenceUser user, MauApplicationKey applicationKey) {
        return String.format("%s-%s", user.getKey().getStringValue(), applicationKey.getKey());
    }
}

