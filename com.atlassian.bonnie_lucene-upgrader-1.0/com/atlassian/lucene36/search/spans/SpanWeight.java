/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search.spans;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.ComplexExplanation;
import com.atlassian.lucene36.search.Explanation;
import com.atlassian.lucene36.search.Query;
import com.atlassian.lucene36.search.Scorer;
import com.atlassian.lucene36.search.Searcher;
import com.atlassian.lucene36.search.Similarity;
import com.atlassian.lucene36.search.Weight;
import com.atlassian.lucene36.search.spans.SpanQuery;
import com.atlassian.lucene36.search.spans.SpanScorer;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SpanWeight
extends Weight {
    protected Similarity similarity;
    protected float value;
    protected float idf;
    protected float queryNorm;
    protected float queryWeight;
    protected Set<Term> terms;
    protected SpanQuery query;
    private Explanation.IDFExplanation idfExp;

    public SpanWeight(SpanQuery query, Searcher searcher) throws IOException {
        this.similarity = query.getSimilarity(searcher);
        this.query = query;
        this.terms = new HashSet<Term>();
        query.extractTerms(this.terms);
        this.idfExp = this.similarity.idfExplain(this.terms, searcher);
        this.idf = this.idfExp.getIdf();
    }

    public Query getQuery() {
        return this.query;
    }

    public float getValue() {
        return this.value;
    }

    public float sumOfSquaredWeights() throws IOException {
        this.queryWeight = this.idf * this.query.getBoost();
        return this.queryWeight * this.queryWeight;
    }

    public void normalize(float queryNorm) {
        this.queryNorm = queryNorm;
        this.queryWeight *= queryNorm;
        this.value = this.queryWeight * this.idf;
    }

    public Scorer scorer(IndexReader reader, boolean scoreDocsInOrder, boolean topScorer) throws IOException {
        if (this.query.getField() == null) {
            return null;
        }
        return new SpanScorer(this.query.getSpans(reader), this, this.similarity, reader.norms(this.query.getField()));
    }

    public Explanation explain(IndexReader reader, int doc) throws IOException {
        ComplexExplanation result = new ComplexExplanation();
        result.setDescription("weight(" + this.getQuery() + " in " + doc + "), product of:");
        String field = ((SpanQuery)this.getQuery()).getField();
        Explanation idfExpl = new Explanation(this.idf, "idf(" + field + ": " + this.idfExp.explain() + ")");
        Explanation queryExpl = new Explanation();
        queryExpl.setDescription("queryWeight(" + this.getQuery() + "), product of:");
        Explanation boostExpl = new Explanation(this.getQuery().getBoost(), "boost");
        if (this.getQuery().getBoost() != 1.0f) {
            queryExpl.addDetail(boostExpl);
        }
        queryExpl.addDetail(idfExpl);
        Explanation queryNormExpl = new Explanation(this.queryNorm, "queryNorm");
        queryExpl.addDetail(queryNormExpl);
        queryExpl.setValue(boostExpl.getValue() * idfExpl.getValue() * queryNormExpl.getValue());
        result.addDetail(queryExpl);
        ComplexExplanation fieldExpl = new ComplexExplanation();
        fieldExpl.setDescription("fieldWeight(" + field + ":" + this.query.toString(field) + " in " + doc + "), product of:");
        Explanation tfExpl = ((SpanScorer)this.scorer(reader, true, false)).explain(doc);
        fieldExpl.addDetail(tfExpl);
        fieldExpl.addDetail(idfExpl);
        Explanation fieldNormExpl = new Explanation();
        byte[] fieldNorms = reader.norms(field);
        float fieldNorm = fieldNorms != null ? this.similarity.decodeNormValue(fieldNorms[doc]) : 1.0f;
        fieldNormExpl.setValue(fieldNorm);
        fieldNormExpl.setDescription("fieldNorm(field=" + field + ", doc=" + doc + ")");
        fieldExpl.addDetail(fieldNormExpl);
        fieldExpl.setMatch(tfExpl.isMatch());
        fieldExpl.setValue(tfExpl.getValue() * idfExpl.getValue() * fieldNormExpl.getValue());
        result.addDetail(fieldExpl);
        result.setMatch(fieldExpl.getMatch());
        result.setValue(queryExpl.getValue() * fieldExpl.getValue());
        if (queryExpl.getValue() == 1.0f) {
            return fieldExpl;
        }
        return result;
    }
}

