/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queries.mlt;

import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Set;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queries.mlt.MoreLikeThis;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class MoreLikeThisQuery
extends Query {
    private String likeText;
    private String[] moreLikeFields;
    private Analyzer analyzer;
    private final String fieldName;
    private float percentTermsToMatch = 0.3f;
    private int minTermFrequency = 1;
    private int maxQueryTerms = 5;
    private Set<?> stopWords = null;
    private int minDocFreq = -1;

    public MoreLikeThisQuery(String likeText, String[] moreLikeFields, Analyzer analyzer, String fieldName) {
        this.likeText = likeText;
        this.moreLikeFields = moreLikeFields;
        this.analyzer = analyzer;
        this.fieldName = fieldName;
    }

    public Query rewrite(IndexReader reader) throws IOException {
        MoreLikeThis mlt = new MoreLikeThis(reader);
        mlt.setFieldNames(this.moreLikeFields);
        mlt.setAnalyzer(this.analyzer);
        mlt.setMinTermFreq(this.minTermFrequency);
        if (this.minDocFreq >= 0) {
            mlt.setMinDocFreq(this.minDocFreq);
        }
        mlt.setMaxQueryTerms(this.maxQueryTerms);
        mlt.setStopWords(this.stopWords);
        BooleanQuery bq = (BooleanQuery)mlt.like(new StringReader(this.likeText), this.fieldName);
        BooleanClause[] clauses = bq.getClauses();
        bq.setMinimumNumberShouldMatch((int)((float)clauses.length * this.percentTermsToMatch));
        return bq;
    }

    public String toString(String field) {
        return "like:" + this.likeText;
    }

    public float getPercentTermsToMatch() {
        return this.percentTermsToMatch;
    }

    public void setPercentTermsToMatch(float percentTermsToMatch) {
        this.percentTermsToMatch = percentTermsToMatch;
    }

    public Analyzer getAnalyzer() {
        return this.analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public String getLikeText() {
        return this.likeText;
    }

    public void setLikeText(String likeText) {
        this.likeText = likeText;
    }

    public int getMaxQueryTerms() {
        return this.maxQueryTerms;
    }

    public void setMaxQueryTerms(int maxQueryTerms) {
        this.maxQueryTerms = maxQueryTerms;
    }

    public int getMinTermFrequency() {
        return this.minTermFrequency;
    }

    public void setMinTermFrequency(int minTermFrequency) {
        this.minTermFrequency = minTermFrequency;
    }

    public String[] getMoreLikeFields() {
        return this.moreLikeFields;
    }

    public void setMoreLikeFields(String[] moreLikeFields) {
        this.moreLikeFields = moreLikeFields;
    }

    public Set<?> getStopWords() {
        return this.stopWords;
    }

    public void setStopWords(Set<?> stopWords) {
        this.stopWords = stopWords;
    }

    public int getMinDocFreq() {
        return this.minDocFreq;
    }

    public void setMinDocFreq(int minDocFreq) {
        this.minDocFreq = minDocFreq;
    }

    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.analyzer == null ? 0 : this.analyzer.hashCode());
        result = 31 * result + (this.fieldName == null ? 0 : this.fieldName.hashCode());
        result = 31 * result + (this.likeText == null ? 0 : this.likeText.hashCode());
        result = 31 * result + this.maxQueryTerms;
        result = 31 * result + this.minDocFreq;
        result = 31 * result + this.minTermFrequency;
        result = 31 * result + Arrays.hashCode(this.moreLikeFields);
        result = 31 * result + Float.floatToIntBits(this.percentTermsToMatch);
        result = 31 * result + (this.stopWords == null ? 0 : this.stopWords.hashCode());
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
        MoreLikeThisQuery other = (MoreLikeThisQuery)((Object)obj);
        if (this.analyzer == null ? other.analyzer != null : !this.analyzer.equals(other.analyzer)) {
            return false;
        }
        if (this.fieldName == null ? other.fieldName != null : !this.fieldName.equals(other.fieldName)) {
            return false;
        }
        if (this.likeText == null ? other.likeText != null : !this.likeText.equals(other.likeText)) {
            return false;
        }
        if (this.maxQueryTerms != other.maxQueryTerms) {
            return false;
        }
        if (this.minDocFreq != other.minDocFreq) {
            return false;
        }
        if (this.minTermFrequency != other.minTermFrequency) {
            return false;
        }
        if (!Arrays.equals(this.moreLikeFields, other.moreLikeFields)) {
            return false;
        }
        if (Float.floatToIntBits(this.percentTermsToMatch) != Float.floatToIntBits(other.percentTermsToMatch)) {
            return false;
        }
        return !(this.stopWords == null ? other.stopWords != null : !this.stopWords.equals(other.stopWords));
    }
}

