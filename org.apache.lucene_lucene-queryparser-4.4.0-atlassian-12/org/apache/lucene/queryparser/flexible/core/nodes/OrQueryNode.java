/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.Iterator;
import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class OrQueryNode
extends BooleanQueryNode {
    public OrQueryNode(List<QueryNode> clauses) {
        super(clauses);
        if (clauses == null || clauses.size() == 0) {
            throw new IllegalArgumentException("OR query must have at least one clause");
        }
    }

    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<boolean operation='or'/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<boolean operation='or'>");
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
        Iterator<QueryNode> it = this.getChildren().iterator();
        while (it.hasNext()) {
            sb.append(filler).append(it.next().toQueryString(escapeSyntaxParser));
            filler = " OR ";
        }
        if (this.getParent() != null && this.getParent() instanceof GroupQueryNode || this.isRoot()) {
            return sb.toString();
        }
        return "( " + sb.toString() + " )";
    }
}

