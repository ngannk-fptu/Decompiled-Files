/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.service.content.SpaceService
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.DefaultSearch
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.lucene.SearchIndex
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ConstantScoreQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.query.TermSetQuery
 *  com.atlassian.confluence.search.v2.query.TextFieldQuery
 *  com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.features.DarkFeatureManager
 *  com.google.common.base.Suppliers
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.retentionrules.impl.service;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Space;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.service.content.SpaceService;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.DefaultSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.lucene.SearchIndex;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TermSetQuery;
import com.atlassian.confluence.search.v2.query.TextFieldQuery;
import com.atlassian.confluence.search.v2.query.WildcardTextFieldQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.features.DarkFeatureManager;
import com.google.common.base.Suppliers;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SearchService {
    private static final Logger logger = LoggerFactory.getLogger(SearchService.class);
    private final int SPACES_THRESHOLD = Integer.getInteger("confluence.retention.search.space.threshold", 60000);
    private final int RETENTION_POLICIES_THRESHOLD = Integer.getInteger("confluence.retention.search.policies.threshold", 10000);
    private static final String USE_ONLY_INDEX_FOR_SEARCH_FILTER_DF = "confluence.retention.search.policies.index.only";
    private static final int SEARCH_LIMIT = 20;
    private static final Supplier<SearchQuery> contentQuerySupplier = () -> new ContentTypeQuery(Arrays.asList(ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION));
    private static final Supplier<SearchQuery> retentionPolicyQuerySupplier = () -> new TermQuery("retentionPolicy", "true");
    private final Supplier<Integer> totalSpacesSupplier = Suppliers.memoizeWithExpiration(() -> {
        try {
            return this.getApproximateNumberOfSpaces();
        }
        catch (InvalidSearchException e) {
            logger.error("Error fetching approximate number of spaces: {}", (Object)e.getMessage());
            logger.debug("Error fetching approximate number of spaces", (Throwable)e);
            return this.SPACES_THRESHOLD;
        }
    }, (long)30L, (TimeUnit)TimeUnit.MINUTES);
    private final Supplier<Integer> totalRetentionRulesSupplier = Suppliers.memoizeWithExpiration(() -> {
        try {
            return this.getApproximateNumberOfSpacesWithRetentionRules();
        }
        catch (InvalidSearchException e) {
            logger.error("Error fetching approximate number of retention policies overrides: {}", (Object)e.getMessage());
            logger.debug("Error fetching approximate number of retention policies overrides", (Throwable)e);
            return this.RETENTION_POLICIES_THRESHOLD;
        }
    }, (long)10L, (TimeUnit)TimeUnit.MINUTES);
    private final SearchManager searchManager;
    private final SpaceService spaceService;
    private final DarkFeatureManager darkFeatureManager;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;

    @Autowired
    public SearchService(@ComponentImport SearchManager searchManager, @ComponentImport SpaceService spaceService, @ComponentImport @Qualifier(value="darkFeatureManager") DarkFeatureManager darkFeatureManager, @ComponentImport SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory) {
        this.searchManager = searchManager;
        this.spaceService = spaceService;
        this.darkFeatureManager = darkFeatureManager;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
    }

    public SearchResults spaces(String titleOrSpaceKey, boolean spacesWithRetentionPoliciesOnly) throws InvalidSearchException {
        boolean useDbForRetentionPoliciesSearch = this.useDbForRetentionPoliciesSearch();
        DefaultSearch search = new DefaultSearch(EnumSet.of(SearchIndex.CONTENT), this.searchQuery(titleOrSpaceKey, spacesWithRetentionPoliciesOnly, useDbForRetentionPoliciesSearch), null, 0, 20){

            public String getSearchType() {
                return "SiteSearch";
            }
        };
        return this.searchManager.search((ISearch)search);
    }

    private boolean useDbForRetentionPoliciesSearch() {
        boolean isForceCqlSearchDarkFeatureEnabled = this.darkFeatureManager.isEnabledForAllUsers(USE_ONLY_INDEX_FOR_SEARCH_FILTER_DF).orElse(false);
        int totalSpaces = this.totalSpacesSupplier.get();
        int totalRetentionRules = this.totalRetentionRulesSupplier.get();
        logger.debug("CQL search is enforced: {}; Total number of spaces: {}; Total number of retention rules: {}", new Object[]{isForceCqlSearchDarkFeatureEnabled, totalSpaces, totalRetentionRules});
        return !isForceCqlSearchDarkFeatureEnabled && totalSpaces < this.SPACES_THRESHOLD && totalRetentionRules < this.RETENTION_POLICIES_THRESHOLD;
    }

    private SearchQuery searchQuery(String titleOrSpaceKey, boolean spacesWithRetentionPoliciesOnly, boolean useDbForRetentionPoliciesSearch) {
        BooleanQuery.Builder searchQueryBuilder = BooleanQuery.builder();
        Object wildcardToken = titleOrSpaceKey.trim().endsWith("*") ? titleOrSpaceKey.trim() : titleOrSpaceKey.trim() + "*";
        WildcardTextFieldQuery titleFieldWildcardQuery = new WildcardTextFieldQuery(SearchFieldNames.TITLE, (String)wildcardToken, BooleanOperator.AND);
        WildcardTextFieldQuery contentNameWildcardQuery = new WildcardTextFieldQuery(SearchFieldNames.CONTENT_NAME_UNSTEMMED, (String)wildcardToken, BooleanOperator.AND);
        ConstantScoreQuery spaceKeyQuery = new ConstantScoreQuery((SearchQuery)new TextFieldQuery(SearchFieldNames.SPACE_KEY, titleOrSpaceKey, BooleanOperator.AND), 2.0f);
        SearchQuery fullTextSearchQuery = BooleanQuery.orQuery((SearchQuery[])new SearchQuery[]{titleFieldWildcardQuery, contentNameWildcardQuery, spaceKeyQuery});
        searchQueryBuilder.addMust((Object)fullTextSearchQuery);
        SearchQuery contentAndRetentionPolicyQuery = spacesWithRetentionPoliciesOnly && !useDbForRetentionPoliciesSearch ? BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{contentQuerySupplier.get(), retentionPolicyQuerySupplier.get()}) : contentQuerySupplier.get();
        searchQueryBuilder.addMust((Object)contentAndRetentionPolicyQuery);
        if (spacesWithRetentionPoliciesOnly && useDbForRetentionPoliciesSearch) {
            searchQueryBuilder.addFilter((SearchQuery)new TermSetQuery(SearchFieldNames.SPACE_KEY, this.spacesWithRetentionPolicies()));
        }
        searchQueryBuilder.addFilter(this.siteSearchPermissionsQueryFactory.create());
        return searchQueryBuilder.build();
    }

    private Set<String> spacesWithRetentionPolicies() {
        PageResponse response;
        int batchSize = 1000;
        HashSet<String> result = new HashSet<String>();
        int start = 0;
        do {
            response = this.spaceService.find(new Expansion[0]).withHasRetentionPolicy(true).fetchMany((PageRequest)new SimplePageRequest(start, 1000));
            result.addAll(response.getResults().stream().map(Space::getKey).collect(Collectors.toList()));
            start += 1000;
        } while (response.hasMore());
        return result;
    }

    private int getApproximateNumberOfSpaces() throws InvalidSearchException {
        return this.searchManager.search(this.createSearchToEstimateScale(contentQuerySupplier.get())).getUnfilteredResultsCount();
    }

    private int getApproximateNumberOfSpacesWithRetentionRules() throws InvalidSearchException {
        return this.searchManager.search(this.createSearchToEstimateScale(BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{contentQuerySupplier.get(), retentionPolicyQuerySupplier.get()}))).getUnfilteredResultsCount();
    }

    private ISearch createSearchToEstimateScale(SearchQuery query) {
        return new DefaultSearch(EnumSet.of(SearchIndex.CONTENT), query, null, 0, 1){

            public String getSearchType() {
                return "SiteSearch";
            }
        };
    }
}

