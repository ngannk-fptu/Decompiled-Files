/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.actions.AbstractPageAwareAction
 *  com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext
 *  com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition
 *  com.atlassian.confluence.themes.ThemeManager
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.pagebanner;

import com.atlassian.confluence.pages.actions.AbstractPageAwareAction;
import com.atlassian.confluence.plugin.descriptor.web.WebInterfaceContext;
import com.atlassian.confluence.plugin.descriptor.web.conditions.BaseConfluenceCondition;
import com.atlassian.confluence.themes.ThemeManager;
import org.apache.commons.lang3.StringUtils;

public class PageBannerCondition
extends BaseConfluenceCondition {
    private ThemeManager themeManager;

    public PageBannerCondition(ThemeManager themeManager) {
        this.themeManager = themeManager;
    }

    protected boolean shouldDisplay(WebInterfaceContext context) {
        String spaceKey;
        Object action = context.getParameter("action");
        if (action instanceof AbstractPageAwareAction && (spaceKey = ((AbstractPageAwareAction)action).getSpaceKey()) != null) {
            return !this.isDocumentationTheme(spaceKey);
        }
        return false;
    }

    private boolean isDocumentationTheme(String spaceKey) {
        String themeKey = this.getThemeKey(spaceKey);
        return StringUtils.isNotBlank((CharSequence)themeKey) && themeKey.contains("documentation");
    }

    private String getThemeKey(String spaceKey) {
        String spaceThemeKey = this.themeManager.getSpaceThemeKey(spaceKey);
        if (StringUtils.isBlank((CharSequence)spaceThemeKey)) {
            return this.themeManager.getGlobalThemeKey();
        }
        return spaceThemeKey;
    }
}

