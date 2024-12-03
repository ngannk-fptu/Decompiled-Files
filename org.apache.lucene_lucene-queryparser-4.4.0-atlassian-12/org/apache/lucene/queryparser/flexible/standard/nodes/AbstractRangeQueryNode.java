/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldValuePairQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.nodes.RangeQueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;

public class AbstractRangeQueryNode<T extends FieldValuePairQueryNode<?>>
extends QueryNodeImpl
implements RangeQueryNode<FieldValuePairQueryNode<?>> {
    private boolean lowerInclusive;
    private boolean upperInclusive;

    protected AbstractRangeQueryNode() {
        this.setLeaf(false);
        this.allocate();
    }

    @Override
    public CharSequence getField() {
        CharSequence field = null;
        T lower = this.getLowerBound();
        T upper = this.getUpperBound();
        if (lower != null) {
            field = lower.getField();
        } else if (upper != null) {
            field = upper.getField();
        }
        return field;
    }

    @Override
    public void setField(CharSequence fieldName) {
        T lower = this.getLowerBound();
        T upper = this.getUpperBound();
        if (lower != null) {
            lower.setField(fieldName);
        }
        if (upper != null) {
            upper.setField(fieldName);
        }
    }

    @Override
    public T getLowerBound() {
        return (T)((FieldValuePairQueryNode)this.getChildren().get(0));
    }

    @Override
    public T getUpperBound() {
        return (T)((FieldValuePairQueryNode)this.getChildren().get(1));
    }

    @Override
    public boolean isLowerInclusive() {
        return this.lowerInclusive;
    }

    @Override
    public boolean isUpperInclusive() {
        return this.upperInclusive;
    }

    public void setBounds(T lower, T upper, boolean lowerInclusive, boolean upperInclusive) {
        if (lower != null && upper != null) {
            String lowerField = StringUtils.toString(lower.getField());
            String upperField = StringUtils.toString(upper.getField());
            if (!(upperField == null && lowerField == null || (upperField == null || upperField.equals(lowerField)) && lowerField.equals(upperField))) {
                throw new IllegalArgumentException("lower and upper bounds should have the same field name!");
            }
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
            ArrayList<QueryNode> children = new ArrayList<QueryNode>(2);
            children.add((QueryNode)lower);
            children.add((QueryNode)upper);
            this.set(children);
        }
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        StringBuilder sb = new StringBuilder();
        T lower = this.getLowerBound();
        T upper = this.getUpperBound();
        if (this.lowerInclusive) {
            sb.append('[');
        } else {
            sb.append('{');
        }
        if (lower != null) {
            sb.append(lower.toQueryString(escapeSyntaxParser));
        } else {
            sb.append("...");
        }
        sb.append(' ');
        if (upper != null) {
            sb.append(upper.toQueryString(escapeSyntaxParser));
        } else {
            sb.append("...");
        }
        if (this.upperInclusive) {
            sb.append(']');
        } else {
            sb.append('}');
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<").append(this.getClass().getCanonicalName());
        sb.append(" lowerInclusive=").append(this.isLowerInclusive());
        sb.append(" upperInclusive=").append(this.isUpperInclusive());
        sb.append(">\n\t");
        sb.append(this.getUpperBound()).append("\n\t");
        sb.append(this.getLowerBound()).append("\n");
        sb.append("</").append(this.getClass().getCanonicalName()).append(">\n");
        return sb.toString();
    }
}

