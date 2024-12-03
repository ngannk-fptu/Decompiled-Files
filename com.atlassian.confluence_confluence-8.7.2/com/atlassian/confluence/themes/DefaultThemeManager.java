/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.confluence.upgrade.UpgradeManager
 *  com.atlassian.confluence.upgrade.UpgradedFlag
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.themes;

import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.audit.AuditingContext;
import com.atlassian.confluence.impl.audit.listener.LookAndFeelAuditListener;
import com.atlassian.confluence.impl.themes.BandanaThemeKeyDao;
import com.atlassian.confluence.impl.themes.PluginThemesAccessor;
import com.atlassian.confluence.impl.themes.ThemeKeyDao;
import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.DefaultTheme;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.events.ThemeChangedEvent;
import com.atlassian.confluence.upgrade.UpgradeManager;
import com.atlassian.confluence.upgrade.UpgradedFlag;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import com.atlassian.plugin.PluginAccessor;
import java.util.List;

public class DefaultThemeManager
implements ThemeManager {
    private EventManager eventManager;
    private UpgradedFlag upgradedFlag;
    private ThemeKeyDao themeKeyDao;
    private PluginThemesAccessor pluginHelper;
    private AuditingContext auditingContext;

    @Override
    public Theme getGlobalTheme() {
        String themeKey = this.getGlobalThemeKey();
        Theme theme = this.extractTheme(themeKey);
        return theme != null ? theme : DefaultTheme.getInstance();
    }

    @Override
    public String getGlobalThemeKey() {
        if (this.upgradedFlag.isUpgraded()) {
            return this.themeKeyDao.getGlobalThemeKey().orElse(null);
        }
        return null;
    }

    @Override
    public Theme getSpaceTheme(String spaceKey) {
        String spaceThemeKey = this.getSpaceThemeKey(spaceKey);
        Theme theme = this.extractTheme(spaceThemeKey);
        return theme != null ? theme : this.getGlobalTheme();
    }

    @Override
    public String getSpaceThemeKey(String spaceKey) {
        if (this.upgradedFlag.isUpgraded()) {
            return this.themeKeyDao.getSpaceThemeKey(spaceKey).orElse(null);
        }
        return null;
    }

    @Override
    public void setGlobalTheme(String themeCompleteKey) {
        String oldThemeKey = this.getGlobalThemeKey();
        this.auditingContext.onlyAuditFor(LookAndFeelAuditListener.THEME_CHANGED_SUMMARY, () -> {
            this.themeKeyDao.setGlobalThemeKey(themeCompleteKey);
            this.eventManager.publishEvent((Event)new ThemeChangedEvent(this, null, oldThemeKey, themeCompleteKey));
        });
    }

    @Override
    public void setSpaceTheme(String spaceKey, String themeCompleteKey) {
        String oldThemeKey = this.getSpaceThemeKey(spaceKey);
        this.auditingContext.onlyAuditFor(LookAndFeelAuditListener.THEME_CHANGED_SUMMARY, () -> {
            this.themeKeyDao.setSpaceThemeKey(spaceKey, themeCompleteKey);
            this.eventManager.publishEvent((Event)new ThemeChangedEvent(this, spaceKey, oldThemeKey, themeCompleteKey));
        });
    }

    private Theme extractTheme(String themeModuleKey) {
        return this.pluginHelper.extractTheme(themeModuleKey);
    }

    @Override
    public List<ThemeModuleDescriptor> getAvailableThemeDescriptors() {
        return this.pluginHelper.getAvailableThemeDescriptors();
    }

    @Deprecated
    public void setBandanaManager(BandanaManager bandanaManager) {
        this.setThemeKeyDao(new BandanaThemeKeyDao(bandanaManager));
    }

    public void setThemeKeyDao(ThemeKeyDao themeKeyDao) {
        this.themeKeyDao = themeKeyDao;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    @Deprecated
    public void setUpgradeManager(UpgradeManager upgradeManager) {
        this.setUpgradedFlag(() -> ((UpgradeManager)upgradeManager).isUpgraded());
    }

    public void setUpgradedFlag(UpgradedFlag upgradedFlag) {
        this.upgradedFlag = upgradedFlag;
    }

    public void setAuditingContext(AuditingContext auditingContext) {
        this.auditingContext = auditingContext;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginHelper = new PluginThemesAccessor(pluginAccessor);
    }
}

