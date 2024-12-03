/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class DeletedQueryNode
extends QueryNodeImpl {
    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        return "[DELETEDCHILD]";
    }

    @Override
    public String toString() {
        return "<deleted/>";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        DeletedQueryNode clone = (DeletedQueryNode)super.cloneTree();
        return clone;
    }
}

