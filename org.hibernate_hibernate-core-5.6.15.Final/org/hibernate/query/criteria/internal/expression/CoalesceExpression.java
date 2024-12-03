/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaBuilder$Coalesce
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

public class CoalesceExpression<T>
extends ExpressionImpl<T>
implements CriteriaBuilder.Coalesce<T>,
Serializable {
    private final List<Expression<? extends T>> expressions;
    private Class<T> javaType;

    public CoalesceExpression(CriteriaBuilderImpl criteriaBuilder) {
        this(criteriaBuilder, (Class<T>)null);
    }

    public CoalesceExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType) {
        super(criteriaBuilder, javaType);
        this.javaType = javaType;
        this.expressions = new ArrayList<Expression<? extends T>>();
    }

    @Override
    public Class<T> getJavaType() {
        return this.javaType;
    }

    public CriteriaBuilder.Coalesce<T> value(T value) {
        return this.value((Expression<? extends T>)new LiteralExpression<T>(this.criteriaBuilder(), value));
    }

    public CriteriaBuilder.Coalesce<T> value(Expression<? extends T> value) {
        this.expressions.add(value);
        if (this.javaType == null) {
            this.javaType = value.getJavaType();
        }
        return this;
    }

    public List<Expression<? extends T>> getExpressions() {
        return this.expressions;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (Expression<T> expression : this.getExpressions()) {
            ParameterContainer.Helper.possibleParameter(expression, registry);
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        StringBuilder buffer = new StringBuilder("coalesce(");
        String sep = "";
        for (Expression<T> expression : this.getExpressions()) {
            buffer.append(sep).append(((Renderable)expression).render(renderingContext));
            sep = ", ";
        }
        return buffer.append(")").toString();
    }
}

