/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.db.internal.cacheable.dao;

import com.atlassian.ratelimiting.dao.DefaultUserRateLimitSettings;
import com.atlassian.ratelimiting.dao.UserRateLimitSettingsDao;
import com.atlassian.ratelimiting.dao.UserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dmz.UserRateLimitSettings;
import com.atlassian.sal.api.user.UserKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingUserRateLimitingSettingsProvider
implements UserRateLimitingSettingsProvider {
    private static final Logger log = LoggerFactory.getLogger(CachingUserRateLimitingSettingsProvider.class);
    private volatile ConcurrentHashMap<UserKey, UserRateLimitSettings> configMap;
    private final UserRateLimitSettingsDao userRateLimitSettingsDao;
    static final long UNINITIALISED_SETTINGS_VERSION = 0L;
    private AtomicLong settingsVersion = new AtomicLong(0L);

    public CachingUserRateLimitingSettingsProvider(@Nonnull UserRateLimitSettingsDao userRateLimitSettingsDao) {
        this.userRateLimitSettingsDao = userRateLimitSettingsDao;
        this.configMap = new ConcurrentHashMap();
    }

    @Override
    public Optional<UserRateLimitSettings> get(UserKey userKey) {
        return Optional.ofNullable(this.configMap.get(userKey));
    }

    @Override
    public boolean tryReloadCache() {
        long latestLocal = this.settingsVersion.get();
        Instant start = Instant.now();
        Optional<Long> returnedFromDao = this.userRateLimitSettingsDao.getLatestUserSettingsVersion();
        Instant end = Instant.now();
        long fetchDuration = Duration.between(start, end).toMillis();
        Long latestRemote = returnedFromDao.orElse(latestLocal);
        if (latestRemote > latestLocal) {
            log.debug("User settings changed in the db (local version: {} db version: {}), fetch took {} ms, reloading...", new Object[]{latestLocal, latestRemote, fetchDuration});
            this.updateSettings();
            this.settingsVersion.compareAndSet(latestLocal, latestRemote);
            return true;
        }
        log.debug("User settings reload not necessary (local version: {}, db version: {}), fetch took {} ms", new Object[]{latestLocal, returnedFromDao, fetchDuration});
        return false;
    }

    @Override
    public void forceReloadCache() {
        Long latestRemote = this.userRateLimitSettingsDao.getLatestUserSettingsVersion().orElse(0L);
        this.updateSettings();
        this.settingsVersion.set(latestRemote);
    }

    private void updateSettings() {
        ConcurrentHashMap tempMap = new ConcurrentHashMap();
        this.userRateLimitSettingsDao.findAll().forEach(userLimit -> {
            UserRateLimitSettings cfr_ignored_0 = tempMap.put(userLimit.getUserKey(), DefaultUserRateLimitSettings.builder(userLimit).build());
        });
        this.configMap = tempMap;
        log.debug("Successfully reloaded [{}] user setting objects", (Object)this.configMap.size());
    }
}

