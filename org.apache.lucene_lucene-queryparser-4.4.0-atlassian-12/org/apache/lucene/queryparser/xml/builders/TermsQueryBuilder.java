/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.util.BytesRef;
import org.w3c.dom.Element;

public class TermsQueryBuilder
implements QueryBuilder {
    private final Analyzer analyzer;

    public TermsQueryBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String text = DOMUtils.getNonBlankTextOrFail(e);
        BooleanQuery bq = new BooleanQuery(DOMUtils.getAttribute(e, "disableCoord", false));
        bq.setMinimumNumberShouldMatch(DOMUtils.getAttribute(e, "minimumNumberShouldMatch", 0));
        try {
            TokenStream ts = this.analyzer.tokenStream(fieldName, text);
            TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute(TermToBytesRefAttribute.class);
            Term term = null;
            BytesRef bytes = termAtt.getBytesRef();
            ts.reset();
            while (ts.incrementToken()) {
                termAtt.fillBytesRef();
                term = new Term(fieldName, BytesRef.deepCopyOf((BytesRef)bytes));
                bq.add(new BooleanClause((Query)new TermQuery(term), BooleanClause.Occur.SHOULD));
            }
            ts.end();
            ts.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Error constructing terms from index:" + ioe);
        }
        bq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return bq;
    }
}

