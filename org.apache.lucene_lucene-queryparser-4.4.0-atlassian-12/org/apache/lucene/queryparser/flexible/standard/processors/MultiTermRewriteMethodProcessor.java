/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.AbstractRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;
import org.apache.lucene.search.MultiTermQuery;

public class MultiTermRewriteMethodProcessor
extends QueryNodeProcessorImpl {
    public static final String TAG_ID = "MultiTermRewriteMethodConfiguration";

    @Override
    protected QueryNode postProcessNode(QueryNode node) {
        if (node instanceof WildcardQueryNode || node instanceof AbstractRangeQueryNode || node instanceof RegexpQueryNode) {
            MultiTermQuery.RewriteMethod rewriteMethod = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD);
            if (rewriteMethod == null) {
                throw new IllegalArgumentException("StandardQueryConfigHandler.ConfigurationKeys.MULTI_TERM_REWRITE_METHOD should be set on the QueryConfigHandler");
            }
            node.setTag(TAG_ID, rewriteMethod);
        }
        return node;
    }

    @Override
    protected QueryNode preProcessNode(QueryNode node) {
        return node;
    }

    @Override
    protected List<QueryNode> setChildrenOrder(List<QueryNode> children) {
        return children;
    }
}

