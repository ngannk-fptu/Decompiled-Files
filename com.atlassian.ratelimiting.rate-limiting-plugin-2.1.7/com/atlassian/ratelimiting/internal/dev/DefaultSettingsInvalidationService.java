/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.ratelimiting.internal.dev;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsDevService;
import com.atlassian.pocketknife.api.querydsl.DatabaseAccessor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.dao.UserRateLimitingSettingsProvider;
import com.atlassian.ratelimiting.db.internal.dao.Tables;
import com.atlassian.ratelimiting.dev.SettingsInvalidationService;
import com.atlassian.ratelimiting.events.RateLimitingSettingsReloadedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@ExportAsDevService(value={SettingsInvalidationService.class})
@VisibleForTesting
public class DefaultSettingsInvalidationService
implements SettingsInvalidationService {
    private final DatabaseAccessor databaseAccessor;
    private final SystemPropertiesService systemPropertiesService;
    private final SystemRateLimitingSettingsProvider systemSettingsProvider;
    private final UserRateLimitingSettingsProvider userSettingsProvider;
    private final EventPublisher eventPublisher;

    @Autowired
    public DefaultSettingsInvalidationService(DatabaseAccessor databaseAccessor, SystemPropertiesService systemPropertiesService, SystemRateLimitingSettingsProvider systemSettingsProvider, UserRateLimitingSettingsProvider userSettingsProvider, EventPublisher eventPublisher) {
        this.databaseAccessor = databaseAccessor;
        this.systemPropertiesService = systemPropertiesService;
        this.systemSettingsProvider = systemSettingsProvider;
        this.userSettingsProvider = userSettingsProvider;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void resetAllSettings() {
        this.resetUserSettings();
        this.resetSystemSettings();
        this.resetHistory();
        this.reloadSettings();
    }

    private void resetUserSettings() {
        this.databaseAccessor.runInTransaction(databaseConnection -> {
            databaseConnection.delete(Tables.RL_USER_SETTINGS).execute();
            return null;
        }, OnRollback.NOOP);
    }

    private void resetSystemSettings() {
        this.databaseAccessor.runInTransaction(databaseConnection -> {
            databaseConnection.delete(Tables.RL_SYSTEM_SETTINGS).execute();
            databaseConnection.delete(Tables.SETTINGS_VERSION).execute();
            return null;
        }, OnRollback.NOOP);
        this.systemPropertiesService.initializeData();
    }

    private void resetHistory() {
        this.databaseAccessor.runInTransaction(databaseConnection -> {
            databaseConnection.delete(Tables.RL_COUNTER).execute();
            return null;
        }, OnRollback.NOOP);
    }

    @Override
    public void reloadSettings() {
        this.systemSettingsProvider.forceReloadCache();
        this.userSettingsProvider.forceReloadCache();
        this.eventPublisher.publish((Object)new RateLimitingSettingsReloadedEvent());
    }
}

