/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.spans.SpanNotQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.spans.SpanNotQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanNotBuilder
extends SpanBuilderBase {
    private final SpanQueryBuilder factory;

    public SpanNotBuilder(SpanQueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        Element includeElem = DOMUtils.getChildByTagOrFail(e, "Include");
        includeElem = DOMUtils.getFirstChildOrFail(includeElem);
        Element excludeElem = DOMUtils.getChildByTagOrFail(e, "Exclude");
        excludeElem = DOMUtils.getFirstChildOrFail(excludeElem);
        SpanQuery include = this.factory.getSpanQuery(includeElem);
        SpanQuery exclude = this.factory.getSpanQuery(excludeElem);
        SpanNotQuery snq = new SpanNotQuery(include, exclude);
        snq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return snq;
    }
}

