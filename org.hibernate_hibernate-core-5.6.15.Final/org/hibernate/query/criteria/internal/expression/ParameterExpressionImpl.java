/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.ParameterExpression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.criteria.ParameterExpression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.ExplicitParameterInfo;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;

public class ParameterExpressionImpl<T>
extends ExpressionImpl<T>
implements ParameterExpression<T>,
Serializable {
    private String name;
    private final Integer position;
    private boolean isNameGenerated;

    public ParameterExpressionImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, String name) {
        super(criteriaBuilder, javaType);
        this.name = name;
        this.position = null;
    }

    public ParameterExpressionImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, Integer position) {
        super(criteriaBuilder, javaType);
        this.name = null;
        this.position = position;
    }

    public ParameterExpressionImpl(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType) {
        super(criteriaBuilder, javaType);
        this.name = null;
        this.position = null;
    }

    public String getName() {
        return this.name;
    }

    public boolean isNameGenerated() {
        return this.isNameGenerated;
    }

    public Integer getPosition() {
        return this.position;
    }

    public Class<T> getParameterType() {
        return this.getJavaType();
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        registry.registerParameter(this);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        ExplicitParameterInfo parameterInfo = renderingContext.registerExplicitParameter(this);
        if (this.name == null && this.position == null) {
            this.isNameGenerated = true;
            this.name = parameterInfo.getName();
        }
        return parameterInfo.render();
    }
}

