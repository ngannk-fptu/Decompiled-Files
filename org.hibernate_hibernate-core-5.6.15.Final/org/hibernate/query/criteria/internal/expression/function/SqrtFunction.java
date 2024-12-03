/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.function.ParameterizedFunctionExpression;

public class SqrtFunction
extends ParameterizedFunctionExpression<Double>
implements Serializable {
    public static final String NAME = "sqrt";

    public SqrtFunction(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Number> expression) {
        super(criteriaBuilder, Double.class, NAME, expression);
    }

    @Override
    protected boolean isStandardJpaFunction() {
        return true;
    }
}

