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

public class LengthFunction
extends ParameterizedFunctionExpression<Integer>
implements Serializable {
    public static final String NAME = "length";

    @Override
    protected boolean isStandardJpaFunction() {
        return true;
    }

    public LengthFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> value) {
        super(criteriaBuilder, Integer.class, NAME, value);
    }
}

