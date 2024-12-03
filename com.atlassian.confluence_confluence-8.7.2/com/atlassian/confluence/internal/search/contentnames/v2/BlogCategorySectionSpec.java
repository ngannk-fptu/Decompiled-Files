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

class BlogCategorySectionSpec
implements ContentNameSearchSectionSpec {
    private final Function<SearchResult, ContentNameMatch> searchResultTransformer;

    BlogCategorySectionSpec(Function<SearchResult, ContentNameMatch> searchResultTransformer) {
        this.searchResultTransformer = Objects.requireNonNull(searchResultTransformer);
    }

    @Override
    public Category getCategory() {
        return Category.BLOGS;
    }

    @Override
    public SearchQuery getFilter() {
        return new ContentCategoryQuery(Category.BLOGS);
    }

    @Override
    public Set<String> getFields() {
        return FieldValuesTransformers.SPACED_FIELD_NAMES;
    }

    @Override
    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer() {
        return FieldValuesTransformers.blogTransformer();
    }

    @Override
    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer() {
        return this.searchResultTransformer;
    }
}

