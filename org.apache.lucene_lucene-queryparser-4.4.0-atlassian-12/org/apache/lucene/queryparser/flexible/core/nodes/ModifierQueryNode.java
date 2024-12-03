/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.ArrayList;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNodeImpl;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class ModifierQueryNode
extends QueryNodeImpl {
    private Modifier modifier = Modifier.MOD_NONE;

    public ModifierQueryNode(QueryNode query, Modifier mod) {
        if (query == null) {
            throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, "query", "null"));
        }
        this.allocate();
        this.setLeaf(false);
        this.add(query);
        this.modifier = mod;
    }

    public QueryNode getChild() {
        return this.getChildren().get(0);
    }

    public Modifier getModifier() {
        return this.modifier;
    }

    @Override
    public String toString() {
        return "<modifier operation='" + this.modifier.toString() + "'>\n" + this.getChild().toString() + "\n</modifier>";
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        if (this.getChild() == null) {
            return "";
        }
        String leftParenthensis = "";
        String rightParenthensis = "";
        if (this.getChild() != null && this.getChild() instanceof ModifierQueryNode) {
            leftParenthensis = "(";
            rightParenthensis = ")";
        }
        if (this.getChild() instanceof BooleanQueryNode) {
            return this.modifier.toLargeString() + leftParenthensis + this.getChild().toQueryString(escapeSyntaxParser) + rightParenthensis;
        }
        return this.modifier.toDigitString() + leftParenthensis + this.getChild().toQueryString(escapeSyntaxParser) + rightParenthensis;
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        ModifierQueryNode clone = (ModifierQueryNode)super.cloneTree();
        clone.modifier = this.modifier;
        return clone;
    }

    public void setChild(QueryNode child) {
        ArrayList<QueryNode> list = new ArrayList<QueryNode>();
        list.add(child);
        this.set(list);
    }

    public static enum Modifier {
        MOD_NONE,
        MOD_NOT,
        MOD_REQ;


        public String toString() {
            switch (this) {
                case MOD_NONE: {
                    return "MOD_NONE";
                }
                case MOD_NOT: {
                    return "MOD_NOT";
                }
                case MOD_REQ: {
                    return "MOD_REQ";
                }
            }
            return "MOD_DEFAULT";
        }

        public String toDigitString() {
            switch (this) {
                case MOD_NONE: {
                    return "";
                }
                case MOD_NOT: {
                    return "-";
                }
                case MOD_REQ: {
                    return "+";
                }
            }
            return "";
        }

        public String toLargeString() {
            switch (this) {
                case MOD_NONE: {
                    return "";
                }
                case MOD_NOT: {
                    return "NOT ";
                }
                case MOD_REQ: {
                    return "+";
                }
            }
            return "";
        }
    }
}

