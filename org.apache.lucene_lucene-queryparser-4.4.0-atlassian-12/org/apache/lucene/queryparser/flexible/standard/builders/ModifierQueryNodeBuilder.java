/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.Query;

public class ModifierQueryNodeBuilder
implements StandardQueryBuilder {
    @Override
    public Query build(QueryNode queryNode) throws QueryNodeException {
        ModifierQueryNode modifierNode = (ModifierQueryNode)queryNode;
        return (Query)modifierNode.getChild().getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
    }
}

