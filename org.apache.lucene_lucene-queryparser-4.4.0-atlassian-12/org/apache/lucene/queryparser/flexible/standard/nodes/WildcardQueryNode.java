/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class WildcardQueryNode
extends FieldQueryNode {
    public WildcardQueryNode(CharSequence field, CharSequence text, int begin, int end) {
        super(field, text, begin, end);
    }

    public WildcardQueryNode(FieldQueryNode fqn) {
        this(fqn.getField(), fqn.getText(), fqn.getBegin(), fqn.getEnd());
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return this.text;
        }
        return this.field + ":" + this.text;
    }

    @Override
    public String toString() {
        return "<wildcard field='" + this.field + "' term='" + this.text + "'/>";
    }

    @Override
    public WildcardQueryNode cloneTree() throws CloneNotSupportedException {
        WildcardQueryNode clone = (WildcardQueryNode)super.cloneTree();
        return clone;
    }
}

