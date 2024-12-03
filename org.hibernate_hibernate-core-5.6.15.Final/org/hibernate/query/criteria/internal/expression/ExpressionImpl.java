/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ExpressionImplementor;
import org.hibernate.query.criteria.internal.expression.SelectionImpl;
import org.hibernate.query.criteria.internal.expression.function.CastFunction;

public abstract class ExpressionImpl<T>
extends SelectionImpl<T>
implements ExpressionImplementor<T>,
Serializable {
    public ExpressionImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType) {
        super(criteriaBuilder, javaType);
    }

    public <X> Expression<X> as(Class<X> type) {
        return type.equals(this.getJavaType()) ? this : new CastFunction(this.criteriaBuilder(), type, this);
    }

    public Predicate isNull() {
        return this.criteriaBuilder().isNull(this);
    }

    public Predicate isNotNull() {
        return this.criteriaBuilder().isNotNull(this);
    }

    public Predicate in(Object ... values) {
        return this.criteriaBuilder().in(this, values);
    }

    public Predicate in(Expression<?> ... values) {
        return this.criteriaBuilder().in(this, (Expression<? extends T>[])values);
    }

    public Predicate in(Collection<?> values) {
        return this.criteriaBuilder().in(this, values.toArray());
    }

    public Predicate in(Expression<Collection<?>> values) {
        return this.criteriaBuilder().in(this, (Expression<? extends T>[])new Expression[]{values});
    }

    @Override
    public ExpressionImplementor<Long> asLong() {
        this.resetJavaType(Long.class);
        return this;
    }

    @Override
    public ExpressionImplementor<Integer> asInteger() {
        this.resetJavaType(Integer.class);
        return this;
    }

    @Override
    public ExpressionImplementor<Float> asFloat() {
        this.resetJavaType(Float.class);
        return this;
    }

    @Override
    public ExpressionImplementor<Double> asDouble() {
        this.resetJavaType(Double.class);
        return this;
    }

    @Override
    public ExpressionImplementor<BigDecimal> asBigDecimal() {
        this.resetJavaType(BigDecimal.class);
        return this;
    }

    @Override
    public ExpressionImplementor<BigInteger> asBigInteger() {
        this.resetJavaType(BigInteger.class);
        return this;
    }

    @Override
    public ExpressionImplementor<String> asString() {
        this.resetJavaType(String.class);
        return this;
    }
}

