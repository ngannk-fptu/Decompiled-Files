/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Order
 */
package org.hibernate.query.criteria.internal;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;

public class OrderImpl
implements Order,
Serializable {
    private final Expression<?> expression;
    private final boolean ascending;
    private final Boolean nullsFirst;

    public OrderImpl(Expression<?> expression) {
        this(expression, true, null);
    }

    public OrderImpl(Expression<?> expression, boolean ascending) {
        this(expression, ascending, null);
    }

    public OrderImpl(Expression<?> expression, boolean ascending, Boolean nullsFirst) {
        this.expression = expression;
        this.ascending = ascending;
        this.nullsFirst = nullsFirst;
    }

    public Order reverse() {
        return new OrderImpl(this.expression, !this.ascending);
    }

    public boolean isAscending() {
        return this.ascending;
    }

    public Expression<?> getExpression() {
        return this.expression;
    }

    public Boolean getNullsFirst() {
        return this.nullsFirst;
    }
}

