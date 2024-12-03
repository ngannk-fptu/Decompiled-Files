/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.ConstantScoreQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilderFactory;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.ConstantScoreQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class ConstantScoreQueryBuilder
implements QueryBuilder {
    private final FilterBuilderFactory filterFactory;

    public ConstantScoreQueryBuilder(FilterBuilderFactory filterFactory) {
        this.filterFactory = filterFactory;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        Element filterElem = DOMUtils.getFirstChildOrFail(e);
        ConstantScoreQuery q = new ConstantScoreQuery(this.filterFactory.getFilter(filterElem));
        q.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return q;
    }
}

