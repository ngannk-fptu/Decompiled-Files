/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ContentCategoryQuery
implements SearchQuery {
    public static final String KEY = "contentCategory";
    private final Set<Category> contentCategories;

    public ContentCategoryQuery(Category contentCategory) {
        this(Sets.newHashSet((Object[])new Category[]{contentCategory}));
    }

    public ContentCategoryQuery(Set<Category> contentCategories) {
        if (contentCategories.isEmpty()) {
            throw new IllegalArgumentException("Must specify at least one Category");
        }
        this.contentCategories = contentCategories;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List<Category> getParameters() {
        return new ArrayList<Category>(this.contentCategories);
    }

    @Override
    public SearchQuery expand() {
        List<Category> list = this.getParameters();
        if (list.size() == 1) {
            return this.makeSingleQuery(list.get(0));
        }
        return this.makeBooleanQuery(list);
    }

    private SearchQuery makeBooleanQuery(List<Category> contentCategories) {
        return (SearchQuery)BooleanQuery.builder().addShould(contentCategories.stream().map(this::makeSingleQuery).collect(Collectors.toList())).build();
    }

    private SearchQuery makeSingleQuery(Category contentCategory) {
        return new TermQuery(SearchFieldNames.TYPE, contentCategory.getName());
    }
}

