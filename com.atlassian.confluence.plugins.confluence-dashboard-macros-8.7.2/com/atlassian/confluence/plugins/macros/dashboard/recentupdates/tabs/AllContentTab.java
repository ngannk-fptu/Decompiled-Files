/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AbstractTab;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.renderer.RenderContext;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class AllContentTab
extends AbstractTab {
    public AllContentTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager) {
        super(httpContext, i18NBeanFactory, localeManager);
    }

    @Override
    public String getName() {
        return "all";
    }

    @Override
    public boolean shouldDisplay(RenderContext renderContext) {
        return true;
    }

    @Override
    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams macroParams, RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return new RecentUpdateQueryParameters(macroParams.getUsers(), macroParams.getValidLabels(), this.getSpaceFilter(macroParams, renderContext), macroParams.getTypes());
    }

    @Override
    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return Collections.emptyMap();
    }

    private Set<String> getSpaceFilter(RecentlyUpdatedMacroParams macroParams, RenderContext renderContext) {
        PageContext context;
        String spaceKey;
        if (macroParams.hasSpaces()) {
            return macroParams.getSpaces();
        }
        if (renderContext instanceof PageContext && (spaceKey = (context = (PageContext)renderContext).getSpaceKey()) != null) {
            return Collections.singleton(spaceKey);
        }
        return null;
    }
}

