/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.ratelimiting.db.internal.cacheable.dao;

import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsDao;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingSystemRateLimitingSettingsProvider
implements SystemRateLimitingSettingsProvider {
    private static final Logger log = LoggerFactory.getLogger(CachingSystemRateLimitingSettingsProvider.class);
    static final long UNINITIALISED_SETTINGS_VERSION = 0L;
    private final SystemRateLimitingSettingsDao delegate;
    private final AtomicReference<SystemRateLimitingSettings> systemSettings = new AtomicReference<SystemRateLimitingSettings>(SystemRateLimitingSettings.RATE_LIMITING_DISABLED);
    private AtomicLong latestVersion = new AtomicLong(0L);

    public CachingSystemRateLimitingSettingsProvider(@Nonnull SystemRateLimitingSettingsDao systemRateLimitingSettingsDao) {
        this.delegate = systemRateLimitingSettingsDao;
    }

    @Override
    public SystemRateLimitingSettings getSystemSettings() {
        return this.systemSettings.get();
    }

    @Override
    public boolean tryReloadCache() {
        Long latestLocal = this.latestVersion.get();
        Instant start = Instant.now();
        Optional<Long> returnedFromDao = this.delegate.getLatestSystemSettingsVersion();
        Instant end = Instant.now();
        long fetchDuration = Duration.between(start, end).toMillis();
        Long latestRemote = returnedFromDao.orElse(latestLocal);
        if (latestRemote > latestLocal) {
            log.debug("System settings changed in the db (local version: {}, db version: {}), rate limiting mode is {}, fetch took {} ms, reloading...", new Object[]{latestLocal, latestRemote, this.systemSettings.get().getMode(), fetchDuration});
            this.systemSettings.set(this.delegate.getSystemSettings());
            this.latestVersion.compareAndSet(latestLocal, latestRemote);
            return true;
        }
        log.debug("System settings reload not necessary (localVersion: {}, db version: {}), rate limiting mode is {}, fetch took {} ms", new Object[]{latestLocal, returnedFromDao, this.systemSettings.get().getMode(), fetchDuration});
        return false;
    }

    @Override
    public void forceReloadCache() {
        Long latestRemote = this.delegate.getLatestSystemSettingsVersion().get();
        this.systemSettings.set(this.delegate.getSystemSettings());
        this.latestVersion.set(latestRemote);
    }
}

