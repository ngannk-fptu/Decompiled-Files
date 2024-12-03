/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml;

import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public interface QueryBuilder {
    public Query getQuery(Element var1) throws ParserException;
}

