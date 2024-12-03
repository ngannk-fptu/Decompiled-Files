/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor
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
import com.atlassian.plugin.web.WebInterfaceManager;
import com.atlassian.plugin.web.descriptors.WebItemModuleDescriptor;
import com.atlassian.renderer.RenderContext;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PopularTab
extends AbstractTab {
    public static final String NAME = "popular";
    private final WebInterfaceManager webInterfaceManager;

    public PopularTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, LocaleManager localeManager, WebInterfaceManager webInterfaceManager) {
        super(httpContext, i18NBeanFactory, localeManager);
        this.webInterfaceManager = webInterfaceManager;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public boolean shouldDisplay(RenderContext renderContext) {
        return renderContext instanceof PageContext && ((PageContext)renderContext).getEntity() == null && this.isPopularTabEnabled();
    }

    @Override
    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams macroParams, RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return new RecentUpdateQueryParameters(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), null);
    }

    @Override
    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return Collections.emptyMap();
    }

    private boolean isPopularTabEnabled() {
        List tabDescriptors = this.webInterfaceManager.getDisplayableItems("system.dashboard.tabs", Collections.emptyMap());
        for (WebItemModuleDescriptor tabDescriptor : tabDescriptors) {
            if (!"popular-tab".equals(tabDescriptor.getKey())) continue;
            return true;
        }
        return false;
    }
}

