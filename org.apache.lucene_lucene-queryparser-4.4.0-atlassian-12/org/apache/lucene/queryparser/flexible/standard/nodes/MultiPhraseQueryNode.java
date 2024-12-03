/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.standard.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;

public class MultiPhraseQueryNode
extends QueryNodeImpl
implements FieldableNode {
    public MultiPhraseQueryNode() {
        this.setLeaf(false);
        this.allocate();
    }

    @Override
    public String toString() {
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<multiPhrase/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<multiPhrase>");
        for (QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</multiPhrase>");
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
            filler = ",";
        }
        return "[MTP[" + sb.toString() + "]]";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        MultiPhraseQueryNode clone = (MultiPhraseQueryNode)super.cloneTree();
        return clone;
    }

    @Override
    public CharSequence getField() {
        List<QueryNode> children = this.getChildren();
        if (children == null || children.size() == 0) {
            return null;
        }
        return ((FieldableNode)children.get(0)).getField();
    }

    @Override
    public void setField(CharSequence fieldName) {
        List<QueryNode> children = this.getChildren();
        if (children != null) {
            for (QueryNode child : children) {
                if (!(child instanceof FieldableNode)) continue;
                ((FieldableNode)child).setField(fieldName);
            }
        }
    }
}

