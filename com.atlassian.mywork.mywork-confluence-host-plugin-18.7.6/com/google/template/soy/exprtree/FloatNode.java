/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.exprtree;

import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.FloatType;

public class FloatNode
extends AbstractPrimitiveNode {
    private final double value;

    public FloatNode(double value) {
        this.value = value;
    }

    protected FloatNode(FloatNode orig) {
        super(orig);
        this.value = orig.value;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.FLOAT_NODE;
    }

    @Override
    public SoyType getType() {
        return FloatType.getInstance();
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public String toSourceString() {
        return Double.toString(this.value).replace('E', 'e');
    }

    @Override
    public FloatNode clone() {
        return new FloatNode(this);
    }
}

