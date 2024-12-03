/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.web.ContextProvider
 */
package com.atlassian.confluence.pages;

import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.themes.DefaultTheme;
import com.atlassian.confluence.themes.Theme;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.ContextProvider;
import java.util.Map;

public class ListPagesContextProvider
implements ContextProvider {
    public static final String REORDER_PAGES_URL = "/pages/reorderpages.action";
    public static final String LIST_PAGES_URL = "/pages/listpages-dirview.action";
    private ThemeManager themeManager;

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        Space space = (Space)context.get("space");
        String pagesTreeUrl = this.isDefaultTheme(space) ? REORDER_PAGES_URL : LIST_PAGES_URL;
        context.put("pagesTreeUrl", pagesTreeUrl);
        return context;
    }

    private boolean isDefaultTheme(Space space) {
        Theme theme = space != null ? this.themeManager.getSpaceTheme(space.getKey()) : this.themeManager.getGlobalTheme();
        return theme == DefaultTheme.getInstance();
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }
}

