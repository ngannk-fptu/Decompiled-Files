/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal;

import java.math.BigDecimal;
import java.math.BigInteger;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.SelectionImplementor;

public interface ExpressionImplementor<T>
extends SelectionImplementor<T>,
Expression<T>,
Renderable {
    public ExpressionImplementor<Long> asLong();

    public ExpressionImplementor<Integer> asInteger();

    public ExpressionImplementor<Float> asFloat();

    public ExpressionImplementor<Double> asDouble();

    public ExpressionImplementor<BigDecimal> asBigDecimal();

    public ExpressionImplementor<BigInteger> asBigInteger();

    public ExpressionImplementor<String> asString();
}

