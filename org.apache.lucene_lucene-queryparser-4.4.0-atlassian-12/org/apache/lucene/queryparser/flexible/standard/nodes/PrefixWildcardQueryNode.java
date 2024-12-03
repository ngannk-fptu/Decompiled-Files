/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.standard.nodes.WildcardQueryNode;

public class PrefixWildcardQueryNode
extends WildcardQueryNode {
    public PrefixWildcardQueryNode(CharSequence field, CharSequence text, int begin, int end) {
        super(field, text, begin, end);
    }

    public PrefixWildcardQueryNode(FieldQueryNode fqn) {
        this(fqn.getField(), fqn.getText(), fqn.getBegin(), fqn.getEnd());
    }

    @Override
    public String toString() {
        return "<prefixWildcard field='" + this.field + "' term='" + this.text + "'/>";
    }

    @Override
    public PrefixWildcardQueryNode cloneTree() throws CloneNotSupportedException {
        PrefixWildcardQueryNode clone = (PrefixWildcardQueryNode)super.cloneTree();
        return clone;
    }
}

