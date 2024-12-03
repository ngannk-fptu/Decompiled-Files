/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.spans.SpanQuery
 *  org.apache.lucene.search.spans.SpanTermQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanTermQuery;
import org.w3c.dom.Element;

public class SpanTermBuilder
extends SpanBuilderBase {
    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String value = DOMUtils.getNonBlankTextOrFail(e);
        SpanTermQuery stq = new SpanTermQuery(new Term(fieldName, value));
        stq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return stq;
    }
}

