/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.Query
 *  org.apache.lucene.search.TermQuery
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.w3c.dom.Element;

public class TermQueryBuilder
implements QueryBuilder {
    @Override
    public Query getQuery(Element e) throws ParserException {
        String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String value = DOMUtils.getNonBlankTextOrFail(e);
        TermQuery tq = new TermQuery(new Term(field, value));
        tq.setBoost(DOMUtils.getAttribute(e, "boost", 1.0f));
        return tq;
    }
}

