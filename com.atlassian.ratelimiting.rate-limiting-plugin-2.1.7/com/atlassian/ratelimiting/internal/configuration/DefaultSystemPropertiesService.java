/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.internal.configuration;

import com.atlassian.ratelimiting.configuration.SystemPropertiesService;
import com.atlassian.ratelimiting.dao.SystemRateLimitingSettingsDao;
import com.atlassian.ratelimiting.dmz.RateLimitingMode;
import com.atlassian.ratelimiting.dmz.SystemJobControlSettings;
import com.atlassian.ratelimiting.dmz.SystemRateLimitingSettings;
import com.atlassian.ratelimiting.dmz.TokenBucketSettings;

public class DefaultSystemPropertiesService
implements SystemPropertiesService {
    private final SystemRateLimitingSettingsDao systemRateLimitingSettingsDao;
    private final SystemRateLimitingSettings initialSystemRLSettings;
    private final boolean isJmxEnabled;

    public DefaultSystemPropertiesService(SystemRateLimitingSettingsDao systemRateLimitingSettingsDao, SystemRateLimitingSettings initialSystemRLSettings, boolean isJmxEnabled) {
        this.systemRateLimitingSettingsDao = systemRateLimitingSettingsDao;
        this.initialSystemRLSettings = initialSystemRLSettings;
        this.isJmxEnabled = isJmxEnabled;
    }

    @Override
    public RateLimitingMode getRateLimitingMode() {
        return this.getSystemSettings().getMode();
    }

    @Override
    public TokenBucketSettings getDefaultRateLimitSettings() {
        return this.getSystemSettings().getBucketSettings();
    }

    @Override
    public void updateSystemRateLimitSettings(SystemRateLimitingSettings defaultRateLimitSettings) {
        SystemRateLimitingSettings dbSystemSettings = this.getSystemSettings();
        dbSystemSettings.setMode(defaultRateLimitSettings.getMode());
        dbSystemSettings.setBucketSettings(defaultRateLimitSettings.getBucketSettings());
        this.systemRateLimitingSettingsDao.saveOrUpdate(dbSystemSettings);
    }

    @Override
    public boolean isJmxEnabled() {
        return this.isJmxEnabled;
    }

    @Override
    public SystemRateLimitingSettings getSystemSettings() {
        return this.systemRateLimitingSettingsDao.getSystemSettings();
    }

    @Override
    public SystemJobControlSettings updateSystemJobControlSettings(SystemJobControlSettings systemJobControlSettings) {
        SystemRateLimitingSettings currentSystemSettings = this.getSystemSettings();
        currentSystemSettings.setJobControlSettings(systemJobControlSettings);
        SystemRateLimitingSettings updatedSettings = this.systemRateLimitingSettingsDao.saveOrUpdate(currentSystemSettings);
        return updatedSettings.getJobControlSettings();
    }

    @Override
    public void initializeData() {
        this.systemRateLimitingSettingsDao.initializeDbIfNeeded(this.initialSystemRLSettings);
    }
}

