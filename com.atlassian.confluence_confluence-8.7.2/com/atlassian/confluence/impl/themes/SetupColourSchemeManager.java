/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.themes;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.AUIDefaultColorScheme;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;

public class SetupColourSchemeManager
implements ColourSchemeManager {
    private final BaseColourScheme baseColourScheme = new BaseColourScheme();

    @Override
    public ColourScheme getDefaultColourScheme() {
        return AUIDefaultColorScheme.getInstance();
    }

    @Override
    public ColourScheme getSpaceColourScheme(Space space) {
        return this.baseColourScheme;
    }

    @Override
    public ColourScheme getSpaceColourScheme(String string) {
        return this.baseColourScheme;
    }

    @Override
    public ColourScheme getGlobalColourScheme() {
        return this.baseColourScheme;
    }

    @Override
    public ColourScheme getThemeColourScheme() {
        return this.baseColourScheme;
    }

    @Override
    public BaseColourScheme getSpaceColourSchemeIsolated(String string) {
        return this.baseColourScheme;
    }

    @Override
    public BaseColourScheme getGlobalColourSchemeIsolated() {
        return this.baseColourScheme;
    }

    @Override
    public ColourScheme getSpaceColourSchemeCustom(String spaceKey) {
        return this.baseColourScheme;
    }

    @Override
    public ColourScheme getGlobalColourSchemeCustom() {
        return this.baseColourScheme;
    }

    @Override
    public void resetColourScheme(Space space) {
    }

    @Override
    public void saveSpaceColourScheme(Space space, BaseColourScheme baseColourScheme) {
    }

    @Override
    public void saveGlobalColourScheme(BaseColourScheme baseColourScheme) {
    }

    @Override
    public ColourScheme getSpaceThemeColourScheme(String string) {
        return this.baseColourScheme;
    }

    @Override
    public void setColourSchemeSetting(Space space, String string) {
    }

    @Override
    public String getColourSchemeSetting(Space space) {
        return null;
    }
}

