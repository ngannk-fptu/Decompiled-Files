/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAdministrative;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.themes.DefaultTheme;
import com.atlassian.confluence.util.LayoutHelper;

public abstract class AbstractLookAndFeelAction
extends AbstractSpaceAction
implements SpaceAdministrative {
    protected LayoutHelper layoutHelper;
    protected ColourSchemeManager colourSchemeManager;

    @Override
    public boolean isPermitted() {
        if (!this.getBootstrapStatusProvider().isSetupComplete()) {
            return true;
        }
        return super.isPermitted();
    }

    public boolean isThemeSet() {
        return this.isCustomeThemeSet();
    }

    public boolean isCustomeThemeSet() {
        if (this.getSpace() != null) {
            return this.themeManager.getSpaceTheme(this.getSpace().getKey()) != DefaultTheme.getInstance();
        }
        return this.themeManager.getGlobalTheme() != DefaultTheme.getInstance();
    }

    public LayoutHelper getLayoutHelper() {
        return this.layoutHelper;
    }

    public void setLayoutHelper(LayoutHelper layoutHelper) {
        this.layoutHelper = layoutHelper;
    }

    public ColourSchemeManager getColourSchemeManager() {
        return this.colourSchemeManager;
    }

    public void setColourSchemeManager(ColourSchemeManager colourSchemeManager) {
        this.colourSchemeManager = colourSchemeManager;
    }
}

