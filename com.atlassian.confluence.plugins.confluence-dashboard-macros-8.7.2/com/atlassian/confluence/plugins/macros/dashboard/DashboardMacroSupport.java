/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.ListBuilder
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.spaces.SpaceStatus
 *  com.atlassian.confluence.spaces.SpaceType
 *  com.atlassian.confluence.spaces.SpacesQuery
 *  com.atlassian.confluence.spaces.SpacesQuery$Builder
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserInterfaceState
 *  com.atlassian.confluence.util.SpaceComparator
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.ServletActionContext
 */
package com.atlassian.confluence.plugins.macros.dashboard;

import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.ListBuilder;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.SpacesListMacro;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.spaces.SpaceStatus;
import com.atlassian.confluence.spaces.SpaceType;
import com.atlassian.confluence.spaces.SpacesQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserInterfaceState;
import com.atlassian.confluence.util.SpaceComparator;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.ServletActionContext;

public class DashboardMacroSupport {
    @ComponentImport
    private final LabelManager labelManager;
    @ComponentImport
    private final SpaceManager spaceManager;
    @ComponentImport
    private final LocaleManager localeManager;
    @ComponentImport
    private final PermissionManager permissionManager;
    @ComponentImport
    private final FormatSettingsManager formatSettingsManager;
    @ComponentImport
    private final UserAccessor userAccessor;
    private List<Space> favSpaces;
    private List<Space> newSpaces;
    private List<Space> teamSpaces;
    private List<String> viewableTeamLabels;
    private Set<Space> permittedSpaces;
    private final SpacesListMacro.Tabs DEFAULT_SPACES_TAB = SpacesListMacro.Tabs.ALL;
    private final int spaceListPageSize;
    private final boolean includeArchivedSpaces;

    public DashboardMacroSupport(LabelManager labelManager, SpaceManager spaceManager, LocaleManager localeManager, FormatSettingsManager formatSettingsManager, UserAccessor userAccessor, PermissionManager permissionManager, boolean includeArchivedSpaces) {
        this(labelManager, spaceManager, localeManager, permissionManager, formatSettingsManager, userAccessor, includeArchivedSpaces, 500);
    }

    DashboardMacroSupport(LabelManager labelManager, SpaceManager spaceManager, LocaleManager localeManager, PermissionManager permissionManager, FormatSettingsManager formatSettingsManager, UserAccessor userAccessor, boolean includeArchivedSpaces, int spaceListPageSize) {
        this.labelManager = Objects.requireNonNull(labelManager);
        this.spaceManager = Objects.requireNonNull(spaceManager);
        this.localeManager = Objects.requireNonNull(localeManager);
        this.permissionManager = Objects.requireNonNull(permissionManager);
        this.formatSettingsManager = Objects.requireNonNull(formatSettingsManager);
        this.userAccessor = userAccessor;
        this.includeArchivedSpaces = includeArchivedSpaces;
        this.spaceListPageSize = spaceListPageSize;
    }

    public List<Space> getFavouriteSpaces() {
        if (this.favSpaces != null) {
            return this.favSpaces;
        }
        if (this.getUser() == null) {
            this.favSpaces = Collections.emptyList();
        } else {
            List labelledSpaces = this.labelManager.getFavouriteSpaces(this.getUser().getName());
            this.favSpaces = this.retainViewPermissions(labelledSpaces);
            Collections.sort(this.favSpaces, new SpaceComparator());
        }
        return this.favSpaces;
    }

    public DateFormatter getDateFormatter() {
        ConfluenceUser user = this.getUser();
        return this.userAccessor.getConfluenceUserPreferences((User)user).getDateFormatter(this.formatSettingsManager, this.localeManager);
    }

    public List<Space> getNewSpaces() {
        if (this.newSpaces != null) {
            return this.newSpaces;
        }
        Calendar cal = this.getDateFormatter().getCalendar();
        cal.setTime(new Date());
        cal.add(5, -7);
        this.newSpaces = this.spaceManager.getAllSpaces(this.createSpacesQueryBuilder().createdAfter(cal.getTime()).build());
        return this.newSpaces;
    }

    private <T> List<T> retainViewPermissions(List<T> unfilteredResult) {
        ArrayList<T> result = new ArrayList<T>(unfilteredResult.size());
        for (T val : unfilteredResult) {
            if (!this.permissionManager.hasPermission((User)this.getUser(), Permission.VIEW, val)) continue;
            result.add(val);
        }
        return result;
    }

    public List<Space> getTeamSpaces() {
        if (this.teamSpaces != null) {
            return this.teamSpaces;
        }
        String selectedTeam = this.getSelectedTeamLabelName();
        Label label = this.labelManager.getLabel("team:" + selectedTeam);
        if (label == null) {
            this.teamSpaces = Collections.emptyList();
        } else {
            List labelledSpaces = this.labelManager.getSpacesWithLabel(label);
            this.teamSpaces = this.retainViewPermissions(labelledSpaces);
        }
        return this.teamSpaces;
    }

    @Deprecated
    public Set<Space> getPermittedSpaces() {
        if (this.permittedSpaces == null) {
            this.permittedSpaces = new HashSet<Space>(this.spaceManager.getAllSpaces(this.createSpacesQueryBuilder().build()));
        }
        return this.permittedSpaces;
    }

    public ListBuilder<Space> getPermittedSpacesBuilder() {
        return this.spaceManager.getSpaces(this.createSpacesQueryBuilder().build());
    }

    protected String getSelectedTeamLabelName() {
        List<String> availableTeams = this.getViewableTeamLabels();
        return this.getSelectedTeamLabelName(availableTeams);
    }

    private String getSelectedTeamLabelName(List availableTeams) {
        if (availableTeams == null || availableTeams.size() == 0) {
            return "";
        }
        String existingPreference = this.getUserInterfaceState().getDashboardSpacesSelectedTeam();
        if (StringUtils.isNotEmpty((CharSequence)existingPreference) && availableTeams.contains(existingPreference)) {
            return existingPreference;
        }
        return (String)availableTeams.get(0);
    }

    public List<String> getViewableTeamLabels() {
        if (this.viewableTeamLabels != null) {
            return this.viewableTeamLabels;
        }
        List<Label> labels = this.getLabelsForPermittedSpaces();
        this.viewableTeamLabels = new ArrayList<String>(labels.size());
        for (Label label : labels) {
            this.viewableTeamLabels.add(label.getName());
        }
        return this.viewableTeamLabels;
    }

    private List<Label> getLabelsForPermittedSpaces() {
        HashSet result = new HashSet();
        ListBuilder<Space> permittedSpaces = this.getPermittedSpacesBuilder();
        int pageSize = this.spaceListPageSize;
        for (int i = 0; i < permittedSpaces.getAvailableSize(); i += pageSize) {
            List permittedSpacePage = permittedSpaces.getPage(i, pageSize);
            result.addAll(this.labelManager.getTeamLabelsForSpaces((Collection)permittedSpacePage));
        }
        return new ArrayList<Label>(result);
    }

    private ConfluenceUser getUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    protected UserInterfaceState getUserInterfaceState() {
        return new UserInterfaceState((User)this.getUser(), this.userAccessor);
    }

    public String getSpacesSelectedTab() {
        String result;
        String selectedTabParam = this.getRequestParameter("spacesSelectedTab");
        if (StringUtils.isNotEmpty((CharSequence)selectedTabParam)) {
            this.getUserInterfaceState().setDashboardSpacesTab(selectedTabParam);
            result = selectedTabParam;
        } else {
            if (this.getUser() == null) {
                return SpacesListMacro.Tabs.ALL.toString();
            }
            String existingPreference = this.getUserInterfaceState().getDashboardSpacesTab();
            result = StringUtils.isNotEmpty((CharSequence)existingPreference) ? existingPreference : this.DEFAULT_SPACES_TAB.toString();
        }
        return this.sanitiseSelectedSpacesTab(result);
    }

    protected String sanitiseSelectedSpacesTab(String spacesSelectedTab) {
        String defaultTab = this.DEFAULT_SPACES_TAB.toString();
        if (!SpacesListMacro.Tabs.isValidName(spacesSelectedTab)) {
            return defaultTab;
        }
        if (SpacesListMacro.Tabs.NEW.equals(spacesSelectedTab) && this.getNewSpaces().isEmpty()) {
            return defaultTab;
        }
        if (SpacesListMacro.Tabs.CATEGORY.equals(spacesSelectedTab) && this.getViewableTeamLabels().isEmpty()) {
            return defaultTab;
        }
        return spacesSelectedTab;
    }

    public String getRequestParameter(String key) {
        HttpServletRequest request = this.getRequest();
        if (request == null) {
            return null;
        }
        return request.getParameter(key);
    }

    public HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    public List<Space> getAllViewableSpaces() {
        List allSpaces = this.spaceManager.getAllSpaces(this.createSpacesQueryBuilder().withSpaceType(SpaceType.GLOBAL).build());
        Collections.sort(allSpaces, new SpaceComparator());
        return allSpaces;
    }

    private SpacesQuery.Builder createSpacesQueryBuilder() {
        SpacesQuery.Builder spacesQueryBuilder = SpacesQuery.newQuery().forUser((User)this.getUser());
        if (!this.includeArchivedSpaces) {
            spacesQueryBuilder.withSpaceStatus(SpaceStatus.CURRENT);
        }
        return spacesQueryBuilder;
    }
}

