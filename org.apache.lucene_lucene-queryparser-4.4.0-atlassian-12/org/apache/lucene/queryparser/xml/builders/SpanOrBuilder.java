/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.spans.SpanOrQuery
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import java.util.ArrayList;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanBuilderBase;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.spans.SpanOrQuery;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SpanOrBuilder
extends SpanBuilderBase {
    private final SpanQueryBuilder factory;

    public SpanOrBuilder(SpanQueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        ArrayList<SpanQuery> clausesList = new ArrayList<SpanQuery>();
        for (Node kid = e.getFirstChild(); kid != null; kid = kid.getNextSibling()) {
            if (kid.getNodeType() != 1) continue;
            SpanQuery clause = this.factory.getSpanQuery((Element)kid);
            clausesList.add(clause);
        }
        SpanQuery[] clauses = clausesList.toArray(new SpanQuery[clausesList.size()]);
        SpanOrQuery soq = new SpanOrQuery(clauses);
        soq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return soq;
    }
}

