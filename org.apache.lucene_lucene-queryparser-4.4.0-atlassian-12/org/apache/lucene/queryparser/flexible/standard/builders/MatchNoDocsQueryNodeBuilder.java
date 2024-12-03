/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.search.BooleanQuery;

public class MatchNoDocsQueryNodeBuilder
implements StandardQueryBuilder {
    public BooleanQuery build(QueryNode queryNode) throws QueryNodeException {
        if (!(queryNode instanceof MatchNoDocsQueryNode)) {
            throw new QueryNodeException(new MessageImpl(QueryParserMessages.LUCENE_QUERY_CONVERSION_ERROR, queryNode.toQueryString(new EscapeQuerySyntaxImpl()), queryNode.getClass().getName()));
        }
        return new BooleanQuery();
    }
}

