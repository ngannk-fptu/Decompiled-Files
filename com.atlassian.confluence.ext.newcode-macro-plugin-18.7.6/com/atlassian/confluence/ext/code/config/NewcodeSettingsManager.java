/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.config;

import com.atlassian.confluence.ext.code.config.NewcodeSettings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewcodeSettingsManager {
    private final SettingsManager settingsManager;
    private final Supplier<NewcodeSettings> currentSettings;

    @Autowired
    public NewcodeSettingsManager(@ComponentImport SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
        this.currentSettings = () -> {
            Serializable settings = settingsManager.getPluginSettings("com.atlassian.confluence.ext.newcode-macro-plugin");
            NewcodeSettings result = new NewcodeSettings();
            if (settings != null && settings instanceof Map) {
                result.mapToSettings((Map)((Object)settings));
            }
            return result;
        };
    }

    public NewcodeSettings getCurrentSettings() {
        return this.currentSettings.get();
    }

    public void updateSettings(String theme, String language) {
        NewcodeSettings newSettings = new NewcodeSettings();
        newSettings.setDefaultTheme(theme);
        newSettings.setDefaultLanguage(language);
        this.settingsManager.updatePluginSettings("com.atlassian.confluence.ext.newcode-macro-plugin", (Serializable)((Object)newSettings.settingsToMap()));
    }
}

