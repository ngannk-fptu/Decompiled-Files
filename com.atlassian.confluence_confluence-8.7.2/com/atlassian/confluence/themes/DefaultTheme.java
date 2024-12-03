/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.ClasspathThemeStylesheet;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.themes.ThemedDecorator;
import java.util.Collection;
import java.util.Collections;

public class DefaultTheme
implements Theme {
    public static final String STYLESHEET_NAME = "default-theme.css";
    public static final String PLUGIN_KEY = "com.atlassian.confluence.themes.default";
    public static final String MODULE_KEY = "theme-default";
    public static final String STYLESHEET_MODULE_KEY = "com.atlassian.confluence.themes.default:styles";
    public static final String TOP_NAV_LOCATION = "";
    private static Theme instance = new DefaultTheme();

    private DefaultTheme() {
    }

    @Override
    public ColourScheme getColourScheme() {
        return null;
    }

    @Override
    public ThemedDecorator getDecorator(String path) {
        return null;
    }

    @Override
    public Collection<? extends ThemeResource> getStylesheets() {
        return Collections.singletonList(new ClasspathThemeStylesheet(STYLESHEET_MODULE_KEY, STYLESHEET_NAME, "/includes/css/default-theme.css"));
    }

    @Override
    public Collection<? extends ThemeResource> getJavascript() {
        return Collections.emptyList();
    }

    @Override
    public void init(ThemeModuleDescriptor moduleDescriptor) {
    }

    @Override
    public String getPluginKey() {
        return PLUGIN_KEY;
    }

    @Override
    public String getModuleKey() {
        return MODULE_KEY;
    }

    public static Theme getInstance() {
        return instance;
    }

    @Override
    public boolean isDisableSitemesh() {
        return false;
    }

    @Override
    public String getXworkVelocityPath(String packageName, String actionName, String result, String template) {
        return template;
    }

    @Override
    public String getBodyClass() {
        return this.getModuleKey();
    }

    @Override
    public String getTopNavLocation() {
        return TOP_NAV_LOCATION;
    }

    @Override
    public boolean hasSpaceSideBar() {
        return true;
    }
}

