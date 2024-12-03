/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.service.DateRangeEnum
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.service.SearchQueryParameters
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.DefaultSearchResults
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRange
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.user.User
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Strings
 *  javax.ws.rs.POST
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest;

import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.events.KbSearchPerformedEvent;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.SearchWithHighlight;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.request.SearchRequest;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.KbSearchResult;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.rest.response.KbSearchResults;
import com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services.SearchResultAugmenterV2;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.service.DateRangeEnum;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.DefaultSearchResults;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Option;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.user.User;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path(value="/searchV2")
@Produces(value={"application/json;charset=UTF-8"})
public class SearchResourceV2 {
    private static final int DEFAULT_MAX_PAGE_SIZE = 50;
    private final TransactionTemplate transactionTemplate;
    private final EventPublisher eventPublisher;
    private final SearchResultAugmenterV2 searchResultAugmenter;
    private final UserAccessor userAccessor;
    private final SearchManager searchManager;
    private final PredefinedSearchBuilder predefinedSearchBuilder;

    public SearchResourceV2(TransactionTemplate transactionTemplate, EventPublisher eventPublisher, SearchResultAugmenterV2 searchResultAugmenter, UserAccessor userAccessor, SearchManager searchManager, PredefinedSearchBuilder predefinedSearchBuilder) {
        this.transactionTemplate = transactionTemplate;
        this.eventPublisher = eventPublisher;
        this.searchResultAugmenter = searchResultAugmenter;
        this.userAccessor = userAccessor;
        this.searchManager = searchManager;
        this.predefinedSearchBuilder = predefinedSearchBuilder;
    }

    @POST
    @AnonymousAllowed
    public Response search(SearchRequest searchRequest) {
        SearchQueryParameters searchQueryParameters = this.buildSearchQueryParameters(SearchResourceV2.sanitizeQuery(searchRequest.query), searchRequest.type, searchRequest.where, searchRequest.lastModified, searchRequest.contributor, searchRequest.contributorUsername, searchRequest.includeArchivedSpaces, searchRequest.labels, searchRequest.spaceKeys);
        SearchResults rawResults = this.executeSearch(searchQueryParameters, searchRequest.startIndex, searchRequest.pageSize, searchRequest.highlight);
        Option permCheckUserOpt = Option.option((Object)this.userAccessor.getUserByName(searchRequest.user));
        DefaultSearchResults searchResultsWithPermissionChecks = new DefaultSearchResults(this.searchResultAugmenter.addViewPermissionChecksToResults(rawResults.getAll(), (Option<ConfluenceUser>)permCheckUserOpt), rawResults.getUnfilteredResultsCount());
        KbSearchResults searchResults = this.mapResultsToRestResponse((SearchResults)searchResultsWithPermissionChecks, searchRequest.highlight);
        return Response.ok((Object)searchResults).build();
    }

    private KbSearchResults mapResultsToRestResponse(SearchResults results, boolean highlight) {
        return new KbSearchResults(results.getAll().stream().map(article -> new KbSearchResult(article.getHandleId(), highlight ? article.getDisplayTitleWithHighlights() : article.getDisplayTitle(), highlight ? article.getResultExcerptWithHighlights() : article.getResultExcerpt(), article.getUrlPath(), article.getType(), article.getExtraFields(), article.getSpaceKey(), article.getSpaceName(), null)).collect(Collectors.toList()), results.getUnfilteredResultsCount());
    }

    private SearchQueryParameters buildSearchQueryParameters(String query, String type, String where, String lastModified, String contributor, String contributorUsername, boolean includeArchivedSpaces, Set<String> labels, Set<String> spaceKeys) {
        ConfluenceUser contributorUser = this.userAccessor.getUserByName(Strings.isNullOrEmpty((String)contributorUsername) ? contributor : contributorUsername);
        SearchQueryParameters searchQuery = new SearchQueryParameters(query);
        searchQuery.setSpaceKeys(spaceKeys);
        if (!Strings.isNullOrEmpty((String)type)) {
            searchQuery.setContentType(ContentTypeEnum.getByRepresentation((String)type));
        }
        if (!Strings.isNullOrEmpty((String)where)) {
            searchQuery.setCategory(SpaceCategoryEnum.get((String)where));
        }
        if (contributorUser != null) {
            searchQuery.setContributor(contributorUser);
        }
        searchQuery.setIncludeArchivedSpaces(includeArchivedSpaces);
        searchQuery.setLabels(labels);
        if (!Strings.isNullOrEmpty((String)lastModified)) {
            try {
                DateRangeEnum lastModifiedDateRange = DateRangeEnum.valueOf((String)lastModified);
                DateRangeQuery.DateRange lastModifiedRange = lastModifiedDateRange.dateRange();
                searchQuery.setLastModified(lastModifiedRange);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
        return searchQuery;
    }

    private SearchResults executeSearch(SearchQueryParameters searchQueryParameters, int startIndex, Integer pageSize, boolean highlight) {
        return (SearchResults)this.transactionTemplate.execute(() -> {
            SearchResults searchResults;
            ISearch search = this.predefinedSearchBuilder.buildSiteSearch(searchQueryParameters, startIndex, Math.min(50, pageSize == null ? 50 : pageSize));
            SearchWithHighlight searchWithHighlight = new SearchWithHighlight(search, highlight);
            try {
                searchResults = this.searchManager.search((ISearch)searchWithHighlight);
            }
            catch (InvalidSearchException e) {
                return new DefaultSearchResults(Collections.emptyList(), 0);
            }
            this.publishKbSearchPerformedEvent(searchQueryParameters, searchResults);
            return searchResults;
        });
    }

    private void publishKbSearchPerformedEvent(SearchQueryParameters searchQuery, SearchResults searchResults) {
        SearchQuery searchV2Query = searchQuery.getSearchQueryFilter();
        this.eventPublisher.publish((Object)new KbSearchPerformedEvent(this, searchV2Query, (User)AuthenticatedUserThreadLocal.get(), searchResults.getUnfilteredResultsCount()));
    }

    @VisibleForTesting
    static String sanitizeQuery(String query) {
        return query.replaceAll("[\\{\\}]", " ");
    }
}

