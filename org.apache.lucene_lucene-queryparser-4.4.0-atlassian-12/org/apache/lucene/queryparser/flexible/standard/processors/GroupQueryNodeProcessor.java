/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;

public class GroupQueryNodeProcessor
implements QueryNodeProcessor {
    private ArrayList<QueryNode> queryNodeList;
    private boolean latestNodeVerified;
    private QueryConfigHandler queryConfig;
    private Boolean usingAnd = false;

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        StandardQueryConfigHandler.Operator defaultOperator = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
        if (defaultOperator == null) {
            throw new IllegalArgumentException("DEFAULT_OPERATOR should be set on the QueryConfigHandler");
        }
        this.usingAnd = StandardQueryConfigHandler.Operator.AND == defaultOperator;
        if (queryTree instanceof GroupQueryNode) {
            queryTree = ((GroupQueryNode)queryTree).getChild();
        }
        this.queryNodeList = new ArrayList();
        this.latestNodeVerified = false;
        this.readTree(queryTree);
        ArrayList<QueryNode> actualQueryNodeList = this.queryNodeList;
        for (int i = 0; i < actualQueryNodeList.size(); ++i) {
            QueryNode node = (QueryNode)actualQueryNodeList.get(i);
            if (!(node instanceof GroupQueryNode)) continue;
            actualQueryNodeList.set(i, this.process(node));
        }
        this.usingAnd = false;
        if (queryTree instanceof BooleanQueryNode) {
            queryTree.set(actualQueryNodeList);
            return queryTree;
        }
        return new BooleanQueryNode(actualQueryNodeList);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private QueryNode applyModifier(QueryNode node, QueryNode parent) {
        if (this.usingAnd.booleanValue()) {
            if (parent instanceof OrQueryNode) {
                ModifierQueryNode modNode;
                if (!(node instanceof ModifierQueryNode) || (modNode = (ModifierQueryNode)node).getModifier() != ModifierQueryNode.Modifier.MOD_REQ) return node;
                return modNode.getChild();
            }
            if (!(node instanceof ModifierQueryNode)) return new BooleanModifierNode(node, ModifierQueryNode.Modifier.MOD_REQ);
            ModifierQueryNode modNode = (ModifierQueryNode)node;
            if (modNode.getModifier() != ModifierQueryNode.Modifier.MOD_NONE) return node;
            return new BooleanModifierNode(modNode.getChild(), ModifierQueryNode.Modifier.MOD_REQ);
        }
        if (!(node.getParent() instanceof AndQueryNode)) return node;
        if (!(node instanceof ModifierQueryNode)) return new BooleanModifierNode(node, ModifierQueryNode.Modifier.MOD_REQ);
        ModifierQueryNode modNode = (ModifierQueryNode)node;
        if (modNode.getModifier() != ModifierQueryNode.Modifier.MOD_NONE) return node;
        return new BooleanModifierNode(modNode.getChild(), ModifierQueryNode.Modifier.MOD_REQ);
    }

    private void readTree(QueryNode node) {
        if (node instanceof BooleanQueryNode) {
            List<QueryNode> children = node.getChildren();
            if (children != null && children.size() > 0) {
                for (int i = 0; i < children.size() - 1; ++i) {
                    this.readTree(children.get(i));
                }
                this.processNode(node);
                this.readTree(children.get(children.size() - 1));
            } else {
                this.processNode(node);
            }
        } else {
            this.processNode(node);
        }
    }

    private void processNode(QueryNode node) {
        if (node instanceof AndQueryNode || node instanceof OrQueryNode) {
            if (!this.latestNodeVerified && !this.queryNodeList.isEmpty()) {
                this.queryNodeList.add(this.applyModifier(this.queryNodeList.remove(this.queryNodeList.size() - 1), node));
                this.latestNodeVerified = true;
            }
        } else if (!(node instanceof BooleanQueryNode)) {
            this.queryNodeList.add(this.applyModifier(node, node.getParent()));
            this.latestNodeVerified = false;
        }
    }

    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }

    @Override
    public void setQueryConfigHandler(QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }
}

