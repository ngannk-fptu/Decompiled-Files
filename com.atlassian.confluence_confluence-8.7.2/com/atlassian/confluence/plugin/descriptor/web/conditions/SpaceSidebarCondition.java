/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginAccessor
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.PluginAccessor;

public class SpaceSidebarCondition
extends BaseConfluenceCondition {
    private PluginAccessor pluginAccessor;
    private ThemeManager themeManager;
    private static final String SIDEBAR_PLUGIN_KEY = new String("com.atlassian.confluence.plugins.confluence-space-ia");

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        Space space = context.getSpace();
        String spaceKey = space == null ? context.getSpaceKey() : space.getKey();
        boolean pluginEnabled = this.pluginAccessor.isPluginEnabled(SIDEBAR_PLUGIN_KEY);
        boolean correctSpaceTheme = spaceKey != null && this.themeManager.getSpaceTheme(spaceKey).hasSpaceSideBar();
        return correctSpaceTheme && pluginEnabled;
    }

    public void setPluginAccessor(PluginAccessor pluginAccessor) {
        this.pluginAccessor = pluginAccessor;
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }
}

