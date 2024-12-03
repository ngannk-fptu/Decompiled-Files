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
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.StandardBooleanQueryNode;
import org.apache.lucene.queryparser.flexible.standard.parser.EscapeQuerySyntaxImpl;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;

public class StandardBooleanQueryNodeBuilder
implements StandardQueryBuilder {
    public BooleanQuery build(QueryNode queryNode) throws QueryNodeException {
        StandardBooleanQueryNode booleanNode = (StandardBooleanQueryNode)queryNode;
        BooleanQuery bQuery = new BooleanQuery(booleanNode.isDisableCoord());
        List<QueryNode> children = booleanNode.getChildren();
        if (children != null) {
            for (QueryNode child : children) {
                Object obj = child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
                if (obj == null) continue;
                Query query = (Query)obj;
                try {
                    bQuery.add(query, StandardBooleanQueryNodeBuilder.getModifierValue(child));
                }
                catch (BooleanQuery.TooManyClauses ex) {
                    throw new QueryNodeException(new MessageImpl(QueryParserMessages.TOO_MANY_BOOLEAN_CLAUSES, BooleanQuery.getMaxClauseCount(), queryNode.toQueryString(new EscapeQuerySyntaxImpl())), (Throwable)ex);
                }
            }
        }
        return bQuery;
    }

    private static BooleanClause.Occur getModifierValue(QueryNode node) {
        if (node instanceof ModifierQueryNode) {
            ModifierQueryNode mNode = (ModifierQueryNode)node;
            ModifierQueryNode.Modifier modifier = mNode.getModifier();
            if (ModifierQueryNode.Modifier.MOD_NONE.equals((Object)modifier)) {
                return BooleanClause.Occur.SHOULD;
            }
            if (ModifierQueryNode.Modifier.MOD_NOT.equals((Object)modifier)) {
                return BooleanClause.Occur.MUST_NOT;
            }
            return BooleanClause.Occur.MUST;
        }
        return BooleanClause.Occur.SHOULD;
    }
}

