/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class RemoveEmptyNonLeafQueryNodeProcessor
extends QueryNodeProcessorImpl {
    private LinkedList<QueryNode> childrenBuffer = new LinkedList();

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        List<QueryNode> children;
        if (!((queryTree = super.process(queryTree)).isLeaf() || (children = queryTree.getChildren()) != null && children.size() != 0)) {
            return new MatchNoDocsQueryNode();
        }
        return queryTree;
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        try {
            for (QueryNode child : children) {
                if (!child.isLeaf()) {
                    List<QueryNode> grandChildren = child.getChildren();
                    if (grandChildren == null || grandChildren.size() <= 0) continue;
                    this.childrenBuffer.add(child);
                    continue;
                }
                this.childrenBuffer.add(child);
            }
            children.clear();
            children.addAll(this.childrenBuffer);
        }
        finally {
            this.childrenBuffer.clear();
        }
        return children;
    }
}

