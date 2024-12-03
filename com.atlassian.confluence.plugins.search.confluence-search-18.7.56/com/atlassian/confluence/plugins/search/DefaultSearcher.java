/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.BooleanQueryBuilder
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.DefaultHighlightParams
 *  com.atlassian.confluence.search.v2.HightlightParams
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchFieldNames
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory
 *  com.atlassian.confluence.search.v2.lucene.LuceneUtils
 *  com.atlassian.confluence.search.v2.query.AllQuery
 *  com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContentStatusQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRange
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.search.v2.query.LabelsQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.SiteTextSearchQuery
 *  com.atlassian.confluence.search.v2.query.SpaceCategoryQuery
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.confluence.search.v2.query.TermRangeQuery
 *  com.atlassian.confluence.search.v2.sort.RelevanceSort
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.fugue.Option
 *  com.atlassian.plugin.PluginAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.user.User
 *  com.google.common.base.Function
 *  com.google.common.base.Strings
 *  com.google.common.collect.Iterables
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.search;

import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.search.api.Searcher;
import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchQueryParameters;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import com.atlassian.confluence.plugins.search.api.model.SearchResults;
import com.atlassian.confluence.plugins.search.model.SearchResultBuilder;
import com.atlassian.confluence.plugins.search.model.SearchResultBuilderFactory;
import com.atlassian.confluence.plugins.search.query.ApplyPrefixToLabelFunction;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.BooleanQueryBuilder;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.DefaultHighlightParams;
import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SiteSearchPermissionsQueryFactory;
import com.atlassian.confluence.search.v2.lucene.LuceneUtils;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.ArchivedSpacesQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentStatusQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelsQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.confluence.search.v2.query.NonViewableCustomContentTypeQuery;
import com.atlassian.confluence.search.v2.query.SiteTextSearchQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.confluence.search.v2.query.TermRangeQuery;
import com.atlassian.confluence.search.v2.sort.RelevanceSort;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.fugue.Option;
import com.atlassian.plugin.PluginAccessor;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.user.User;
import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="searcherv3")
@ExportAsService(value={Searcher.class})
public class DefaultSearcher
implements Searcher {
    private final SearchManager searchManager;
    private final SpaceManager spaceManager;
    private final LabelManager labelManager;
    private final UserAccessor userAccessor;
    private final SearchResultBuilderFactory resultBuilderFactory;
    private final ContentEntityManager contentEntityManager;
    private final SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory;
    private final PluginAccessor pluginAccessor;

    @Autowired
    public DefaultSearcher(@ComponentImport UserAccessor userAccessor, @ComponentImport LabelManager labelManager, @ComponentImport SpaceManager spaceManager, @ComponentImport SearchManager searchManager, @ComponentImport ContentEntityManager contentEntityManager, @ComponentImport SiteSearchPermissionsQueryFactory siteSearchPermissionsQueryFactory, @ComponentImport PluginAccessor pluginAccessor, SearchResultBuilderFactory resultBuilderFactory) {
        this.userAccessor = userAccessor;
        this.labelManager = labelManager;
        this.spaceManager = spaceManager;
        this.searchManager = searchManager;
        this.contentEntityManager = contentEntityManager;
        this.siteSearchPermissionsQueryFactory = siteSearchPermissionsQueryFactory;
        this.resultBuilderFactory = resultBuilderFactory;
        this.pluginAccessor = pluginAccessor;
    }

    @Override
    public SearchResults search(SearchQueryParameters params, final boolean explain) {
        com.atlassian.confluence.search.v2.SearchResults results;
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.addMust((Object)this.getQuery(params));
        queryBuilder.addFilter(this.getFilter(params));
        final Optional highlightParams = params.isHighlight() ? Optional.of(new DefaultHighlightParams((SearchQuery)MatchNoDocsQuery.getInstance())) : Optional.empty();
        ContentSearch search = new ContentSearch(queryBuilder.build(), (SearchSort)new RelevanceSort(), params.getStartIndex(), params.getPageSize()){

            public String getSearchType() {
                return "SiteSearch";
            }

            public Optional<HightlightParams> getHighlight() {
                return highlightParams;
            }

            public boolean isExplain() {
                return explain;
            }
        };
        long start = System.currentTimeMillis();
        try {
            results = this.searchManager.search((ISearch)search);
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
        List<SearchResult> elements = StreamSupport.stream(results.spliterator(), false).map(result -> {
            SearchResultBuilder builder = this.resultBuilderFactory.createBuilder(result.getType(), (User)AuthenticatedUserThreadLocal.get());
            long id = SearchResultBuilder.getId(result.getField(SearchFieldNames.HANDLE));
            ContentEntityObject contentEntity = this.contentEntityManager.getById(id);
            return builder.newSearchResult(arg_0 -> ((com.atlassian.confluence.search.v2.SearchResult)result).getField(arg_0), () -> ((com.atlassian.confluence.search.v2.SearchResult)result).getDisplayTitleWithHighlights(), () -> ((com.atlassian.confluence.search.v2.SearchResult)result).getResultExcerptWithHighlights(), () -> new SearchExplanation(result.getExplain().orElse(null), contentEntity));
        }).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        if (results.getUnfilteredResultsCount() == 0 && !params.isIncludeArchivedSpaces()) {
            int numberOfResultsInArchivedSpace = this.findNumberOfResultsInArchivedSpace(params);
            return new SearchResults(0, numberOfResultsInArchivedSpace, elements, end - start, results.getSearchQuery());
        }
        return new SearchResults(results.getUnfilteredResultsCount(), 0, elements, end - start, results.getSearchQuery());
    }

    @Override
    public List<SearchExplanation> explain(SearchQueryParameters params, long[] contentIds) {
        BooleanQuery.Builder queryBuilder = new BooleanQuery.Builder();
        queryBuilder.addMust((Object)this.getQuery(params));
        queryBuilder.addFilter(this.getFilter(params));
        ContentSearch search = new ContentSearch(queryBuilder.build(), null, params.getStartIndex(), params.getPageSize()){

            public String getSearchType() {
                return "SiteSearch";
            }
        };
        ArrayList<SearchExplanation> result = new ArrayList<SearchExplanation>();
        for (long id : contentIds) {
            String explanation = this.searchManager.explain((ISearch)search, id);
            result.add(new SearchExplanation(explanation, this.contentEntityManager.getById(id)));
        }
        return result;
    }

    private int findNumberOfResultsInArchivedSpace(SearchQueryParameters params) {
        SearchQueryParameters searchWithArchivedSpaces = SearchQueryParameters.newSearchQueryParameters(params).includeArchivedSpaces(true).build();
        SearchResults extendedSearchResults = this.search(searchWithArchivedSpaces, false);
        return extendedSearchResults.getTotalSize();
    }

    private SearchQuery getFilter(SearchQueryParameters params) {
        DateRangeQuery.DateRange lastModified;
        Date fromDate;
        BooleanQueryBuilder builder = BooleanQuery.builder().addMust((Object)this.siteSearchPermissionsQueryFactory.create()).addMust((Object)this.getIgnoredTypeFilter()).addMust((Object)this.getIgnoredUsersFilter());
        if (!params.isIncludeArchivedSpaces() && StringUtils.isBlank((CharSequence)params.getSpaceKey())) {
            builder.addMust((Object)new ArchivedSpacesQuery(false, this.spaceManager));
        }
        if (params.getSpaceCategory() != null && params.getSpaceCategory() != SpaceCategoryEnum.ALL) {
            builder.addMust((Object)new SpaceCategoryQuery(Collections.singleton(params.getSpaceCategory()), this.labelManager));
        }
        if (StringUtils.isNotBlank((CharSequence)params.getSpaceKey())) {
            builder.addMust((Object)new InSpaceQuery(Collections.singleton(params.getSpaceKey())));
        }
        if (params.getContentType() != null) {
            builder.addMust((Object)new TermQuery(SearchFieldNames.TYPE, params.getContentType().getRepresentation()));
        }
        Iterable optionLabels = Iterables.transform(params.getLabels(), (Function)ApplyPrefixToLabelFunction.getInstance());
        HashSet labels = new HashSet();
        for (Option item : optionLabels) {
            item.foreach(labels::add);
        }
        if (!labels.isEmpty()) {
            builder.addMust((Object)new LabelsQuery(labels));
        }
        if (params.getPluginContentType() != null) {
            builder.addMust((Object)new CustomContentTypeQuery(Collections.singleton(params.getPluginContentType().getIdentifier())));
        }
        if (params.getLastModified() != null && (fromDate = (Date)(lastModified = params.getLastModified().dateRange()).getFrom()) != null) {
            builder.addMust((Object)new TermRangeQuery(SearchFieldNames.LAST_MODIFICATION_DATE, LuceneUtils.dateToString((Date)fromDate), null, lastModified.isIncludeFrom(), false));
        }
        if (params.getContributor() != null) {
            builder.addMust((Object)this.getContributorFilter(params));
        }
        builder.addMust((Object)new NonViewableCustomContentTypeQuery(this.pluginAccessor));
        builder.addMust((Object)ContentStatusQuery.getDefaultContentStatusQuery());
        return (SearchQuery)builder.build();
    }

    private SearchQuery getQuery(SearchQueryParameters params) {
        if (Strings.isNullOrEmpty((String)params.getQuery())) {
            return AllQuery.getInstance();
        }
        return new SiteTextSearchQuery(params.getQuery());
    }

    private SearchQuery getIgnoredTypeFilter() {
        return (SearchQuery)BooleanQuery.builder().addMustNot((Object)new TermQuery(SearchFieldNames.TYPE, "space")).addMustNot((Object)new TermQuery(SearchFieldNames.TYPE, "globaldescription")).build();
    }

    private SearchQuery getIgnoredUsersFilter() {
        return (SearchQuery)BooleanQuery.builder().addMustNot((Object)new TermQuery(SearchFieldNames.IS_EXTERNALLY_DELETED_USER, Boolean.TRUE.toString())).addMustNot((Object)new TermQuery("isDeactivatedUser", Boolean.TRUE.toString())).build();
    }

    private SearchQuery getContributorFilter(SearchQueryParameters params) {
        ConfluenceUser user = this.userAccessor.getUserByName(params.getContributor());
        if (user != null) {
            return (SearchQuery)BooleanQuery.builder().addShould((Object)new TermQuery(SearchFieldNames.CREATOR, user.getKey().getStringValue())).addShould((Object)new TermQuery(SearchFieldNames.LAST_MODIFIERS, user.getKey().getStringValue())).build();
        }
        return MatchNoDocsQuery.getInstance();
    }
}

