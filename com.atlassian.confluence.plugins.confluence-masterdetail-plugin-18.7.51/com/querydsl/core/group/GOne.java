/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;

class GOne<T>
extends AbstractGroupExpression<T, T> {
    private static final long serialVersionUID = 3518868612387641383L;

    public GOne(Expression<T> expr) {
        super(expr.getType(), expr);
    }

    @Override
    public GroupCollector<T, T> createGroupCollector() {
        return new GroupCollector<T, T>(){
            private boolean first = true;
            private T val;

            @Override
            public void add(T o) {
                if (this.first) {
                    this.val = o;
                    this.first = false;
                }
            }

            @Override
            public T get() {
                return this.val;
            }
        };
    }
}

