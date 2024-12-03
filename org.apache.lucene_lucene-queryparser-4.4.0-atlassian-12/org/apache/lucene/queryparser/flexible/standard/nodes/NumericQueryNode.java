/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.text.NumberFormat;
import java.util.Locale;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldValuePairQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class NumericQueryNode
extends QueryNodeImpl
implements FieldValuePairQueryNode<Number> {
    private NumberFormat numberFormat;
    private CharSequence field;
    private Number value;

    public NumericQueryNode(CharSequence field, Number value, NumberFormat numberFormat) {
        this.setNumberFormat(numberFormat);
        this.setField(field);
        this.setValue(value);
    }

    @Override
    public CharSequence getField() {
        return this.field;
    }

    @Override
    public void setField(CharSequence fieldName) {
        this.field = fieldName;
    }

    protected CharSequence getTermEscaped(EscapeQuerySyntax escaper) {
        return escaper.escape(this.numberFormat.format(this.value), Locale.ROOT, EscapeQuerySyntax.Type.NORMAL);
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.isDefaultField(this.field)) {
            return this.getTermEscaped(escapeSyntaxParser);
        }
        return this.field + ":" + this.getTermEscaped(escapeSyntaxParser);
    }

    public void setNumberFormat(NumberFormat format) {
        this.numberFormat = format;
    }

    public NumberFormat getNumberFormat() {
        return this.numberFormat;
    }

    @Override
    public Number getValue() {
        return this.value;
    }

    @Override
    public void setValue(Number value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "<numeric field='" + this.field + "' number='" + this.numberFormat.format(this.value) + "'/>";
    }
}

