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

class SpaceCategorySectionSpec
implements ContentNameSearchSectionSpec {
    private final Function<SearchResult, ContentNameMatch> searchResultTransformer;

    SpaceCategorySectionSpec(Function<SearchResult, ContentNameMatch> searchResultTransformer) {
        this.searchResultTransformer = Objects.requireNonNull(searchResultTransformer);
    }

    @Override
    public Category getCategory() {
        return Category.SPACES;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public SearchQuery getFilter() {
        return new ContentCategoryQuery(Category.SPACES);
    }

    @Override
    public int getWeight() {
        return 40;
    }

    @Override
    public int getLimit() {
        return 2;
    }

    @Override
    public Set<String> getFields() {
        return FieldValuesTransformers.SPACED_FIELD_NAMES;
    }

    @Override
    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer() {
        return FieldValuesTransformers.spaceTransformer();
    }

    @Override
    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer() {
        return this.searchResultTransformer;
    }
}

