/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.themes.AbstractColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.PropertiesBackedColorSupport;

public class AUIDefaultColorScheme
extends AbstractColourScheme {
    private static final AUIDefaultColorScheme AUI_DEFAULT_COLOR_SCHEME = new AUIDefaultColorScheme();
    private PropertiesBackedColorSupport defaultColours = new PropertiesBackedColorSupport("aui-default-colours.properties");

    private AUIDefaultColorScheme() {
    }

    public static ColourScheme getInstance() {
        return AUI_DEFAULT_COLOR_SCHEME;
    }

    @Override
    public String get(String colourName) {
        return this.defaultColours.get(colourName);
    }
}

