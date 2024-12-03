/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.xml;

import java.util.HashMap;
import org.apache.lucene.queryparser.xml.ParserException;
import org.apache.lucene.queryparser.xml.QueryBuilder;
import org.apache.lucene.search.Query;
import org.w3c.dom.Element;

public class QueryBuilderFactory
implements QueryBuilder {
    HashMap<String, QueryBuilder> builders = new HashMap();

    @Override
    public Query getQuery(Element n) throws ParserException {
        QueryBuilder builder = this.builders.get(n.getNodeName());
        if (builder == null) {
            throw new ParserException("No QueryObjectBuilder defined for node " + n.getNodeName());
        }
        return builder.getQuery(n);
    }

    public void addBuilder(String nodeName, QueryBuilder builder) {
        this.builders.put(nodeName, builder);
    }

    public QueryBuilder getQueryBuilder(String nodeName) {
        return this.builders.get(nodeName);
    }
}

