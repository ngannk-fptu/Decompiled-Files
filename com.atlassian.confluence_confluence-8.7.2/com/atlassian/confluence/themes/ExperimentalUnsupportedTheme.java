/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.BasicTheme;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.themes.ThemedDecorator;
import com.atlassian.confluence.themes.VelocityResultOverride;
import com.google.common.collect.ImmutableList;
import java.util.Collection;

public class ExperimentalUnsupportedTheme
implements Theme {
    private Theme delegate = new BasicTheme();
    private Collection<VelocityResultOverride> velocityResultOverrides;
    private boolean disableSitemesh;
    private String bodyClass;

    @Override
    public void init(ThemeModuleDescriptor moduleDescriptor) {
        this.delegate.init(moduleDescriptor);
        this.velocityResultOverrides = ImmutableList.copyOf(moduleDescriptor.getVelocityResultOverrides());
        this.disableSitemesh = moduleDescriptor.isDisableSitemesh();
        this.bodyClass = moduleDescriptor.getBodyClass();
        if (this.bodyClass == null) {
            this.bodyClass = moduleDescriptor.getKey();
        }
    }

    @Override
    public ColourScheme getColourScheme() {
        return this.delegate.getColourScheme();
    }

    @Override
    public String getBodyClass() {
        return this.bodyClass;
    }

    @Override
    public ThemedDecorator getDecorator(String path) {
        return this.delegate.getDecorator(path);
    }

    @Override
    public Collection<? extends ThemeResource> getStylesheets() {
        return this.delegate.getStylesheets();
    }

    @Override
    public Collection<? extends ThemeResource> getJavascript() {
        return this.delegate.getJavascript();
    }

    @Override
    public String getPluginKey() {
        return this.delegate.getPluginKey();
    }

    @Override
    public String getModuleKey() {
        return this.delegate.getModuleKey();
    }

    @Override
    public String getXworkVelocityPath(String packageName, String actionName, String result, String template) {
        for (VelocityResultOverride velocityResultOverride : this.velocityResultOverrides) {
            template = velocityResultOverride.getOverridePath(packageName, actionName, result, template);
        }
        return template;
    }

    @Override
    public String getTopNavLocation() {
        return "";
    }

    @Override
    public boolean hasSpaceSideBar() {
        return false;
    }

    @Override
    public boolean isDisableSitemesh() {
        return this.disableSitemesh;
    }
}

