/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.queries.BooleanFilter
 *  org.apache.lucene.queries.FilterClause
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.Filter
 */
package org.apache.lucene.queryparser.xml.builders;

import org.apache.lucene.queries.BooleanFilter;
import org.apache.lucene.queries.FilterClause;
import org.apache.lucene.queryparser.xml.DOMUtils;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class BooleanFilterBuilder
implements FilterBuilder {
    private final FilterBuilder factory;

    public BooleanFilterBuilder(FilterBuilder factory) {
        this.factory = factory;
    }

    @Override
    public Filter getFilter(Element e) throws ParserException {
        BooleanFilter bf = new BooleanFilter();
        NodeList nl = e.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            if (!node.getNodeName().equals("Clause")) continue;
            Element clauseElem = (Element)node;
            BooleanClause.Occur occurs = BooleanQueryBuilder.getOccursValue(clauseElem);
            Element clauseFilter = DOMUtils.getFirstChildOrFail(clauseElem);
            Filter f = this.factory.getFilter(clauseFilter);
            bf.add(new FilterClause(f, occurs));
        }
        return bf;
    }
}

