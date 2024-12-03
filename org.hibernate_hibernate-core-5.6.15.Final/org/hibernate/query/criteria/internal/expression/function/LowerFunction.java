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

public class LowerFunction
extends ParameterizedFunctionExpression<String>
implements Serializable {
    public static final String NAME = "lower";

    public LowerFunction(CriteriaBuilderImpl criteriaBuilder, Expression<String> string) {
        super(criteriaBuilder, String.class, NAME, string);
    }

    @Override
    protected boolean isStandardJpaFunction() {
        return true;
    }
}

