/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.PluginParseException
 *  com.google.common.collect.Lists
 *  javax.servlet.ServletRequest
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugin.descriptor.web.conditions;

import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.themes.ThemeContext;
import com.atlassian.confluence.themes.ThemeManager;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.PluginParseException;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Map;
import javax.servlet.ServletRequest;
import org.apache.commons.lang3.StringUtils;

public class ActiveThemeCondition
extends BaseConfluenceCondition {
    private ArrayList<String> themeses;
    private ThemeManager themeManager;
    private HttpContext httpContext;

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
        this.themeses = Lists.newArrayList((Object[])params.get("themes").split(","));
    }

    @Override
    protected boolean shouldDisplay(WebInterfaceContext context) {
        String activeThemeKey = null;
        ThemeContext themeContext = ThemeContext.get((ServletRequest)this.httpContext.getRequest());
        String spaceKey = themeContext.getSpaceKey();
        if (!StringUtils.isBlank((CharSequence)spaceKey)) {
            activeThemeKey = this.themeManager.getSpaceThemeKey(spaceKey);
        }
        if (StringUtils.isBlank(activeThemeKey)) {
            activeThemeKey = this.themeManager.getGlobalThemeKey() == null ? "" : this.themeManager.getGlobalThemeKey();
        }
        return this.themeses.contains(activeThemeKey);
    }

    public void setThemeManager(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    public void setHttpContext(HttpContext httpContext) {
        this.httpContext = httpContext;
    }
}

