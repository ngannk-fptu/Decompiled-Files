/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.nodes;

import java.util.List;
import org.apache.lucene.queryparser.flexible.core.QueryNodeError;
import org.apache.lucene.queryparser.flexible.core.messages.QueryParserMessages;
import org.apache.lucene.queryparser.flexible.core.nodes.BooleanQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.parser.EscapeQuerySyntax;
import org.apache.lucene.queryparser.flexible.messages.MessageImpl;

public class ProximityQueryNode
extends BooleanQueryNode {
    private Type proximityType = Type.SENTENCE;
    private int distance = -1;
    private boolean inorder = false;
    private CharSequence field = null;

    public ProximityQueryNode(List<QueryNode> clauses, CharSequence field, Type type, int distance, boolean inorder) {
        super(clauses);
        this.setLeaf(false);
        this.proximityType = type;
        this.inorder = inorder;
        this.field = field;
        if (type == Type.NUMBER) {
            if (distance <= 0) {
                throw new QueryNodeError(new MessageImpl(QueryParserMessages.PARAMETER_VALUE_NOT_SUPPORTED, "distance", distance));
            }
            this.distance = distance;
        }
        ProximityQueryNode.clearFields(clauses, field);
    }

    public ProximityQueryNode(List<QueryNode> clauses, CharSequence field, Type type, boolean inorder) {
        this(clauses, field, type, -1, inorder);
    }

    private static void clearFields(List<QueryNode> nodes, CharSequence field) {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        for (QueryNode clause : nodes) {
            if (!(clause instanceof FieldQueryNode)) continue;
            ((FieldQueryNode)clause).toQueryStringIgnoreFields = true;
            ((FieldQueryNode)clause).setField(field);
        }
    }

    public Type getProximityType() {
        return this.proximityType;
    }

    @Override
    public String toString() {
        String distanceSTR;
        String string = distanceSTR = this.distance == -1 ? "" : " distance='" + this.distance + "'";
        if (this.getChildren() == null || this.getChildren().size() == 0) {
            return "<proximity field='" + this.field + "' inorder='" + this.inorder + "' type='" + this.proximityType.toString() + "'" + distanceSTR + "/>";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<proximity field='" + this.field + "' inorder='" + this.inorder + "' type='" + this.proximityType.toString() + "'" + distanceSTR + ">");
        for (QueryNode child : this.getChildren()) {
            sb.append("\n");
            sb.append(child.toString());
        }
        sb.append("\n</proximity>");
        return sb.toString();
    }

    @Override
    public CharSequence toQueryString(EscapeQuerySyntax escapeSyntaxParser) {
        String withinSTR = this.proximityType.toQueryString() + (this.distance == -1 ? "" : " " + this.distance) + (this.inorder ? " INORDER" : "");
        StringBuilder sb = new StringBuilder();
        if (this.getChildren() != null && this.getChildren().size() != 0) {
            String filler = "";
            for (QueryNode child : this.getChildren()) {
                sb.append(filler).append(child.toQueryString(escapeSyntaxParser));
                filler = " ";
            }
        }
        if (this.isDefaultField(this.field)) {
            return "( " + sb.toString() + " ) " + withinSTR;
        }
        return this.field + ":(( " + sb.toString() + " ) " + withinSTR + ")";
    }

    @Override
    public QueryNode cloneTree() throws CloneNotSupportedException {
        ProximityQueryNode clone = (ProximityQueryNode)super.cloneTree();
        clone.proximityType = this.proximityType;
        clone.distance = this.distance;
        clone.field = this.field;
        return clone;
    }

    public int getDistance() {
        return this.distance;
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

    public boolean isInOrder() {
        return this.inorder;
    }

    public static class ProximityType {
        int pDistance = 0;
        Type pType = null;

        public ProximityType(Type type) {
            this(type, 0);
        }

        public ProximityType(Type type, int distance) {
            this.pType = type;
            this.pDistance = distance;
        }
    }

    public static enum Type {
        PARAGRAPH{

            @Override
            CharSequence toQueryString() {
                return "WITHIN PARAGRAPH";
            }
        }
        ,
        SENTENCE{

            @Override
            CharSequence toQueryString() {
                return "WITHIN SENTENCE";
            }
        }
        ,
        NUMBER{

            @Override
            CharSequence toQueryString() {
                return "WITHIN";
            }
        };


        abstract CharSequence toQueryString();
    }
}

