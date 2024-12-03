/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class BasicTheme
implements Theme {
    private String pluginKey;
    private String moduleKey;
    private ColourScheme colourScheme;
    private List<ThemedDecorator> decorators;
    private Collection<ThemeResource> stylesheets;
    private Collection<ThemeResource> javascript;
    private String bodyClass;
    private String topNavLocation;
    private boolean hasSpaceSideBar;

    @Override
    public ColourScheme getColourScheme() {
        return this.colourScheme;
    }

    @Override
    public ThemedDecorator getDecorator(String path) {
        if (StringUtils.isNotEmpty((CharSequence)path)) {
            for (ThemedDecorator decorator : this.decorators) {
                if (!decorator.getName().equals(path)) continue;
                return decorator;
            }
        }
        return null;
    }

    @Override
    public Collection<? extends ThemeResource> getStylesheets() {
        return this.stylesheets;
    }

    @Override
    public Collection<? extends ThemeResource> getJavascript() {
        return this.javascript;
    }

    @Override
    public String getPluginKey() {
        return this.pluginKey;
    }

    @Override
    public void init(ThemeModuleDescriptor moduleDescriptor) {
        this.pluginKey = moduleDescriptor.getPluginKey();
        this.moduleKey = moduleDescriptor.getKey();
        this.colourScheme = moduleDescriptor.getColourScheme();
        this.decorators = moduleDescriptor.getLayouts();
        this.stylesheets = ImmutableList.copyOf(moduleDescriptor.getStylesheets());
        this.javascript = ImmutableList.copyOf(moduleDescriptor.getJavascript());
        this.bodyClass = moduleDescriptor.getBodyClass() != null ? moduleDescriptor.getBodyClass() : this.moduleKey;
        this.topNavLocation = moduleDescriptor.getTopNavLocation() != null ? moduleDescriptor.getTopNavLocation() : "";
        this.hasSpaceSideBar = moduleDescriptor.hasSpaceSideBar();
    }

    @Override
    public String getXworkVelocityPath(String packageName, String actionName, String result, String template) {
        return template;
    }

    @Override
    public boolean isDisableSitemesh() {
        return false;
    }

    @Override
    public String getModuleKey() {
        return this.moduleKey;
    }

    @Override
    public String getBodyClass() {
        return this.bodyClass;
    }

    @Override
    public String getTopNavLocation() {
        return this.topNavLocation;
    }

    @Override
    public boolean hasSpaceSideBar() {
        return this.hasSpaceSideBar;
    }
}

