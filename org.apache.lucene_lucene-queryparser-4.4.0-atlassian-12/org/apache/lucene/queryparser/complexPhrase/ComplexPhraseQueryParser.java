/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.search.TermRangeQuery
 *  org.apache.lucene.search.spans.SpanNearQuery
 *  org.apache.lucene.search.spans.SpanNotQuery
 *  org.apache.lucene.search.spans.SpanOrQuery
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.queryparser.complexPhrase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.Version;

public class ComplexPhraseQueryParser
extends QueryParser {
    private ArrayList<ComplexPhraseQuery> complexPhrases = null;
    private boolean isPass2ResolvingPhrases;
    private ComplexPhraseQuery currentPhraseQuery = null;

    public ComplexPhraseQueryParser(Version matchVersion, String f, Analyzer a) {
        super(matchVersion, f, a);
    }

    @Override
    protected Query getFieldQuery(String field, String queryText, int slop) {
        ComplexPhraseQuery cpq = new ComplexPhraseQuery(field, queryText, slop);
        this.complexPhrases.add(cpq);
        return cpq;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Query parse(String query) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            MultiTermQuery.RewriteMethod oldMethod = this.getMultiTermRewriteMethod();
            try {
                this.setMultiTermRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
                Query query2 = super.parse(query);
                return query2;
            }
            finally {
                this.setMultiTermRewriteMethod(oldMethod);
            }
        }
        this.complexPhrases = new ArrayList();
        Query q = super.parse(query);
        this.isPass2ResolvingPhrases = true;
        try {
            for (ComplexPhraseQuery this.currentPhraseQuery : this.complexPhrases) {
                this.currentPhraseQuery.parsePhraseElements(this);
            }
        }
        finally {
            this.isPass2ResolvingPhrases = false;
        }
        return q;
    }

    @Override
    protected Query newTermQuery(Term term) {
        if (this.isPass2ResolvingPhrases) {
            try {
                this.checkPhraseClauseIsForSameField(term.field());
            }
            catch (ParseException pe) {
                throw new RuntimeException("Error parsing complex phrase", pe);
            }
        }
        return super.newTermQuery(term);
    }

    private void checkPhraseClauseIsForSameField(String field) throws ParseException {
        if (!field.equals(this.currentPhraseQuery.field)) {
            throw new ParseException("Cannot have clause for field \"" + field + "\" nested in phrase  for field \"" + this.currentPhraseQuery.field + "\"");
        }
    }

    @Override
    protected Query getWildcardQuery(String field, String termStr) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getWildcardQuery(field, termStr);
    }

    @Override
    protected Query getRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }

    @Override
    protected Query newRangeQuery(String field, String part1, String part2, boolean startInclusive, boolean endInclusive) {
        if (this.isPass2ResolvingPhrases) {
            TermRangeQuery rangeQuery = TermRangeQuery.newStringRange((String)field, (String)part1, (String)part2, (boolean)startInclusive, (boolean)endInclusive);
            rangeQuery.setRewriteMethod(MultiTermQuery.SCORING_BOOLEAN_QUERY_REWRITE);
            return rangeQuery;
        }
        return super.newRangeQuery(field, part1, part2, startInclusive, endInclusive);
    }

    @Override
    protected Query getFuzzyQuery(String field, String termStr, float minSimilarity) throws ParseException {
        if (this.isPass2ResolvingPhrases) {
            this.checkPhraseClauseIsForSameField(field);
        }
        return super.getFuzzyQuery(field, termStr, minSimilarity);
    }

    static class ComplexPhraseQuery
    extends Query {
        String field;
        String phrasedQueryStringContents;
        int slopFactor;
        private Query contents;

        public ComplexPhraseQuery(String field, String phrasedQueryStringContents, int slopFactor) {
            this.field = field;
            this.phrasedQueryStringContents = phrasedQueryStringContents;
            this.slopFactor = slopFactor;
        }

        protected void parsePhraseElements(QueryParser qp) throws ParseException {
            this.contents = qp.parse(this.phrasedQueryStringContents);
        }

        public Query rewrite(IndexReader reader) throws IOException {
            if (this.contents instanceof TermQuery) {
                return this.contents;
            }
            int numNegatives = 0;
            if (!(this.contents instanceof BooleanQuery)) {
                throw new IllegalArgumentException("Unknown query type \"" + this.contents.getClass().getName() + "\" found in phrase query string \"" + this.phrasedQueryStringContents + "\"");
            }
            BooleanQuery bq = (BooleanQuery)this.contents;
            BooleanClause[] bclauses = bq.getClauses();
            SpanQuery[] allSpanClauses = new SpanQuery[bclauses.length];
            for (int i = 0; i < bclauses.length; ++i) {
                Query qc = bclauses[i].getQuery();
                qc = qc.rewrite(reader);
                if (bclauses[i].getOccur().equals((Object)BooleanClause.Occur.MUST_NOT)) {
                    ++numNegatives;
                }
                if (qc instanceof BooleanQuery) {
                    ArrayList<SpanQuery> sc = new ArrayList<SpanQuery>();
                    this.addComplexPhraseClause(sc, (BooleanQuery)qc);
                    if (sc.size() > 0) {
                        allSpanClauses[i] = sc.get(0);
                        continue;
                    }
                    allSpanClauses[i] = new SpanTermQuery(new Term(this.field, "Dummy clause because no terms found - must match nothing"));
                    continue;
                }
                if (qc instanceof TermQuery) {
                    TermQuery tq = (TermQuery)qc;
                    allSpanClauses[i] = new SpanTermQuery(tq.getTerm());
                    continue;
                }
                throw new IllegalArgumentException("Unknown query type \"" + qc.getClass().getName() + "\" found in phrase query string \"" + this.phrasedQueryStringContents + "\"");
            }
            if (numNegatives == 0) {
                return new SpanNearQuery(allSpanClauses, this.slopFactor, true);
            }
            ArrayList<SpanQuery> positiveClauses = new ArrayList<SpanQuery>();
            for (int j = 0; j < allSpanClauses.length; ++j) {
                if (bclauses[j].getOccur().equals((Object)BooleanClause.Occur.MUST_NOT)) continue;
                positiveClauses.add(allSpanClauses[j]);
            }
            SpanQuery[] includeClauses = positiveClauses.toArray(new SpanQuery[positiveClauses.size()]);
            Object include = null;
            include = includeClauses.length == 1 ? includeClauses[0] : new SpanNearQuery(includeClauses, this.slopFactor + numNegatives, true);
            SpanNearQuery exclude = new SpanNearQuery(allSpanClauses, this.slopFactor, true);
            SpanNotQuery snot = new SpanNotQuery(include, (SpanQuery)exclude);
            return snot;
        }

        private void addComplexPhraseClause(List<SpanQuery> spanClauses, BooleanQuery qc) {
            ArrayList<SpanQuery> ors = new ArrayList<SpanQuery>();
            ArrayList nots = new ArrayList();
            BooleanClause[] bclauses = qc.getClauses();
            for (int i = 0; i < bclauses.length; ++i) {
                Query childQuery = bclauses[i].getQuery();
                ArrayList<SpanQuery> chosenList = ors;
                if (bclauses[i].getOccur() == BooleanClause.Occur.MUST_NOT) {
                    chosenList = nots;
                }
                if (childQuery instanceof TermQuery) {
                    TermQuery tq = (TermQuery)childQuery;
                    SpanTermQuery stq = new SpanTermQuery(tq.getTerm());
                    stq.setBoost(tq.getBoost());
                    chosenList.add((SpanQuery)stq);
                    continue;
                }
                if (childQuery instanceof BooleanQuery) {
                    BooleanQuery cbq = (BooleanQuery)childQuery;
                    this.addComplexPhraseClause(chosenList, cbq);
                    continue;
                }
                throw new IllegalArgumentException("Unknown query type:" + childQuery.getClass().getName());
            }
            if (ors.size() == 0) {
                return;
            }
            SpanOrQuery soq = new SpanOrQuery(ors.toArray(new SpanQuery[ors.size()]));
            if (nots.size() == 0) {
                spanClauses.add((SpanQuery)soq);
            } else {
                SpanOrQuery snqs = new SpanOrQuery(nots.toArray(new SpanQuery[nots.size()]));
                SpanNotQuery snq = new SpanNotQuery((SpanQuery)soq, (SpanQuery)snqs);
                spanClauses.add((SpanQuery)snq);
            }
        }

        public String toString(String field) {
            return "\"" + this.phrasedQueryStringContents + "\"";
        }

        public int hashCode() {
            int prime = 31;
            int result = super.hashCode();
            result = 31 * result + (this.field == null ? 0 : this.field.hashCode());
            result = 31 * result + (this.phrasedQueryStringContents == null ? 0 : this.phrasedQueryStringContents.hashCode());
            result = 31 * result + this.slopFactor;
            return result;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (((Object)((Object)this)).getClass() != obj.getClass()) {
                return false;
            }
            if (!super.equals(obj)) {
                return false;
            }
            ComplexPhraseQuery other = (ComplexPhraseQuery)((Object)obj);
            if (this.field == null ? other.field != null : !this.field.equals(other.field)) {
                return false;
            }
            if (this.phrasedQueryStringContents == null ? other.phrasedQueryStringContents != null : !this.phrasedQueryStringContents.equals(other.phrasedQueryStringContents)) {
                return false;
            }
            return this.slopFactor == other.slopFactor;
        }
    }
}

