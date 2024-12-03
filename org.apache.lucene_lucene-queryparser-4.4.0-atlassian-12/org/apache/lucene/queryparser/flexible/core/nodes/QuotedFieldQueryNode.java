/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class QuotedFieldQueryNode
extends FieldQueryNode {
    public QuotedFieldQueryNode(CharSequence field, CharSequence text, int begin, int end) {
        super(field, text, begin, end);
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return "\"" + this.getTermEscapeQuoted(escaper) + "\"";
        }
        return this.field + ":\"" + this.getTermEscapeQuoted(escaper) + "\"";
    }

    @Override
    public String toString() {
        return "<quotedfield start='" + this.begin + "' end='" + this.end + "' field='" + this.field + "' term='" + this.text + "'/>";
    }

    @Override
    public QuotedFieldQueryNode cloneTree() throws CloneNotSupportedException {
        QuotedFieldQueryNode clone = (QuotedFieldQueryNode)super.cloneTree();
        return clone;
    }
}

