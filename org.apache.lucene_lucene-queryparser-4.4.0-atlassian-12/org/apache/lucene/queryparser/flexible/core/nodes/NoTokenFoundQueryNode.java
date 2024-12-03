/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.DeletedQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class NoTokenFoundQueryNode
extends DeletedQueryNode {
    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        return "[NTF]";
    }

    @Override
    public String toString() {
        return "<notokenfound/>";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        NoTokenFoundQueryNode clone = (NoTokenFoundQueryNode)super.cloneTree();
        return clone;
    }
}

