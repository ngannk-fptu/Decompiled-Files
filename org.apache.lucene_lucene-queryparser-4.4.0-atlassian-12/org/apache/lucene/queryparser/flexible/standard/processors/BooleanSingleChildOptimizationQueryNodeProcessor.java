/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;

public class BooleanSingleChildOptimizationQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        List<QueryNode> children;
        if (node instanceof BooleanQueryNode && (children = node.getChildren()) != null && children.size() == 1) {
            QueryNode child = children.get(0);
            if (child instanceof ModifierQueryNode) {
                ModifierQueryNode modNode = (ModifierQueryNode)child;
                if (modNode instanceof BooleanModifierNode || modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                    return child;
                }
            } else {
                return child;
            }
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

