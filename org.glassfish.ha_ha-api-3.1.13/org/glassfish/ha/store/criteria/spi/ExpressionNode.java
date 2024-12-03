/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import org.glassfish.ha.store.criteria.Expression;
import org.glassfish.ha.store.criteria.spi.Opcode;

public abstract class ExpressionNode<T>
implements Expression<T> {
    private Opcode opcode;
    protected Class<T> returnType;

    public ExpressionNode(Opcode opcode, Class<T> returnType) {
        this.opcode = opcode;
        this.returnType = returnType;
    }

    public Opcode getOpcode() {
        return this.opcode;
    }

    @Override
    public Class<T> getReturnType() {
        return this.returnType;
    }
}

