/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.AmpsOverridesManager
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.upgrade.impl;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.upgrade.AmpsOverridesManager;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAmpsOverridesManager
implements AmpsOverridesManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultAmpsOverridesManager.class);
    private final String ampsBaseUrl = System.getProperty("baseurl");
    private final String ampsBandanaKey = DefaultAmpsOverridesManager.class.getName();
    private final BandanaContext ampsBandanaContext = new ConfluenceBandanaContext(this.ampsBandanaKey);
    private final BandanaManager bandanaManager;
    private final SettingsManager settingsManager;

    public DefaultAmpsOverridesManager(BandanaManager bandanaManager, SettingsManager settingsManager) {
        this.bandanaManager = (BandanaManager)Preconditions.checkNotNull((Object)bandanaManager);
        this.settingsManager = (SettingsManager)Preconditions.checkNotNull((Object)settingsManager);
    }

    public void doOverride() {
        if (this.bandanaManager.getValue(this.ampsBandanaContext, this.ampsBandanaKey) == null && this.ampsBaseUrl != null) {
            Settings settings = this.settingsManager.getGlobalSettings();
            settings.setBaseUrl(this.ampsBaseUrl);
            this.settingsManager.updateGlobalSettings(settings);
            log.warn("baseurl has been updated from {} to {}", (Object)settings.getBaseUrl(), (Object)this.ampsBaseUrl);
            this.bandanaManager.setValue(this.ampsBandanaContext, this.ampsBandanaKey, (Object)Boolean.TRUE);
        }
    }
}

