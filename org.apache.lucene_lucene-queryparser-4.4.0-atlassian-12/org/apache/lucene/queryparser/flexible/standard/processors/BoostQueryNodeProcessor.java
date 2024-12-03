/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.BoostQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;

public class BoostQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof FieldableNode && (node.getParent() == null || !(node.getParent() instanceof FieldableNode))) {
            Float boost;
            CharSequence field;
            FieldConfig fieldConfig;
            FieldableNode fieldNode = (FieldableNode)node;
            QueryConfigHandler config = this.getQueryConfigHandler();
            if (config != null && (fieldConfig = config.getFieldConfig(StringUtils.toString(field = fieldNode.getField()))) != null && (boost = fieldConfig.get(StandardQueryConfigHandler.ConfigurationKeys.BOOST)) != null) {
                return new BoostQueryNode(node, boost.floatValue());
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

