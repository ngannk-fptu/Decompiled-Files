/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.themes;

import com.atlassian.confluence.plugin.descriptor.ThemeModuleDescriptor;
import com.atlassian.confluence.themes.Theme;
import java.util.List;

public interface ThemeManager {
    public Theme getGlobalTheme();

    public Theme getSpaceTheme(String var1);

    public String getGlobalThemeKey();

    public String getSpaceThemeKey(String var1);

    public List<ThemeModuleDescriptor> getAvailableThemeDescriptors();

    public void setGlobalTheme(String var1);

    public void setSpaceTheme(String var1, String var2);
}

