/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.MatchAllDocsQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchAllDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.search.MatchAllDocsQuery;

public class MatchAllDocsQueryNodeBuilder
implements StandardQueryBuilder {
    public MatchAllDocsQuery build(QueryNode queryNode) throws QueryNodeException {
        if (!(queryNode instanceof MatchAllDocsQueryNode)) {
            throw new QueryNodeException(new MessageImpl(QueryParserMessages.LUCENE_QUERY_CONVERSION_ERROR, queryNode.toQueryString(new EscapeQuerySyntaxImpl()), queryNode.getClass().getName()));
        }
        return new MatchAllDocsQuery();
    }
}

