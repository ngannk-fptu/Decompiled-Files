/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.DistanceQuery;
import org.apache.lucene.queryparser.surround.query.RewriteQuery;
import org.apache.lucene.search.Query;

class DistanceRewriteQuery
extends RewriteQuery<DistanceQuery> {
    DistanceRewriteQuery(DistanceQuery srndQuery, String fieldName, BasicQueryFactory qf) {
        super(srndQuery, fieldName, qf);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        return ((DistanceQuery)this.srndQuery).getSpanNearQuery(reader, this.fieldName, this.getBoost(), this.qf);
    }
}

