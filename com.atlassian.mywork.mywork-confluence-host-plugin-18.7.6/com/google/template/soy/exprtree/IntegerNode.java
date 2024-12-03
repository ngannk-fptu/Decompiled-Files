/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 */
package com.google.template.soy.exprtree;

import com.google.common.base.Objects;
import com.google.template.soy.exprtree.AbstractPrimitiveNode;
import com.google.template.soy.exprtree.ExprNode;
import com.google.template.soy.types.SoyType;
import com.google.template.soy.types.primitive.IntType;

public class IntegerNode
extends AbstractPrimitiveNode {
    private final int value;

    public IntegerNode(int value) {
        this.value = value;
    }

    protected IntegerNode(IntegerNode orig) {
        super(orig);
        this.value = orig.value;
    }

    @Override
    public ExprNode.Kind getKind() {
        return ExprNode.Kind.INTEGER_NODE;
    }

    @Override
    public SoyType getType() {
        return IntType.getInstance();
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toSourceString() {
        return Integer.toString(this.value);
    }

    @Override
    public IntegerNode clone() {
        return new IntegerNode(this);
    }

    public boolean equals(Object other) {
        if (other == null || other.getClass() != this.getClass()) {
            return false;
        }
        IntegerNode otherInt = (IntegerNode)other;
        return this.value == otherInt.value;
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.getClass(), this.value});
    }
}

