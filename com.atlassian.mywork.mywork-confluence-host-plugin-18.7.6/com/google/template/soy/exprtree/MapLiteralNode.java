/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractParentExprNode;
import com.google.template.soy.exprtree.ExprNode;
import java.util.List;

public class MapLiteralNode
extends AbstractParentExprNode {
    public MapLiteralNode(List<ExprNode> alternatingKeysAndValues) {
        this.addChildren((List<? extends ExprNode>)alternatingKeysAndValues);
    }

    protected MapLiteralNode(MapLiteralNode orig) {
        super(orig);
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.MAP_LITERAL_NODE;
    }

    @Override
    public String toSourceString() {
        if (this.numChildren() == 0) {
            return "[:]";
        }
        StringBuilder sourceSb = new StringBuilder();
        sourceSb.append('[');
        int n = this.numChildren();
        for (int i = 0; i < n; i += 2) {
            if (i != 0) {
                sourceSb.append(", ");
            }
            sourceSb.append(this.getChild(i).toSourceString()).append(": ").append(this.getChild(i + 1).toSourceString());
        }
        sourceSb.append(']');
        return sourceSb.toString();
    }

    @Override
    public MapLiteralNode clone() {
        return new MapLiteralNode(this);
    }
}

