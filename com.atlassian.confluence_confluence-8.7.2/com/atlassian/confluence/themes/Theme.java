/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.ColourScheme;
import com.atlassian.confluence.themes.ThemeResource;
import com.atlassian.confluence.themes.ThemedDecorator;
import java.util.Collection;

public interface Theme {
    public ColourScheme getColourScheme();

    public String getBodyClass();

    public ThemedDecorator getDecorator(String var1);

    public Collection<? extends ThemeResource> getStylesheets();

    public Collection<? extends ThemeResource> getJavascript();

    public void init(ThemeModuleDescriptor var1);

    public String getPluginKey();

    public String getModuleKey();

    public boolean isDisableSitemesh();

    public String getXworkVelocityPath(String var1, String var2, String var3, String var4);

    @Deprecated
    public String getTopNavLocation();

    public boolean hasSpaceSideBar();
}

