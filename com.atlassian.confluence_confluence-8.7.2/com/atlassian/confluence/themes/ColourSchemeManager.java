/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourScheme;

public interface ColourSchemeManager {
    public ColourScheme getDefaultColourScheme();

    public ColourScheme getSpaceColourScheme(Space var1);

    public ColourScheme getSpaceColourScheme(String var1);

    public ColourScheme getGlobalColourScheme();

    public ColourScheme getThemeColourScheme();

    public BaseColourScheme getSpaceColourSchemeIsolated(String var1);

    public ColourScheme getSpaceColourSchemeCustom(String var1);

    public BaseColourScheme getGlobalColourSchemeIsolated();

    public ColourScheme getGlobalColourSchemeCustom();

    public void resetColourScheme(Space var1);

    public void saveSpaceColourScheme(Space var1, BaseColourScheme var2);

    public void saveGlobalColourScheme(BaseColourScheme var1);

    public ColourScheme getSpaceThemeColourScheme(String var1);

    public void setColourSchemeSetting(Space var1, String var2);

    public String getColourSchemeSetting(Space var1);
}

