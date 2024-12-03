/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria;

import org.glassfish.ha.store.criteria.Criteria;
import org.glassfish.ha.store.criteria.Expression;
import org.glassfish.ha.store.criteria.spi.AttributeAccessNode;
import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.LiteralNode;
import org.glassfish.ha.store.criteria.spi.LogicalExpressionNode;
import org.glassfish.ha.store.criteria.spi.Opcode;
import org.glassfish.ha.store.spi.AttributeMetadata;

public class ExpressionBuilder<V> {
    Class<V> entryClazz;

    public ExpressionBuilder(Class<V> entryClazz) {
        this.entryClazz = entryClazz;
    }

    public Criteria<V> setCriteria(Expression<Boolean> expr) {
        Criteria<V> c = new Criteria<V>(this.entryClazz);
        c.setExpression(expr);
        return c;
    }

    public <T> AttributeAccessNode<V, T> attr(AttributeMetadata<V, T> meta) {
        return new AttributeAccessNode<V, T>(meta);
    }

    public <T> LiteralNode<T> literal(Class<T> type, T value) {
        return new LiteralNode<T>(type, value);
    }

    public <T> LogicalExpressionNode eq(T value, AttributeMetadata<V, T> meta) {
        return new LogicalExpressionNode(Opcode.EQ, new LiteralNode<T>(meta.getAttributeType(), value), new AttributeAccessNode<V, T>(meta));
    }

    public <T> LogicalExpressionNode eq(AttributeMetadata<V, T> meta, T value) {
        return new LogicalExpressionNode(Opcode.EQ, new AttributeAccessNode<V, T>(meta), new LiteralNode<T>(meta.getAttributeType(), value));
    }

    public <T> LogicalExpressionNode eq(AttributeMetadata<V, T> meta1, AttributeMetadata<V, T> meta2) {
        return new LogicalExpressionNode(Opcode.EQ, new AttributeAccessNode<V, T>(meta1), new AttributeAccessNode<V, T>(meta2));
    }

    public <T> LogicalExpressionNode eq(ExpressionNode<T> expr1, ExpressionNode<T> expr2) {
        return new LogicalExpressionNode(Opcode.EQ, expr1, expr2);
    }

    public <T extends Number> LogicalExpressionNode eq(LiteralNode<T> value, AttributeMetadata<V, T> meta) {
        return new LogicalExpressionNode(Opcode.EQ, value, new AttributeAccessNode<V, T>(meta));
    }

    public <T extends Number> LogicalExpressionNode eq(AttributeMetadata<V, T> meta, LiteralNode<T> value) {
        return new LogicalExpressionNode(Opcode.EQ, new AttributeAccessNode<V, T>(meta), value);
    }
}

