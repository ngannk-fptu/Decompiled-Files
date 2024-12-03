/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.path.AbstractPathImpl;

public class PathTypeExpression<T>
extends ExpressionImpl<T>
implements Serializable {
    private final AbstractPathImpl<T> pathImpl;

    public PathTypeExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, AbstractPathImpl<T> pathImpl) {
        super(criteriaBuilder, javaType);
        this.pathImpl = pathImpl;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return "type(" + this.pathImpl.getPathIdentifier() + ")";
    }
}

