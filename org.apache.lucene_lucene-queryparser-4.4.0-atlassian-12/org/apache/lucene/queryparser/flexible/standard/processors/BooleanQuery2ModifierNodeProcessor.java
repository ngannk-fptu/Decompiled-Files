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
import org.apache.lucene.queryparser.flexible.core.nodes.ModifierQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.BooleanModifierNode;

public class BooleanQuery2ModifierNodeProcessor
implements QueryNodeProcessor {
    static final String TAG_REMOVE = "remove";
    static final String TAG_MODIFIER = "wrapWithModifier";
    static final String TAG_BOOLEAN_ROOT = "booleanRoot";
    QueryConfigHandler queryConfigHandler;
    private final ArrayList<QueryNode> childrenBuffer = new ArrayList();
    private Boolean usingAnd = false;

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        StandardQueryConfigHandler.Operator op = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR);
        if (op == null) {
            throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.DEFAULT_OPERATOR should be set on the QueryConfigHandler");
        }
        this.usingAnd = StandardQueryConfigHandler.Operator.AND == op;
        return this.processIteration(queryTree);
    }

    protected void processChildren(QueryNode queryTree) throws QueryNodeException {
        List<QueryNode> children = queryTree.getChildren();
        if (children != null && children.size() > 0) {
            for (QueryNode child : children) {
                child = this.processIteration(child);
            }
        }
    }

    private QueryNode processIteration(QueryNode queryTree) throws QueryNodeException {
        queryTree = this.preProcessNode(queryTree);
        this.processChildren(queryTree);
        queryTree = this.postProcessNode(queryTree);
        return queryTree;
    }

    protected void fillChildrenBufferAndApplyModifiery(QueryNode parent) {
        for (QueryNode node : parent.getChildren()) {
            if (node.containsTag(TAG_REMOVE)) {
                this.fillChildrenBufferAndApplyModifiery(node);
                continue;
            }
            if (node.containsTag(TAG_MODIFIER)) {
                this.childrenBuffer.add(this.applyModifier(node, (ModifierQueryNode.Modifier)((Object)node.getTag(TAG_MODIFIER))));
                continue;
            }
            this.childrenBuffer.add(node);
        }
    }

    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node.containsTag(TAG_BOOLEAN_ROOT)) {
            this.childrenBuffer.clear();
            this.fillChildrenBufferAndApplyModifiery(node);
            node.set(this.childrenBuffer);
        }
        return node;
    }

    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        QueryNode parent = node.getParent();
        if (node instanceof BooleanQueryNode) {
            if (parent instanceof BooleanQueryNode) {
                node.setTag(TAG_REMOVE, Boolean.TRUE);
            } else {
                node.setTag(TAG_BOOLEAN_ROOT, Boolean.TRUE);
            }
        } else if (parent instanceof BooleanQueryNode && (parent instanceof AndQueryNode || this.usingAnd.booleanValue() && this.isDefaultBooleanQueryNode(parent))) {
            this.tagModifierButDoNotOverride(node, ModifierQueryNode.Modifier.MOD_REQ);
        }
        return node;
    }

    protected boolean isDefaultBooleanQueryNode(QueryNode toTest) {
        return toTest != null && BooleanQueryNode.class.equals(toTest.getClass());
    }

    private QueryNode applyModifier(QueryNode node, ModifierQueryNode.Modifier mod) {
        if (!(node instanceof ModifierQueryNode)) {
            return new BooleanModifierNode(node, mod);
        }
        ModifierQueryNode modNode = (ModifierQueryNode)node;
        if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
            return new ModifierQueryNode(modNode.getChild(), mod);
        }
        return node;
    }

    protected void tagModifierButDoNotOverride(QueryNode node, ModifierQueryNode.Modifier mod) {
        if (node instanceof ModifierQueryNode) {
            ModifierQueryNode modNode = (ModifierQueryNode)node;
            if (modNode.getModifier() == ModifierQueryNode.Modifier.MOD_NONE) {
                node.setTag(TAG_MODIFIER, (Object)mod);
            }
        } else {
            node.setTag(TAG_MODIFIER, (Object)ModifierQueryNode.Modifier.MOD_REQ);
        }
    }

    @Override
    public void setQueryConfigHandler(QueryConfigHandler queryConfigHandler) {
        this.queryConfigHandler = queryConfigHandler;
    }

    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfigHandler;
    }
}

