/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.expression;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.expression.LiteralExpression;

public class NullifExpression<T>
extends ExpressionImpl<T>
implements Serializable {
    private final Expression<? extends T> primaryExpression;
    private final Expression<?> secondaryExpression;

    public NullifExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, Expression<? extends T> primaryExpression, Expression<?> secondaryExpression) {
        super(criteriaBuilder, NullifExpression.determineType(javaType, primaryExpression));
        this.primaryExpression = primaryExpression;
        this.secondaryExpression = secondaryExpression;
    }

    public NullifExpression(CriteriaBuilderImpl criteriaBuilder, Class<T> javaType, Expression<? extends T> primaryExpression, Object secondaryExpression) {
        super(criteriaBuilder, NullifExpression.determineType(javaType, primaryExpression));
        this.primaryExpression = primaryExpression;
        this.secondaryExpression = new LiteralExpression<Object>(criteriaBuilder, secondaryExpression);
    }

    private static Class determineType(Class javaType, Expression primaryExpression) {
        return javaType != null ? javaType : primaryExpression.getJavaType();
    }

    public Expression<? extends T> getPrimaryExpression() {
        return this.primaryExpression;
    }

    public Expression<?> getSecondaryExpression() {
        return this.secondaryExpression;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getPrimaryExpression(), registry);
        ParameterContainer.Helper.possibleParameter(this.getSecondaryExpression(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return "nullif(" + ((Renderable)this.getPrimaryExpression()).render(renderingContext) + ',' + ((Renderable)this.getSecondaryExpression()).render(renderingContext) + ")";
    }
}

