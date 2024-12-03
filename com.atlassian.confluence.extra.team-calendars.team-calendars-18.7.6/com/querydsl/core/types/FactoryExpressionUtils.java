/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.ArrayConstructorExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.ProjectionRole;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.util.ArrayUtils;
import java.util.ArrayList;
import java.util.List;

public final class FactoryExpressionUtils {
    public static FactoryExpression<?> wrap(List<? extends Expression<?>> projection) {
        boolean usesFactoryExpressions = false;
        for (Expression<?> e : projection) {
            usesFactoryExpressions |= e instanceof FactoryExpression;
        }
        if (usesFactoryExpressions) {
            return FactoryExpressionUtils.wrap(new ArrayConstructorExpression(projection.toArray(new Expression[projection.size()])));
        }
        return null;
    }

    public static <T> FactoryExpression<T> wrap(FactoryExpression<T> expr, List<Expression<?>> conversions) {
        return new FactoryExpressionAdapter<T>(expr, conversions);
    }

    public static <T> FactoryExpression<T> wrap(FactoryExpression<T> expr) {
        for (Expression<Object> arg : expr.getArgs()) {
            if (arg instanceof ProjectionRole) {
                arg = ((ProjectionRole)((Object)arg)).getProjection();
            }
            if (!(arg instanceof FactoryExpression)) continue;
            return new FactoryExpressionAdapter<T>(expr);
        }
        return expr;
    }

    private static List<Expression<?>> expand(List<Expression<?>> exprs) {
        ArrayList rv = new ArrayList(exprs.size());
        for (Expression<Object> expr : exprs) {
            if (expr instanceof ProjectionRole) {
                expr = ((ProjectionRole)((Object)expr)).getProjection();
            }
            if (expr instanceof FactoryExpression) {
                rv.addAll(FactoryExpressionUtils.expand(((FactoryExpression)expr).getArgs()));
                continue;
            }
            rv.add(expr);
        }
        return rv;
    }

    private static int countArguments(FactoryExpression<?> expr) {
        int counter = 0;
        for (Expression<Object> arg : expr.getArgs()) {
            if (arg instanceof ProjectionRole) {
                arg = ((ProjectionRole)((Object)arg)).getProjection();
            }
            if (arg instanceof FactoryExpression) {
                counter += FactoryExpressionUtils.countArguments((FactoryExpression)arg);
                continue;
            }
            ++counter;
        }
        return counter;
    }

    private static Object[] compress(List<Expression<?>> exprs, Object[] args) {
        Object[] rv = new Object[exprs.size()];
        int offset = 0;
        for (int i = 0; i < exprs.size(); ++i) {
            Expression<Object> expr = exprs.get(i);
            if (expr instanceof ProjectionRole) {
                expr = ((ProjectionRole)((Object)expr)).getProjection();
            }
            if (expr instanceof FactoryExpression) {
                FactoryExpression fe = (FactoryExpression)expr;
                int fullArgsLength = FactoryExpressionUtils.countArguments(fe);
                Object[] compressed = FactoryExpressionUtils.compress(fe.getArgs(), ArrayUtils.subarray(args, offset, offset + fullArgsLength));
                rv[i] = fe.newInstance(compressed);
                offset += fullArgsLength;
                continue;
            }
            rv[i] = args[offset];
            ++offset;
        }
        return rv;
    }

    private FactoryExpressionUtils() {
    }

    public static class FactoryExpressionAdapter<T>
    extends ExpressionBase<T>
    implements FactoryExpression<T> {
        private static final long serialVersionUID = -2742333128230913512L;
        private final FactoryExpression<T> inner;
        private final List<Expression<?>> args;

        FactoryExpressionAdapter(FactoryExpression<T> inner) {
            super(inner.getType());
            this.inner = inner;
            this.args = FactoryExpressionUtils.expand(inner.getArgs());
        }

        FactoryExpressionAdapter(FactoryExpression<T> inner, List<Expression<?>> args) {
            super(inner.getType());
            this.inner = inner;
            this.args = FactoryExpressionUtils.expand(args);
        }

        @Override
        public List<Expression<?>> getArgs() {
            return this.args;
        }

        @Override
        public T newInstance(Object ... a) {
            return this.inner.newInstance(FactoryExpressionUtils.compress(this.inner.getArgs(), a));
        }

        @Override
        public <R, C> R accept(Visitor<R, C> v, C context) {
            return v.visit(this, context);
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof FactoryExpression) {
                FactoryExpression e = (FactoryExpression)o;
                return this.args.equals(e.getArgs()) && this.getType().equals(e.getType());
            }
            return false;
        }
    }
}

