/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.ha.store.criteria.spi;

import java.util.Collection;
import org.glassfish.ha.store.criteria.spi.BinaryExpressionNode;
import org.glassfish.ha.store.criteria.spi.ExpressionNode;
import org.glassfish.ha.store.criteria.spi.LiteralNode;
import org.glassfish.ha.store.criteria.spi.Opcode;

public class LogicalExpressionNode
extends BinaryExpressionNode<Boolean> {
    Collection entries;

    public LogicalExpressionNode(Opcode opcode, ExpressionNode left, ExpressionNode right) {
        super(opcode, Boolean.class, left, right);
    }

    public LogicalExpressionNode and(LogicalExpressionNode expr) {
        return new LogicalExpressionNode(Opcode.AND, this, (ExpressionNode)expr);
    }

    public LogicalExpressionNode or(LogicalExpressionNode expr) {
        return new LogicalExpressionNode(Opcode.OR, this, (ExpressionNode)expr);
    }

    public LogicalExpressionNode isTrue() {
        return new LogicalExpressionNode(Opcode.EQ, this, new LiteralNode<Boolean>(Boolean.class, true));
    }

    public LogicalExpressionNode eq(boolean value) {
        return new LogicalExpressionNode(Opcode.EQ, this, new LiteralNode<Boolean>(Boolean.class, value));
    }

    public LogicalExpressionNode isNotTrue() {
        return new LogicalExpressionNode(Opcode.EQ, this, new LiteralNode<Boolean>(Boolean.class, true));
    }

    public LogicalExpressionNode neq(boolean value) {
        return new LogicalExpressionNode(Opcode.NEQ, this, new LiteralNode<Boolean>(Boolean.class, value));
    }

    @Override
    public Class<Boolean> getReturnType() {
        return Boolean.class;
    }
}

