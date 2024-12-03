/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.search.highlight;

import java.util.HashMap;
import java.util.HashSet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.QueryTermExtractor;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.WeightedTerm;

public class QueryTermScorer
implements Scorer {
    TextFragment currentTextFragment = null;
    HashSet<String> uniqueTermsInFragment;
    float totalScore = 0.0f;
    float maxTermWeight = 0.0f;
    private HashMap<String, WeightedTerm> termsToFind = new HashMap();
    private CharTermAttribute termAtt;

    public QueryTermScorer(Query query) {
        this(QueryTermExtractor.getTerms(query));
    }

    public QueryTermScorer(Query query, String fieldName) {
        this(QueryTermExtractor.getTerms(query, false, fieldName));
    }

    public QueryTermScorer(Query query, IndexReader reader, String fieldName) {
        this(QueryTermExtractor.getIdfWeightedTerms(query, reader, fieldName));
    }

    public QueryTermScorer(WeightedTerm[] weightedTerms) {
        for (int i = 0; i < weightedTerms.length; ++i) {
            WeightedTerm existingTerm = this.termsToFind.get(weightedTerms[i].term);
            if (existingTerm != null && !(existingTerm.weight < weightedTerms[i].weight)) continue;
            this.termsToFind.put(weightedTerms[i].term, weightedTerms[i]);
            this.maxTermWeight = Math.max(this.maxTermWeight, weightedTerms[i].getWeight());
        }
    }

    @Override
    public TokenStream init(TokenStream tokenStream) {
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
        return null;
    }

    @Override
    public void startFragment(TextFragment newFragment) {
        this.uniqueTermsInFragment = new HashSet();
        this.currentTextFragment = newFragment;
        this.totalScore = 0.0f;
    }

    @Override
    public float getTokenScore() {
        String termText = this.termAtt.toString();
        WeightedTerm queryTerm = this.termsToFind.get(termText);
        if (queryTerm == null) {
            return 0.0f;
        }
        if (!this.uniqueTermsInFragment.contains(termText)) {
            this.totalScore += queryTerm.getWeight();
            this.uniqueTermsInFragment.add(termText);
        }
        return queryTerm.getWeight();
    }

    @Override
    public float getFragmentScore() {
        return this.totalScore;
    }

    public void allFragmentsProcessed() {
    }

    public float getMaxTermWeight() {
        return this.maxTermWeight;
    }
}

