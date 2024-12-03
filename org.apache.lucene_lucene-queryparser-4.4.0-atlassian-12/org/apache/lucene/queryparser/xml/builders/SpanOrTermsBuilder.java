/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.spans.SpanOrQuery
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.apache.lucene.util.BytesRef;
import org.w3c.dom.Element;

public class SpanOrTermsBuilder
extends SpanBuilderBase {
    private final Analyzer analyzer;

    public SpanOrTermsBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String value = DOMUtils.getNonBlankTextOrFail(e);
        try {
            ArrayList<SpanTermQuery> clausesList = new ArrayList<SpanTermQuery>();
            TokenStream ts = this.analyzer.tokenStream(fieldName, value);
            TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute(TermToBytesRefAttribute.class);
            BytesRef bytes = termAtt.getBytesRef();
            ts.reset();
            while (ts.incrementToken()) {
                termAtt.fillBytesRef();
                SpanTermQuery stq = new SpanTermQuery(new Term(fieldName, BytesRef.deepCopyOf((BytesRef)bytes)));
                clausesList.add(stq);
            }
            ts.end();
            ts.close();
            SpanOrQuery soq = new SpanOrQuery(clausesList.toArray(new SpanQuery[clausesList.size()]));
            soq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
            return soq;
        }
        catch (IOException ioe) {
            throw new ParserException("IOException parsing value:" + value);
        }
    }
}

