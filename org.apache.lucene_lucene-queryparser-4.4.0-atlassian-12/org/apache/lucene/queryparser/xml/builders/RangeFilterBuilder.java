/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Filter
 *  org.apache.lucene.search.TermRangeFilter
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.TermRangeFilter;
import org.w3c.dom.Element;

public class RangeFilterBuilder
implements FilterBuilder {
    @Override
    public Filter getFilter(Element e) throws ParserException {
        String fieldName = DOMUtils.getAttributeWithInheritance(e, "fieldName");
        String lowerTerm = e.getAttribute("lowerTerm");
        String upperTerm = e.getAttribute("upperTerm");
        boolean includeLower = DOMUtils.getAttribute(e, "includeLower", true);
        boolean includeUpper = DOMUtils.getAttribute(e, "includeUpper", true);
        return TermRangeFilter.newStringRange((String)fieldName, (String)lowerTerm, (String)upperTerm, (boolean)includeLower, (boolean)includeUpper);
    }
}

