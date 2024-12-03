/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.Query;

public class GroupQueryNodeBuilder
implements StandardQueryBuilder {
    @Override
    public Query build(QueryNode queryNode) throws QueryNodeException {
        GroupQueryNode groupNode = (GroupQueryNode)queryNode;
        return (Query)groupNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
    }
}

