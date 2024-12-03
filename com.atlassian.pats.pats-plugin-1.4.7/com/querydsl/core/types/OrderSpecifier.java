/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.concurrent.Immutable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import java.io.Serializable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class OrderSpecifier<T extends Comparable>
implements Serializable {
    private static final long serialVersionUID = 3427652988262514678L;
    private final Order order;
    private final Expression<T> target;
    private final NullHandling nullHandling;

    public OrderSpecifier(Order order, Expression<T> target, NullHandling nullhandling) {
        this.order = order;
        this.target = target;
        this.nullHandling = nullhandling;
    }

    public OrderSpecifier(Order order, Expression<T> target) {
        this(order, target, NullHandling.Default);
    }

    public Order getOrder() {
        return this.order;
    }

    public boolean isAscending() {
        return this.order == Order.ASC;
    }

    public Expression<T> getTarget() {
        return this.target;
    }

    public NullHandling getNullHandling() {
        return this.nullHandling;
    }

    public OrderSpecifier<T> nullsFirst() {
        return new OrderSpecifier<T>(this.order, this.target, NullHandling.NullsFirst);
    }

    public OrderSpecifier<T> nullsLast() {
        return new OrderSpecifier<T>(this.order, this.target, NullHandling.NullsLast);
    }

    public String toString() {
        return this.target + " " + (Object)((Object)this.order);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof OrderSpecifier) {
            OrderSpecifier os = (OrderSpecifier)o;
            return os.order.equals((Object)this.order) && os.target.equals(this.target) && os.nullHandling.equals((Object)this.nullHandling);
        }
        return false;
    }

    public int hashCode() {
        return this.target.hashCode();
    }

    public static enum NullHandling {
        Default,
        NullsFirst,
        NullsLast;

    }
}

