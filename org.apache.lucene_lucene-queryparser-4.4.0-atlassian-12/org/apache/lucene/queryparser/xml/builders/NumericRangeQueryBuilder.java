/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.NumericRangeQuery
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class NumericRangeQueryBuilder
implements QueryBuilder {
    @Override
    public Query getQuery(Element e) throws ParserException {
        String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String lowerTerm = DOMUtils.getAttributeOrFail(e, "lowerTerm");
        String upperTerm = DOMUtils.getAttributeOrFail(e, "upperTerm");
        boolean lowerInclusive = DOMUtils.getAttribute(e, "includeLower", true);
        boolean upperInclusive = DOMUtils.getAttribute(e, "includeUpper", true);
        int precisionStep = DOMUtils.getAttribute(e, "precisionStep", 4);
        String type = DOMUtils.getAttribute(e, "type", "int");
        try {
            NumericRangeQuery filter;
            if (type.equalsIgnoreCase("int")) {
                filter = NumericRangeQuery.newIntRange((String)field, (int)precisionStep, (Integer)Integer.valueOf(lowerTerm), (Integer)Integer.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("long")) {
                filter = NumericRangeQuery.newLongRange((String)field, (int)precisionStep, (Long)Long.valueOf(lowerTerm), (Long)Long.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("double")) {
                filter = NumericRangeQuery.newDoubleRange((String)field, (int)precisionStep, (Double)Double.valueOf(lowerTerm), (Double)Double.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("float")) {
                filter = NumericRangeQuery.newFloatRange((String)field, (int)precisionStep, (Float)Float.valueOf(lowerTerm), (Float)Float.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else {
                throw new ParserException("type attribute must be one of: [long, int, double, float]");
            }
            return filter;
        }
        catch (NumberFormatException nfe) {
            throw new ParserException("Could not parse lowerTerm or upperTerm into a number", nfe);
        }
    }
}

