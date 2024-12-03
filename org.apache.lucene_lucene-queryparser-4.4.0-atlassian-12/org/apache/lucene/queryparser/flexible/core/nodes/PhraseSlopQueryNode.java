/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldableNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class PhraseSlopQueryNode
extends QueryNodeImpl
implements FieldableNode {
    private int value = 0;

    public PhraseSlopQueryNode(QueryNode query, int value) {
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED, "query", "null"));
        }
        this.value = value;
        this.setLeaf(false);
        this.allocate();
        this.add(query);
    }

    public QueryNode getChild() {
        return this.getChildren().get(0);
    }

    public int getValue() {
        return this.value;
    }

    private CharSequence getValueString() {
        Float f = Float.valueOf(this.value);
        if (f.floatValue() == (float)f.longValue()) {
            return "" + f.longValue();
        }
        return "" + f;
    }

    @Override
    public String toString() {
        return "<phraseslop value='" + this.getValueString() + "'>\n" + this.getChild().toString() + "\n</phraseslop>";
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return this.getChild().toQueryString(escapeSyntaxParser) + "~" + this.getValueString();
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        PhraseSlopQueryNode clone = (PhraseSlopQueryNode)super.cloneTree();
        clone.value = this.value;
        return clone;
    }

    @Override
    public CharSequence getField() {
        QueryNode child = this.getChild();
        if (child instanceof FieldableNode) {
            return ((FieldableNode)child).getField();
        }
        return null;
    }

    @Override
    public void setField(CharSequence fieldName) {
        QueryNode child = this.getChild();
        if (child instanceof FieldableNode) {
            ((FieldableNode)child).setField(fieldName);
        }
    }
}

