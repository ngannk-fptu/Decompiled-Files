/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Visitor;
import java.util.List;
import javax.annotation.Nullable;

public abstract class FactoryExpressionBase<T>
extends ExpressionBase<T>
implements FactoryExpression<T> {
    public FactoryExpressionBase(Class<? extends T> type) {
        super(type);
    }

    public FactoryExpression<T> skipNulls() {
        return new FactoryExpressionWrapper(this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof FactoryExpression) {
            return this.getClass().equals(o.getClass()) && this.getArgs().equals(((FactoryExpression)o).getArgs());
        }
        return false;
    }

    private static class FactoryExpressionWrapper<T>
    extends ExpressionBase<T>
    implements FactoryExpression<T> {
        private final FactoryExpression<T> expr;

        public FactoryExpressionWrapper(FactoryExpression<T> expr) {
            super(expr.getType());
            this.expr = expr;
        }

        @Override
        public List<Expression<?>> getArgs() {
            return this.expr.getArgs();
        }

        @Override
        @Nullable
        public T newInstance(Object ... args) {
            if (args != null) {
                for (Object arg : args) {
                    if (arg == null) continue;
                    return this.expr.newInstance(args);
                }
            }
            return null;
        }

        @Override
        @Nullable
        public <R, C> R accept(Visitor<R, C> v, @Nullable C context) {
            return this.expr.accept(v, context);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof FactoryExpressionWrapper) {
                return this.expr.equals(((FactoryExpressionWrapper)o).expr);
            }
            return false;
        }
    }
}

