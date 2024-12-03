/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.settings.GlobalSettings
 *  com.atlassian.confluence.api.service.settings.SettingsService
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.impl.service.settings;

import com.atlassian.confluence.api.model.settings.GlobalSettings;
import com.atlassian.confluence.api.service.settings.SettingsService;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SettingsServiceImpl
implements SettingsService {
    private final SettingsManager settingsManager;

    public SettingsServiceImpl(SettingsManager settingsManager) {
        this.settingsManager = Objects.requireNonNull(settingsManager);
    }

    public @NonNull GlobalSettings getGlobalSettings() {
        Settings settings = this.settingsManager.getGlobalSettings();
        return GlobalSettings.builder().attachmentMaxSizeBytes(settings.getAttachmentMaxSize()).defaultTimezoneId(settings.getDefaultTimezoneId()).globalDefaultLocale(settings.getGlobalDefaultLocale()).baseUrl(settings.getBaseUrl()).build();
    }
}

