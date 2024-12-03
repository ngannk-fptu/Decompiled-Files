/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.ParameterSafe
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  com.opensymphony.xwork2.ActionContext
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.user.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.admin.criteria.CanInviteUserCriteria;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.people.PeopleDirectoryViewEvent;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneSearchResults;
import com.atlassian.confluence.pages.ManualTotalPaginationSupport;
import com.atlassian.confluence.plugin.descriptor.web.conditions.PeopleDirectoryEnabledCondition;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.HasPersonalSpaceQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.UserTextQuery;
import com.atlassian.confluence.search.v2.sort.FullnameSort;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.PersonalInformation;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.ParameterSafe;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.opensymphony.xwork2.ActionContext;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeopleDirectoryAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(PeopleDirectoryAction.class);
    public static final int MAX_PEOPLE_PER_PAGE = 50;
    public static final int BLANK_EXPERIENCE_USER_THRESHOLD = 4;
    private SearchManager searchManager;
    private SearchQueryBean searchQueryBean;
    private String queryString = "";
    private Set<String> searchResults = Sets.newLinkedHashSet();
    private int startIndex;
    public static final String BROWSE_PEOPLE = "browsepeople";
    private boolean showOnlyPersonal;
    private boolean showShadowedUsers;
    private boolean showExternallyDeletedUsers;
    private boolean showDeactivatedUsers;
    private boolean showUnlicensedUsers;
    private CanInviteUserCriteria canInviteUserCriteria;
    private boolean showBlankExperience;
    private boolean hasUserCreationPermission;
    private EventPublisher eventPublisher;
    private SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private PaginationSupport<Searchable> paginationSupport = new ManualTotalPaginationSupport<Searchable>(50);

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void validate() {
        if (this.getQueryString() != null && this.getQueryString().startsWith("*")) {
            this.addFieldError("queryString", this.getText("people.search.error.wildcard"));
        }
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        if (StringUtils.isNotEmpty((CharSequence)this.getOldSearchString())) {
            return "oldsearch";
        }
        return BROWSE_PEOPLE;
    }

    private boolean isPeopleDirectoryDisabled() {
        PeopleDirectoryEnabledCondition condition = new PeopleDirectoryEnabledCondition();
        condition.setPermissionManager(this.permissionManager);
        return condition.isPeopleDirectoryDisabled(this.getAuthenticatedUser());
    }

    public String getSelectedTab() {
        return (String)ActionContext.getContext().getSession().get("confluence.user.dir.selected.tab");
    }

    public void setSelectedTab(String selectedTab) {
        ActionContext.getContext().getSession().put("confluence.user.dir.selected.tab", selectedTab);
    }

    public String getOldSearchString() {
        return (String)ActionContext.getContext().getSession().get("confluence.user.dir.search.string");
    }

    public String getOldStartIndex() {
        return (String)ActionContext.getContext().getSession().get("confluence.user.dir.start.index");
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doSearch() {
        ActionContext.getContext().getSession().put("confluence.user.dir.search.string", this.getQueryString());
        this.search();
        return "success";
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String doBrowse() {
        this.setQueryString("");
        this.search();
        return "success";
    }

    @Override
    public boolean isPermitted() {
        if (this.permissionManager.isConfluenceAdministrator(this.getAuthenticatedUser())) {
            return true;
        }
        if (this.isPeopleDirectoryDisabled()) {
            return false;
        }
        return super.isPermitted();
    }

    private void search() {
        this.eventPublisher.publish((Object)new PeopleDirectoryViewEvent(this));
        BooleanQuery.Builder boolQuery = new BooleanQuery.Builder();
        boolQuery.addMust(this.makeSearchQuery());
        SearchQuery searchFilter = this.makeSearchFilter();
        if (searchFilter != null) {
            boolQuery.addFilter(this.makeSearchFilter());
        }
        SearchResults results = this.performSearch(boolQuery.build());
        List<Searchable> resultObjects = this.searchManager.convertToEntities(results, SearchManager.EntityVersionPolicy.LATEST_VERSION);
        this.paginationSupport.setStartIndex(this.startIndex);
        this.paginationSupport.setTotal(results.getUnfilteredResultsCount());
        this.paginationSupport.setItems(resultObjects);
        for (Searchable resultObject : resultObjects) {
            PersonalInformation personalInfo = (PersonalInformation)resultObject;
            this.searchResults.add(personalInfo.getUsername());
        }
        if (this.isShowingAllPeople()) {
            this.determineBlankExperience();
        }
    }

    private SearchResults performSearch(SearchQuery query) {
        try {
            return this.searchManager.search(new ContentSearch(query, FullnameSort.ASCENDING, this.startIndex, 50));
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException("Invalid search: " + e, e);
        }
        catch (RuntimeException e) {
            log.info("Error executing people directory search, returning nothing. " + e, (Throwable)e);
            return LuceneSearchResults.EMPTY_RESULTS;
        }
    }

    private SearchQuery makeSearchFilter() {
        BooleanQuery.Builder searchFilterBuilder = BooleanQuery.builder();
        if (!this.showDeactivatedUsers) {
            searchFilterBuilder.addMust(new TermQuery(SearchFieldNames.IS_DEACTIVATED_USER, Boolean.FALSE.toString()));
        }
        if (!this.showExternallyDeletedUsers) {
            searchFilterBuilder.addMust(new TermQuery(SearchFieldNames.IS_EXTERNALLY_DELETED_USER, Boolean.FALSE.toString()));
        }
        if (!this.showShadowedUsers) {
            searchFilterBuilder.addMust(new TermQuery(SearchFieldNames.IS_SHADOWED_USER, Boolean.FALSE.toString()));
        }
        if (!this.showUnlicensedUsers) {
            searchFilterBuilder.addMust(new TermQuery(SearchFieldNames.IS_LICENSED_USER, Boolean.TRUE.toString()));
        }
        if (searchFilterBuilder.isEmpty()) {
            return null;
        }
        return searchFilterBuilder.build();
    }

    private SearchQuery makeSearchQuery() {
        HashSet searchTerms = Sets.newHashSet();
        searchTerms.add(new ContentTypeQuery(ContentTypeEnum.PERSONAL_INFORMATION));
        if (StringUtils.isNotBlank((CharSequence)this.getQueryString())) {
            searchTerms.add(new UserTextQuery(this.getQueryString()));
        }
        if (this.isShowOnlyPersonal()) {
            searchTerms.add(new HasPersonalSpaceQuery());
        }
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        searchQueryBuilder.addMust(searchTerms);
        searchQueryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        return searchQueryBuilder.build();
    }

    public PaginationSupport<Searchable> getPaginationSupport() {
        return this.paginationSupport;
    }

    @ParameterSafe
    public SearchQueryBean getSearchQueryBean() {
        if (this.searchQueryBean == null) {
            this.searchQueryBean = new SearchQueryBean();
        }
        return this.searchQueryBean;
    }

    public List<String> getSearchResults() {
        return Lists.newArrayList(this.searchResults);
    }

    public int getStartIndex() {
        return this.startIndex;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
        ActionContext.getContext().getSession().put("confluence.user.dir.start.index", Integer.toString(startIndex));
    }

    public boolean isShowOnlyPersonal() {
        return this.showOnlyPersonal;
    }

    public void setShowOnlyPersonal(boolean showOnlyPersonal) {
        this.showOnlyPersonal = showOnlyPersonal;
    }

    public boolean isShowDeactivatedUsers() {
        return this.showDeactivatedUsers;
    }

    public void setShowDeactivatedUsers(boolean showDeactivatedUsers) {
        this.showDeactivatedUsers = showDeactivatedUsers;
    }

    public boolean isShowShadowedUsers() {
        return this.showShadowedUsers;
    }

    public void setShowShadowedUsers(boolean showShadowedUsers) {
        this.showShadowedUsers = showShadowedUsers;
    }

    public boolean isShowExternallyDeletedUsers() {
        return this.showExternallyDeletedUsers;
    }

    public void setShowExternallyDeletedUsers(boolean showExternallyDeletedUsers) {
        this.showExternallyDeletedUsers = showExternallyDeletedUsers;
    }

    public boolean isShowUnlicensedUsers() {
        return this.showUnlicensedUsers;
    }

    public void setShowUnlicensedUsers(boolean showUnlicensedUsers) {
        this.showUnlicensedUsers = showUnlicensedUsers;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    public void setCanInviteUserCriteria(CanInviteUserCriteria canInviteUserCriteria) {
        this.canInviteUserCriteria = canInviteUserCriteria;
    }

    public boolean isShowBlankExperience() {
        return this.showBlankExperience;
    }

    public boolean isHasUserCreationPermission() {
        return this.hasUserCreationPermission;
    }

    private void determineBlankExperience() {
        this.showBlankExperience = this.searchResults.size() < 4 && this.canInviteUserCriteria.isMet();
        this.hasUserCreationPermission = this.permissionManager.hasCreatePermission((User)AuthenticatedUserThreadLocal.get(), PermissionManager.TARGET_APPLICATION, User.class);
    }

    private boolean isShowingAllPeople() {
        return this.queryString.isEmpty() && !this.showOnlyPersonal;
    }

    public void setSiteSearchPermissionsQueryFactory(SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    private class SearchQueryBean {
        private SearchQueryBean() {
        }

        public String getQueryString() {
            return PeopleDirectoryAction.this.queryString;
        }

        public void setQueryString(String queryString) {
            PeopleDirectoryAction.this.queryString = queryString;
        }
    }
}

