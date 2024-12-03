/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.builders.QueryTreeBuilder;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.Query;

public class BoostQueryNodeBuilder
implements StandardQueryBuilder {
    @Override
    public Query build(QueryNode queryNode) throws QueryNodeException {
        BoostQueryNode boostNode = (BoostQueryNode)queryNode;
        QueryNode child = boostNode.getChild();
        if (child == null) {
            return null;
        }
        Query query = (Query)child.getTag(QueryTreeBuilder.QUERY_TREE_BUILDER_TAGID);
        query.setBoost(boostNode.getValue());
        return query;
    }
}

