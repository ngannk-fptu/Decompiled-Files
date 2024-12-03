/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.NullType;

public class NullNode
extends AbstractPrimitiveNode {
    public NullNode() {
    }

    protected NullNode(NullNode orig) {
        super(orig);
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.NULL_NODE;
    }

    @Override
    public SoyType getType() {
        return NullType.getInstance();
    }

    @Override
    public String toSourceString() {
        return "null";
    }

    @Override
    public NullNode clone() {
        return new NullNode(this);
    }
}

