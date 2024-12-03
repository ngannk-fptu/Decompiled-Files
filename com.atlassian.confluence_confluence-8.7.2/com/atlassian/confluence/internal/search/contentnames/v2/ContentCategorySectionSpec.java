/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.internal.search.contentnames.v2.FieldValuesTransformers;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentCategoryQuery;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

class ContentCategorySectionSpec
implements ContentNameSearchSectionSpec {
    private final Function<SearchResult, ContentNameMatch> searchResultTransformer;

    ContentCategorySectionSpec(Function<SearchResult, ContentNameMatch> searchResultTransformer) {
        this.searchResultTransformer = Objects.requireNonNull(searchResultTransformer);
    }

    @Override
    public Category getCategory() {
        return Category.CONTENT;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public SearchQuery getFilter() {
        return new ContentCategoryQuery(Category.CONTENT);
    }

    @Override
    public int getWeight() {
        return 10;
    }

    @Override
    public int getLimit() {
        return 6;
    }

    @Override
    public Set<String> getFields() {
        return FieldValuesTransformers.SPACED_FIELD_NAMES;
    }

    @Override
    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer() {
        return FieldValuesTransformers.contentTransformer();
    }

    @Override
    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer() {
        return this.searchResultTransformer;
    }
}

