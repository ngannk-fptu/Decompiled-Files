/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradedFlag
 *  com.atlassian.event.api.EventPublisher
 *  io.atlassian.fugue.Suppliers
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.impl.settings.BandanaGlobalSettingsManager;
import com.atlassian.confluence.impl.settings.BandanaLegacyPluginSettingsManager;
import com.atlassian.confluence.impl.settings.BandanaSpaceSettingsManager;
import com.atlassian.confluence.impl.settings.DefaultGlobalDescriptionManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaKeys;
import com.atlassian.confluence.setup.settings.GlobalDescription;
import com.atlassian.confluence.setup.settings.GlobalDescriptionDao;
import com.atlassian.confluence.setup.settings.GlobalDescriptionManager;
import com.atlassian.confluence.setup.settings.GlobalSettingsManager;
import com.atlassian.confluence.setup.settings.LegacyPluginSettingsManager;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.SpaceSettingsManager;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradedFlag;
import com.atlassian.event.api.EventPublisher;
import io.atlassian.fugue.Suppliers;
import java.io.Serializable;
import java.util.function.Supplier;

@Deprecated
public class DefaultSettingsManager
implements SettingsManager,
ConfluenceBandanaKeys {
    private final Supplier<GlobalSettingsManager> globalSettingsManager = Suppliers.memoize(() -> new BandanaGlobalSettingsManager(this.bandanaManager, this.upgradedFlag));
    private final Supplier<SpaceSettingsManager> spaceSettingsManager = Suppliers.memoize(() -> new BandanaSpaceSettingsManager(this.bandanaManager, this.upgradedFlag));
    private final Supplier<LegacyPluginSettingsManager> pluginSettingsManager = Suppliers.memoize(() -> new BandanaLegacyPluginSettingsManager(this.bandanaManager));
    private final Supplier<GlobalDescriptionManager> globalDescriptionManager = Suppliers.memoize(() -> new DefaultGlobalDescriptionManager(this.globalDescriptionDao, this.bandanaManager, this.eventPublisher));
    BandanaManager bandanaManager;
    EventPublisher eventPublisher;
    private UpgradedFlag upgradedFlag;
    private GlobalDescriptionDao globalDescriptionDao;

    @Override
    public Settings getGlobalSettings() {
        return this.globalSettingsManager.get().getGlobalSettings();
    }

    @Override
    public void updateGlobalSettings(Settings settings) {
        this.globalSettingsManager.get().updateGlobalSettings(settings);
    }

    @Override
    public SpaceSettings getSpaceSettings(String spaceKey) {
        return this.spaceSettingsManager.get().getSpaceSettings(spaceKey);
    }

    @Override
    public void updateSpaceSettings(SpaceSettings spaceSettings) {
        this.spaceSettingsManager.get().updateSpaceSettings(spaceSettings);
    }

    @Override
    public Serializable getPluginSettings(String pluginKey) {
        return this.pluginSettingsManager.get().getPluginSettings(pluginKey);
    }

    @Override
    public void updatePluginSettings(String pluginKey, Serializable pluginSettings) {
        this.pluginSettingsManager.get().updatePluginSettings(pluginKey, pluginSettings);
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public boolean isNofollowExternalLinks() {
        return this.getGlobalSettings().isNofollowExternalLinks();
    }

    @Deprecated
    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.setUpgradedFlag(() -> ((UpgradeManager)upgradeManager).isUpgraded());
    }

    public void setUpgradedFlag(UpgradedFlag upgradedFlag) {
        this.upgradedFlag = upgradedFlag;
    }

    @Override
    public GlobalDescription getGlobalDescription() {
        return this.globalDescriptionManager.get().getGlobalDescription();
    }

    @Override
    public void updateGlobalDescription(GlobalDescription globalDescription) {
        this.globalDescriptionManager.get().updateGlobalDescription(globalDescription);
    }

    public void setGlobalDescriptionDao(GlobalDescriptionDao globalDescriptionDao) {
        this.globalDescriptionDao = globalDescriptionDao;
    }
}

