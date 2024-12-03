/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class MatchAllDocsQueryNode
extends QueryNodeImpl {
    @Override
    public String toString() {
        return "<matchAllDocs field='*' term='*'/>";
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        return "*:*";
    }

    @Override
    public MatchAllDocsQueryNode cloneTree() throws CloneNotSupportedException {
        MatchAllDocsQueryNode clone = (MatchAllDocsQueryNode)super.cloneTree();
        return clone;
    }
}

