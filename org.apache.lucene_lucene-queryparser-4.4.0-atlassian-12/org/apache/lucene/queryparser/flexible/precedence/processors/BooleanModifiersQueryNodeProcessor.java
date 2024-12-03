/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.precedence.processors;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class BooleanModifiersQueryNodeProcessor
extends QueryNodeProcessorImpl {
    private ArrayList<QueryNode> childrenBuffer = new ArrayList();
    private Boolean usingAnd = false;

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        StandardQueryConfigHandler.Operator op = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
        if (op == null) {
            throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR should be set on the QueryConfigHandler");
        }
        this.usingAnd = StandardQueryConfigHandler.Operator.AND == op;
        return super.process(queryTree);
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof AndQueryNode) {
            this.childrenBuffer.clear();
            List<QueryNode> children = node.getChildren();
            for (QueryNode child : children) {
                this.childrenBuffer.add(this.applyModifier(child, ModifierQueryNode.Modifier.MOD_REQ));
            }
            node.set(this.childrenBuffer);
        } else if (this.usingAnd.booleanValue() && node instanceof BooleanQueryNode && !(node instanceof OrQueryNode)) {
            this.childrenBuffer.clear();
            List<QueryNode> children = node.getChildren();
            for (QueryNode child : children) {
                this.childrenBuffer.add(this.applyModifier(child, ModifierQueryNode.Modifier.MOD_REQ));
            }
            node.set(this.childrenBuffer);
        }
        return node;
    }

    private QueryNode applyModifier(QueryNode node, ModifierQueryNode.Modifier mod) {
        if (!(node instanceof ModifierQueryNode)) {
            return new ModifierQueryNode(node, mod);
        }
        ModifierQueryNode modNode = (ModifierQueryNode)node;
        if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
            return new ModifierQueryNode(modNode.getChild(), mod);
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

