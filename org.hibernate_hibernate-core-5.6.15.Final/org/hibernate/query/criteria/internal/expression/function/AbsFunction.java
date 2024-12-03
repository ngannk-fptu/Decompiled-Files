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

public class AbsFunction<N extends Number>
extends ParameterizedFunctionExpression<N>
implements Serializable {
    public static final String NAME = "abs";

    public AbsFunction(CriteriaBuilderImpl criteriaBuilder, Expression expression) {
        super(criteriaBuilder, expression.getJavaType(), NAME, expression);
    }

    @Override
    protected boolean isStandardJpaFunction() {
        return true;
    }
}

