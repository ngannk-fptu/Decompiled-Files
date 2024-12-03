/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.ext.code.themes;

import com.atlassian.confluence.ext.code.themes.Theme;
import java.util.Collection;
import java.util.Map;

public interface ThemeRegistry {
    public boolean isThemeRegistered(String var1);

    public Collection<Theme> listThemes() throws Exception;

    public String getWebResourceForTheme(String var1) throws Exception;

    public Map<String, String> getThemeLookAndFeel(String var1) throws Exception;
}

