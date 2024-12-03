/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.domain.JfrSettings;

public interface JfrSettingsService {
    public JfrSettings storeSettings(JfrSettings var1);

    public JfrSettings getSettings();

    public boolean isPluginSystemReady();
}

