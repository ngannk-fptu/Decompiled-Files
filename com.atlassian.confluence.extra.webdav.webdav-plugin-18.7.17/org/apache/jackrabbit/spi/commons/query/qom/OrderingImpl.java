/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi.commons.query.qom;

import javax.jcr.query.qom.DynamicOperand;
import javax.jcr.query.qom.Ordering;
import org.apache.jackrabbit.commons.query.qom.Order;
import org.apache.jackrabbit.spi.commons.conversion.NamePathResolver;
import org.apache.jackrabbit.spi.commons.query.qom.AbstractQOMNode;
import org.apache.jackrabbit.spi.commons.query.qom.DynamicOperandImpl;
import org.apache.jackrabbit.spi.commons.query.qom.QOMTreeVisitor;

public class OrderingImpl
extends AbstractQOMNode
implements Ordering {
    public static final OrderingImpl[] EMPTY_ARRAY = new OrderingImpl[0];
    private final DynamicOperandImpl operand;
    private final Order order;

    OrderingImpl(NamePathResolver resolver, DynamicOperandImpl operand, String order) {
        super(resolver);
        this.operand = operand;
        this.order = Order.getOrderByName(order);
    }

    @Override
    public DynamicOperand getOperand() {
        return this.operand;
    }

    @Override
    public String getOrder() {
        return this.order.getName();
    }

    public boolean isAscending() {
        return this.order == Order.ASCENDING;
    }

    @Override
    public Object accept(QOMTreeVisitor visitor, Object data) throws Exception {
        return visitor.visit(this, data);
    }

    public String toString() {
        if (this.order == Order.ASCENDING) {
            return this.operand + " ASC";
        }
        return this.operand + " DESC";
    }
}

