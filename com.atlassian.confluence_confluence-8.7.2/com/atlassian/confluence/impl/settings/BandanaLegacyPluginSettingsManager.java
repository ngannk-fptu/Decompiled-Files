/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.LegacyPluginSettingsManager;
import java.io.Serializable;
import java.util.Objects;

public class BandanaLegacyPluginSettingsManager
implements LegacyPluginSettingsManager {
    private final BandanaManager bandanaManager;

    public BandanaLegacyPluginSettingsManager(BandanaManager bandanaManager) {
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
    }

    @Override
    public Serializable getPluginSettings(String pluginKey) {
        return (Serializable)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "plugin.settings." + pluginKey);
    }

    @Override
    public void updatePluginSettings(String pluginKey, Serializable pluginSettings) {
        this.bandanaManager.setValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "plugin.settings." + pluginKey, (Object)pluginSettings);
    }
}

