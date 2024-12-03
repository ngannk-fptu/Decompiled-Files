/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.BoolType;

public class BooleanNode
extends AbstractPrimitiveNode {
    private final boolean value;

    public BooleanNode(boolean value) {
        this.value = value;
    }

    protected BooleanNode(BooleanNode orig) {
        super(orig);
        this.value = orig.value;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.BOOLEAN_NODE;
    }

    @Override
    public SoyType getType() {
        return BoolType.getInstance();
    }

    public boolean getValue() {
        return this.value;
    }

    @Override
    public String toSourceString() {
        return this.value ? "true" : "false";
    }

    @Override
    public BooleanNode clone() {
        return new BooleanNode(this);
    }
}

