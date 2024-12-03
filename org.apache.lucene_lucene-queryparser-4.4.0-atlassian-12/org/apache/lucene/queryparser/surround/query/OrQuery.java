/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.ComposedQuery;
import org.apache.lucene.queryparser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryparser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryparser.surround.query.SrndBooleanQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

public class OrQuery
extends ComposedQuery
implements DistanceSubQuery {
    public OrQuery(List<SrndQuery> queries, boolean infix, String opName) {
        super(queries, infix, opName);
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        return SrndBooleanQuery.makeBooleanQuery(this.makeLuceneSubQueriesField(fieldName, qf), BooleanClause.Occur.SHOULD);
    }

    @Override
    public String distanceSubQueryNotAllowed() {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            SrndQuery leq = sqi.next();
            if (leq instanceof DistanceSubQuery) {
                String m = ((DistanceSubQuery)((Object)leq)).distanceSubQueryNotAllowed();
                if (m == null) continue;
                return m;
            }
            return "subquery not allowed: " + leq.toString();
        }
        return null;
    }

    @Override
    public void addSpanQueries(SpanNearClauseFactory sncf) throws IOException {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            ((DistanceSubQuery)((Object)sqi.next())).addSpanQueries(sncf);
        }
    }
}

