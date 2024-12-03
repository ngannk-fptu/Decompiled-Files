/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;
import java.util.List;

public class ListLiteralNode
extends AbstractParentExprNode {
    public ListLiteralNode(List<ExprNode> items) {
        this.addChildren((List<? extends ExprNode>)items);
    }

    protected ListLiteralNode(ListLiteralNode orig) {
        super(orig);
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.LIST_LITERAL_NODE;
    }

    @Override
    public String toSourceString() {
        StringBuilder sourceSb = new StringBuilder();
        sourceSb.append('[');
        boolean isFirst = true;
        for (ExprNode child : this.getChildren()) {
            if (isFirst) {
                isFirst = false;
            } else {
                sourceSb.append(", ");
            }
            sourceSb.append(child.toSourceString());
        }
        sourceSb.append(']');
        return sourceSb.toString();
    }

    @Override
    public ListLiteralNode clone() {
        return new ListLiteralNode(this);
    }
}

