/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.processors;

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessor;

public abstract class QueryNodeProcessorImpl
implements QueryNodeProcessor {
    private ArrayList<ChildrenList> childrenListPool = new ArrayList();
    private QueryConfigHandler queryConfig;

    public QueryNodeProcessorImpl() {
    }

    public QueryNodeProcessorImpl(QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        return this.processIteration(queryTree);
    }

    private QueryNode processIteration(QueryNode queryTree) throws QueryNodeException {
        queryTree = this.preProcessNode(queryTree);
        this.processChildren(queryTree);
        queryTree = this.postProcessNode(queryTree);
        return queryTree;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void processChildren(QueryNode queryTree) throws QueryNodeException {
        List<QueryNode> children = queryTree.getChildren();
        if (children != null && children.size() > 0) {
            ChildrenList newChildren = this.allocateChildrenList();
            try {
                for (QueryNode child : children) {
                    if ((child = this.processIteration(child)) == null) {
                        throw new NullPointerException();
                    }
                    newChildren.add(child);
                }
                List<QueryNode> orderedChildrenList = this.setChildrenOrder(newChildren);
                queryTree.set(orderedChildrenList);
            }
            finally {
                newChildren.beingUsed = false;
            }
        }
    }

    private ChildrenList allocateChildrenList() {
        ChildrenList list = null;
        for (ChildrenList auxList : this.childrenListPool) {
            if (auxList.beingUsed) continue;
            list = auxList;
            list.clear();
            break;
        }
        if (list == null) {
            list = new ChildrenList();
            this.childrenListPool.add(list);
        }
        list.beingUsed = true;
        return list;
    }

    @Override
    public void setQueryConfigHandler(QueryConfigHandler queryConfigHandler) {
        this.queryConfig = queryConfigHandler;
    }

    @Override
    public QueryConfigHandler getQueryConfigHandler() {
        return this.queryConfig;
    }

    protected abstract QueryNode preProcessNode(QueryNode var1) throws QueryNodeException;

    protected abstract QueryNode postProcessNode(QueryNode var1) throws QueryNodeException;

    protected abstract List<QueryNode> setChildrenOrder(List<QueryNode> var1) throws QueryNodeException;

    private static class ChildrenList
    extends ArrayList<QueryNode> {
        boolean beingUsed;

        private ChildrenList() {
        }
    }
}

