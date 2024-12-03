/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.CustomPageSettings
 *  com.atlassian.confluence.core.CustomPageSettingsManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.themes.BaseColourScheme
 *  com.atlassian.confluence.themes.ColourScheme
 *  com.atlassian.confluence.themes.ColourSchemeManager
 *  com.atlassian.confluence.themes.StylesheetManager
 *  com.atlassian.confluence.themes.ThemeManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.core.CustomPageSettings;
import com.atlassian.confluence.core.CustomPageSettingsManager;
import com.atlassian.confluence.plugin.copyspace.service.LookAndFeelCopier;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.BaseColourScheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ColourSchemeManager;
import com.atlassian.confluence.themes.StylesheetManager;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import org.springframework.stereotype.Component;

@Component(value="lookAndFeelCopierImpl")
public class LookAndFeelCopierImpl
implements LookAndFeelCopier {
    private final ThemeManager themeManager;
    private final ColourSchemeManager colourSchemeManager;
    private final StylesheetManager stylesheetManager;
    private final CustomPageSettingsManager customPageSettingsManager;

    public LookAndFeelCopierImpl(@ComponentImport ThemeManager themeManager, @ComponentImport ColourSchemeManager colourSchemeManager, @ComponentImport StylesheetManager stylesheetManager, @ComponentImport CustomPageSettingsManager customPageSettingsManager) {
        this.themeManager = themeManager;
        this.colourSchemeManager = colourSchemeManager;
        this.stylesheetManager = stylesheetManager;
        this.customPageSettingsManager = customPageSettingsManager;
    }

    @Override
    public void copyLookAndFeel(Space source, Space destination) {
        String originalThemeKey = this.themeManager.getSpaceThemeKey(source.getKey());
        this.themeManager.setSpaceTheme(destination.getKey(), originalThemeKey);
        ColourScheme scheme = this.colourSchemeManager.getSpaceColourScheme(source);
        this.colourSchemeManager.saveSpaceColourScheme(destination, new BaseColourScheme(scheme));
        this.colourSchemeManager.setColourSchemeSetting(destination, this.colourSchemeManager.getColourSchemeSetting(source));
        String originalStyleSheet = this.stylesheetManager.getSpaceStylesheet(source.getKey());
        this.stylesheetManager.addSpaceStylesheet(destination.getKey(), originalStyleSheet);
        CustomPageSettings customPageSettings = this.customPageSettingsManager.retrieveSettings(source.getKey());
        this.customPageSettingsManager.saveSettings(destination.getKey(), customPageSettings);
    }
}

