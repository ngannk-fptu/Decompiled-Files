/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Filter
 */
package org.apache.lucene.queryparser.xml;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Filter;
import org.w3c.dom.Element;

public interface FilterBuilder {
    public Filter getFilter(Element var1) throws ParserException;
}

