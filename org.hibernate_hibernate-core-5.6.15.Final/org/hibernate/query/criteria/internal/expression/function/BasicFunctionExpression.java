/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;

public class BasicFunctionExpression<X>
extends ExpressionImpl<X>
implements FunctionExpression<X>,
Serializable {
    private final String functionName;

    public BasicFunctionExpression(CriteriaBuilderImpl criteriaBuilder, Class<X> javaType, String functionName) {
        super(criteriaBuilder, javaType);
        this.functionName = functionName;
    }

    protected static int properSize(int number) {
        return number + (int)((double)number * 0.75) + 1;
    }

    @Override
    public String getFunctionName() {
        return this.functionName;
    }

    @Override
    public boolean isAggregation() {
        return false;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.getFunctionName() + "()";
    }
}

