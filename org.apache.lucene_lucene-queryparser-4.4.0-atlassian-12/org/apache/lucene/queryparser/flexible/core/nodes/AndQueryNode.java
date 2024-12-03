/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class AndQueryNode
extends BooleanQueryNode {
    public AndQueryNode(List<QueryNode> clauses) {
        super(clauses);
        if (clauses == null || clauses.size() == 0) {
            throw new IllegalArgumentException("AND query must have at least one clause");
        }
    }

    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<boolean operation='and'/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<boolean operation='and'>");
        for (QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</boolean>");
        return sb.toString();
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        String filler = "";
        for (QueryNode child : this.getChildren()) {
            sb.append(filler).append(child.toQueryString(escapeSyntaxParser));
            filler = " AND ";
        }
        if (this.getParent() != null && this.getParent() instanceof GroupQueryNode || this.isRoot()) {
            return sb.toString();
        }
        return "( " + sb.toString() + " )";
    }
}

