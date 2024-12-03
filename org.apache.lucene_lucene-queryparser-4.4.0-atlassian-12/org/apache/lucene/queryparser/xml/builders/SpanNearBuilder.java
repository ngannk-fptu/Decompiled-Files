/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.spans.SpanNearQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import java.util.ArrayList;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SpanNearBuilder
extends SpanBuilderBase {
    private final SpanQueryBuilder factory;

    public SpanNearBuilder(SpanQueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        String slopString = DOMUtils.getAttributeOrFail(e, "slop");
        int slop = Integer.parseInt(slopString);
        boolean inOrder = DOMUtils.getAttribute(e, "inOrder", false);
        ArrayList<SpanQuery> spans = new ArrayList<SpanQuery>();
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() != 1) continue;
            spans.add(this.factory.getSpanQuery((Element)kid));
        }
        SpanQuery[] spanQueries = spans.toArray(new SpanQuery[spans.size()]);
        return new SpanNearQuery(spanQueries, slop, inOrder);
    }
}

