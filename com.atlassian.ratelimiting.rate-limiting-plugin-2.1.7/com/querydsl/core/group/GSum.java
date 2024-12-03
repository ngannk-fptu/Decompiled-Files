/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;
import com.querydsl.core.util.MathUtils;
import java.math.BigDecimal;

class GSum<T extends Number>
extends AbstractGroupExpression<T, T> {
    private static final long serialVersionUID = 3518868612387641383L;

    public GSum(Expression<T> expr) {
        super(expr.getType(), expr);
    }

    @Override
    public GroupCollector<T, T> createGroupCollector() {
        return new GroupCollector<T, T>(){
            private BigDecimal sum = BigDecimal.ZERO;

            @Override
            public void add(T t) {
                if (t != null) {
                    this.sum = this.sum.add(new BigDecimal(t.toString()));
                }
            }

            @Override
            public T get() {
                return MathUtils.cast(this.sum, GSum.this.getType());
            }
        };
    }
}

