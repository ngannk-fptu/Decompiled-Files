/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.UpgradedFlag
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import com.atlassian.confluence.upgrade.UpgradedFlag;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BandanaSpaceSettingsManager
implements SpaceSettingsManager {
    private static final Logger log = LoggerFactory.getLogger(BandanaSpaceSettingsManager.class);
    private final BandanaManager bandanaManager;
    private final UpgradedFlag upgradedFlag;

    public BandanaSpaceSettingsManager(BandanaManager bandanaManager, UpgradedFlag upgradedFlag) {
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
        this.upgradedFlag = Objects.requireNonNull(upgradedFlag);
    }

    @Override
    public SpaceSettings getSpaceSettings(String spaceKey) {
        try {
            SpaceSettings spaceSettings = (SpaceSettings)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.space.settings");
            return spaceSettings == null ? new SpaceSettings(spaceKey) : spaceSettings;
        }
        catch (RuntimeException e) {
            if (this.upgradedFlag.isUpgraded()) {
                throw e;
            }
            log.warn("Unable to retrieve space settings for " + spaceKey + " during upgrade. Returning read-only settings object just in case", (Throwable)e);
            return SpaceSettings.unsavableSettings(spaceKey);
        }
    }

    @Override
    public void updateSpaceSettings(SpaceSettings spaceSettings) {
        if (!StringUtils.isNotEmpty((CharSequence)spaceSettings.getSpaceKey())) {
            throw new IllegalArgumentException("SpaceSettings object cannot be saved. It does not have a space key set on it.");
        }
        if (spaceSettings.isSaveable()) {
            this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceSettings.getSpaceKey()), "atlassian.confluence.space.settings", (Object)spaceSettings);
        } else {
            log.error("Unable to save temporary settings object.", (Throwable)new RuntimeException("settings object marked read-only"));
        }
    }
}

