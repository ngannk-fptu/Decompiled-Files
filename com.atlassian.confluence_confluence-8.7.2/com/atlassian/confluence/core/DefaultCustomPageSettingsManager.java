/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 */
package com.atlassian.confluence.core;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;

public class DefaultCustomPageSettingsManager
implements CustomPageSettingsManager {
    private final BandanaManager bandanaManager;
    private static final String CUSTOM_PAGE_SETTINGS_KEY = "com.atlassian.core.CustomPageSettings";

    public DefaultCustomPageSettingsManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public CustomPageSettings retrieveSettings(String spaceKey) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        return this.getSettingsFromBandana(context);
    }

    @Override
    public CustomPageSettings retrieveSettings() {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext();
        return this.getSettingsFromBandana(context);
    }

    @Override
    public void saveSettings(String spaceKey, CustomPageSettings settings) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
        this.saveSettingsInBandana(context, settings);
    }

    @Override
    public void saveSettings(CustomPageSettings settings) {
        ConfluenceBandanaContext context = new ConfluenceBandanaContext();
        this.saveSettingsInBandana(context, settings);
    }

    private CustomPageSettings getSettingsFromBandana(ConfluenceBandanaContext context) {
        CustomPageSettings customPageSettings = (CustomPageSettings)this.bandanaManager.getValue((BandanaContext)context, CUSTOM_PAGE_SETTINGS_KEY, false);
        return customPageSettings == null ? CustomPageSettings.DEFAULT_SETTINGS : customPageSettings;
    }

    private void saveSettingsInBandana(ConfluenceBandanaContext context, CustomPageSettings settings) {
        this.bandanaManager.setValue((BandanaContext)context, CUSTOM_PAGE_SETTINGS_KEY, (Object)settings);
    }
}

