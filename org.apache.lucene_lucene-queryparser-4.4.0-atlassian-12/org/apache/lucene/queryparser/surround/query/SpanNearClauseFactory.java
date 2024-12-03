/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.spans.SpanOrQuery
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class SpanNearClauseFactory {
    private IndexReader reader;
    private String fieldName;
    private HashMap<SpanQuery, Float> weightBySpanQuery;
    private BasicQueryFactory qf;

    public SpanNearClauseFactory(IndexReader reader, String fieldName, BasicQueryFactory qf) {
        this.reader = reader;
        this.fieldName = fieldName;
        this.weightBySpanQuery = new HashMap();
        this.qf = qf;
    }

    public IndexReader getIndexReader() {
        return this.reader;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public BasicQueryFactory getBasicQueryFactory() {
        return this.qf;
    }

    public int size() {
        return this.weightBySpanQuery.size();
    }

    public void clear() {
        this.weightBySpanQuery.clear();
    }

    protected void addSpanQueryWeighted(SpanQuery sq, float weight) {
        Float w = this.weightBySpanQuery.get(sq);
        w = w != null ? Float.valueOf(w.floatValue() + weight) : Float.valueOf(weight);
        this.weightBySpanQuery.put(sq, w);
    }

    public void addTermWeighted(Term t, float weight) throws IOException {
        SpanTermQuery stq = this.qf.newSpanTermQuery(t);
        this.addSpanQueryWeighted((SpanQuery)stq, weight);
    }

    public void addSpanQuery(Query q) {
        if (q == SrndQuery.theEmptyLcnQuery) {
            return;
        }
        if (!(q instanceof SpanQuery)) {
            throw new AssertionError((Object)("Expected SpanQuery: " + q.toString(this.getFieldName())));
        }
        this.addSpanQueryWeighted((SpanQuery)q, q.getBoost());
    }

    public SpanQuery makeSpanClause() {
        SpanQuery[] spanQueries = new SpanQuery[this.size()];
        Iterator<SpanQuery> sqi = this.weightBySpanQuery.keySet().iterator();
        int i = 0;
        while (sqi.hasNext()) {
            SpanQuery sq = sqi.next();
            sq.setBoost(this.weightBySpanQuery.get(sq).floatValue());
            spanQueries[i++] = sq;
        }
        if (spanQueries.length == 1) {
            return spanQueries[0];
        }
        return new SpanOrQuery(spanQueries);
    }
}

