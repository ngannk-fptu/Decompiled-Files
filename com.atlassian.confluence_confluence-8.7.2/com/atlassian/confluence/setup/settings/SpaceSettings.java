/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.setup.settings;

import com.atlassian.confluence.setup.settings.beans.ColourSchemesSettings;
import java.io.Serializable;

public class SpaceSettings
implements Serializable {
    private String spaceKey;
    private boolean disableLogo = false;
    private ColourSchemesSettings colourSchemesSettings;
    private boolean doNotSave;

    public static SpaceSettings unsavableSettings(String spaceKey) {
        SpaceSettings settings = new SpaceSettings(spaceKey);
        settings.doNotSave = true;
        return settings;
    }

    public SpaceSettings() {
    }

    public SpaceSettings(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public boolean isDisableLogo() {
        return this.disableLogo;
    }

    public void setDisableLogo(boolean disableLogo) {
        this.disableLogo = disableLogo;
    }

    public ColourSchemesSettings getColourSchemesSettings() {
        if (this.colourSchemesSettings == null) {
            this.colourSchemesSettings = new ColourSchemesSettings("global");
        }
        return this.colourSchemesSettings;
    }

    public void setColourSchemesSettings(ColourSchemesSettings colourSchemesSettings) {
        this.colourSchemesSettings = colourSchemesSettings;
    }

    public boolean isSaveable() {
        return !this.doNotSave;
    }
}

