/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.surround.query;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.surround.query.BasicQueryFactory;
import org.apache.lucene.queryparser.surround.query.RewriteQuery;
import org.apache.lucene.queryparser.surround.query.SimpleTerm;
import org.apache.lucene.queryparser.surround.query.SrndBooleanQuery;
import org.apache.lucene.queryparser.surround.query.SrndQuery;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;

class SimpleTermRewriteQuery
extends RewriteQuery<SimpleTerm> {
    SimpleTermRewriteQuery(SimpleTerm srndQuery, String fieldName, BasicQueryFactory qf) {
        super(srndQuery, fieldName, qf);
    }

    @Override
    public Query rewrite(IndexReader reader) throws IOException {
        final ArrayList<Query> luceneSubQueries = new ArrayList<Query>();
        ((SimpleTerm)this.srndQuery).visitMatchingTerms(reader, this.fieldName, new SimpleTerm.MatchingTermVisitor(){

            @Override
            public void visitMatchingTerm(Term term) throws IOException {
                luceneSubQueries.add(SimpleTermRewriteQuery.this.qf.newTermQuery(term));
            }
        });
        return luceneSubQueries.size() == 0 ? SrndQuery.theEmptyLcnQuery : (luceneSubQueries.size() == 1 ? (Query)luceneSubQueries.get(0) : SrndBooleanQuery.makeBooleanQuery(luceneSubQueries, BooleanClause.Occur.SHOULD));
    }
}

