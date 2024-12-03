/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.api.service.settings;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.settings.GlobalSettings;
import org.checkerframework.checker.nullness.qual.NonNull;

@ExperimentalApi
public interface SettingsService {
    public @NonNull GlobalSettings getGlobalSettings();
}

