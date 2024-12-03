/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.Namespace
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserInterfaceState
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.user.User
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs;

import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.SpaceCategoryName;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentlyUpdatedMacroRequestParams;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AbstractTab;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserInterfaceState;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.renderer.RenderContext;
import com.atlassian.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang3.StringUtils;

public class SpaceCategoryTab
extends AbstractTab {
    private static final String RENDER_CONTEXT_CACHE_KEY = "permittedCategories";
    private final LabelManager labelManager;
    private final PermissionManager permissionManager;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;

    public SpaceCategoryTab(HttpContext httpContext, I18NBeanFactory i18NBeanFactory, LabelManager labelManager, PermissionManager permissionManager, SpaceManager spaceManager, UserAccessor userAccessor, LocaleManager localeManager) {
        super(httpContext, i18NBeanFactory, localeManager);
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
        this.spaceManager = spaceManager;
        this.userAccessor = userAccessor;
    }

    @Override
    public String getName() {
        return "team";
    }

    @Override
    public boolean shouldDisplay(RenderContext renderContext) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        boolean isNotAnonymousUser = user != null;
        List<SpaceCategoryName> permittedCategories = this.getPermittedCategories(user, renderContext);
        return isNotAnonymousUser && !permittedCategories.isEmpty();
    }

    @Override
    public RecentUpdateQueryParameters getQueryParameters(RecentlyUpdatedMacroParams macroParams, RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        String selectedCategory = this.getSelectedCategory(requestParams, this.getPermittedCategories(AuthenticatedUserThreadLocal.get(), renderContext));
        return new RecentUpdateQueryParameters(macroParams.getUsers(), macroParams.getValidLabels(), this.getSpaceFilter(selectedCategory, renderContext), macroParams.getTypes());
    }

    @Override
    public Map<String, Object> getRenderContext(RecentlyUpdatedMacroRequestParams requestParams, RenderContext renderContext) {
        List<SpaceCategoryName> permittedCategories = this.getPermittedCategories(AuthenticatedUserThreadLocal.get(), renderContext);
        ImmutableMap.Builder contextMap = ImmutableMap.builder();
        String selectedCategory = this.getSelectedCategory(requestParams, permittedCategories);
        if (selectedCategory != null) {
            contextMap.put((Object)"selectedCategory", (Object)selectedCategory);
        }
        contextMap.put((Object)"viewableCategories", permittedCategories);
        return contextMap.build();
    }

    private Set<String> getSpaceFilter(String selectedCategory, RenderContext renderContext) {
        Set<String> spaceKeys;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        if (selectedCategory != null && !(spaceKeys = this.getSpaceKeysForCategory((User)user, selectedCategory)).isEmpty()) {
            return spaceKeys;
        }
        List<SpaceCategoryName> availableCategories = this.getPermittedCategories(user, renderContext);
        if (availableCategories.isEmpty()) {
            return Collections.emptySet();
        }
        return this.getSpaceKeysForCategory((User)user, availableCategories.get(0).toString());
    }

    private UserInterfaceState getUserInterfaceState(User user) {
        return new UserInterfaceState(user, this.userAccessor);
    }

    private Set<String> getSpaceKeysForCategory(User user, String selectedCategory) {
        Label label = this.labelManager.getLabel(selectedCategory, Namespace.TEAM);
        if (label == null) {
            return Collections.emptySet();
        }
        List matchingSpaces = this.labelManager.getSpacesWithLabel(label);
        HashSet spaceKeys = Sets.newHashSet();
        for (Space space : matchingSpaces) {
            if (!this.permissionManager.hasPermission(user, Permission.VIEW, (Object)space)) continue;
            spaceKeys.add(space.getKey());
        }
        return spaceKeys;
    }

    private List<SpaceCategoryName> getPermittedCategories(ConfluenceUser user, RenderContext renderContext) {
        if (this.getCachedPermittedCategories(renderContext) != null) {
            return this.getCachedPermittedCategories(renderContext);
        }
        TreeSet labelNames = Sets.newTreeSet();
        ListBuilder permittedSpaces = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)user).build());
        for (List page : permittedSpaces) {
            List labels = this.labelManager.getTeamLabelsForSpaces((Collection)page);
            for (Label label : labels) {
                labelNames.add(new SpaceCategoryName(label.getName()));
            }
        }
        ImmutableList result = ImmutableList.copyOf((Collection)labelNames);
        renderContext.addParam((Object)RENDER_CONTEXT_CACHE_KEY, (Object)result);
        return result;
    }

    private List<SpaceCategoryName> getCachedPermittedCategories(RenderContext renderContext) {
        return (List)renderContext.getParam((Object)RENDER_CONTEXT_CACHE_KEY);
    }

    private String getSelectedCategory(RecentlyUpdatedMacroRequestParams requestParams, List<SpaceCategoryName> viewableCategories) {
        if (viewableCategories == null || viewableCategories.size() == 0) {
            return null;
        }
        UserInterfaceState uiState = this.getUserInterfaceState((User)AuthenticatedUserThreadLocal.get());
        String requestCategory = requestParams.getSelectedCategory();
        if (StringUtils.isNotBlank((CharSequence)requestCategory) && viewableCategories.contains(new SpaceCategoryName(requestCategory))) {
            uiState.setDashboardSpacesSelectedTeam(requestCategory);
            return requestCategory;
        }
        String existingPreference = uiState.getDashboardSpacesSelectedTeam();
        if (viewableCategories.isEmpty() || viewableCategories.contains(new SpaceCategoryName(existingPreference))) {
            return existingPreference;
        }
        return viewableCategories.get(0).toString();
    }
}

