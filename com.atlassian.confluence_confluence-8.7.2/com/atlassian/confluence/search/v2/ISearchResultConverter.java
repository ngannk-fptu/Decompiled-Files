/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.List;

public interface ISearchResultConverter {
    public List<Searchable> convertToEntities(Iterable<SearchResult> var1, SearchManager.EntityVersionPolicy var2);
}

