/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.spans.SpanNearQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.ComposedQuery;
import org.apache.lucene.queryparser.surround.query.DistanceRewriteQuery;
import org.apache.lucene.queryparser.surround.query.DistanceSubQuery;
import org.apache.lucene.queryparser.surround.query.SpanNearClauseFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;

public class DistanceQuery
extends ComposedQuery
implements DistanceSubQuery {
    private int opDistance;
    private boolean ordered;

    public DistanceQuery(List<SrndQuery> queries, boolean infix, int opDistance, String opName, boolean ordered) {
        super(queries, infix, opName);
        this.opDistance = opDistance;
        this.ordered = ordered;
    }

    public int getOpDistance() {
        return this.opDistance;
    }

    public boolean subQueriesOrdered() {
        return this.ordered;
    }

    @Override
    public String distanceSubQueryNotAllowed() {
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        while (sqi.hasNext()) {
            SrndQuery leq = sqi.next();
            if (leq instanceof DistanceSubQuery) {
                DistanceSubQuery dsq = (DistanceSubQuery)((Object)leq);
                String m = dsq.distanceSubQueryNotAllowed();
                if (m == null) continue;
                return m;
            }
            return "Operator " + this.getOperatorName() + " does not allow subquery " + ((Object)leq).toString();
        }
        return null;
    }

    @Override
    public void addSpanQueries(SpanNearClauseFactory sncf) throws IOException {
        Query snq = this.getSpanNearQuery(sncf.getIndexReader(), sncf.getFieldName(), this.getWeight(), sncf.getBasicQueryFactory());
        sncf.addSpanQuery(snq);
    }

    public Query getSpanNearQuery(IndexReader reader, String fieldName, float boost, BasicQueryFactory qf) throws IOException {
        SpanQuery[] spanClauses = new SpanQuery[this.getNrSubQueries()];
        Iterator<SrndQuery> sqi = this.getSubQueriesIterator();
        int qi = 0;
        while (sqi.hasNext()) {
            SpanNearClauseFactory sncf = new SpanNearClauseFactory(reader, fieldName, qf);
            ((DistanceSubQuery)((Object)sqi.next())).addSpanQueries(sncf);
            if (sncf.size() == 0) {
                while (sqi.hasNext()) {
                    ((DistanceSubQuery)((Object)sqi.next())).addSpanQueries(sncf);
                    sncf.clear();
                }
                return SrndQuery.theEmptyLcnQuery;
            }
            spanClauses[qi] = sncf.makeSpanClause();
            ++qi;
        }
        SpanNearQuery r = new SpanNearQuery(spanClauses, this.getOpDistance() - 1, this.subQueriesOrdered());
        r.setBoost(boost);
        return r;
    }

    @Override
    public Query makeLuceneQueryFieldNoBoost(String fieldName, BasicQueryFactory qf) {
        return new DistanceRewriteQuery(this, fieldName, qf);
    }
}

