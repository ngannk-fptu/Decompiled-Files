/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Root
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Root;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;
import org.hibernate.query.criteria.internal.expression.function.ParameterizedFunctionExpression;

public class AggregationFunction<T>
extends ParameterizedFunctionExpression<T>
implements Serializable {
    public AggregationFunction(CriteriaBuilderImpl criteriaBuilder, Class<T> returnType, String functionName, Object argument) {
        this(criteriaBuilder, returnType, functionName, (Expression<?>)new LiteralExpression<Object>(criteriaBuilder, argument));
    }

    public AggregationFunction(CriteriaBuilderImpl criteriaBuilder, Class<T> returnType, String functionName, Expression<?> argument) {
        super(criteriaBuilder, returnType, functionName, argument);
    }

    @Override
    public boolean isAggregation() {
        return true;
    }

    @Override
    protected boolean isStandardJpaFunction() {
        return true;
    }

    public static class GREATEST<X extends Comparable<X>>
    extends AggregationFunction<X> {
        public static final String NAME = "max";

        public GREATEST(CriteriaBuilderImpl criteriaBuilder, Expression<X> expression) {
            super(criteriaBuilder, expression.getJavaType(), NAME, (Expression<?>)expression);
        }
    }

    public static class LEAST<X extends Comparable<X>>
    extends AggregationFunction<X> {
        public static final String NAME = "min";

        public LEAST(CriteriaBuilderImpl criteriaBuilder, Expression<X> expression) {
            super(criteriaBuilder, expression.getJavaType(), NAME, (Expression<?>)expression);
        }
    }

    public static class MAX<N extends Number>
    extends AggregationFunction<N> {
        public static final String NAME = "max";

        public MAX(CriteriaBuilderImpl criteriaBuilder, Expression<N> expression) {
            super(criteriaBuilder, expression.getJavaType(), NAME, (Expression<?>)expression);
        }
    }

    public static class MIN<N extends Number>
    extends AggregationFunction<N> {
        public static final String NAME = "min";

        public MIN(CriteriaBuilderImpl criteriaBuilder, Expression<N> expression) {
            super(criteriaBuilder, expression.getJavaType(), NAME, (Expression<?>)expression);
        }
    }

    public static class SUM<N extends Number>
    extends AggregationFunction<N> {
        public static final String NAME = "sum";

        public SUM(CriteriaBuilderImpl criteriaBuilder, Expression<N> expression) {
            super(criteriaBuilder, expression.getJavaType(), NAME, (Expression<?>)expression);
            this.resetJavaType(expression.getJavaType());
        }

        public SUM(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Number> expression, Class<N> returnType) {
            super(criteriaBuilder, returnType, NAME, (Expression<?>)expression);
            this.resetJavaType(returnType);
        }
    }

    public static class AVG
    extends AggregationFunction<Double> {
        public static final String NAME = "avg";

        public AVG(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Number> expression) {
            super(criteriaBuilder, Double.class, NAME, (Expression<?>)expression);
        }
    }

    public static class COUNT
    extends AggregationFunction<Long> {
        public static final String NAME = "count";
        private final boolean distinct;

        public COUNT(CriteriaBuilderImpl criteriaBuilder, Expression<?> expression, boolean distinct) {
            super(criteriaBuilder, Long.class, NAME, expression);
            this.distinct = distinct;
        }

        @Override
        protected void renderArguments(StringBuilder buffer, RenderingContext renderingContext) {
            if (this.isDistinct()) {
                buffer.append("distinct ");
            } else {
                Root root;
                Expression<?> argExpr;
                List<Expression<?>> argExprs = this.getArgumentExpressions();
                if (argExprs.size() == 1 && (argExpr = argExprs.get(0)) instanceof Root && !(root = (Root)argExpr).getModel().hasSingleIdAttribute()) {
                    buffer.append('*');
                    return;
                }
            }
            super.renderArguments(buffer, renderingContext);
        }

        public boolean isDistinct() {
            return this.distinct;
        }
    }
}

