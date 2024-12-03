/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute
 *  org.apache.lucene.index.IndexReader
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.search.highlight;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.TextFragment;
import org.apache.lucene.search.highlight.WeightedSpanTerm;
import org.apache.lucene.search.highlight.WeightedSpanTermExtractor;

public class QueryScorer
implements Scorer {
    private float totalScore;
    private Set<String> foundTerms;
    private Map<String, WeightedSpanTerm> fieldWeightedSpanTerms;
    private float maxTermWeight;
    private int position = -1;
    private String defaultField;
    private CharTermAttribute termAtt;
    private PositionIncrementAttribute posIncAtt;
    private boolean expandMultiTermQuery = true;
    private Query query;
    private String field;
    private IndexReader reader;
    private boolean skipInitExtractor;
    private boolean wrapToCaching = true;
    private int maxCharsToAnalyze;

    public QueryScorer(Query query) {
        this.init(query, null, null, true);
    }

    public QueryScorer(Query query, String field) {
        this.init(query, field, null, true);
    }

    public QueryScorer(Query query, IndexReader reader, String field) {
        this.init(query, field, reader, true);
    }

    public QueryScorer(Query query, IndexReader reader, String field, String defaultField) {
        this.defaultField = defaultField;
        this.init(query, field, reader, true);
    }

    public QueryScorer(Query query, String field, String defaultField) {
        this.defaultField = defaultField;
        this.init(query, field, null, true);
    }

    public QueryScorer(WeightedSpanTerm[] weightedTerms) {
        this.fieldWeightedSpanTerms = new HashMap<String, WeightedSpanTerm>(weightedTerms.length);
        for (int i = 0; i < weightedTerms.length; ++i) {
            WeightedSpanTerm existingTerm = this.fieldWeightedSpanTerms.get(weightedTerms[i].term);
            if (existingTerm != null && !(existingTerm.weight < weightedTerms[i].weight)) continue;
            this.fieldWeightedSpanTerms.put(weightedTerms[i].term, weightedTerms[i]);
            this.maxTermWeight = Math.max(this.maxTermWeight, weightedTerms[i].getWeight());
        }
        this.skipInitExtractor = true;
    }

    @Override
    public float getFragmentScore() {
        return this.totalScore;
    }

    public float getMaxTermWeight() {
        return this.maxTermWeight;
    }

    @Override
    public float getTokenScore() {
        this.position += this.posIncAtt.getPositionIncrement();
        String termText = this.termAtt.toString();
        WeightedSpanTerm weightedSpanTerm = this.fieldWeightedSpanTerms.get(termText);
        if (weightedSpanTerm == null) {
            return 0.0f;
        }
        if (weightedSpanTerm.positionSensitive && !weightedSpanTerm.checkPosition(this.position)) {
            return 0.0f;
        }
        float score = weightedSpanTerm.getWeight();
        if (!this.foundTerms.contains(termText)) {
            this.totalScore += score;
            this.foundTerms.add(termText);
        }
        return score;
    }

    @Override
    public TokenStream init(TokenStream tokenStream) throws IOException {
        this.position = -1;
        this.termAtt = (CharTermAttribute)tokenStream.addAttribute(CharTermAttribute.class);
        this.posIncAtt = (PositionIncrementAttribute)tokenStream.addAttribute(PositionIncrementAttribute.class);
        if (!this.skipInitExtractor) {
            if (this.fieldWeightedSpanTerms != null) {
                this.fieldWeightedSpanTerms.clear();
            }
            return this.initExtractor(tokenStream);
        }
        return null;
    }

    public WeightedSpanTerm getWeightedSpanTerm(String token) {
        return this.fieldWeightedSpanTerms.get(token);
    }

    private void init(Query query, String field, IndexReader reader, boolean expandMultiTermQuery) {
        this.reader = reader;
        this.expandMultiTermQuery = expandMultiTermQuery;
        this.query = query;
        this.field = field;
    }

    private TokenStream initExtractor(TokenStream tokenStream) throws IOException {
        WeightedSpanTermExtractor qse = this.newTermExtractor(this.defaultField);
        qse.setMaxDocCharsToAnalyze(this.maxCharsToAnalyze);
        qse.setExpandMultiTermQuery(this.expandMultiTermQuery);
        qse.setWrapIfNotCachingTokenFilter(this.wrapToCaching);
        this.fieldWeightedSpanTerms = this.reader == null ? qse.getWeightedSpanTerms(this.query, tokenStream, this.field) : qse.getWeightedSpanTermsWithScores(this.query, tokenStream, this.field, this.reader);
        if (qse.isCachedTokenStream()) {
            return qse.getTokenStream();
        }
        return null;
    }

    protected WeightedSpanTermExtractor newTermExtractor(String defaultField) {
        return defaultField == null ? new WeightedSpanTermExtractor() : new WeightedSpanTermExtractor(defaultField);
    }

    @Override
    public void startFragment(TextFragment newFragment) {
        this.foundTerms = new HashSet<String>();
        this.totalScore = 0.0f;
    }

    public boolean isExpandMultiTermQuery() {
        return this.expandMultiTermQuery;
    }

    public void setExpandMultiTermQuery(boolean expandMultiTermQuery) {
        this.expandMultiTermQuery = expandMultiTermQuery;
    }

    public void setWrapIfNotCachingTokenFilter(boolean wrap) {
        this.wrapToCaching = wrap;
    }

    public void setMaxDocCharsToAnalyze(int maxDocCharsToAnalyze) {
        this.maxCharsToAnalyze = maxDocCharsToAnalyze;
    }
}

