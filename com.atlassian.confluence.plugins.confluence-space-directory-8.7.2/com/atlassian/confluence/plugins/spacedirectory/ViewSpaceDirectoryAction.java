/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.PaginationSupport
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.labels.Label
 *  com.atlassian.confluence.pages.ManualTotalPaginationSupport
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.sort.TitleSort
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceLogoManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.spacedirectory;

import bucket.core.PaginationSupport;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.pages.ManualTotalPaginationSupport;
import com.atlassian.confluence.plugins.spacedirectory.events.SpaceDirectoryViewEvent;
import com.atlassian.confluence.plugins.spacedirectory.rest.SpaceDirectoryScope;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceLogoManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ViewSpaceDirectoryAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ViewSpaceDirectoryAction.class);
    public static final int BLANK_EXPERIENCE_SPACE_THRESHOLD = 4;
    private SpaceManager spaceManager;
    private int startIndex;
    private static final int DEFAULT_PAGE_SIZE = 24;
    private String teamLabel;
    private List<Space> spaceList;
    private int totalSize = 0;
    private PaginationSupport<Space> paginationSupport;
    private PredefinedSearchBuilder predefinedSearchBuilder;
    private SpaceLogoManager spaceLogoManager;
    private SearchManager searchManager;
    private String selectedSpaceCategory;
    private boolean showBlankExperience;
    private boolean hasCreatePermission;
    private EventPublisher eventPublisher;

    public ViewSpaceDirectoryAction(SpaceManager spaceManager, SpaceLogoManager spaceLogoManager, PredefinedSearchBuilder predefinedSearchBuilder, SearchManager searchManager) {
        this.searchManager = searchManager;
        this.startIndex = 0;
        this.spaceManager = spaceManager;
        this.spaceLogoManager = spaceLogoManager;
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    public PaginationSupport<Space> getPaginationSupport() {
        if (this.paginationSupport == null) {
            this.initSpaces();
            this.paginationSupport = new ManualTotalPaginationSupport(this.spaceList, this.startIndex, this.totalSize, 24);
        }
        return this.paginationSupport;
    }

    private void initSpaces() {
        this.spaceList = new ArrayList<Space>();
        SearchQueryParameters params = new SearchQueryParameters();
        params.setContentTypes(SpaceDirectoryScope.GLOBAL.getContentTypes());
        params.setSort((SearchSort)TitleSort.ASCENDING);
        ISearch search = this.predefinedSearchBuilder.buildSiteSearch(params, this.startIndex, 24);
        try {
            SearchResults searchResults = this.searchManager.search(search);
            this.totalSize = searchResults.getUnfilteredResultsCount();
            for (SearchResult searchResult : searchResults) {
                Space space = this.spaceManager.getSpace(searchResult.getSpaceKey());
                if (space == null) continue;
                this.spaceList.add(space);
            }
        }
        catch (InvalidSearchException e) {
            log.error("Error while searching for spaces", (Throwable)e);
        }
    }

    public String convertToNiceTeamLabel(String teamLabel) {
        return StringUtils.capitalize((String)teamLabel);
    }

    public List<Space> getSpaces() {
        return this.getPaginationSupport().getPage();
    }

    public List<Label> getTeamLabels() {
        return this.labelManager.getTeamLabels();
    }

    public String getTeamLabel() {
        return this.teamLabel;
    }

    public String getLogoDownloadPath(Space space) {
        return this.spaceLogoManager.getLogoDownloadPath(space, (User)AuthenticatedUserThreadLocal.get());
    }

    public int getPageSize() {
        return 24;
    }

    public void setTeamLabel(String teamLabel) {
        this.teamLabel = teamLabel;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public void setSelectedSpaceCategory(String selectedSpaceCategory) {
        this.selectedSpaceCategory = selectedSpaceCategory;
    }

    public String getSelectedSpaceCategory() {
        return this.selectedSpaceCategory;
    }

    public boolean isShowBlankExperience() {
        return this.showBlankExperience;
    }

    public boolean isHasCreatePermission() {
        return this.hasCreatePermission;
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.initSpaces();
        this.paginationSupport = new ManualTotalPaginationSupport(this.spaceList, this.startIndex, this.totalSize, 24);
        this.hasCreatePermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, Space.class);
        this.showBlankExperience = this.determineShowBlankExperience();
        this.eventPublisher.publish((Object)new SpaceDirectoryViewEvent(this));
        return "success";
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    private boolean determineShowBlankExperience() {
        return this.totalSize < 4;
    }
}

