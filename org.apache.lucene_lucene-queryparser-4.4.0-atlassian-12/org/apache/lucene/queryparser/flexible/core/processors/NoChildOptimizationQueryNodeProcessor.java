/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.DeletedQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class NoChildOptimizationQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof BooleanQueryNode || node instanceof BoostQueryNode || node instanceof TokenizedPhraseQueryNode || node instanceof ModifierQueryNode) {
            List<QueryNode> children = node.getChildren();
            if (children != null && children.size() > 0) {
                for (QueryNode child : children) {
                    if (child instanceof DeletedQueryNode) continue;
                    return node;
                }
            }
            return new MatchNoDocsQueryNode();
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

