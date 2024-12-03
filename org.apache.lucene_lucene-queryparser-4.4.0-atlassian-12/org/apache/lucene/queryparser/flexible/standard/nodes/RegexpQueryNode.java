/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.BytesRef
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.util.BytesRef;

public class RegexpQueryNode
extends QueryNodeImpl
implements TextableQueryNode,
FieldableNode {
    private CharSequence text;
    private CharSequence field;

    public RegexpQueryNode(CharSequence field, CharSequence text, int begin, int end) {
        this.field = field;
        this.text = text.subSequence(begin, end);
    }

    public BytesRef textToBytesRef() {
        return new BytesRef(this.text);
    }

    @Override
    public String toString() {
        return "<regexp field='" + this.field + "' term='" + this.text + "'/>";
    }

    @Override
    public RegexpQueryNode cloneTree() throws CloneNotSupportedException {
        RegexpQueryNode clone = (RegexpQueryNode)super.cloneTree();
        clone.field = this.field;
        clone.text = this.text;
        return clone;
    }

    @Override
    public CharSequence getText() {
        return this.text;
    }

    @Override
    public void setText(CharSequence text) {
        this.text = text;
    }

    @Override
    public CharSequence getField() {
        return this.field;
    }

    public String getFieldAsString() {
        return this.field.toString();
    }

    @Override
    public void setField(CharSequence field) {
        this.field = field;
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        return this.isDefaultField(this.field) ? "/" + this.text + "/" : this.field + ":/" + this.text + "/";
    }
}

