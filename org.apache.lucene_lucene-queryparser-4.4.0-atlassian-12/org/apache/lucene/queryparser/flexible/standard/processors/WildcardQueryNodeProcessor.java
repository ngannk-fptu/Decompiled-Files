/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.processors;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QuotedFieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.processors.QueryNodeProcessorImpl;
import org.apache.lucene.queryparser.flexible.core.util.UnescapedCharSequence;
import org.apache.lucene.queryparser.flexible.standard.nodes.PrefixWildcardQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;

public class WildcardQueryNodeProcessor
extends QueryNodeProcessorImpl {
    @Override
    protected QueryNode postProcessNode(QueryNode node) throws QueryNodeException {
        if (node instanceof FieldQueryNode || node instanceof FuzzyQueryNode) {
            FieldQueryNode fqn = (FieldQueryNode)node;
            CharSequence text = fqn.getText();
            if (fqn.getParent() instanceof TermRangeQueryNode || fqn instanceof QuotedFieldQueryNode || text.length() <= 0) {
                return node;
            }
            if (this.isPrefixWildcard(text)) {
                PrefixWildcardQueryNode prefixWildcardQN = new PrefixWildcardQueryNode(fqn);
                return prefixWildcardQN;
            }
            if (this.isWildcard(text)) {
                WildcardQueryNode wildcardQN = new WildcardQueryNode(fqn);
                return wildcardQN;
            }
        }
        return node;
    }

    private boolean isWildcard(CharSequence text) {
        if (text == null || text.length() <= 0) {
            return false;
        }
        for (int i = text.length() - 1; i >= 0; --i) {
            if (text.charAt(i) != '*' && text.charAt(i) != '?' || UnescapedCharSequence.wasEscaped(text, i)) continue;
            return true;
        }
        return false;
    }

    private boolean isPrefixWildcard(CharSequence text) {
        if (text == null || text.length() <= 0 || !this.isWildcard(text)) {
            return false;
        }
        if (text.charAt(text.length() - 1) != '*') {
            return false;
        }
        if (UnescapedCharSequence.wasEscaped(text, text.length() - 1)) {
            return false;
        }
        if (text.length() == 1) {
            return false;
        }
        for (int i = 0; i < text.length(); ++i) {
            if (text.charAt(i) == '?') {
                return false;
            }
            if (text.charAt(i) != '*' || UnescapedCharSequence.wasEscaped(text, i)) continue;
            return i == text.length() - 1;
        }
        return false;
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

