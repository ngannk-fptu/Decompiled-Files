/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.internal.search.contentnames.v2.FieldValuesTransformers;
import com.atlassian.confluence.internal.search.contentnames.v2.V2ContentNameQueryFactory;
import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.ContentNameSearcher;
import com.atlassian.confluence.search.contentnames.QueryToken;
import com.atlassian.confluence.search.contentnames.ResultTemplate;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

class V2ContentNameSearcher
implements ContentNameSearcher {
    private final SearchManager searchManager;
    private final V2ContentNameQueryFactory queryFactory;
    private final Supplier<Map<Category, ContentNameSearchSectionSpec>> sectionSpecsProvider;

    public V2ContentNameSearcher(V2ContentNameQueryFactory queryFactory, SearchManager searchManager, Supplier<Map<Category, ContentNameSearchSectionSpec>> sectionSpecsProvider) {
        this.queryFactory = Objects.requireNonNull(queryFactory);
        this.searchManager = Objects.requireNonNull(searchManager);
        this.sectionSpecsProvider = Objects.requireNonNull(sectionSpecsProvider);
    }

    @Override
    public Map<Category, List<SearchResult>> search(List<QueryToken> queryTokens, ResultTemplate grouping, Set<Attachment.Type> attachmentTypes, boolean searchParentName, int startIndex, Integer pageSize, Map<String, Object> params, String ... spaceKeys) {
        ContentSearch search = this.createContentSearch(queryTokens, attachmentTypes, searchParentName, startIndex, pageSize, spaceKeys, params);
        Map<Category, ContentNameSearchSectionSpec> sectionSpecs = this.sectionSpecsProvider.get();
        HashMap<Category, List<SearchResult>> result = new HashMap<Category, List<SearchResult>>();
        try {
            this.searchManager.searchCategorised(search, new ContentCategorizer(sectionSpecs, grouping)).forEach((category, list) -> {
                Function<List<Map<String, String>>, List<SearchResult>> transformer = ((ContentNameSearchSectionSpec)sectionSpecs.get(category)).getFieldValuesTransformer();
                result.put((Category)category, transformer.apply((List<Map<String, String>>)list));
            });
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    @Override
    public List<SearchResult> searchNoCategorisation(List<QueryToken> queryTokens, ResultTemplate results, Set<Attachment.Type> attachmentTypes, boolean searchParentName, int startIndex, Integer pageSize, Map<String, Object> params, String ... spaceKeys) {
        ContentSearch search = this.createContentSearch(queryTokens, attachmentTypes, searchParentName, startIndex, pageSize, spaceKeys, params);
        try {
            return StreamSupport.stream(this.searchManager.search(search).spliterator(), false).map(x -> FieldValuesTransformers.fieldValuesMapper().apply(x::getField)).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
        }
        catch (InvalidSearchException e) {
            throw new RuntimeException(e);
        }
    }

    private ContentSearch createContentSearch(List<QueryToken> queryTokens, Set<Attachment.Type> attachmentTypes, boolean searchParentName, int startIndex, Integer pageSize, String[] spaceKeys, Map<String, Object> params) {
        BooleanQuery.Builder booleanQueryBuilder = BooleanQuery.builder();
        SearchQuery query = this.queryFactory.createQuery(queryTokens, searchParentName, params);
        SearchQuery filter = this.queryFactory.createFilter(attachmentTypes, spaceKeys);
        booleanQueryBuilder.addMust(query);
        booleanQueryBuilder.addFilter(filter);
        return new ContentSearch(booleanQueryBuilder.build(), null, startIndex, pageSize == null ? 10 : pageSize);
    }

    private static class ContentCategorizer
    implements SearchManager.Categorizer<Category> {
        private final Map<Category, ContentNameSearchSectionSpec> sectionSpecs;
        private final ResultTemplate resultTemplate;

        ContentCategorizer(Map<Category, ContentNameSearchSectionSpec> sectionSpecs, ResultTemplate resultTemplate) {
            this.sectionSpecs = sectionSpecs;
            this.resultTemplate = resultTemplate;
        }

        @Override
        public Set<Category> getCategories() {
            return this.resultTemplate.getCategories();
        }

        @Override
        public int getLimit(Category category) {
            return this.resultTemplate.getMaxResultCount(category);
        }

        @Override
        public Set<String> getFields(Category category) {
            return this.sectionSpecs.get(category).getFields();
        }
    }
}

