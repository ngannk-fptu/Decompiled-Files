/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.analysis.Analyzer
 *  org.apache.lucene.analysis.TokenStream
 *  org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute
 *  org.apache.lucene.queries.TermsFilter
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermToBytesRefAttribute;
import org.apache.lucene.queries.TermsFilter;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.util.BytesRef;
import org.w3c.dom.Element;

public class TermsFilterBuilder
implements FilterBuilder {
    private final Analyzer analyzer;

    public TermsFilterBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    @Override
    public Filter getFilter(Element e) throws ParserException {
        ArrayList<BytesRef> terms = new ArrayList<BytesRef>();
        String text = DOMUtils.getNonBlankTextOrFail(e);
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        try {
            TokenStream ts = this.analyzer.tokenStream(fieldName, text);
            TermToBytesRefAttribute termAtt = (TermToBytesRefAttribute)ts.addAttribute(TermToBytesRefAttribute.class);
            BytesRef bytes = termAtt.getBytesRef();
            ts.reset();
            while (ts.incrementToken()) {
                termAtt.fillBytesRef();
                terms.add(BytesRef.deepCopyOf((BytesRef)bytes));
            }
            ts.end();
            ts.close();
        }
        catch (IOException ioe) {
            throw new RuntimeException("Error constructing terms from index:" + ioe);
        }
        return new TermsFilter(fieldName, terms);
    }
}

