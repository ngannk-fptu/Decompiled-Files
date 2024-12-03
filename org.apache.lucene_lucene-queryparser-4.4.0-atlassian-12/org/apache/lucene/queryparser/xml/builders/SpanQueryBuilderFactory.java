/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.spans.SpanQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import java.util.HashMap;
import java.util.Map;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.SpanQueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.spans.SpanQuery;
import org.w3c.dom.Element;

public class SpanQueryBuilderFactory
implements SpanQueryBuilder {
    private final Map<String, SpanQueryBuilder> builders = new HashMap<String, SpanQueryBuilder>();

    @Override
    public Query getQuery(Element e) throws ParserException {
        return this.getSpanQuery(e);
    }

    public void addBuilder(String nodeName, SpanQueryBuilder builder) {
        this.builders.put(nodeName, builder);
    }

    @Override
    public SpanQuery getSpanQuery(Element e) throws ParserException {
        SpanQueryBuilder builder = this.builders.get(e.getNodeName());
        if (builder == null) {
            throw new ParserException("No SpanQueryObjectBuilder defined for node " + e.getNodeName());
        }
        return builder.getSpanQuery(e);
    }
}

