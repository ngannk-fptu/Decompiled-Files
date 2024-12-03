/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.util.List;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.ComposedQuery;
import org.apache.lucene.queryparser.surround.query.SrndBooleanQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class AndQuery
extends ComposedQuery {
    public AndQuery(List<SrndQuery> queries, boolean inf, String opName) {
        super(queries, inf, opName);
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        return SrndBooleanQuery.makeBooleanQuery(this.makeLuceneSubQueriesField(fieldName, qf), BooleanClause.Occur.MUST);
    }
}

