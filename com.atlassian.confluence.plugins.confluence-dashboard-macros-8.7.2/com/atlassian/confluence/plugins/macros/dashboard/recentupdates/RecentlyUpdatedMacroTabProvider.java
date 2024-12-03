/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.WebInterfaceManager
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.AllContentTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.FavouriteSpacesTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.FollowService;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.NetworkTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.PopularTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.RecentlyUpdatedMacroTab;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.tabs.SpaceCategoryTab;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.WebInterfaceManager;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecentlyUpdatedMacroTabProvider
implements InitializingBean {
    private final LabelManager labelManager;
    private final PermissionManager permissionManager;
    private final FollowService followService;
    private final SpaceManager spaceManager;
    private final UserAccessor userAccessor;
    private final WebInterfaceManager webInterfaceManager;
    private final HttpContext httpContext;
    private final I18NBeanFactory i18NBeanFactory;
    private final LocaleManager localeManager;
    private final Map<String, RecentlyUpdatedMacroTab> availableTabs = Maps.newLinkedHashMap();
    private RecentlyUpdatedMacroTab defaultTab;

    @Autowired
    public RecentlyUpdatedMacroTabProvider(@ComponentImport SpaceManager spaceManager, @ComponentImport LabelManager labelManager, @ComponentImport PermissionManager permissionManager, FollowService followService, @ComponentImport UserAccessor userAccessor, @ComponentImport WebInterfaceManager webInterfaceManager, @ComponentImport HttpContext httpContext, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport LocaleManager localeManager) {
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.permissionManager = permissionManager;
        this.followService = followService;
        this.userAccessor = userAccessor;
        this.webInterfaceManager = webInterfaceManager;
        this.httpContext = httpContext;
        this.i18NBeanFactory = i18NBeanFactory;
        this.localeManager = localeManager;
    }

    public void afterPropertiesSet() throws Exception {
        this.defaultTab = new PopularTab(this.httpContext, this.i18NBeanFactory, this.localeManager, this.webInterfaceManager);
        this.setAvailableTabs(this.defaultTab, new AllContentTab(this.httpContext, this.i18NBeanFactory, this.localeManager), new FavouriteSpacesTab(this.httpContext, this.i18NBeanFactory, this.labelManager, this.permissionManager, this.localeManager), new NetworkTab(this.httpContext, this.i18NBeanFactory, this.followService, this.localeManager), new SpaceCategoryTab(this.httpContext, this.i18NBeanFactory, this.labelManager, this.permissionManager, this.spaceManager, this.userAccessor, this.localeManager));
    }

    private void setAvailableTabs(RecentlyUpdatedMacroTab ... tabs) {
        for (RecentlyUpdatedMacroTab tab : tabs) {
            this.availableTabs.put(StringUtils.lowerCase((String)tab.getName()), tab);
        }
    }

    public List<RecentlyUpdatedMacroTab> getAvailableTabs() {
        return ImmutableList.copyOf(this.availableTabs.values());
    }

    public RecentlyUpdatedMacroTab getTabByName(String name) {
        String normalisedName = StringUtils.lowerCase((String)name);
        if (!this.availableTabs.containsKey(normalisedName)) {
            return this.defaultTab;
        }
        return this.availableTabs.get(normalisedName);
    }

    public RecentlyUpdatedMacroTab getDefaultTab() {
        return this.defaultTab;
    }
}

