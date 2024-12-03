/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class BoostQueryNode
extends QueryNodeImpl {
    private float value = 0.0f;

    public BoostQueryNode(QueryNode query, float value) {
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.NODE_ACTION_NOT_SUPPORTED, "query", "null"));
        }
        this.value = value;
        this.setLeaf(false);
        this.allocate();
        this.add(query);
    }

    public QueryNode getChild() {
        List<QueryNode> children = this.getChildren();
        if (children == null || children.size() == 0) {
            return null;
        }
        return children.get(0);
    }

    public float getValue() {
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
        return "<boost value='" + this.getValueString() + "'>\n" + this.getChild().toString() + "\n</boost>";
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return this.getChild().toQueryString(escapeSyntaxParser) + "^" + this.getValueString();
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        BoostQueryNode clone = (BoostQueryNode)super.cloneTree();
        clone.value = this.value;
        return clone;
    }
}

