/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;

class GMax<T extends Comparable<? super T>>
extends AbstractGroupExpression<T, T> {
    private static final long serialVersionUID = 3815394663181131511L;

    public GMax(Expression<T> expr) {
        super(expr.getType(), expr);
    }

    @Override
    public GroupCollector<T, T> createGroupCollector() {
        return new GroupCollector<T, T>(){
            private T max;

            @Override
            public void add(T o) {
                this.max = this.max != null ? (o.compareTo(this.max) > 0 ? o : this.max) : o;
            }

            @Override
            public T get() {
                return this.max;
            }
        };
    }
}

