/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.criteria.Expression;

public interface UnaryOperatorExpression<T>
extends Expression<T>,
Serializable {
    public Expression<?> getOperand();
}

