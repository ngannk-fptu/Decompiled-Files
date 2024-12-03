/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cache.Cache
 *  com.atlassian.cache.CacheFactory
 *  com.atlassian.cache.CacheManager
 *  com.atlassian.cache.CacheSettingsBuilder
 *  com.atlassian.util.concurrent.Lazy
 *  com.atlassian.util.concurrent.Supplier
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.confluence.plugins.like.notifications.dao;

import com.atlassian.cache.Cache;
import com.atlassian.cache.CacheFactory;
import com.atlassian.cache.CacheManager;
import com.atlassian.cache.CacheSettingsBuilder;
import com.atlassian.confluence.plugins.like.notifications.LikeNotification;
import com.atlassian.confluence.plugins.like.notifications.dao.NotificationDao;
import com.atlassian.util.concurrent.Lazy;
import com.atlassian.util.concurrent.Supplier;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class CacheBackedNotificationDao
implements NotificationDao,
InitializingBean {
    private static final Logger log = LoggerFactory.getLogger(CacheBackedNotificationDao.class);
    private static final String CACHE_NAME = NotificationDao.class.getName();
    private final Supplier<Cache<String, Boolean>> cacheRef = Lazy.supplier(() -> CacheBackedNotificationDao.createCache((CacheFactory)cacheFactory));

    public CacheBackedNotificationDao(CacheManager cacheFactory) {
    }

    public void afterPropertiesSet() throws Exception {
        this.cache();
    }

    private static Cache<String, Boolean> createCache(CacheFactory cacheFactory) {
        return cacheFactory.getCache(CACHE_NAME, null, new CacheSettingsBuilder().local().expireAfterWrite(14L, TimeUnit.DAYS).build());
    }

    @Override
    public boolean exists(LikeNotification notification) {
        try {
            return Optional.ofNullable((Boolean)this.cache().get((Object)CacheBackedNotificationDao.cacheKey(notification))).orElse(false);
        }
        catch (RuntimeException ex) {
            log.warn("Failed to retrieve cache entry: {}", (Object)ex.getMessage());
            return false;
        }
    }

    @Override
    public void save(LikeNotification notification) {
        try {
            this.cache().put((Object)CacheBackedNotificationDao.cacheKey(notification), (Object)Boolean.TRUE);
        }
        catch (RuntimeException ex) {
            log.warn("Failed to put cache entry: {}", (Object)ex.getMessage());
        }
    }

    private Cache<String, Boolean> cache() {
        return (Cache)this.cacheRef.get();
    }

    private static String cacheKey(LikeNotification notification) {
        return String.format("%s-%s", notification.getRecipient().getName(), notification.getContent().getId());
    }
}

