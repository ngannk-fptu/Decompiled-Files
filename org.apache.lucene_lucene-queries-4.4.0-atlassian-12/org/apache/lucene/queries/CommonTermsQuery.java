/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.index.Fields
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.index.TermContext
 *  org.apache.lucene.index.Terms
 *  org.apache.lucene.index.TermsEnum
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.util.ToStringUtils
 */
package org.apache.lucene.queries;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.Fields;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.ToStringUtils;

public class CommonTermsQuery
extends Query {
    protected final List<Term> terms = new ArrayList<Term>();
    protected final boolean disableCoord;
    protected final float maxTermFrequency;
    protected final BooleanClause.Occur lowFreqOccur;
    protected final BooleanClause.Occur highFreqOccur;
    protected float lowFreqBoost = 1.0f;
    protected float highFreqBoost = 1.0f;
    protected float minNrShouldMatch = 0.0f;

    public CommonTermsQuery(BooleanClause.Occur highFreqOccur, BooleanClause.Occur lowFreqOccur, float maxTermFrequency) {
        this(highFreqOccur, lowFreqOccur, maxTermFrequency, false);
    }

    public CommonTermsQuery(BooleanClause.Occur highFreqOccur, BooleanClause.Occur lowFreqOccur, float maxTermFrequency, boolean disableCoord) {
        if (highFreqOccur == BooleanClause.Occur.MUST_NOT) {
            throw new IllegalArgumentException("highFreqOccur should be MUST or SHOULD but was MUST_NOT");
        }
        if (lowFreqOccur == BooleanClause.Occur.MUST_NOT) {
            throw new IllegalArgumentException("lowFreqOccur should be MUST or SHOULD but was MUST_NOT");
        }
        this.disableCoord = disableCoord;
        this.highFreqOccur = highFreqOccur;
        this.lowFreqOccur = lowFreqOccur;
        this.maxTermFrequency = maxTermFrequency;
    }

    public void add(Term term) {
        if (term == null) {
            throw new IllegalArgumentException("Term must not be null");
        }
        this.terms.add(term);
    }

    public Query rewrite(IndexReader reader) throws IOException {
        if (this.terms.isEmpty()) {
            return new BooleanQuery();
        }
        if (this.terms.size() == 1) {
            TermQuery tq = new TermQuery(this.terms.get(0));
            tq.setBoost(this.getBoost());
            return tq;
        }
        List leaves = reader.leaves();
        int maxDoc = reader.maxDoc();
        TermContext[] contextArray = new TermContext[this.terms.size()];
        Term[] queryTerms = this.terms.toArray(new Term[0]);
        this.collectTermContext(reader, leaves, contextArray, queryTerms);
        return this.buildQuery(maxDoc, contextArray, queryTerms);
    }

    protected int calcLowFreqMinimumNumberShouldMatch(int numOptional) {
        if (this.minNrShouldMatch >= 1.0f || this.minNrShouldMatch == 0.0f) {
            return (int)this.minNrShouldMatch;
        }
        return Math.round(this.minNrShouldMatch * (float)numOptional);
    }

    protected Query buildQuery(int maxDoc, TermContext[] contextArray, Term[] queryTerms) {
        BooleanQuery lowFreq = new BooleanQuery(this.disableCoord);
        BooleanQuery highFreq = new BooleanQuery(this.disableCoord);
        highFreq.setBoost(this.highFreqBoost);
        lowFreq.setBoost(this.lowFreqBoost);
        BooleanQuery query = new BooleanQuery(true);
        for (int i = 0; i < queryTerms.length; ++i) {
            TermContext termContext = contextArray[i];
            if (termContext == null) {
                lowFreq.add((Query)new TermQuery(queryTerms[i]), this.lowFreqOccur);
                continue;
            }
            if (this.maxTermFrequency >= 1.0f && (float)termContext.docFreq() > this.maxTermFrequency || termContext.docFreq() > (int)Math.ceil(this.maxTermFrequency * (float)maxDoc)) {
                highFreq.add((Query)new TermQuery(queryTerms[i], termContext), this.highFreqOccur);
                continue;
            }
            lowFreq.add((Query)new TermQuery(queryTerms[i], termContext), this.lowFreqOccur);
        }
        int numLowFreqClauses = lowFreq.clauses().size();
        if (this.lowFreqOccur == BooleanClause.Occur.SHOULD && numLowFreqClauses > 0) {
            int minMustMatch = this.calcLowFreqMinimumNumberShouldMatch(numLowFreqClauses);
            lowFreq.setMinimumNumberShouldMatch(minMustMatch);
        }
        if (lowFreq.clauses().isEmpty()) {
            if (this.highFreqOccur == BooleanClause.Occur.MUST) {
                highFreq.setBoost(this.getBoost());
                return highFreq;
            }
            BooleanQuery highFreqConjunction = new BooleanQuery();
            for (BooleanClause booleanClause : highFreq) {
                highFreqConjunction.add(booleanClause.getQuery(), BooleanClause.Occur.MUST);
            }
            highFreqConjunction.setBoost(this.getBoost());
            return highFreqConjunction;
        }
        if (highFreq.clauses().isEmpty()) {
            lowFreq.setBoost(this.getBoost());
            return lowFreq;
        }
        query.add((Query)highFreq, BooleanClause.Occur.SHOULD);
        query.add((Query)lowFreq, BooleanClause.Occur.MUST);
        query.setBoost(this.getBoost());
        return query;
    }

    public void collectTermContext(IndexReader reader, List<AtomicReaderContext> leaves, TermContext[] contextArray, Term[] queryTerms) throws IOException {
        TermsEnum termsEnum = null;
        for (AtomicReaderContext context : leaves) {
            Fields fields = context.reader().fields();
            if (fields == null) continue;
            for (int i = 0; i < queryTerms.length; ++i) {
                Term term = queryTerms[i];
                TermContext termContext = contextArray[i];
                Terms terms = fields.terms(term.field());
                if (terms == null) continue;
                termsEnum = terms.iterator(termsEnum);
                assert (termsEnum != null);
                if (termsEnum == TermsEnum.EMPTY || !termsEnum.seekExact(term.bytes(), false)) continue;
                if (termContext == null) {
                    contextArray[i] = new TermContext(reader.getContext(), termsEnum.termState(), context.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
                    continue;
                }
                termContext.register(termsEnum.termState(), context.ord, termsEnum.docFreq(), termsEnum.totalTermFreq());
            }
        }
    }

    public boolean isCoordDisabled() {
        return this.disableCoord;
    }

    public void setMinimumNumberShouldMatch(float min) {
        this.minNrShouldMatch = min;
    }

    public float getMinimumNumberShouldMatch() {
        return this.minNrShouldMatch;
    }

    public void extractTerms(Set<Term> terms) {
        terms.addAll(this.terms);
    }

    public String toString(String field) {
        boolean needParens;
        StringBuilder buffer = new StringBuilder();
        boolean bl = needParens = (double)this.getBoost() != 1.0 || this.getMinimumNumberShouldMatch() > 0.0f;
        if (needParens) {
            buffer.append("(");
        }
        for (int i = 0; i < this.terms.size(); ++i) {
            Term t = this.terms.get(i);
            buffer.append(new TermQuery(t).toString());
            if (i == this.terms.size() - 1) continue;
            buffer.append(", ");
        }
        if (needParens) {
            buffer.append(")");
        }
        if (this.getMinimumNumberShouldMatch() > 0.0f) {
            buffer.append('~');
            buffer.append(this.getMinimumNumberShouldMatch());
        }
        if (this.getBoost() != 1.0f) {
            buffer.append(ToStringUtils.boost((float)this.getBoost()));
        }
        return buffer.toString();
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.disableCoord ? 1231 : 1237);
        result = 31 * result + Float.floatToIntBits(this.highFreqBoost);
        result = 31 * result + (this.highFreqOccur == null ? 0 : this.highFreqOccur.hashCode());
        result = 31 * result + Float.floatToIntBits(this.lowFreqBoost);
        result = 31 * result + (this.lowFreqOccur == null ? 0 : this.lowFreqOccur.hashCode());
        result = 31 * result + Float.floatToIntBits(this.maxTermFrequency);
        result = 31 * result + Float.floatToIntBits(this.minNrShouldMatch);
        result = 31 * result + (this.terms == null ? 0 : this.terms.hashCode());
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
        CommonTermsQuery other = (CommonTermsQuery)((Object)obj);
        if (this.disableCoord != other.disableCoord) {
            return false;
        }
        if (Float.floatToIntBits(this.highFreqBoost) != Float.floatToIntBits(other.highFreqBoost)) {
            return false;
        }
        if (this.highFreqOccur != other.highFreqOccur) {
            return false;
        }
        if (Float.floatToIntBits(this.lowFreqBoost) != Float.floatToIntBits(other.lowFreqBoost)) {
            return false;
        }
        if (this.lowFreqOccur != other.lowFreqOccur) {
            return false;
        }
        if (Float.floatToIntBits(this.maxTermFrequency) != Float.floatToIntBits(other.maxTermFrequency)) {
            return false;
        }
        if (this.minNrShouldMatch != other.minNrShouldMatch) {
            return false;
        }
        return !(this.terms == null ? other.terms != null : !this.terms.equals(other.terms));
    }
}

