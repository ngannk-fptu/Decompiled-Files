/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.DisjunctionMaxQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DisjunctionMaxQueryBuilder
implements QueryBuilder {
    private final QueryBuilder factory;

    public DisjunctionMaxQueryBuilder(QueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        float tieBreaker = DOMUtils.getAttribute(e, "tieBreaker", 0.0f);
        DisjunctionMaxQuery dq = new DisjunctionMaxQuery(tieBreaker);
        dq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!(node instanceof Element)) continue;
            Element queryElem = (Element)node;
            Query q = this.factory.getQuery(queryElem);
            dq.add(q);
        }
        return dq;
    }
}

