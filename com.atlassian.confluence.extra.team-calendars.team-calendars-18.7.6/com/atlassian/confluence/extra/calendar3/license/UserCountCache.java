/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.cache.CachedReference
 *  com.atlassian.confluence.event.events.user.UserCreateEvent
 *  com.atlassian.confluence.event.events.user.UserDeactivateEvent
 *  com.atlassian.confluence.event.events.user.UserReactivateEvent
 *  com.atlassian.confluence.util.UserChecker
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.extra.calendar3.license;

import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.cache.CachedReference;
import com.atlassian.confluence.event.events.user.UserCreateEvent;
import com.atlassian.confluence.event.events.user.UserDeactivateEvent;
import com.atlassian.confluence.event.events.user.UserReactivateEvent;
import com.atlassian.confluence.util.UserChecker;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class UserCountCache
implements InitializingBean,
DisposableBean {
    private static final String CACHE_NAME = UserCountCache.class.getName() + ":license";
    private final EventPublisher eventPublisher;
    private final UserChecker userChecker;
    private final CachedReference<Integer> activeUserCountCache;

    public UserCountCache(CacheManager cacheManager, EventPublisher eventPublisher, UserChecker userChecker) {
        this.eventPublisher = eventPublisher;
        this.userChecker = userChecker;
        this.activeUserCountCache = cacheManager.getCachedReference(CACHE_NAME, this::getActiveUserCountFromConfluence, new CacheSettingsBuilder().remote().replicateViaInvalidation().replicateAsynchronously().build());
    }

    public int getActiveUserCount() {
        return (Integer)this.activeUserCountCache.get();
    }

    public void afterPropertiesSet() {
        this.eventPublisher.register((Object)this);
    }

    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void handleUserCreatedEvent(UserCreateEvent userCreateEvent) {
        this.activeUserCountCache.reset();
    }

    @EventListener
    public void handleUserReactivatedEvent(UserReactivateEvent userReactivateEvent) {
        this.activeUserCountCache.reset();
    }

    @EventListener
    public void handleUserDeactivatedEvent(UserDeactivateEvent userDeactivateEvent) {
        this.activeUserCountCache.reset();
    }

    private int getActiveUserCountFromConfluence() {
        return this.userChecker.getNumberOfRegisteredUsers();
    }
}

