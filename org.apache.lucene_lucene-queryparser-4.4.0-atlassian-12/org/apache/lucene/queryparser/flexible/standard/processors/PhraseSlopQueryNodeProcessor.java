/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;

public class PhraseSlopQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        SlopQueryNode phraseSlopNode;
        if (node instanceof SlopQueryNode && !((phraseSlopNode = (SlopQueryNode)node).getChild() instanceof TokenizedPhraseQueryNode) && !(phraseSlopNode.getChild() instanceof MultiPhraseQueryNode)) {
            return phraseSlopNode.getChild();
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

