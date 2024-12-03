/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression.function;

import javax.persistence.criteria.Expression;

public interface FunctionExpression<T>
extends Expression<T> {
    public String getFunctionName();

    public boolean isAggregation();
}

