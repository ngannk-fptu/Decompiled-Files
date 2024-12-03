/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.MatchAllDocsQuery
 *  org.apache.lucene.search.Query
 */
package com.atlassian.confluence.impl.search.v2.lucene;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;

public class MatchNoDocsQuery {
    public static final String KEY = "none";

    public static Query newInstance() {
        BooleanQuery query = new BooleanQuery();
        query.add((Query)new MatchAllDocsQuery(), BooleanClause.Occur.MUST_NOT);
        return query;
    }
}

