/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import java.util.Collection;
import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.LogicalExpressionNode;
import org.glassfish.ha.store.criteria.spi.Opcode;

public class InExpressionNode<T>
extends LogicalExpressionNode {
    Collection<? extends T> entries;

    public InExpressionNode(ExpressionNode<T> value, Collection<? extends T> entries) {
        super(Opcode.IN, value, null);
        this.entries = entries;
    }

    public Collection<? extends T> getEntries() {
        return this.entries;
    }
}

