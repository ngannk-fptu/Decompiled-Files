/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.AtomicReaderContext
 *  org.apache.lucene.search.DocIdSet
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.NumericRangeFilter
 *  org.apache.lucene.util.Bits
 */
package org.apache.lucene.queryparser.xml.builders;

import java.io.IOException;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.DocIdSet;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.util.Bits;
import org.w3c.dom.Element;

public class NumericRangeFilterBuilder
implements FilterBuilder {
    private static final NoMatchFilter NO_MATCH_FILTER = new NoMatchFilter();
    private boolean strictMode = false;

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    @Override
    public Filter getFilter(Element e) throws ParserException {
        String field = DOMUtils.getAttributeWithInheritanceOrFail(e, "fieldName");
        String lowerTerm = DOMUtils.getAttributeOrFail(e, "lowerTerm");
        String upperTerm = DOMUtils.getAttributeOrFail(e, "upperTerm");
        boolean lowerInclusive = DOMUtils.getAttribute(e, "includeLower", true);
        boolean upperInclusive = DOMUtils.getAttribute(e, "includeUpper", true);
        int precisionStep = DOMUtils.getAttribute(e, "precisionStep", 4);
        String type = DOMUtils.getAttribute(e, "type", "int");
        try {
            NumericRangeFilter filter;
            if (type.equalsIgnoreCase("int")) {
                filter = NumericRangeFilter.newIntRange((String)field, (int)precisionStep, (Integer)Integer.valueOf(lowerTerm), (Integer)Integer.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("long")) {
                filter = NumericRangeFilter.newLongRange((String)field, (int)precisionStep, (Long)Long.valueOf(lowerTerm), (Long)Long.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("double")) {
                filter = NumericRangeFilter.newDoubleRange((String)field, (int)precisionStep, (Double)Double.valueOf(lowerTerm), (Double)Double.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else if (type.equalsIgnoreCase("float")) {
                filter = NumericRangeFilter.newFloatRange((String)field, (int)precisionStep, (Float)Float.valueOf(lowerTerm), (Float)Float.valueOf(upperTerm), (boolean)lowerInclusive, (boolean)upperInclusive);
            } else {
                throw new ParserException("type attribute must be one of: [long, int, double, float]");
            }
            return filter;
        }
        catch (NumberFormatException nfe) {
            if (this.strictMode) {
                throw new ParserException("Could not parse lowerTerm or upperTerm into a number", nfe);
            }
            return NO_MATCH_FILTER;
        }
    }

    static class NoMatchFilter
    extends Filter {
        NoMatchFilter() {
        }

        public DocIdSet getDocIdSet(AtomicReaderContext context, Bits acceptDocs) throws IOException {
            return null;
        }
    }
}

