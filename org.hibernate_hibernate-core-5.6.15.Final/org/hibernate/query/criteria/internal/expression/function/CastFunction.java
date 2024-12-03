/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression.function;

import java.io.Serializable;
import java.util.Locale;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.function.BasicFunctionExpression;
import org.hibernate.query.criteria.internal.expression.function.FunctionExpression;

public class CastFunction<T, Y>
extends BasicFunctionExpression<T>
implements FunctionExpression<T>,
Serializable {
    public static final String CAST_NAME = "cast";
    private final ExpressionImpl<Y> castSource;

    public CastFunction(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, ExpressionImpl<Y> castSource) {
        super(criteriaBuilder, javaType, CAST_NAME);
        this.castSource = castSource;
    }

    public ExpressionImpl<Y> getCastSource() {
        return this.castSource;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getCastSource(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        renderingContext.getFunctionStack().push(this);
        try {
            String string = String.format(Locale.ROOT, "cast(%s as %s)", this.castSource.render(renderingContext), renderingContext.getCastType(this.getJavaType()));
            return string;
        }
        finally {
            renderingContext.getFunctionStack().pop();
        }
    }
}

