/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.UpgradedFlag
 *  com.atlassian.johnson.Johnson
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.CannotCreateTransactionException
 */
package com.atlassian.confluence.impl.settings;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.cache.ThreadLocalCacheAccessor;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.upgrade.UpgradedFlag;
import com.atlassian.johnson.Johnson;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.CannotCreateTransactionException;

public class BandanaGlobalSettingsManager
implements GlobalSettingsManager {
    private static final ThreadLocalCacheAccessor<Keys, Settings> CACHE_ACCESSOR = ThreadLocalCacheAccessor.newInstance();
    private static final Logger log = LoggerFactory.getLogger(BandanaGlobalSettingsManager.class);
    private final BandanaManager bandanaManager;
    private final UpgradedFlag upgradedFlag;

    public BandanaGlobalSettingsManager(BandanaManager bandanaManager, UpgradedFlag upgradedFlag) {
        this.bandanaManager = Objects.requireNonNull(bandanaManager);
        this.upgradedFlag = Objects.requireNonNull(upgradedFlag);
    }

    @Override
    public Settings getGlobalSettings() {
        try {
            Settings settings = null;
            if (CACHE_ACCESSOR.isInit()) {
                settings = CACHE_ACCESSOR.get(Keys.GLOBAL_SETTINGS);
            }
            if (null == settings) {
                settings = (Settings)this.bandanaManager.getValue((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, "atlassian.confluence.settings");
                if (null == settings) {
                    settings = new Settings();
                }
                if (CACHE_ACCESSOR.isInit()) {
                    CACHE_ACCESSOR.put(Keys.GLOBAL_SETTINGS, settings);
                }
            }
            return settings;
        }
        catch (RuntimeException e) {
            if (e.getCause() instanceof CannotCreateTransactionException && Johnson.getEventContainer().hasEvents()) {
                return Settings.unsavableSettings();
            }
            if (this.upgradedFlag.isUpgraded()) {
                throw e;
            }
            log.warn("Unable to retrieve settings object during upgrade. Returning read-only settings object just in case", (Throwable)e);
            return Settings.unsavableSettings();
        }
    }

    @Override
    public void updateGlobalSettings(Settings settings) {
        if (settings.isSaveable()) {
            this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.settings", (Object)settings);
            CACHE_ACCESSOR.flush();
        } else {
            log.error("Unable to save temporary settings object.", (Throwable)new RuntimeException("settings object marked read-only"));
        }
    }

    private static enum Keys {
        GLOBAL_SETTINGS;

    }
}

