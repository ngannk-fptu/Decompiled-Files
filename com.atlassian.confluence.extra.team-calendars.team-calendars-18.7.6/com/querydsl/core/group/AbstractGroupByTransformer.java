/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.ResultTransformer;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.GMap;
import com.querydsl.core.group.GOne;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.group.QPair;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionBase;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Visitor;
import java.util.ArrayList;
import java.util.List;

abstract class AbstractGroupByTransformer<K, T>
implements ResultTransformer<T> {
    protected final List<GroupExpression<?, ?>> groupExpressions = new ArrayList();
    protected final List<QPair<?, ?>> maps = new ArrayList();
    protected final Expression<?>[] expressions;

    AbstractGroupByTransformer(Expression<K> key, Expression<?> ... expressions) {
        ArrayList projection = new ArrayList(expressions.length);
        this.groupExpressions.add(new GOne<K>(key));
        projection.add(key);
        for (Expression<?> expr : expressions) {
            if (expr instanceof GroupExpression) {
                GroupExpression groupExpr = (GroupExpression)expr;
                this.groupExpressions.add(groupExpr);
                Expression colExpression = groupExpr.getExpression();
                if (colExpression instanceof Operation && ((Operation)colExpression).getOperator() == Ops.ALIAS) {
                    projection.add(((Operation)colExpression).getArg(0));
                } else {
                    projection.add(colExpression);
                }
                if (!(groupExpr instanceof GMap)) continue;
                this.maps.add((QPair)colExpression);
                continue;
            }
            this.groupExpressions.add(new GOne(expr));
            projection.add(expr);
        }
        this.expressions = projection.toArray(new Expression[projection.size()]);
    }

    protected static FactoryExpression<Tuple> withoutGroupExpressions(FactoryExpression<Tuple> expr) {
        ArrayList args = new ArrayList(expr.getArgs().size());
        for (Expression<?> arg : expr.getArgs()) {
            if (arg instanceof GroupExpression) {
                args.add(((GroupExpression)arg).getExpression());
                continue;
            }
            args.add(arg);
        }
        return new FactoryExpressionAdapter<Tuple>(expr, args);
    }

    private static final class FactoryExpressionAdapter<T>
    extends ExpressionBase<T>
    implements FactoryExpression<T> {
        private final FactoryExpression<T> expr;
        private final List<Expression<?>> args;

        private FactoryExpressionAdapter(FactoryExpression<T> expr, List<Expression<?>> args) {
            super(expr.getType());
            this.expr = expr;
            this.args = args;
        }

        @Override
        public <R, C> R accept(Visitor<R, C> v, C context) {
            return this.expr.accept(v, context);
        }

        @Override
        public List<Expression<?>> getArgs() {
            return this.args;
        }

        @Override
        public T newInstance(Object ... args) {
            return this.expr.newInstance(args);
        }
    }
}

