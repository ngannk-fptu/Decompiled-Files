/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.actions.PaginationSupport
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.google.common.collect.Sets
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.pages.actions;

import bucket.core.actions.PaginationSupport;
import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SpacePermissionQueryFactory;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.PrefixQuery;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.confluence.spaces.actions.SpaceAware;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.google.common.collect.Sets;
import java.util.HashSet;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class AlphabeticalListPagesAction
extends AbstractSpaceAction
implements SpaceAware {
    private static final int ITEMS_PER_PAGE = 30;
    private static final String PLUGIN_KEY = "list-alphabetically";
    private static final String DEFAULT_LETTER = "";
    private String startsWith = "";
    private SearchManager searchManager;
    private final PaginationSupport paginationSupport = new PaginationSupport(30);
    private List<Searchable> results;
    private int startIndex;
    private SpacePermissionQueryFactory spacePermissionQueryFactory;
    private ContentPermissionsQueryFactory contentPermissionsQueryFactory;

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        GeneralUtil.setCookie("confluence.list.pages.cookie", PLUGIN_KEY);
        ISearch search = this.getSearch(this.startsWith, this.startIndex);
        SearchResults result = this.searchManager.search(search);
        this.results = this.searchManager.convertToEntities(result, SearchManager.EntityVersionPolicy.INDEXED_VERSION);
        this.paginationSupport.setStartIndex(this.startIndex);
        this.paginationSupport.setTotal(result.getUnfilteredResultsCount());
        return "success";
    }

    private ISearch getSearch(String startsWith, int startIndex) {
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        SearchQuery query = this.getQuery(startsWith, user);
        SearchQuery filter = this.getFilter(user);
        queryBuilder.addMust(query);
        if (filter != null) {
            queryBuilder.addFilter(filter);
        }
        return new ContentSearch(queryBuilder.build(), TitleSort.DEFAULT, startIndex, 30);
    }

    private SearchQuery getQuery(String titlePrefix, ConfluenceUser user) {
        HashSet searchQueries = Sets.newHashSet();
        searchQueries.add(new InSpaceQuery(this.getSpaceKey()));
        searchQueries.add(new ContentTypeQuery(ContentTypeEnum.PAGE));
        this.contentPermissionsQueryFactory.create(user).ifPresent(searchQueries::add);
        SearchQuery query = BooleanQuery.composeAndQuery(searchQueries);
        if (StringUtils.isBlank((CharSequence)titlePrefix)) {
            return query;
        }
        return BooleanQuery.andQuery(query, BooleanQuery.orQuery(new PrefixQuery("content-name-untokenized", titlePrefix.toLowerCase()), new PrefixQuery("content-name-untokenized", titlePrefix.toUpperCase())));
    }

    private SearchQuery getFilter(ConfluenceUser user) {
        if (!this.permissionManager.isSystemAdministrator(user)) {
            return this.spacePermissionQueryFactory.create(user);
        }
        return null;
    }

    public List<Searchable> getResults() {
        return this.results;
    }

    public void setStartsWith(String startsWith) {
        this.startsWith = StringUtils.isBlank((CharSequence)startsWith) || startsWith.length() > 1 || !StringUtils.isAlpha((CharSequence)startsWith) ? DEFAULT_LETTER : startsWith;
    }

    public String getStartsWith() {
        return this.startsWith;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public PaginationSupport getPaginationSupport() {
        return this.paginationSupport;
    }

    public void setSearchManager(SearchManager searchManager) {
        this.searchManager = searchManager;
    }

    @Override
    public boolean isSpaceRequired() {
        return true;
    }

    @Override
    public boolean isViewPermissionRequired() {
        return false;
    }

    public void setSpacePermissionQueryFactory(SpacePermissionQueryFactory spacePermissionQueryFactory) {
        this.spacePermissionQueryFactory = spacePermissionQueryFactory;
    }

    public void setContentPermissionsQueryFactory(ContentPermissionsQueryFactory contentPermissionsQueryFactory) {
        this.contentPermissionsQueryFactory = contentPermissionsQueryFactory;
    }
}

