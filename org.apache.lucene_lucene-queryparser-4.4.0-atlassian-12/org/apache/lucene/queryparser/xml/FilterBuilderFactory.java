/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Filter
 */
package org.apache.lucene.queryparser.xml;

import java.util.HashMap;
import org.apache.lucene.queryparser.xml.FilterBuilder;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;

public class FilterBuilderFactory
implements FilterBuilder {
    HashMap<String, FilterBuilder> builders = new HashMap();

    @Override
    public Filter getFilter(Element n) throws ParserException {
        FilterBuilder builder = this.builders.get(n.getNodeName());
        if (builder == null) {
            throw new ParserException("No FilterBuilder defined for node " + n.getNodeName());
        }
        return builder.getFilter(n);
    }

    public void addBuilder(String nodeName, FilterBuilder builder) {
        this.builders.put(nodeName, builder);
    }

    public FilterBuilder getFilterBuilder(String nodeName) {
        return this.builders.get(nodeName);
    }
}

