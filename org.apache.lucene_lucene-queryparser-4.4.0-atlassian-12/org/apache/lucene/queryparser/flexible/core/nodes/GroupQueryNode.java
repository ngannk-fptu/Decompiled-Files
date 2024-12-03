/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class GroupQueryNode
extends QueryNodeImpl {
    public GroupQueryNode(QueryNode query) {
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, "query", "null"));
        }
        this.allocate();
        this.setLeaf(false);
        this.add(query);
    }

    public QueryNode getChild() {
        return this.getChildren().get(0);
    }

    @Override
    public String toString() {
        return "<group>\n" + this.getChild().toString() + "\n</group>";
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        return "( " + this.getChild().toQueryString(escapeSyntaxParser) + " )";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        GroupQueryNode clone = (GroupQueryNode)super.cloneTree();
        return clone;
    }

    public void setChild(QueryNode child) {
        ArrayList<QueryNode> list = new ArrayList<QueryNode>();
        list.add(child);
        this.set(list);
    }
}

