/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.payloads.AveragePayloadFunction
 *  org.apache.lucene.search.payloads.PayloadFunction
 *  org.apache.lucene.search.payloads.PayloadTermQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.search.payloads.AveragePayloadFunction;
import org.apache.lucene.search.payloads.PayloadFunction;
import org.apache.lucene.search.payloads.PayloadTermQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class BoostingTermBuilder
extends SpanBuilderBase {
    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String value = DOMUtils.getNonBlankTextOrFail(e);
        PayloadTermQuery btq = new PayloadTermQuery(new Term(fieldName, value), (PayloadFunction)new AveragePayloadFunction());
        btq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return btq;
    }
}

