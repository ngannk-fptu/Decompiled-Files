/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.Settings;

public interface GlobalSettingsManager {
    public Settings getGlobalSettings();

    public void updateGlobalSettings(Settings var1);
}

