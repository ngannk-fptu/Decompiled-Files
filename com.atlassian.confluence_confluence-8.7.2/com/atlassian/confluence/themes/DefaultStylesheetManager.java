/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  com.atlassian.event.Event
 *  com.atlassian.event.EventManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.themes;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.themes.events.StylesheetChangedEvent;
import com.atlassian.event.Event;
import com.atlassian.event.EventManager;
import org.apache.commons.lang3.StringUtils;

public class DefaultStylesheetManager
implements StylesheetManager {
    private ThemeManager themeManager;
    private BandanaManager bandanaManager;
    private EventManager eventManager;
    private SpaceManager spaceManager;

    @Override
    public String getSpaceStylesheet(String spaceKey) {
        return this.getSpaceStylesheet(spaceKey, true);
    }

    @Override
    public String getSpaceStylesheet(String spaceKey, boolean shouldLookGlobal) {
        if (!StringUtils.isEmpty((CharSequence)spaceKey) && !StringUtils.isEmpty((CharSequence)this.themeManager.getSpaceThemeKey(spaceKey))) {
            shouldLookGlobal = false;
        }
        return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.custom", shouldLookGlobal);
    }

    @Override
    public String getGlobalStylesheet() {
        return (String)this.bandanaManager.getValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.custom", false);
    }

    @Override
    public void addGlobalStylesheet(String style) {
        if (!StringUtils.isEmpty((CharSequence)style)) {
            this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.custom", (Object)style);
        } else {
            this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.custom", null);
        }
        this.eventManager.publishEvent((Event)new StylesheetChangedEvent(this, null, StylesheetChangedEvent.StylesheetChangeType.ADDED));
    }

    @Override
    public void addSpaceStylesheet(String spaceKey, String style) {
        if (this.spaceManager.getSpace(spaceKey) != null) {
            if (!StringUtils.isEmpty((CharSequence)style)) {
                this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.custom", (Object)style);
            } else {
                this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.custom", null);
            }
            this.eventManager.publishEvent((Event)new StylesheetChangedEvent(this, spaceKey, StylesheetChangedEvent.StylesheetChangeType.ADDED));
        }
    }

    @Override
    public void removeSpaceStylesheet(String spaceKey) {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.css.resource.custom", null);
        this.eventManager.publishEvent((Event)new StylesheetChangedEvent(this, spaceKey, StylesheetChangedEvent.StylesheetChangeType.REMOVED));
    }

    @Override
    public void removeGlobalStylesheet() {
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.css.resource.custom", null);
        this.eventManager.publishEvent((Event)new StylesheetChangedEvent(this, null, StylesheetChangedEvent.StylesheetChangeType.REMOVED));
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setBandanaManager(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    public void setEventManager(EventManager eventManager) {
        this.eventManager = eventManager;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }
}

