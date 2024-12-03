/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Strings
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.search;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.plugins.search.CQLSearchResult;
import com.atlassian.confluence.plugins.search.SearchResultHighlights;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="cqlSearcher")
@Internal
public class CQLSearcher {
    private static final Function<SearchResult, SearchResult> HTML_PREP_CQL_SEARCH_RESULT = result -> {
        Preconditions.checkNotNull((Object)result);
        String title = result.getTitle();
        String highlights = result.getExcerpt();
        String htmlHighlights = StringUtils.replaceEach((String)highlights, (String[])SearchResultHighlights.API_HIGHLIGHT, (String[])SearchResultHighlights.HTML_HIGHLIGHT);
        String htmlTitle = StringUtils.replaceEach((String)title, (String[])SearchResultHighlights.API_HIGHLIGHT, (String[])SearchResultHighlights.HTML_HIGHLIGHT);
        return SearchResult.builder((Object)result.getEntity()).title(htmlTitle).bodyExcerpt(htmlHighlights).entityParentContainer(result.getResultParentContainer()).resultGlobalContainer(result.getResultGlobalContainer()).iconCssClass(result.getIconCssClass()).url(result.getUrl()).friendlyLastModified(result.getFriendlyLastModified()).lastModified(result.getLastModified()).build();
    };
    private final CQLSearchService searchService;

    @Autowired
    public CQLSearcher(@ComponentImport CQLSearchService searchService) {
        this.searchService = Objects.requireNonNull(searchService);
    }

    @VisibleForTesting
    public SearchPageResponse<CQLSearchResult> getCqlSearchResults(String cql, SearchOptions searchOptions, PageRequest pageRequest, Expansion ... expansion) throws BadRequestException {
        if (Strings.isNullOrEmpty((String)cql)) {
            return SearchPageResponse.builder().build();
        }
        SearchPageResponse response = this.searchService.search(cql, searchOptions, pageRequest, expansion);
        List results = response.getResults().stream().map(HTML_PREP_CQL_SEARCH_RESULT).collect(Collectors.toList());
        return SearchPageResponse.builder().cqlQuery(response.getCqlQuery()).pageRequest(response.getPageRequest()).totalSize(response.totalSize()).hasMore(response.hasMore()).archivedResultCount(response.getArchivedResultCount()).searchDuration(response.getSearchDuration()).result((Iterable)results.stream().map(x -> new CQLSearchResult((SearchResult)x, contentId -> null)).collect(Collectors.toList())).build();
    }
}

