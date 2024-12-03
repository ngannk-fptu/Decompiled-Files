/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.themes.ColourSchemeManager
 */
package com.atlassian.oauth2.provider.data.themes;

import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.oauth2.provider.data.themes.ProductCustomTheme;
import com.atlassian.oauth2.provider.data.themes.ProductCustomThemeFactory;

public class ConfluenceCustomThemeFactory
extends ProductCustomThemeFactory {
    private final ColourSchemeManager colourSchemeManager;

    public ConfluenceCustomThemeFactory(ColourSchemeManager colourSchemeManager) {
        this.colourSchemeManager = colourSchemeManager;
    }

    @Override
    public ProductCustomTheme get() {
        return new ProductCustomTheme(this.colourSchemeManager.getGlobalColourScheme().get("property.style.topbarcolour"), "");
    }
}

