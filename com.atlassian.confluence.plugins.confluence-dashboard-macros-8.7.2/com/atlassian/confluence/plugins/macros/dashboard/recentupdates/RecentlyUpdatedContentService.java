/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.ui.ContentUiSupport
 *  com.atlassian.confluence.core.DateFormatter
 *  com.atlassian.confluence.core.FormatSettingsManager
 *  com.atlassian.confluence.core.datetime.FriendlyDateFormatter
 *  com.atlassian.confluence.core.datetime.RequestTimeThreadLocal
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.RecentUpdateQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.ConfluenceUserPreferences
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.user.UserInterfaceState
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.confluence.util.profiling.ConfluenceMonitoring
 *  com.atlassian.confluence.util.profiling.Split
 *  com.atlassian.confluence.web.context.HttpContext
 *  com.atlassian.core.AtlassianCoreException
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.macros.dashboard.recentupdates;

import com.atlassian.confluence.content.ui.ContentUiSupport;
import com.atlassian.confluence.core.DateFormatter;
import com.atlassian.confluence.core.FormatSettingsManager;
import com.atlassian.confluence.core.datetime.FriendlyDateFormatter;
import com.atlassian.confluence.core.datetime.RequestTimeThreadLocal;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentUpdate;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.RecentUpdateGroup;
import com.atlassian.confluence.plugins.macros.dashboard.recentupdates.events.DashboardRecentlyUpdatedQueryEvent;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.ConfluenceUserPreferences;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.user.UserInterfaceState;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.confluence.util.profiling.ConfluenceMonitoring;
import com.atlassian.confluence.util.profiling.Split;
import com.atlassian.confluence.web.context.HttpContext;
import com.atlassian.core.AtlassianCoreException;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RecentlyUpdatedContentService {
    private static final Logger log = LoggerFactory.getLogger(RecentlyUpdatedContentService.class);
    private PredefinedSearchBuilder searchBuilder;
    private SearchManager searchManager;
    private UserAccessor userAccessor;
    private FormatSettingsManager formatSettingsManager;
    private LocaleManager localeManager;
    private HttpContext httpContext;
    private PermissionManager permissionManager;
    private I18NBeanFactory i18NBeanFactory;
    private ContentUiSupport contentUiSupport;
    private final ConfluenceMonitoring confluenceMonitoring;
    private final EventPublisher eventPublisher;

    @Autowired
    public RecentlyUpdatedContentService(@ComponentImport ContentUiSupport contentUiSupport, @ComponentImport I18NBeanFactory i18NBeanFactory, @ComponentImport PermissionManager permissionManager, @ComponentImport HttpContext httpContext, @ComponentImport LocaleManager localeManager, @ComponentImport FormatSettingsManager formatSettingsManager, @ComponentImport UserAccessor userAccessor, @ComponentImport SearchManager searchManager, @ComponentImport PredefinedSearchBuilder searchBuilder, @ComponentImport ConfluenceMonitoring confluenceMonitoring, @ComponentImport EventPublisher eventPublisher) {
        this.contentUiSupport = contentUiSupport;
        this.i18NBeanFactory = i18NBeanFactory;
        this.permissionManager = permissionManager;
        this.httpContext = httpContext;
        this.localeManager = localeManager;
        this.formatSettingsManager = formatSettingsManager;
        this.userAccessor = userAccessor;
        this.searchManager = searchManager;
        this.searchBuilder = searchBuilder;
        this.confluenceMonitoring = confluenceMonitoring;
        this.eventPublisher = eventPublisher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<RecentUpdateGroup> getRecentUpdates(RecentUpdateQueryParameters queryParams, int maxResults) throws InvalidSearchException {
        SearchResults searchResults;
        if (queryParams.getSpaceKeys() != null && queryParams.getSpaceKeys().isEmpty()) {
            return Collections.emptyList();
        }
        if (queryParams.getLabels() != null && queryParams.getLabels().isEmpty()) {
            return Collections.emptyList();
        }
        if (queryParams.getFollowingUsers() != null && queryParams.getFollowingUsers().isEmpty()) {
            return Collections.emptyList();
        }
        if (queryParams.getContentTypes() != null && queryParams.getContentTypes().isEmpty()) {
            return Collections.emptyList();
        }
        ISearch search = this.searchBuilder.buildRecentUpdateSearch(queryParams, 0, maxResults);
        long start = System.currentTimeMillis();
        Split split = this.confluenceMonitoring.startSplit("dashboard.recentlyupdatedquery");
        try {
            searchResults = this.searchManager.search(search);
        }
        finally {
            split.stop();
        }
        long end = System.currentTimeMillis();
        this.eventPublisher.publish((Object)new DashboardRecentlyUpdatedQueryEvent(this, end - start, searchResults.size()));
        ArrayList results = Lists.newArrayList((Iterator)searchResults.iterator());
        return this.groupRecentUpdates(results);
    }

    public void setPreferredTab(String tabName) {
        User user = this.getRemoteUser();
        if (user == null) {
            return;
        }
        try {
            this.userAccessor.getUserPreferences(user).setString("confluence.macros.dashboard.selected.tab", tabName);
        }
        catch (AtlassianCoreException e) {
            log.error("Failed to set user preference confluence.macros.dashboard.selected.tab", (Throwable)e);
        }
    }

    public String getPreferredTab() {
        User user = this.getRemoteUser();
        if (user == null) {
            return null;
        }
        return this.userAccessor.getUserPreferences(user).getString("confluence.macros.dashboard.selected.tab");
    }

    public void setPreferredMaxResults(int numResults) {
        if (this.getRemoteUser() == null) {
            return;
        }
        this.getUserInterfaceState().setMaxRecentChangesSize(numResults);
    }

    public int getPreferredMaxResults() {
        return this.getUserInterfaceState().getMaxRecentChangesSize();
    }

    private UserInterfaceState getUserInterfaceState() {
        return new UserInterfaceState(this.getRemoteUser(), this.userAccessor);
    }

    private User getRemoteUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    private List<RecentUpdateGroup> groupRecentUpdates(Collection<SearchResult> results) {
        ConfluenceUserPreferences pref = this.userAccessor.getConfluenceUserPreferences((User)AuthenticatedUserThreadLocal.get());
        DateFormatter dateFormatter = new DateFormatter(pref.getTimeZone(), this.formatSettingsManager, this.localeManager);
        FriendlyDateFormatter friendlyDateFormatter = new FriendlyDateFormatter(RequestTimeThreadLocal.getTimeOrNow(), dateFormatter);
        String contextPath = this.httpContext.getRequest().getContextPath();
        LinkedList<RecentUpdateGroup> groupedResults = new LinkedList<RecentUpdateGroup>();
        Object lastModifier = null;
        RecentUpdateGroup updateGroup = null;
        for (SearchResult result : results) {
            ConfluenceUser modifier = result.getLastModifierUser();
            boolean differentUser = lastModifier == null && modifier == null ? false : (lastModifier != null ? !lastModifier.equals(modifier) : true);
            if (updateGroup == null || groupedResults.isEmpty() || differentUser) {
                lastModifier = modifier;
                boolean canView = this.permissionManager.hasPermission((User)AuthenticatedUserThreadLocal.get(), Permission.VIEW, (Object)modifier);
                updateGroup = new RecentUpdateGroup((User)modifier, this.userAccessor.getUserProfilePicture((User)modifier), canView);
                groupedResults.add(updateGroup);
            }
            updateGroup.add(new RecentUpdate(result, friendlyDateFormatter, contextPath, this.contentUiSupport, this.i18NBeanFactory));
        }
        return groupedResults;
    }
}

