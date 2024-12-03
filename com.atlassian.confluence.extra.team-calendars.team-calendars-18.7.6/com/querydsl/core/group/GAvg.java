/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;
import com.querydsl.core.util.MathUtils;
import java.math.BigDecimal;

class GAvg<T extends Number>
extends AbstractGroupExpression<T, T> {
    private static final long serialVersionUID = 3518868612387641383L;

    public GAvg(Expression<T> expr) {
        super(expr.getType(), expr);
    }

    @Override
    public GroupCollector<T, T> createGroupCollector() {
        return new GroupCollector<T, T>(){
            private int count = 0;
            private BigDecimal sum = BigDecimal.ZERO;

            @Override
            public void add(T t) {
                ++this.count;
                if (t != null) {
                    this.sum = this.sum.add(new BigDecimal(t.toString()));
                }
            }

            @Override
            public T get() {
                BigDecimal avg = this.sum.divide(BigDecimal.valueOf(this.count));
                return MathUtils.cast(avg, GAvg.this.getType());
            }
        };
    }
}

