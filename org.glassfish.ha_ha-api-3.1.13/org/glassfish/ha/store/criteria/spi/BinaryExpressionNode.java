/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.Opcode;

public class BinaryExpressionNode<T>
extends ExpressionNode<T> {
    private ExpressionNode<T> left;
    private ExpressionNode<T> right;

    public BinaryExpressionNode(Opcode opcode, Class<T> returnType, ExpressionNode<T> left) {
        this(opcode, returnType, left, null);
    }

    public BinaryExpressionNode(Opcode opcode, Class<T> returnType, ExpressionNode<T> left, ExpressionNode<T> right) {
        super(opcode, returnType);
        this.left = left;
        this.right = right;
    }

    public ExpressionNode<T> getLeft() {
        return this.left;
    }

    public ExpressionNode<T> getRight() {
        return this.right;
    }
}

