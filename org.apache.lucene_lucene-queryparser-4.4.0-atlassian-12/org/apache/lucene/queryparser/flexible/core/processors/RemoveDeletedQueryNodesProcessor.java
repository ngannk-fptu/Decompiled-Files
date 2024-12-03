/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.DeletedQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.MatchNoDocsQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;

public class RemoveDeletedQueryNodesProcessor
extends QueryNodeProcessorImpl {
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        if ((queryTree = super.process(queryTree)) instanceof DeletedQueryNode && !(queryTree instanceof MatchNoDocsQueryNode)) {
            return new MatchNoDocsQueryNode();
        }
        return queryTree;
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (!node.isLeaf()) {
            List<QueryNode> children = node.getChildren();
            boolean removeBoolean = false;
            if (children == null || children.size() == 0) {
                removeBoolean = true;
            } else {
                removeBoolean = true;
                Iterator<QueryNode> it = children.iterator();
                while (it.hasNext()) {
                    if (it.next() instanceof DeletedQueryNode) continue;
                    removeBoolean = false;
                    break;
                }
            }
            if (removeBoolean) {
                return new DeletedQueryNode();
            }
        }
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        for (int i = 0; i < children.size(); ++i) {
            if (!(children.get(i) instanceof DeletedQueryNode)) continue;
            children.remove(i--);
        }
        return children;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }
}

