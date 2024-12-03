/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queries.BoostingQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queries.BoostingQuery;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class BoostingQueryBuilder
implements QueryBuilder {
    private static float DEFAULT_BOOST = 0.01f;
    private final QueryBuilder factory;

    public BoostingQueryBuilder(QueryBuilder factory) {
        this.factory = factory;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        Element mainQueryElem = DOMUtils.getChildByTagOrFail(e, "Query");
        mainQueryElem = DOMUtils.getFirstChildOrFail(mainQueryElem);
        Query mainQuery = this.factory.getQuery(mainQueryElem);
        Element boostQueryElem = DOMUtils.getChildByTagOrFail(e, "BoostQuery");
        float boost = DOMUtils.getAttribute(boostQueryElem, "boost", DEFAULT_BOOST);
        boostQueryElem = DOMUtils.getFirstChildOrFail(boostQueryElem);
        Query boostQuery = this.factory.getQuery(boostQueryElem);
        BoostingQuery bq = new BoostingQuery(mainQuery, boostQuery, boost);
        bq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return bq;
    }
}

