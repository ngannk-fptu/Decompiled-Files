/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.spans.SpanFirstQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.spans.SpanFirstQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanFirstBuilder
extends SpanBuilderBase {
    private final SpanQueryBuilder factory;

    public SpanFirstBuilder(SpanQueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        int end = DOMUtils.getAttribute(e, "end", 1);
        Element child = DOMUtils.getFirstChildElement(e);
        SpanQuery q = this.factory.getSpanQuery(child);
        SpanFirstQuery sfq = new SpanFirstQuery(q, end);
        sfq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return sfq;
    }
}

