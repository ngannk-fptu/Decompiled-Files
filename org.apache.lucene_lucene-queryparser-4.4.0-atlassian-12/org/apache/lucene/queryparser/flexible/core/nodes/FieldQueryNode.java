/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldValuePairQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.nodes.TextableQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class FieldQueryNode
extends QueryNodeImpl
implements FieldValuePairQueryNode<CharSequence>,
TextableQueryNode {
    protected CharSequence field;
    protected CharSequence text;
    protected int begin;
    protected int end;
    protected int positionIncrement;

    public FieldQueryNode(CharSequence field, CharSequence text, int begin, int end) {
        this.field = field;
        this.text = text;
        this.begin = begin;
        this.end = end;
        this.setLeaf(true);
    }

    protected CharSequence getTermEscaped(EscapeQuerySyntax escaper) {
        return escaper.escape(this.text, Locale.getDefault(), EscapeQuerySyntax.Type.NORMAL);
    }

    protected CharSequence getTermEscapeQuoted(EscapeQuerySyntax escaper) {
        return escaper.escape(this.text, Locale.getDefault(), EscapeQuerySyntax.Type.STRING);
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escaper) {
        if (this.isDefaultField(this.field)) {
            return this.getTermEscaped(escaper);
        }
        return this.field + ":" + this.getTermEscaped(escaper);
    }

    @Override
    public String toString() {
        return "<field start='" + this.begin + "' end='" + this.end + "' field='" + this.field + "' text='" + this.text + "'/>";
    }

    public String getTextAsString() {
        if (this.text == null) {
            return null;
        }
        return this.text.toString();
    }

    public String getFieldAsString() {
        if (this.field == null) {
            return null;
        }
        return this.field.toString();
    }

    public int getBegin() {
        return this.begin;
    }

    public void setBegin(int begin) {
        this.begin = begin;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    @Override
    public CharSequence getField() {
        return this.field;
    }

    @Override
    public void setField(CharSequence field) {
        this.field = field;
    }

    public int getPositionIncrement() {
        return this.positionIncrement;
    }

    public void setPositionIncrement(int pi) {
        this.positionIncrement = pi;
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
    public FieldQueryNode cloneTree() throws CloneNotSupportedException {
        FieldQueryNode fqn = (FieldQueryNode)super.cloneTree();
        fqn.begin = this.begin;
        fqn.end = this.end;
        fqn.field = this.field;
        fqn.text = this.text;
        fqn.positionIncrement = this.positionIncrement;
        fqn.toQueryStringIgnoreFields = this.toQueryStringIgnoreFields;
        return fqn;
    }

    @Override
    public CharSequence getValue() {
        return this.getText();
    }

    @Override
    public void setValue(CharSequence value) {
        this.setText(value);
    }
}

