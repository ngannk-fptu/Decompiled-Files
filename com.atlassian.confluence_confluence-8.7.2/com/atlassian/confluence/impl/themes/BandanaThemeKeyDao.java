/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaManager
 *  javax.annotation.Nullable
 */
package com.atlassian.confluence.impl.themes;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaManager;
import com.atlassian.confluence.impl.themes.ThemeKeyDao;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;

public class BandanaThemeKeyDao
implements ThemeKeyDao {
    private static final String THEME_KEY = "theme.key";
    private final BandanaManager bandanaManager;

    public BandanaThemeKeyDao(BandanaManager bandanaManager) {
        this.bandanaManager = bandanaManager;
    }

    @Override
    public Optional<String> getGlobalThemeKey() {
        Map<String, String> themeSettings = this.getThemeSettings(new ConfluenceBandanaContext());
        return Optional.ofNullable(themeSettings.get(THEME_KEY));
    }

    @Override
    public void setGlobalThemeKey(String themeCompleteKey) {
        Map<String, String> themeSettings = this.getThemeSettings(new ConfluenceBandanaContext());
        themeSettings.put(THEME_KEY, themeCompleteKey);
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(), "atlassian.confluence.theme.settings", themeSettings);
    }

    @Override
    public Optional<String> getSpaceThemeKey(@Nullable String spaceKey) {
        Map<String, String> themeSettings = this.getThemeSettings(new ConfluenceBandanaContext(spaceKey));
        return Optional.ofNullable(themeSettings.get(THEME_KEY));
    }

    @Override
    public void setSpaceThemeKey(String spaceKey, String themeKey) {
        Map<String, String> themeSettings = this.getThemeSettings(new ConfluenceBandanaContext(spaceKey));
        themeSettings.put(THEME_KEY, themeKey);
        this.bandanaManager.setValue((BandanaContext)new ConfluenceBandanaContext(spaceKey), "atlassian.confluence.theme.settings", themeSettings);
    }

    private Map<String, String> getThemeSettings(BandanaContext bandanaContext) {
        HashMap themeSettings = (HashMap)this.bandanaManager.getValue(bandanaContext, "atlassian.confluence.theme.settings", false);
        if (themeSettings == null) {
            themeSettings = new HashMap(2);
        }
        return themeSettings;
    }
}

