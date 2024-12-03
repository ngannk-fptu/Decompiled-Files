/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 */
package com.atlassian.confluence.internal.search.contentnames.v2;

import com.atlassian.confluence.internal.search.contentnames.v2.FieldValuesTransformers;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.ContentNameSearchSectionSpec;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentCategoryQuery;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

class PeopleCategorySectionSpec
implements ContentNameSearchSectionSpec {
    private static final Set<String> FIELD_NAMES = ImmutableSet.builder().addAll(FieldValuesTransformers.DEFAULT_FIELD_NAMES).add((Object)SearchFieldNames.USER_NAME).build();
    private final Function<SearchResult, ContentNameMatch> searchResultTransformer;

    PeopleCategorySectionSpec(Function<SearchResult, ContentNameMatch> searchResultTransformer) {
        this.searchResultTransformer = Objects.requireNonNull(searchResultTransformer);
    }

    @Override
    public Category getCategory() {
        return Category.PEOPLE;
    }

    @Override
    public boolean isDefault() {
        return true;
    }

    @Override
    public SearchQuery getFilter() {
        return new ContentCategoryQuery(Category.PEOPLE);
    }

    @Override
    public int getWeight() {
        return 30;
    }

    @Override
    public int getLimit() {
        return 3;
    }

    @Override
    public Set<String> getFields() {
        return FIELD_NAMES;
    }

    @Override
    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer() {
        return FieldValuesTransformers.peopleTransformer();
    }

    @Override
    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer() {
        return this.searchResultTransformer;
    }
}

