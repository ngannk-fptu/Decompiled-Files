/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public interface SpanQueryBuilder
extends QueryBuilder {
    public SpanQuery getSpanQuery(Element var1) throws ParserException;
}

