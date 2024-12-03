/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.FilteredQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.FilteredQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class FilteredQueryBuilder
implements QueryBuilder {
    private final FilterBuilder filterFactory;
    private final QueryBuilder queryFactory;

    public FilteredQueryBuilder(FilterBuilder filterFactory, QueryBuilder queryFactory) {
        this.filterFactory = filterFactory;
        this.queryFactory = queryFactory;
    }

    @Override
    public Query getQuery(Element e) throws ParserException {
        Element filterElement = DOMUtils.getChildByTagOrFail(e, "Filter");
        filterElement = DOMUtils.getFirstChildOrFail(filterElement);
        Filter f = this.filterFactory.getFilter(filterElement);
        Element queryElement = DOMUtils.getChildByTagOrFail(e, "Query");
        queryElement = DOMUtils.getFirstChildOrFail(queryElement);
        Query q = this.queryFactory.getQuery(queryElement);
        FilteredQuery fq = new FilteredQuery(q, f);
        fq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return fq;
    }
}

