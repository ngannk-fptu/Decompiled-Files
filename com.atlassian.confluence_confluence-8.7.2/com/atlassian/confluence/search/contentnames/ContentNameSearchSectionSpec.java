/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.search.actions.json.ContentNameMatch;
import com.atlassian.confluence.search.contentnames.Category;
import com.atlassian.confluence.search.contentnames.SearchResult;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@ExperimentalSpi
public interface ContentNameSearchSectionSpec {
    public Category getCategory();

    default public boolean isDefault() {
        return false;
    }

    public SearchQuery getFilter();

    default public int getWeight() {
        return 50;
    }

    default public int getLimit() {
        return 7;
    }

    public Set<String> getFields();

    public Function<List<Map<String, String>>, List<SearchResult>> getFieldValuesTransformer();

    public Function<SearchResult, ContentNameMatch> getSearchResultTransformer();
}

