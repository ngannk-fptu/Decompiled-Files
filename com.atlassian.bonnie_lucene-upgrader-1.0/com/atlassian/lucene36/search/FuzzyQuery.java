/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.search;

import com.atlassian.lucene36.index.IndexReader;
import com.atlassian.lucene36.index.Term;
import com.atlassian.lucene36.search.FilteredTermEnum;
import com.atlassian.lucene36.search.FuzzyTermEnum;
import com.atlassian.lucene36.search.MultiTermQuery;
import com.atlassian.lucene36.search.SingleTermEnum;
import com.atlassian.lucene36.util.ToStringUtils;
import java.io.IOException;

public class FuzzyQuery
extends MultiTermQuery {
    public static final float defaultMinSimilarity = 0.5f;
    public static final int defaultPrefixLength = 0;
    public static final int defaultMaxExpansions = Integer.MAX_VALUE;
    private float minimumSimilarity;
    private int prefixLength;
    private boolean termLongEnough = false;
    protected Term term;

    public FuzzyQuery(Term term, float minimumSimilarity, int prefixLength, int maxExpansions) {
        this.term = term;
        if (minimumSimilarity >= 1.0f) {
            throw new IllegalArgumentException("minimumSimilarity >= 1");
        }
        if (minimumSimilarity < 0.0f) {
            throw new IllegalArgumentException("minimumSimilarity < 0");
        }
        if (prefixLength < 0) {
            throw new IllegalArgumentException("prefixLength < 0");
        }
        if (maxExpansions < 0) {
            throw new IllegalArgumentException("maxExpansions < 0");
        }
        this.setRewriteMethod(new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(maxExpansions));
        if ((float)term.text().length() > 1.0f / (1.0f - minimumSimilarity)) {
            this.termLongEnough = true;
        }
        this.minimumSimilarity = minimumSimilarity;
        this.prefixLength = prefixLength;
    }

    public FuzzyQuery(Term term, float minimumSimilarity, int prefixLength) {
        this(term, minimumSimilarity, prefixLength, Integer.MAX_VALUE);
    }

    public FuzzyQuery(Term term, float minimumSimilarity) {
        this(term, minimumSimilarity, 0, Integer.MAX_VALUE);
    }

    public FuzzyQuery(Term term) {
        this(term, 0.5f, 0, Integer.MAX_VALUE);
    }

    public float getMinSimilarity() {
        return this.minimumSimilarity;
    }

    public int getPrefixLength() {
        return this.prefixLength;
    }

    protected FilteredTermEnum getEnum(IndexReader reader) throws IOException {
        if (!this.termLongEnough) {
            return new SingleTermEnum(reader, this.term);
        }
        return new FuzzyTermEnum(reader, this.getTerm(), this.minimumSimilarity, this.prefixLength);
    }

    public Term getTerm() {
        return this.term;
    }

    public String toString(String field) {
        StringBuilder buffer = new StringBuilder();
        if (!this.term.field().equals(field)) {
            buffer.append(this.term.field());
            buffer.append(":");
        }
        buffer.append(this.term.text());
        buffer.append('~');
        buffer.append(Float.toString(this.minimumSimilarity));
        buffer.append(ToStringUtils.boost(this.getBoost()));
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + Float.floatToIntBits(this.minimumSimilarity);
        result = 31 * result + this.prefixLength;
        result = 31 * result + (this.term == null ? 0 : this.term.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        FuzzyQuery other = (FuzzyQuery)obj;
        if (Float.floatToIntBits(this.minimumSimilarity) != Float.floatToIntBits(other.minimumSimilarity)) {
            return false;
        }
        if (this.prefixLength != other.prefixLength) {
            return false;
        }
        return !(this.term == null ? other.term != null : !this.term.equals(other.term));
    }
}

