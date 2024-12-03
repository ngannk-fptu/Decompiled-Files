/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import javax.persistence.criteria.Expression;

public interface BinaryOperatorExpression<T>
extends Expression<T> {
    public Expression<?> getRightHandOperand();

    public Expression<?> getLeftHandOperand();
}

