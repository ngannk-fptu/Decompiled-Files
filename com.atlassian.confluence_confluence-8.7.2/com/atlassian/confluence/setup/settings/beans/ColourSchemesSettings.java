/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings.beans;

import java.io.Serializable;

public class ColourSchemesSettings
implements Serializable {
    private static final long serialVersionUID = 7032459203241944030L;
    public static final String GLOBAL = "global";
    public static final String THEME = "theme";
    public static final String CUSTOM = "custom";
    String colourSchemeType;

    @Deprecated
    public ColourSchemesSettings() {
        this.colourSchemeType = GLOBAL;
    }

    public ColourSchemesSettings(String colourSchemeType) {
        this.colourSchemeType = colourSchemeType;
    }

    public ColourSchemesSettings(ColourSchemesSettings settings) {
        this.colourSchemeType = settings.getColourSchemeType();
    }

    public String getColourSchemeType() {
        return this.colourSchemeType;
    }

    public void setColourSchemeType(String colourSchemeType) {
        this.colourSchemeType = colourSchemeType;
    }
}

