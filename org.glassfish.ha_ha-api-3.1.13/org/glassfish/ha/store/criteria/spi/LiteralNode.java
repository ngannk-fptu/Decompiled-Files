/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.Opcode;

public class LiteralNode<T>
extends ExpressionNode<T> {
    private T value;

    public LiteralNode(Class<T> clazz, T t) {
        super(Opcode.LITERAL, clazz);
        this.value = t;
    }

    public T getValue() {
        return this.value;
    }
}

