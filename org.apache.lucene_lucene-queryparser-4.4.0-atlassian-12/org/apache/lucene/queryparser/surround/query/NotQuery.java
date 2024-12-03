/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.util.List;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.ComposedQuery;
import org.apache.lucene.queryparser.surround.query.SrndBooleanQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class NotQuery
extends ComposedQuery {
    public NotQuery(List<SrndQuery> queries, String opName) {
        super(queries, true, opName);
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        List<Query> luceneSubQueries = this.makeLuceneSubQueriesField(fieldName, qf);
        BooleanQuery bq = new BooleanQuery();
        bq.add(luceneSubQueries.get(0), BooleanClause.Occur.MUST);
        SrndBooleanQuery.addQueriesToBoolean(bq, luceneSubQueries.subList(1, luceneSubQueries.size()), BooleanClause.Occur.MUST_NOT);
        return bq;
    }
}

