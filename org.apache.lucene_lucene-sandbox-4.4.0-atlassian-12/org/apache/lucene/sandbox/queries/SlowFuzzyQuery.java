/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.SingleTermsEnum
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.MultiTermQuery
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.MultiTermQuery$TopTermsScoringBooleanQueryRewrite
 *  org.apache.lucene.util.AttributeSource
 *  org.apache.lucene.util.ToStringUtils
 */
package org.apache.lucene.sandbox.queries;

import java.io.IOException;
import org.apache.lucene.index.SingleTermsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.sandbox.queries.SlowFuzzyTermsEnum;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.util.ToStringUtils;

@Deprecated
public class SlowFuzzyQuery
extends MultiTermQuery {
    public static final float defaultMinSimilarity = 2.0f;
    public static final int defaultPrefixLength = 0;
    public static final int defaultMaxExpansions = 50;
    private float minimumSimilarity;
    private int prefixLength;
    private boolean termLongEnough = false;
    protected Term term;

    public SlowFuzzyQuery(Term term, float minimumSimilarity, int prefixLength, int maxExpansions) {
        super(term.field());
        this.term = term;
        if (minimumSimilarity >= 1.0f && minimumSimilarity != (float)((int)minimumSimilarity)) {
            throw new IllegalArgumentException("fractional edit distances are not allowed");
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
        this.setRewriteMethod((MultiTermQuery.RewriteMethod)new MultiTermQuery.TopTermsScoringBooleanQueryRewrite(maxExpansions));
        String text = term.text();
        int len = text.codePointCount(0, text.length());
        if (len > 0 && (minimumSimilarity >= 1.0f || (float)len > 1.0f / (1.0f - minimumSimilarity))) {
            this.termLongEnough = true;
        }
        this.minimumSimilarity = minimumSimilarity;
        this.prefixLength = prefixLength;
    }

    public SlowFuzzyQuery(Term term, float minimumSimilarity, int prefixLength) {
        this(term, minimumSimilarity, prefixLength, 50);
    }

    public SlowFuzzyQuery(Term term, float minimumSimilarity) {
        this(term, minimumSimilarity, 0, 50);
    }

    public SlowFuzzyQuery(Term term) {
        this(term, 2.0f, 0, 50);
    }

    public float getMinSimilarity() {
        return this.minimumSimilarity;
    }

    public int getPrefixLength() {
        return this.prefixLength;
    }

    protected TermsEnum getTermsEnum(Terms terms, AttributeSource atts) throws IOException {
        if (!this.termLongEnough) {
            return new SingleTermsEnum(terms.iterator(null), this.term.bytes());
        }
        return new SlowFuzzyTermsEnum(terms, atts, this.getTerm(), this.minimumSimilarity, this.prefixLength);
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
        buffer.append(ToStringUtils.boost((float)this.getBoost()));
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
        if (((Object)((Object)this)).getClass() != obj.getClass()) {
            return false;
        }
        SlowFuzzyQuery other = (SlowFuzzyQuery)((Object)obj);
        if (Float.floatToIntBits(this.minimumSimilarity) != Float.floatToIntBits(other.minimumSimilarity)) {
            return false;
        }
        if (this.prefixLength != other.prefixLength) {
            return false;
        }
        return !(this.term == null ? other.term != null : !this.term.equals((Object)other.term));
    }
}

