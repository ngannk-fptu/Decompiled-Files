/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.renderer.RenderContext
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AbstractTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.FollowService;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.renderer.RenderContext;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class NetworkTab
extends AbstractTab {
    private final FollowService followService;

    public NetworkTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, FollowService followService, LocaleManager localeManager) {
        super(httpContext, i18NBeanFactory, localeManager);
        this.followService = followService;
    }

    @Override
    public String getName() {
        return "network";
    }

    @Override
    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams macroParams, RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return new RecentUpdateQueryParameters(this.getUserFilter(AuthenticatedUserThreadLocal.get(), macroParams), macroParams.getValidLabels(), macroParams.getSpaces(), macroParams.getTypes());
    }

    @Override
    public boolean shouldDisplay(RenderContext renderContext) {
        return AuthenticatedUserThreadLocal.get() != null;
    }

    @Override
    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return Collections.emptyMap();
    }

    private Set<ConfluenceUser> getUserFilter(ConfluenceUser user, RecentlyUpdatedMacroParams macroParams) {
        if (user != null) {
            return this.followService.getFollowingUsers(user);
        }
        return macroParams.getUsers();
    }
}

