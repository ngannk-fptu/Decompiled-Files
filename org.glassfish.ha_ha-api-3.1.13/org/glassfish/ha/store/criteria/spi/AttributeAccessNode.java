/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import java.util.Collection;
import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.InExpressionNode;
import org.glassfish.ha.store.criteria.spi.LogicalExpressionNode;
import org.glassfish.ha.store.criteria.spi.Opcode;
import org.glassfish.ha.store.spi.AttributeMetadata;

public final class AttributeAccessNode<V, T>
extends ExpressionNode<T> {
    private AttributeMetadata<V, T> attr;

    public AttributeAccessNode(AttributeMetadata<V, T> attr) {
        super(Opcode.ATTR, attr.getAttributeType());
        this.attr = attr;
    }

    public AttributeMetadata<V, T> getAttributeMetadata() {
        return this.attr;
    }

    public LogicalExpressionNode in(Collection<? extends T> entries) {
        return new InExpressionNode<T>(this, entries);
    }
}

