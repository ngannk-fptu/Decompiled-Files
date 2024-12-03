/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.themes;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.settings.Settings;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.setup.settings.SpaceSettings;
import com.atlassian.confluence.setup.settings.beans.ColourSchemesSettings;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.AUIDefaultColorScheme;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ChainedColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.events.ColourSchemeChangedEvent;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultColourSchemeManager
implements ColourSchemeManager {
    private static final Logger log = LoggerFactory.getLogger(DefaultColourSchemeManager.class);
    private BandanaManager bandanaManager;
    private SettingsManager settingsManager;
    private ThemeManager themeManager;
    private EventManager eventManager;

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    @Override
    public ColourScheme getDefaultColourScheme() {
        return AUIDefaultColorScheme.getInstance();
    }

    @Override
    public ColourScheme getSpaceColourScheme(Space space) {
        if (space == null) {
            log.error("Trying to retrieve a space colour scheme for a null space.");
            return null;
        }
        return this.getSpaceColourScheme(space.getKey());
    }

    @Override
    public ColourScheme getSpaceColourScheme(String spaceKey) {
        if (!StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            log.error("Trying to retrieve a space colour scheme for a null space.");
            return null;
        }
        ArrayList<ColourScheme> themeList = new ArrayList<ColourScheme>();
        themeList.add(this.getSelectedScheme(spaceKey));
        themeList.add(this.getSelectedScheme(null));
        themeList.add(this.getDefaultColourScheme());
        return new ChainedColourScheme(themeList);
    }

    @Override
    public ColourScheme getGlobalColourScheme() {
        ArrayList<ColourScheme> themeList = new ArrayList<ColourScheme>();
        themeList.add(this.getSelectedScheme(null));
        themeList.add(this.getDefaultColourScheme());
        return new ChainedColourScheme(themeList);
    }

    private ColourScheme getSelectedScheme(String spaceKey) {
        ColourSchemesSettings schemeSettings = this.getColourSchemesSettings(spaceKey);
        if (schemeSettings.getColourSchemeType().equals("theme")) {
            Theme theme;
            Theme theme2 = theme = StringUtils.isBlank((CharSequence)spaceKey) ? this.themeManager.getGlobalTheme() : this.themeManager.getSpaceTheme(spaceKey);
            if (theme != null && theme.getColourScheme() != null) {
                return theme.getColourScheme();
            }
        } else {
            if (schemeSettings.getColourSchemeType().equals("custom")) {
                if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
                    return this.getSpaceColourSchemeIsolated(spaceKey);
                }
                return this.getGlobalColourSchemeIsolated();
            }
            if (schemeSettings.getColourSchemeType().equals("global") && spaceKey != null) {
                return this.getSelectedScheme(null);
            }
        }
        return null;
    }

    private ColourSchemesSettings getColourSchemesSettings(String spaceKey) {
        if (StringUtils.isNotEmpty((CharSequence)spaceKey)) {
            return this.settingsManager.getSpaceSettings(spaceKey).getColourSchemesSettings();
        }
        return this.settingsManager.getGlobalSettings().getColourSchemesSettings();
    }

    @Override
    public ColourScheme getThemeColourScheme() {
        Theme theme = this.themeManager.getGlobalTheme();
        if (theme == null) {
            return null;
        }
        return theme.getColourScheme();
    }

    @Override
    public ColourScheme getSpaceThemeColourScheme(String spaceKey) {
        Theme theme = this.themeManager.getSpaceTheme(spaceKey);
        if (theme == null) {
            return null;
        }
        return theme.getColourScheme();
    }

    @Override
    public BaseColourScheme getSpaceColourSchemeIsolated(String spaceKey) {
        BaseColourScheme colourScheme = (BaseColourScheme)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.colour.scheme", false);
        if (colourScheme == null) {
            colourScheme = new BaseColourScheme(this.getDefaultColourScheme());
        }
        return colourScheme;
    }

    @Override
    public ColourScheme getSpaceColourSchemeCustom(String spaceKey) {
        BaseColourScheme colourScheme = (BaseColourScheme)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.colour.scheme", false);
        if (colourScheme == null) {
            colourScheme = new BaseColourScheme(this.getDefaultColourScheme());
        }
        ArrayList<ColourScheme> themeList = new ArrayList<ColourScheme>();
        themeList.add(colourScheme);
        themeList.add(this.getSelectedScheme(null));
        themeList.add(this.getDefaultColourScheme());
        return new ChainedColourScheme(themeList);
    }

    @Override
    public BaseColourScheme getGlobalColourSchemeIsolated() {
        BaseColourScheme colourScheme = (BaseColourScheme)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.colour.scheme");
        if (colourScheme == null) {
            colourScheme = new BaseColourScheme(this.getDefaultColourScheme());
        }
        return colourScheme;
    }

    @Override
    public ColourScheme getGlobalColourSchemeCustom() {
        BaseColourScheme colourScheme = (BaseColourScheme)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.colour.scheme");
        if (colourScheme == null) {
            colourScheme = new BaseColourScheme(this.getDefaultColourScheme());
        }
        ArrayList<ColourScheme> themeList = new ArrayList<ColourScheme>();
        themeList.add(colourScheme);
        themeList.add(this.getSelectedScheme(null));
        themeList.add(this.getDefaultColourScheme());
        return new ChainedColourScheme(themeList);
    }

    @Override
    public void resetColourScheme(Space space) {
        if (space == null) {
            this.saveGlobalColourScheme(null);
        } else {
            this.saveSpaceColourScheme(space, null);
        }
    }

    @Override
    public synchronized void saveSpaceColourScheme(Space space, BaseColourScheme colourScheme) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(space), "atlassian.confluence.colour.scheme", (Object)colourScheme);
        this.eventManager.publishEvent((Event)new ColourSchemeChangedEvent(this, true, space.getKey()));
    }

    @Override
    public synchronized void saveGlobalColourScheme(BaseColourScheme colourScheme) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.colour.scheme", (Object)colourScheme);
        this.eventManager.publishEvent((Event)new ColourSchemeChangedEvent(this, true, null));
    }

    @Override
    public void setColourSchemeSetting(Space space, String value) {
        if (space == null) {
            Settings settings = this.settingsManager.getGlobalSettings();
            ColourSchemesSettings colourSchemesSettings = settings.getColourSchemesSettings();
            String oldSchemeType = colourSchemesSettings.getColourSchemeType();
            colourSchemesSettings.setColourSchemeType(value);
            this.settingsManager.updateGlobalSettings(settings);
            this.eventManager.publishEvent((Event)new ColourSchemeChangedEvent(this, oldSchemeType, value, null));
        } else {
            SpaceSettings settings = this.settingsManager.getSpaceSettings(space.getKey());
            ColourSchemesSettings colourSchemesSettings = settings.getColourSchemesSettings();
            String oldSchemeType = colourSchemesSettings.getColourSchemeType();
            colourSchemesSettings.setColourSchemeType(value);
            this.settingsManager.updateSpaceSettings(settings);
            this.eventManager.publishEvent((Event)new ColourSchemeChangedEvent(this, oldSchemeType, value, space.getKey()));
        }
    }

    @Override
    public String getColourSchemeSetting(Space space) {
        if (space == null) {
            return this.settingsManager.getGlobalSettings().getColourSchemesSettings().getColourSchemeType();
        }
        return this.settingsManager.getSpaceSettings(space.getKey()).getColourSchemesSettings().getColourSchemeType();
    }

    public SettingsManager getSettingsManager() {
        return this.settingsManager;
    }

    public void setSettingsManager(SettingsManager settingsManager) {
        this.settingsManager = settingsManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }
}

