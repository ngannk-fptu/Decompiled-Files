/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.internal.search.v2.lucene;

import com.atlassian.confluence.search.v2.SearchQuery;
import org.apache.lucene.search.Query;

public interface LuceneQueryMapper<T extends SearchQuery> {
    public Query convertToLuceneQuery(T var1);
}

