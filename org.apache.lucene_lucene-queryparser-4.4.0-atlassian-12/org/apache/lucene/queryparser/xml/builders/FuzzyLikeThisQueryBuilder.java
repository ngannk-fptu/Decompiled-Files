/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.sandbox.queries.FuzzyLikeThisQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class FuzzyLikeThisQueryBuilder
implements QueryBuilder {
    private static final int DEFAULT_MAX_NUM_TERMS = 50;
    private static final float DEFAULT_MIN_SIMILARITY = 2.0f;
    private static final int DEFAULT_PREFIX_LENGTH = 1;
    private static final boolean DEFAULT_IGNORE_TF = false;
    private final Analyzer analyzer;

    public FuzzyLikeThisQueryBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        NodeList nl = e.getElementsByTagName("Field");
        int maxNumTerms = DOMUtils.getAttribute(e, "maxNumTerms", 50);
        FuzzyLikeThisQuery fbq = new FuzzyLikeThisQuery(maxNumTerms, this.analyzer);
        fbq.setIgnoreTF(DOMUtils.getAttribute(e, "ignoreTF", false));
        for (int i = 0; i < nl.getLength(); ++i) {
            Element fieldElem = (Element)nl.item(i);
            float minSimilarity = DOMUtils.getAttribute(fieldElem, "minSimilarity", 2.0f);
            int prefixLength = DOMUtils.getAttribute(fieldElem, "prefixLength", 1);
            String fieldName = DOMUtils.getAttributeWithInheritance(fieldElem, "fieldName");
            String value = DOMUtils.getText(fieldElem);
            fbq.addTerms(value, fieldName, minSimilarity, prefixLength);
        }
        fbq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return fbq;
    }
}

