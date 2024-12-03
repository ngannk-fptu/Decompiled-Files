/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.index.TermDocs;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.IndexSearcher;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.TermScorer;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.util.ReaderUtil;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TermQuery
extends Query {
    private Term term;

    public TermQuery(Term t) {
        this.term = t;
    }

    public Term getTerm() {
        return this.term;
    }

    @Override
    public Weight createWeight(Searcher searcher) throws IOException {
        return new TermWeight(searcher);
    }

    @Override
    public void extractTerms(Set<Term> terms) {
        terms.add(this.getTerm());
    }

    @Override
    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TermQuery)) {
            return false;
        }
        TermQuery other = (TermQuery)o;
        return this.getBoost() == other.getBoost() && this.term.equals(other.term);
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.getBoost()) ^ this.term.hashCode();
    }

    private class TermWeight
    extends Weight {
        private final Similarity similarity;
        private float value;
        private float idf;
        private float queryNorm;
        private float queryWeight;
        private Explanation.IDFExplanation idfExp;
        private final Set<Integer> hash;

        public TermWeight(Searcher searcher) throws IOException {
            this.similarity = TermQuery.this.getSimilarity(searcher);
            if (searcher instanceof IndexSearcher) {
                this.hash = new HashSet<Integer>();
                IndexReader ir = ((IndexSearcher)searcher).getIndexReader();
                final int[] dfSum = new int[1];
                new ReaderUtil.Gather(ir){

                    protected void add(int base, IndexReader r) throws IOException {
                        int df = r.docFreq(TermQuery.this.term);
                        dfSum[0] = dfSum[0] + df;
                        if (df > 0) {
                            TermWeight.this.hash.add(r.hashCode());
                        }
                    }
                }.run();
                this.idfExp = this.similarity.idfExplain(TermQuery.this.term, searcher, dfSum[0]);
            } else {
                this.idfExp = this.similarity.idfExplain(TermQuery.this.term, searcher);
                this.hash = null;
            }
            this.idf = this.idfExp.getIdf();
        }

        public String toString() {
            return "weight(" + TermQuery.this + ")";
        }

        public Query getQuery() {
            return TermQuery.this;
        }

        public float getValue() {
            return this.value;
        }

        public float sumOfSquaredWeights() {
            this.queryWeight = this.idf * TermQuery.this.getBoost();
            return this.queryWeight * this.queryWeight;
        }

        public void normalize(float queryNorm) {
            this.queryNorm = queryNorm;
            this.queryWeight *= queryNorm;
            this.value = this.queryWeight * this.idf;
        }

        public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
            if (this.hash != null && reader.getSequentialSubReaders() == null && !this.hash.contains(reader.hashCode())) {
                return null;
            }
            TermDocs termDocs = reader.termDocs(TermQuery.this.term);
            if (termDocs == null) {
                return null;
            }
            return new TermScorer(this, termDocs, this.similarity, reader.norms(TermQuery.this.term.field()));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public Explanation explain(IndexReader reader, int doc) throws IOException {
            ComplexExplanation result = new ComplexExplanation();
            result.setDescription("weight(" + this.getQuery() + " in " + doc + "), product of:");
            Explanation expl = new Explanation(this.idf, this.idfExp.explain());
            Explanation queryExpl = new Explanation();
            queryExpl.setDescription("queryWeight(" + this.getQuery() + "), product of:");
            Explanation boostExpl = new Explanation(TermQuery.this.getBoost(), "boost");
            if (TermQuery.this.getBoost() != 1.0f) {
                queryExpl.addDetail(boostExpl);
            }
            queryExpl.addDetail(expl);
            Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
            queryExpl.addDetail(queryNormExpl);
            queryExpl.setValue(boostExpl.getValue() * expl.getValue() * queryNormExpl.getValue());
            result.addDetail(queryExpl);
            String field = TermQuery.this.term.field();
            ComplexExplanation fieldExpl = new ComplexExplanation();
            fieldExpl.setDescription("fieldWeight(" + TermQuery.this.term + " in " + doc + "), product of:");
            Explanation tfExplanation = new Explanation();
            int tf = 0;
            TermDocs termDocs = reader.termDocs(TermQuery.this.term);
            if (termDocs != null) {
                try {
                    if (termDocs.skipTo(doc) && termDocs.doc() == doc) {
                        tf = termDocs.freq();
                    }
                    Object var14_13 = null;
                }
                catch (Throwable throwable) {
                    Object var14_14 = null;
                    termDocs.close();
                    throw throwable;
                }
                termDocs.close();
                tfExplanation.setValue(this.similarity.tf(tf));
                tfExplanation.setDescription("tf(termFreq(" + TermQuery.this.term + ")=" + tf + ")");
            } else {
                tfExplanation.setValue(0.0f);
                tfExplanation.setDescription("no matching term");
            }
            fieldExpl.addDetail(tfExplanation);
            fieldExpl.addDetail(expl);
            Explanation fieldNormExpl = new Explanation();
            byte[] fieldNorms = reader.norms(field);
            float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0f;
            fieldNormExpl.setValue(fieldNorm);
            fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc=" + doc + ")");
            fieldExpl.addDetail(fieldNormExpl);
            fieldExpl.setMatch(tfExplanation.isMatch());
            fieldExpl.setValue(tfExplanation.getValue() * expl.getValue() * fieldNormExpl.getValue());
            result.addDetail(fieldExpl);
            result.setMatch(fieldExpl.getMatch());
            result.setValue(queryExpl.getValue() * fieldExpl.getValue());
            if (queryExpl.getValue() == 1.0f) {
                return fieldExpl;
            }
            return result;
        }
    }
}

