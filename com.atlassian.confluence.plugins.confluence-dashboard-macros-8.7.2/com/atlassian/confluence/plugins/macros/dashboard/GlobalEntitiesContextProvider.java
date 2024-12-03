/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.labels.LabelParser
 *  com.atlassian.confluence.labels.ParsedLabelName
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.PluginParseException
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugin.web.ContextProvider
 *  com.atlassian.user.User
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.labels.ParsedLabelName;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.DashboardMacroSupport;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugin.web.ContextProvider;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GlobalEntitiesContextProvider
implements ContextProvider {
    private static final int MAX_PAGE_COUNT = 20;
    private static final int SPACES_MAX_RESULTS = 20;
    public static final EntitiesTab DEFAULT_TAB = EntitiesTab.SPACES;
    private final SpaceManager spaceManager;
    private final LabelManager labelManager;
    private final LocaleManager localeManager;
    private final PermissionManager permissionManager;
    private final PluginAccessor pluginAccessor;
    private final FormatSettingsManager formatSettingsManager;
    private final UserAccessor userAccessor;
    private final SpaceLogoManager spaceLogoManager;

    public GlobalEntitiesContextProvider(@ComponentImport SpaceManager spaceManager, @ComponentImport LabelManager labelManager, @ComponentImport LocaleManager localeManager, @ComponentImport PermissionManager permissionManager, @ComponentImport PluginAccessor pluginAccessor, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport SpaceLogoManager spaceLogoManager) {
        this.spaceManager = spaceManager;
        this.labelManager = labelManager;
        this.localeManager = localeManager;
        this.permissionManager = permissionManager;
        this.pluginAccessor = pluginAccessor;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.spaceLogoManager = spaceLogoManager;
    }

    public void init(Map<String, String> params) throws PluginParseException {
    }

    public Map<String, Object> getContextMap(Map<String, Object> context) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        context.put("remoteUser", user);
        EntitiesTab selectedTab = this.getSelectedTab(context);
        boolean showNetworkPanel = user != null && this.pluginAccessor.isPluginEnabled("confluence.macros.profile");
        boolean showUserPanel = user != null;
        context.put("showPagesPane", showUserPanel);
        context.put("showNetworkPane", showNetworkPanel);
        if (selectedTab == EntitiesTab.PAGES && !showUserPanel || selectedTab == EntitiesTab.NETWORK && !showNetworkPanel) {
            selectedTab = DEFAULT_TAB;
        }
        context.put("selectedTab", (Object)selectedTab);
        if (selectedTab == EntitiesTab.SPACES) {
            this.injectSpaceTabContext(user, context);
        } else if (selectedTab == EntitiesTab.PAGES) {
            this.injectPagesTabContext(context);
        }
        return context;
    }

    public void injectSpaceTabContext(ConfluenceUser user, Map<String, Object> context) {
        DashboardMacroSupport dashboardMacroSupport = new DashboardMacroSupport(this.labelManager, this.spaceManager, this.localeManager, this.formatSettingsManager, this.userAccessor, this.permissionManager, true);
        context.put("spaceLogoManager", this.spaceLogoManager);
        context.put("favouriteSpaces", dashboardMacroSupport.getFavouriteSpaces());
        ListBuilder spaceListBuilder = this.spaceManager.getSpaces(SpacesQuery.newQuery().forUser((User)user).withSpaceType(SpaceType.GLOBAL).withSpaceStatus(SpaceStatus.CURRENT).build());
        List spaces = spaceListBuilder.getPage(0, 20);
        context.put("globalSpaces", spaces);
        context.put("totalSpaces", spaceListBuilder.getAvailableSize());
        context.put("canAddSpaces", this.permissionManager.hasCreatePermission((User)user, PermissionManager.TARGET_APPLICATION, Space.class));
    }

    public void injectPagesTabContext(Map<String, Object> context) {
        List<Object> contents = new ArrayList();
        this.addContentForLabelCollection(contents, "my:favourite");
        this.addContentForLabelCollection(contents, "my:favorite");
        contents = this.permissionManager.getPermittedEntities((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, contents);
        Collections.sort(contents);
        contents = this.filterByContentType(contents, Arrays.asList("page", "blogpost"));
        context.put("totalFavouritePages", contents.size());
        if (contents.size() > 20) {
            contents = contents.subList(0, 20);
        }
        context.put("favouritePages", contents);
        context.put("showPagesPane", true);
    }

    private EntitiesTab getSelectedTab(Map<String, Object> context) {
        String[] selectedTabName = (String[])context.get("entitiesSelectedTab");
        if (selectedTabName != null) {
            try {
                return EntitiesTab.valueOf(selectedTabName[0].toUpperCase());
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return DEFAULT_TAB;
    }

    private void addContentForLabelCollection(Collection contents, String labelName) {
        Label label;
        ParsedLabelName ref = LabelParser.parse((String)labelName);
        if (ref != null && (label = this.labelManager.getLabel(ref)) != null) {
            contents.addAll(this.labelManager.getCurrentContentForLabel(label));
        }
    }

    private List<ContentEntityObject> filterByContentType(List<ContentEntityObject> original, List<String> types) {
        ArrayList<ContentEntityObject> result = new ArrayList<ContentEntityObject>();
        for (ContentEntityObject contentEntityObject : original) {
            if (!types.contains(contentEntityObject.getType())) continue;
            result.add(contentEntityObject);
        }
        return result;
    }

    public static enum EntitiesTab {
        SPACES,
        PAGES,
        NETWORK;


        public boolean equals(String tabName) {
            return this.name().equalsIgnoreCase(tabName);
        }
    }
}

