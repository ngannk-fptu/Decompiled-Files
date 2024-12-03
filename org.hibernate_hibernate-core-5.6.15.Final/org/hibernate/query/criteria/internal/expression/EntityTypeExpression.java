/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public class EntityTypeExpression<T>
extends ExpressionImpl<T>
implements Serializable {
    public EntityTypeExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType) {
        super(criteriaBuilder, javaType);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(RenderingContext renderingContext) {
        throw new IllegalArgumentException("Unexpected call on EntityTypeExpression#render");
    }
}

