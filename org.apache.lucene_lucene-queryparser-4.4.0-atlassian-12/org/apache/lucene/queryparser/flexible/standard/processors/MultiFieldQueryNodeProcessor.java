/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.LinkedList;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.OrQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class MultiFieldQueryNodeProcessor
extends QueryNodeProcessorImpl {
    private boolean processChildren = true;

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        return node;
    }

    @Override
    protected void processChildren(QueryNode queryTree) throws QueryNodeException {
        if (this.processChildren) {
            super.processChildren(queryTree);
        } else {
            this.processChildren = true;
        }
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof FieldableNode) {
            this.processChildren = false;
            FieldableNode fieldNode = (FieldableNode)node;
            if (fieldNode.getField() == null) {
                CharSequence[] fields = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS);
                if (fields == null) {
                    throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.MULTI_FIELDS should be set on the QueryConfigHandler");
                }
                if (fields != null && fields.length > 0) {
                    fieldNode.setField(fields[0]);
                    if (fields.length == 1) {
                        return fieldNode;
                    }
                    LinkedList<QueryNode> children = new LinkedList<QueryNode>();
                    children.add(fieldNode);
                    for (int i = 1; i < fields.length; ++i) {
                        try {
                            fieldNode = (FieldableNode)fieldNode.cloneTree();
                            fieldNode.setField(fields[i]);
                            children.add(fieldNode);
                            continue;
                        }
                        catch (CloneNotSupportedException cloneNotSupportedException) {
                            // empty catch block
                        }
                    }
                    return new GroupQueryNode(new OrQueryNode(children));
                }
            }
        }
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

