/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.GroupQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class BooleanQueryNode
extends QueryNodeImpl {
    public BooleanQueryNode(List<QueryNode> clauses) {
        this.setLeaf(false);
        this.allocate();
        this.set(clauses);
    }

    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<boolean operation='default'/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<boolean operation='default'>");
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
            filler = " ";
        }
        if (this.getParent() != null && this.getParent() instanceof GroupQueryNode || this.isRoot()) {
            return sb.toString();
        }
        return "( " + sb.toString() + " )";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        BooleanQueryNode clone = (BooleanQueryNode)super.cloneTree();
        return clone;
    }
}

