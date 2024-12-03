/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchOptions$Excerpt
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.PermissionException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.event.events.search.SearchPerformedEvent
 *  com.atlassian.confluence.event.events.search.SiteSearchAuditEvent
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.BooleanOperator
 *  com.atlassian.confluence.search.v2.ContentSearch
 *  com.atlassian.confluence.search.v2.DefaultHighlightParams
 *  com.atlassian.confluence.search.v2.HightlightParams
 *  com.atlassian.confluence.search.v2.ISearch
 *  com.atlassian.confluence.search.v2.InvalidSearchException
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.search.v2.SearchResults
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery$Builder
 *  com.atlassian.confluence.search.v2.query.ContainingContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.QueryStringQuery
 *  com.atlassian.confluence.search.v2.score.FunctionScoreQueryFactory
 *  com.atlassian.confluence.search.v2.sort.MultiSearchSort
 *  com.atlassian.confluence.security.access.AccessStatus
 *  com.atlassian.confluence.security.access.ConfluenceAccessManager
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.antlrgen.AqlParser
 *  com.atlassian.querylang.antlrgen.AqlParser$AqlStatementContext
 *  com.atlassian.querylang.exceptions.QueryException
 *  com.atlassian.querylang.lib.fields.FieldRegistry
 *  com.atlassian.querylang.lib.fields.FieldRegistryProvider
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  com.atlassian.querylang.lib.functions.FunctionRegistry
 *  com.atlassian.querylang.lib.functions.FunctionRegistryProvider
 *  com.atlassian.querylang.lib.parserfactory.AqlParserFactory
 *  com.atlassian.querylang.lib.parserfactory.BaseParserConfig
 *  com.atlassian.querylang.lib.parserfactory.DefaultParserFactory
 *  com.atlassian.querylang.lib.parserfactory.ParserConfig
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.user.User
 *  com.google.common.collect.Iterables
 *  org.antlr.v4.runtime.misc.ParseCancellationException
 *  org.antlr.v4.runtime.tree.ParseTree
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.event.events.search.SiteSearchAuditEvent;
import com.atlassian.confluence.plugins.cql.impl.CQLIterableStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLPaginationLimits;
import com.atlassian.confluence.plugins.cql.impl.CQLSearchQueryFactory;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLtoFieldOrderParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLtoV2SearchParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.QueryExceptionMapper;
import com.atlassian.confluence.plugins.cql.impl.factory.ContentSearchResultsFactory;
import com.atlassian.confluence.plugins.cql.impl.factory.SearchResultsFactory;
import com.atlassian.confluence.plugins.cql.rest.CQLMetaDataService;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchSortWrapper;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.BooleanOperator;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.DefaultHighlightParams;
import com.atlassian.confluence.search.v2.HightlightParams;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContainingContentTypeQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.QueryStringQuery;
import com.atlassian.confluence.search.v2.score.FunctionScoreQueryFactory;
import com.atlassian.confluence.search.v2.sort.MultiSearchSort;
import com.atlassian.confluence.security.access.AccessStatus;
import com.atlassian.confluence.security.access.ConfluenceAccessManager;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.exceptions.QueryException;
import com.atlassian.querylang.lib.fields.FieldRegistry;
import com.atlassian.querylang.lib.fields.FieldRegistryProvider;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import com.atlassian.querylang.lib.functions.FunctionRegistry;
import com.atlassian.querylang.lib.functions.FunctionRegistryProvider;
import com.atlassian.querylang.lib.parserfactory.AqlParserFactory;
import com.atlassian.querylang.lib.parserfactory.BaseParserConfig;
import com.atlassian.querylang.lib.parserfactory.DefaultParserFactory;
import com.atlassian.querylang.lib.parserfactory.ParserConfig;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.user.User;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={CQLSearchService.class})
@Component
public class DefaultCQLSearchService
implements CQLSearchService {
    private static final String SITE_SEARCH = "SiteSearch";
    @VisibleForTesting
    public static final String SEARCH_IMPROVEMENTS_RANKING_DARK_FEATURE_KEY = "confluence.search.improvements.ranking";
    static final String AUDIT_LOG_SEARCH_DISABLED_KEY = "audit.log.search.disabled";
    private final FieldRegistryProvider cqlFieldRegistryProvider;
    private final FunctionRegistryProvider cqlFunctionRegistryProvider;
    private final ParserConfig config = BaseParserConfig.DEFAULT_CONFIG;
    private final AqlParserFactory parserFactory = new DefaultParserFactory();
    private final CQLMetaDataService metaDataService;
    private final SearchManager searchManager;
    private final ContentSearchResultsFactory contentSearchResultsFactory;
    private final SearchResultsFactory searchResultsFactory;
    private final ExpressionDataFactory expressionDataFactory;
    private final ConfluenceAccessManager confluenceAccessManager;
    private final CQLSearchQueryFactory cqlSearchQueryFactory;
    private final EventPublisher eventPublisher;
    private final FunctionScoreQueryFactory functionScoreQueryFactory;

    @Autowired
    public DefaultCQLSearchService(FieldRegistryProvider cqlFieldRegistryProvider, FunctionRegistryProvider cqlFunctionRegistryProvider, CQLMetaDataService metaDataService, @ComponentImport SearchManager searchManager, ContentSearchResultsFactory contentSearchResultsFactory, SearchResultsFactory searchResultsFactory, ExpressionDataFactory expressionDataFactory, @ComponentImport ConfluenceAccessManager confluenceAccessManager, CQLSearchQueryFactory cqlSearchQueryFactory, @ComponentImport EventPublisher eventPublisher, @ComponentImport FunctionScoreQueryFactory functionScoreQueryFactory) {
        this.cqlFieldRegistryProvider = cqlFieldRegistryProvider;
        this.cqlFunctionRegistryProvider = cqlFunctionRegistryProvider;
        this.metaDataService = metaDataService;
        this.searchManager = searchManager;
        this.contentSearchResultsFactory = contentSearchResultsFactory;
        this.searchResultsFactory = searchResultsFactory;
        this.expressionDataFactory = expressionDataFactory;
        this.confluenceAccessManager = confluenceAccessManager;
        this.cqlSearchQueryFactory = cqlSearchQueryFactory;
        this.eventPublisher = eventPublisher;
        this.functionScoreQueryFactory = functionScoreQueryFactory;
    }

    public PageResponse<Content> searchContent(String cqlInput, Expansion ... expansions) {
        return this.searchContent(cqlInput, (PageRequest)new SimplePageRequest(0, 25), expansions);
    }

    public PageResponse<Content> searchContent(String cqlInput, PageRequest pageRequest, Expansion ... expansions) {
        CQLEvaluationContext evaluationContext = CQLEvaluationContext.builder().build();
        return this.searchContent(cqlInput, evaluationContext, pageRequest, expansions);
    }

    public PageResponse<Content> searchContent(String cqlInput, SearchContext searchContext, PageRequest pageRequest, Expansion ... expansions) {
        CQLEvaluationContext evaluationContext = CQLEvaluationContext.builder((SearchContext)searchContext).build();
        return this.searchContent(cqlInput, evaluationContext, pageRequest, expansions);
    }

    public SearchPageResponse search(String cqlInput, SearchOptions searchOptions, PageRequest pageRequest, Expansion ... expansions) {
        Expansions parsedExpansions = new Expansions(expansions);
        LimitedRequest limitedRequest = CQLPaginationLimits.limitRequest(pageRequest, parsedExpansions.getSubExpansions("content"));
        long start = System.currentTimeMillis();
        SearchResults results = this.performV2Search(cqlInput, this.getAllTypesQuery(), (Set<String>)this.searchResultsFactory.getRequiredIndexFields(), limitedRequest, searchOptions, CQLEvaluationContext.builder((SearchContext)searchOptions.getSearchContext()).build());
        int searchDuration = (int)(System.currentTimeMillis() - start);
        return this.buildResponse(this.searchResultsFactory.buildFrom((Iterable<SearchResult>)results, searchOptions, cqlInput, expansions), searchDuration, results.getUnfilteredResultsCount(), this.getArchivedResultCount(cqlInput, results, searchOptions), cqlInput, limitedRequest);
    }

    private Optional<Integer> getArchivedResultCount(String cqlInput, SearchResults results, SearchOptions searchOptions) {
        Optional<Integer> archivedResultCount = Optional.empty();
        if (results.getUnfilteredResultsCount() == 0 && !searchOptions.isIncludeArchivedSpaces()) {
            SearchResults archivedResults = this.performV2Search(cqlInput, this.getAllTypesQuery(), (Set<String>)this.searchResultsFactory.getRequiredIndexFields(), LimitedRequestImpl.create((int)0), SearchOptions.builder().includeArchivedSpaces(true).build(), CQLEvaluationContext.builder((SearchContext)searchOptions.getSearchContext()).build());
            archivedResultCount = Optional.of(archivedResults.getUnfilteredResultsCount());
        }
        return archivedResultCount;
    }

    private PageResponse<Content> searchContent(String cqlInput, CQLEvaluationContext evaluationContext, PageRequest pageRequest, Expansion ... expansions) {
        Expansions parsedExpansions = new Expansions(expansions);
        LimitedRequest limitedRequest = CQLPaginationLimits.limitRequest(pageRequest, parsedExpansions);
        long start = System.currentTimeMillis();
        SearchResults results = this.performV2Search(cqlInput, this.getContentTypeQuery(new ContentTypeEnum[0]), (Set<String>)this.searchResultsFactory.getRequiredIndexFields(), limitedRequest, SearchOptions.buildDefault(), evaluationContext);
        int searchDuration = (int)(System.currentTimeMillis() - start);
        return this.buildResponse(this.contentSearchResultsFactory.buildFrom((Iterable<SearchResult>)results, parsedExpansions), searchDuration, results.getUnfilteredResultsCount(), Optional.empty(), cqlInput, limitedRequest);
    }

    private <T> SearchPageResponse<T> buildResponse(Map<SearchResult, T> results, int searchDuration, int totalSize, Optional<Integer> archivedResultCount, String cqlInput, LimitedRequest limitedRequest) {
        int indexOfLastItemOnCurrentPage = limitedRequest.getStart() + limitedRequest.getLimit();
        return SearchPageResponse.builder().cqlQuery(cqlInput).hasMore(totalSize > indexOfLastItemOnCurrentPage).totalSize(totalSize).withArchivedResultCount(archivedResultCount).searchDuration(searchDuration).pageRequest((PageRequest)new SimplePageRequest(limitedRequest)).result(Iterables.limit(results.values(), (int)limitedRequest.getLimit())).build();
    }

    public int countContent(String cqlInput) {
        CQLEvaluationContext evaluationContext = CQLEvaluationContext.builder().build();
        return this.countContent(cqlInput, evaluationContext);
    }

    public int countContent(String s, SearchContext searchContext) {
        CQLEvaluationContext evaluationContext = CQLEvaluationContext.builder((SearchContext)searchContext).build();
        return this.countContent(s, evaluationContext);
    }

    private int countContent(String cqlInput, CQLEvaluationContext evaluationContext) {
        SearchResults results = this.performV2Search(cqlInput, this.getContentTypeQuery(new ContentTypeEnum[0]), Collections.emptySet(), LimitedRequestImpl.create((int)1), SearchOptions.buildDefault(), evaluationContext);
        return results.getUnfilteredResultsCount();
    }

    private SearchQuery getContentTypeQuery(ContentTypeEnum ... additionalTypes) {
        EnumSet<ContentTypeEnum[]> contentTypeEnums = EnumSet.of(ContentTypeEnum.PAGE, additionalTypes);
        contentTypeEnums.add((ContentTypeEnum[])ContentTypeEnum.BLOG);
        contentTypeEnums.add((ContentTypeEnum[])ContentTypeEnum.ATTACHMENT);
        contentTypeEnums.add((ContentTypeEnum[])ContentTypeEnum.COMMENT);
        return BooleanQuery.orQuery((SearchQuery[])new SearchQuery[]{new ContentTypeQuery(contentTypeEnums), BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{new ContentTypeQuery(EnumSet.of(ContentTypeEnum.ATTACHMENT, ContentTypeEnum.COMMENT)), new ContainingContentTypeQuery(EnumSet.of(ContentTypeEnum.BLOG, ContentTypeEnum.PAGE, ContentTypeEnum.CUSTOM, ContentTypeEnum.ATTACHMENT, ContentTypeEnum.COMMENT))})});
    }

    private SearchQuery getAllTypesQuery() {
        return this.getContentTypeQuery(ContentTypeEnum.SPACE, ContentTypeEnum.SPACE_DESCRIPTION, ContentTypeEnum.PERSONAL_INFORMATION, ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION, ContentTypeEnum.CUSTOM);
    }

    private SearchResults performV2Search(final String cqlInput, SearchQuery typeQuery, Set<String> requiredIndexFields, LimitedRequest limitedRequest, final SearchOptions searchOptions, CQLEvaluationContext evaluationContext) {
        this.checkCanUse();
        ContentSearch search = null;
        try {
            FieldRegistry fieldRegistry = this.cqlFieldRegistryProvider.getFieldRegistry();
            FunctionRegistry functionRegistry = this.cqlFunctionRegistryProvider.getFunctionRegistry();
            AqlParser parser = this.createParserForInput(cqlInput, fieldRegistry, functionRegistry);
            SearchQuery searchQuery = this.parseSearchQuery(parser.aqlStatement(), fieldRegistry, functionRegistry, evaluationContext);
            parser.reset();
            searchQuery = BooleanQuery.andQuery((SearchQuery[])new SearchQuery[]{typeQuery, searchQuery});
            Optional<SearchSort> searchSort = this.parseSearchSort(parser.aqlStatement(), fieldRegistry);
            if (!searchSort.isPresent()) {
                searchQuery = this.functionScoreQueryFactory.applyFunctionScoring(searchQuery);
            }
            SearchQuery searchQueryFilter = this.cqlSearchQueryFactory.createFilter(searchOptions.isIncludeArchivedSpaces(), evaluationContext.contentStatuses().orElse(Collections.emptyList()));
            BooleanQuery.Builder boolQueryBuilder = new BooleanQuery.Builder();
            boolQueryBuilder.addMust((Object)searchQuery);
            boolQueryBuilder.addFilter(searchQueryFilter);
            int limit = limitedRequest.getLimit() == 0 ? 1 : limitedRequest.getLimit();
            search = new ContentSearch(boolQueryBuilder.build(), searchSort.orElse(null), limitedRequest.getStart(), limit){

                public String getSearchType() {
                    if (cqlInput.contains("siteSearch")) {
                        return DefaultCQLSearchService.SITE_SEARCH;
                    }
                    return "CQLSearch";
                }

                public String toString() {
                    return cqlInput;
                }

                public Optional<HightlightParams> getHighlight() {
                    if (searchOptions.getExcerptStrategy() == SearchOptions.Excerpt.HIGHLIGHT || searchOptions.getExcerptStrategy() == SearchOptions.Excerpt.HIGHLIGHT_UNESCAPED) {
                        return DefaultCQLSearchService.this.createHighlightQuery(cqlInput).map(highlightQuery -> new DefaultHighlightParams(searchOptions.getExcerptStrategy() == SearchOptions.Excerpt.HIGHLIGHT ? "html" : "none", highlightQuery));
                    }
                    return Optional.empty();
                }
            };
            SearchResults results = this.searchManager.search((ISearch)search, requiredIndexFields);
            if (searchOptions.isFireSearchPerformed()) {
                this.eventPublisher.publish((Object)new SearchPerformedEvent((Object)this, search.getQuery(), (User)AuthenticatedUserThreadLocal.get(), results.size()));
            }
            if (!Boolean.getBoolean(AUDIT_LOG_SEARCH_DISABLED_KEY) && SITE_SEARCH.equals(search.getSearchType())) {
                this.eventPublisher.publish((Object)new SiteSearchAuditEvent(cqlInput, (User)AuthenticatedUserThreadLocal.get()));
            }
            return results;
        }
        catch (IOException e) {
            throw new ServiceException("IOException executing cql : " + cqlInput, (Throwable)e);
        }
        catch (ParseCancellationException e) {
            throw new BadRequestException("Could not parse cql : " + cqlInput, (Throwable)e);
        }
        catch (QueryException e) {
            throw QueryExceptionMapper.mapToServiceException(e);
        }
        catch (InvalidSearchException e) {
            throw new BadRequestException("CQL was parsed but searchManager was unable to execute searchContent. CQL: " + cqlInput + " was parsed to searchContent query : " + search.getQuery() + " due to :" + e.getMessage(), (Throwable)e);
        }
    }

    private SearchQuery parseSearchQuery(AqlParser.AqlStatementContext aqlStatement, FieldRegistry fieldRegistry, FunctionRegistry functionRegistry, CQLEvaluationContext evaluationContext) {
        CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor = new CQLStringValueParseTreeVisitor(functionRegistry, evaluationContext);
        CQLIterableStringValueParseTreeVisitor cqlIterableStringValueParseTreeVisitor = new CQLIterableStringValueParseTreeVisitor(functionRegistry, evaluationContext, cqlStringValueParseTreeVisitor);
        CQLtoV2SearchParseTreeVisitor visitor = new CQLtoV2SearchParseTreeVisitor(fieldRegistry, cqlStringValueParseTreeVisitor, cqlIterableStringValueParseTreeVisitor, this.expressionDataFactory);
        return (SearchQuery)visitor.visit((ParseTree)aqlStatement);
    }

    private Optional<SearchSort> parseSearchSort(AqlParser.AqlStatementContext aqlStatement, FieldRegistry fieldRegistry) {
        CQLtoFieldOrderParseTreeVisitor sortVisitor = new CQLtoFieldOrderParseTreeVisitor(fieldRegistry);
        Iterable orders = (Iterable)sortVisitor.visit((ParseTree)aqlStatement);
        return this.createSearchSortFromFieldOrder(orders);
    }

    private Optional<SearchQuery> createHighlightQuery(String cqlQuery) {
        Iterable<String> textStrings = this.metaDataService.parseTextExpressions(cqlQuery, CQLEvaluationContext.builder().build());
        StringBuilder textQueryBuilder = new StringBuilder();
        for (String text : textStrings) {
            textQueryBuilder.append(text).append(" ");
        }
        return Optional.ofNullable(StringUtils.trimToNull((String)textQueryBuilder.toString())).map(textQuery -> new QueryStringQuery(Collections.emptySet(), textQuery, BooleanOperator.OR));
    }

    @VisibleForTesting
    Optional<SearchSort> createSearchSortFromFieldOrder(Iterable<FieldOrder> orders) {
        ArrayList<SearchSort> sorts = new ArrayList<SearchSort>();
        for (FieldOrder order : orders) {
            if (order instanceof V2SearchSortWrapper) {
                SearchSort sort = ((V2SearchSortWrapper)order).getSearchSort();
                if (sort == null) continue;
                sorts.add(sort);
                continue;
            }
            throw new UnsupportedOperationException("Could not create searchContent sort from " + order.getClass());
        }
        if (sorts.size() > 1) {
            return Optional.of(new MultiSearchSort(sorts));
        }
        if (sorts.size() == 1) {
            return Optional.of((SearchSort)sorts.get(0));
        }
        return Optional.empty();
    }

    private AqlParser createParserForInput(String cql, FieldRegistry fieldRegistry, FunctionRegistry functionRegistry) throws IOException {
        return this.parserFactory.createParser(cql, fieldRegistry, functionRegistry, this.config);
    }

    private void checkCanUse() {
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        AccessStatus userAccessStatus = this.confluenceAccessManager.getUserAccessStatus((User)currentUser);
        if (!userAccessStatus.canUseConfluence()) {
            throw new PermissionException("Not permitted to use confluence : " + AuthenticatedUserThreadLocal.get());
        }
    }
}

