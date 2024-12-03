/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;

class GMin<T extends Comparable<? super T>>
extends AbstractGroupExpression<T, T> {
    private static final long serialVersionUID = 8312168556148122576L;

    public GMin(Expression<T> expr) {
        super(expr.getType(), expr);
    }

    @Override
    public GroupCollector<T, T> createGroupCollector() {
        return new GroupCollector<T, T>(){
            private T min;

            @Override
            public void add(T o) {
                this.min = this.min != null ? (o.compareTo(this.min) < 0 ? o : this.min) : o;
            }

            @Override
            public T get() {
                return this.min;
            }
        };
    }
}

