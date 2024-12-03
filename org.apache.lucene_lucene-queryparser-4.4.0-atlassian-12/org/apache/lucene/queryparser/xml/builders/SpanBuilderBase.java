/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public abstract class SpanBuilderBase
implements SpanQueryBuilder {
    @Override
    public Query getQuery(Element e) throws ParserException {
        return this.getSpanQuery(e);
    }
}

