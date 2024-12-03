/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.GlobalDescriptionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.LegacyPluginSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import java.io.Serializable;

@Deprecated
public interface SettingsManager
extends GlobalSettingsManager,
SpaceSettingsManager,
LegacyPluginSettingsManager,
GlobalDescriptionManager {
    @Override
    public Settings getGlobalSettings();

    @Override
    public void updateGlobalSettings(Settings var1);

    @Override
    public SpaceSettings getSpaceSettings(String var1);

    @Override
    public void updateSpaceSettings(SpaceSettings var1);

    @Override
    public Serializable getPluginSettings(String var1);

    @Override
    public void updatePluginSettings(String var1, Serializable var2);

    @Override
    public GlobalDescription getGlobalDescription();

    @Override
    public void updateGlobalDescription(GlobalDescription var1);
}

