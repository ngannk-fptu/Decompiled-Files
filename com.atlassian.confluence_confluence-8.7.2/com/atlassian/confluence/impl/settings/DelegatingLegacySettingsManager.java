/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.GlobalDescriptionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.LegacyPluginSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import java.io.Serializable;

public class DelegatingLegacySettingsManager
implements SettingsManager {
    private final GlobalSettingsManager globalSettingsManager;
    private final SpaceSettingsManager spaceSettingsManager;
    private final LegacyPluginSettingsManager pluginSettingsManager;
    private final GlobalDescriptionManager globalDescriptionManager;

    public DelegatingLegacySettingsManager(GlobalSettingsManager globalSettingsManager, SpaceSettingsManager spaceSettingsManager, LegacyPluginSettingsManager pluginSettingsManager, GlobalDescriptionManager globalDescriptionManager) {
        this.globalSettingsManager = globalSettingsManager;
        this.spaceSettingsManager = spaceSettingsManager;
        this.pluginSettingsManager = pluginSettingsManager;
        this.globalDescriptionManager = globalDescriptionManager;
    }

    @Override
    public SpaceSettings getSpaceSettings(String spaceKey) {
        return this.spaceSettingsManager.getSpaceSettings(spaceKey);
    }

    @Override
    public void updateSpaceSettings(SpaceSettings spaceSettings) {
        this.spaceSettingsManager.updateSpaceSettings(spaceSettings);
    }

    @Override
    public Serializable getPluginSettings(String pluginKey) {
        return this.pluginSettingsManager.getPluginSettings(pluginKey);
    }

    @Override
    public void updatePluginSettings(String pluginKey, Serializable pluginSettings) {
        this.pluginSettingsManager.updatePluginSettings(pluginKey, pluginSettings);
    }

    @Override
    public Settings getGlobalSettings() {
        return this.globalSettingsManager.getGlobalSettings();
    }

    @Override
    public void updateGlobalSettings(Settings settings) {
        this.globalSettingsManager.updateGlobalSettings(settings);
    }

    @Override
    public GlobalDescription getGlobalDescription() {
        return this.globalDescriptionManager.getGlobalDescription();
    }

    @Override
    public void updateGlobalDescription(GlobalDescription globalDescription) {
        this.globalDescriptionManager.updateGlobalDescription(globalDescription);
    }
}

