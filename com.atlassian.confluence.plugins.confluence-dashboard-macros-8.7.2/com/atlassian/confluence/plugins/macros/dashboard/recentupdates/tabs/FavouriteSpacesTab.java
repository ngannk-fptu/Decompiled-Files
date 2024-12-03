/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AbstractTab;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FavouriteSpacesTab
extends AbstractTab {
    private final LabelManager labelManager;
    private final PermissionManager permissionManager;

    public FavouriteSpacesTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, LabelManager labelManager, PermissionManager permissionManager, LocaleManager localeManager) {
        super(httpContext, i18NBeanFactory, localeManager);
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
    }

    @Override
    public String getName() {
        return "my";
    }

    @Override
    public boolean shouldDisplay(RenderContext renderContext) {
        return AuthenticatedUserThreadLocal.get() != null;
    }

    @Override
    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams macroParams, RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return new RecentUpdateQueryParameters(macroParams.getUsers(), macroParams.getValidLabels(), this.getSpaceFilter(), macroParams.getTypes());
    }

    @Override
    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        return Collections.emptyMap();
    }

    private Set<String> getSpaceFilter() {
        return this.getFavouriteSpaceKeys((User)AuthenticatedUserThreadLocal.get());
    }

    public Set<String> getFavouriteSpaceKeys(User user) {
        if (user == null) {
            return Collections.emptySet();
        }
        List favouriteSpaces = this.labelManager.getFavouriteSpaces(user.getName());
        HashSet spaceKeys = Sets.newHashSet();
        for (Space space : favouriteSpaces) {
            if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) continue;
            spaceKeys.add(space.getKey());
        }
        return spaceKeys;
    }
}

