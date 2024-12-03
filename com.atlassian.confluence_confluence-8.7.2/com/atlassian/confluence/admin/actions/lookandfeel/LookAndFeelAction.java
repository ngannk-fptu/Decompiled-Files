/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.admin.actions.lookandfeel.AbstractLookAndFeelAction;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import java.util.List;

public class LookAndFeelAction
extends AbstractLookAndFeelAction
implements LookAndFeel {
    private BaseColourScheme editableColourScheme;

    public List<String> getColourKeys() {
        return ColourScheme.ORDERED_KEYS;
    }

    public String getColour(ColourScheme scheme, String key) {
        if (scheme != null) {
            return scheme.get(key);
        }
        return "";
    }

    protected ColourScheme getColourScheme() {
        if (this.getSpace() != null) {
            return this.colourSchemeManager.getSpaceColourScheme(this.getSpace().getKey());
        }
        return this.colourSchemeManager.getGlobalColourScheme();
    }

    public ColourScheme getCustomColourScheme() {
        if (this.getSpace() != null) {
            return this.colourSchemeManager.getSpaceColourSchemeCustom(this.getSpace().getKey());
        }
        return this.colourSchemeManager.getGlobalColourSchemeCustom();
    }

    public ColourScheme getGlobalColourScheme() {
        return this.colourSchemeManager.getGlobalColourScheme();
    }

    public ColourScheme getThemeColourScheme() {
        if (this.getSpace() != null) {
            return this.colourSchemeManager.getSpaceThemeColourScheme(this.getSpace().getKey());
        }
        return this.colourSchemeManager.getThemeColourScheme();
    }

    protected BaseColourScheme getEditableColourScheme() {
        if (this.editableColourScheme == null) {
            this.editableColourScheme = this.getSpace() != null ? this.colourSchemeManager.getSpaceColourSchemeIsolated(this.getSpace().getKey()) : this.colourSchemeManager.getGlobalColourSchemeIsolated();
        }
        return this.editableColourScheme;
    }

    public boolean isDefault(String key) {
        return this.getEditableColourScheme().get(key) == null;
    }

    public String getColourSchemeType() {
        return this.colourSchemeManager.getColourSchemeSetting(this.getSpace());
    }
}

