/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.SlopQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TokenizedPhraseQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.MultiPhraseQueryNode;

public class DefaultPhraseSlopQueryNodeProcessor
extends QueryNodeProcessorImpl {
    private boolean processChildren = true;
    private int defaultPhraseSlop;

    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        Integer defaultPhraseSlop;
        QueryConfigHandler queryConfig = this.getQueryConfigHandler();
        if (queryConfig != null && (defaultPhraseSlop = queryConfig.get(StandardQueryConfigHandler.ConfigurationKeys.PHRASE_SLOP)) != null) {
            this.defaultPhraseSlop = defaultPhraseSlop;
            return super.process(queryTree);
        }
        return queryTree;
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof TokenizedPhraseQueryNode || node instanceof MultiPhraseQueryNode) {
            return new SlopQueryNode(node, this.defaultPhraseSlop);
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof SlopQueryNode) {
            this.processChildren = false;
        }
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
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) throws QueryNodeException {
        return children;
    }
}

