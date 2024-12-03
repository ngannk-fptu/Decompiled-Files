/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.mappers;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.search.v2.lucene.MatchNoDocsQuery;
import com.atlassian.confluence.internal.search.v2.lucene.LuceneQueryMapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import org.apache.lucene.search.Query;

@Internal
public class MatchNoDocsQueryMapper
implements LuceneQueryMapper<SearchQuery> {
    @Override
    public Query convertToLuceneQuery(SearchQuery searchQuery) {
        return MatchNoDocsQuery.newInstance();
    }
}

