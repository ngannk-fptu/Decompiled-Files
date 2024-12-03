/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import org.apache.lucene.queryparser.flexible.standard.config.StandardQueryConfigHandler;
import org.apache.lucene.queryparser.flexible.standard.nodes.RegexpQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;

public class LowercaseExpandedTermsQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    public QueryNode process(QueryNode queryTree) throws QueryNodeException {
        Boolean lowercaseExpandedTerms = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOWERCASE_EXPANDED_TERMS);
        if (lowercaseExpandedTerms != null && lowercaseExpandedTerms.booleanValue()) {
            return super.process(queryTree);
        }
        return queryTree;
    }

    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        Locale locale = this.getQueryConfigHandler().get(StandardQueryConfigHandler.ConfigurationKeys.LOCALE);
        if (locale == null) {
            locale = Locale.getDefault();
        }
        if (node instanceof WildcardQueryNode || node instanceof FuzzyQueryNode || node instanceof FieldQueryNode && node.getParent() instanceof RangeQueryNode || node instanceof RegexpQueryNode) {
            TextableQueryNode txtNode;
            CharSequence text = (txtNode = (TextableQueryNode)((Object)node)).getText();
            txtNode.setText(text != null ? UnescapedCharSequence.toLowerCase(text, locale) : null);
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

