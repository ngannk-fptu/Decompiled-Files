/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BooleanQueryBuilder
implements QueryBuilder {
    private final QueryBuilder factory;

    public BooleanQueryBuilder(QueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        BooleanQuery bq = new BooleanQuery(DOMUtils.getAttribute(e, "disableCoord", false));
        bq.setMinimumNumberShouldMatch(DOMUtils.getAttribute(e, "minimumNumberShouldMatch", 0));
        bq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!node.getNodeName().equals("Clause")) continue;
            Element clauseElem = (Element)node;
            BooleanClause.Occur occurs = BooleanQueryBuilder.getOccursValue(clauseElem);
            Element clauseQuery = DOMUtils.getFirstChildOrFail(clauseElem);
            Query q = this.factory.getQuery(clauseQuery);
            bq.add(new BooleanClause(q, occurs));
        }
        return bq;
    }

    static BooleanClause.Occur getOccursValue(Element clauseElem) throws ParserException {
        String occs = clauseElem.getAttribute("occurs");
        BooleanClause.Occur occurs = BooleanClause.Occur.SHOULD;
        if ("must".equalsIgnoreCase(occs)) {
            occurs = BooleanClause.Occur.MUST;
        } else if ("mustNot".equalsIgnoreCase(occs)) {
            occurs = BooleanClause.Occur.MUST_NOT;
        } else if ("should".equalsIgnoreCase(occs) || "".equals(occs)) {
            occurs = BooleanClause.Occur.SHOULD;
        } else if (occs != null) {
            throw new ParserException("Invalid value for \"occurs\" attribute of clause:" + occs);
        }
        return occurs;
    }
}

