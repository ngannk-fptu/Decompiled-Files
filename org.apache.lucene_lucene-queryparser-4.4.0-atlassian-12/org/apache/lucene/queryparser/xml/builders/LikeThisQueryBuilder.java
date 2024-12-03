/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.CharTermAttribute
 *  org.apache.lucene.queries.mlt.MoreLikeThisQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import java.util.HashSet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.queries.mlt.MoreLikeThisQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class LikeThisQueryBuilder
implements QueryBuilder {
    private static final int DEFAULT_MAX_QUERY_TERMS = 20;
    private static final int DEFAULT_MIN_TERM_FREQUENCY = 1;
    private static final float DEFAULT_PERCENT_TERMS_TO_MATCH = 30.0f;
    private final Analyzer analyzer;
    private final String[] defaultFieldNames;

    public LikeThisQueryBuilder(Analyzer analyzer, String[] defaultFieldNames) {
        this.analyzer = analyzer;
        this.defaultFieldNames = defaultFieldNames;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        String fieldsList = e.getAttribute("fieldNames");
        String[] fields = this.defaultFieldNames;
        if (fieldsList != null && fieldsList.trim().length() > 0) {
            fields = fieldsList.trim().split(",");
            for (int i = 0; i < fields.length; ++i) {
                fields[i] = fields[i].trim();
            }
        }
        String stopWords = e.getAttribute("stopWords");
        HashSet<String> stopWordsSet = null;
        if (stopWords != null && fields != null) {
            stopWordsSet = new HashSet<String>();
            for (String field : fields) {
                try {
                    TokenStream ts = this.analyzer.tokenStream(field, stopWords);
                    CharTermAttribute termAtt = (CharTermAttribute)ts.addAttribute(CharTermAttribute.class);
                    ts.reset();
                    while (ts.incrementToken()) {
                        stopWordsSet.add(termAtt.toString());
                    }
                    ts.end();
                    ts.close();
                }
                catch (IOException ioe) {
                    throw new ParserException("IoException parsing stop words list in " + this.getClass().getName() + ":" + ioe.getLocalizedMessage());
                }
            }
        }
        MoreLikeThisQuery mlt = new MoreLikeThisQuery(DOMUtils.getText(e), fields, this.analyzer, fields[0]);
        mlt.setMaxQueryTerms(DOMUtils.getAttribute(e, "maxQueryTerms", 20));
        mlt.setMinTermFrequency(DOMUtils.getAttribute(e, "minTermFrequency", 1));
        mlt.setPercentTermsToMatch(DOMUtils.getAttribute(e, "percentTermsToMatch", 30.0f) / 100.0f);
        mlt.setStopWords(stopWordsSet);
        int minDocFreq = DOMUtils.getAttribute(e, "minDocFreq", -1);
        if (minDocFreq >= 0) {
            mlt.setMinDocFreq(minDocFreq);
        }
        mlt.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return mlt;
    }
}

