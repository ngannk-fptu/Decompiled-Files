/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.persistence.hibernate.HibernateHandle
 */
package com.atlassian.confluence.plugins.search.model;

import com.atlassian.confluence.core.persistence.hibernate.HibernateHandle;
import com.atlassian.confluence.plugins.search.api.model.SearchExplanation;
import com.atlassian.confluence.plugins.search.api.model.SearchResult;
import java.text.ParseException;
import java.util.function.Function;
import java.util.function.Supplier;

public interface SearchResultBuilder {
    public static long getId(String handle) {
        long id;
        try {
            id = new HibernateHandle(handle).getId();
        }
        catch (ParseException e) {
            throw new IllegalStateException("Unable to parse HibernateHandle for document handle field: " + handle);
        }
        return id;
    }

    public SearchResult newSearchResult(Function<String, String> var1, Supplier<String> var2, Supplier<String> var3, Supplier<SearchExplanation> var4);
}

