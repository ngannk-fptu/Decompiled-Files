/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.BooleanClause$Occur
 *  org.apache.lucene.search.BooleanQuery
 *  org.apache.lucene.search.BooleanQuery$TooManyClauses
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.AnyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class AnyQueryNodeBuilder
implements StandardQueryBuilder {
    public BooleanQuery build(QueryNode queryNode) throws QueryNodeException {
        AnyQueryNode andNode = (AnyQueryNode)queryNode;
        BooleanQuery bQuery = new BooleanQuery();
        List<QueryNode> children = andNode.getChildren();
        if (children != null) {
            for (QueryNode child : children) {
                Object obj = child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                if (obj == null) continue;
                Query query = (Query)obj;
                try {
                    bQuery.add(query, BooleanClause.Occur.SHOULD);
                }
                catch (BooleanQuery.TooManyClauses ex) {
                    throw new QueryNodeException(new MessageImpl(QueryParserMessages.EMPTY_MESSAGE), (Throwable)ex);
                }
            }
        }
        bQuery.setMinimumNumberShouldMatch(andNode.getMinimumMatchingElements());
        return bQuery;
    }
}

