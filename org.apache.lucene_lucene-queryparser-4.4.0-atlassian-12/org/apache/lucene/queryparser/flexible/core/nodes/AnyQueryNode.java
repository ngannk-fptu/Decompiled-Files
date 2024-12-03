/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.AndQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class AnyQueryNode
extends AndQueryNode {
    private CharSequence field = null;
    private int minimumMatchingmElements = 0;

    public AnyQueryNode(List<QueryNode> clauses, CharSequence field, int minimumMatchingElements) {
        super(clauses);
        this.field = field;
        this.minimumMatchingmElements = minimumMatchingElements;
        if (clauses != null) {
            for (QueryNode clause : clauses) {
                if (!(clause instanceof FieldQueryNode)) continue;
                if (clause instanceof QueryNodeImpl) {
                    ((QueryNodeImpl)clause).toQueryStringIgnoreFields = true;
                }
                if (!(clause instanceof FieldableNode)) continue;
                ((FieldableNode)clause).setField(field);
            }
        }
    }

    public int getMinimumMatchingElements() {
        return this.minimumMatchingmElements;
    }

    public CharSequence getField() {
        return this.field;
    }

    public String getFieldAsString() {
        if (this.field == null) {
            return null;
        }
        return this.field.toString();
    }

    public void setField(CharSequence field) {
        this.field = field;
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        AnyQueryNode clone = (AnyQueryNode)super.cloneTree();
        clone.field = this.field;
        clone.minimumMatchingmElements = this.minimumMatchingmElements;
        return clone;
    }

    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<any field='" + this.field + "'  matchelements=" + this.minimumMatchingmElements + "/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<any field='" + this.field + "'  matchelements=" + this.minimumMatchingmElements + ">");
        for (QueryNode clause : this.getChildren()) {
            sb.append("\n");
            sb.append(clause.toString());
        }
        sb.append("\n</any>");
        return sb.toString();
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        String anySTR = "ANY " + this.minimumMatchingmElements;
        StringBuilder sb = new StringBuilder();
        if (this.getChildren() != null && this.getChildren().size() != 0) {
            String filler = "";
            for (QueryNode clause : this.getChildren()) {
                sb.append(filler).append(clause.toQueryString(escapeSyntaxParser));
                filler = " ";
            }
        }
        if (this.isDefaultField(this.field)) {
            return "( " + sb.toString() + " ) " + anySTR;
        }
        return this.field + ":(( " + sb.toString() + " ) " + anySTR + ")";
    }
}

