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

public class ConcatExpression
extends ExpressionImpl<String>
implements Serializable {
    private Expression<String> string1;
    private Expression<String> string2;

    public ConcatExpression(CriteriaBuilderImpl criteriaBuilder, Expression<String> expression1, Expression<String> expression2) {
        super(criteriaBuilder, String.class);
        this.string1 = expression1;
        this.string2 = expression2;
    }

    public ConcatExpression(CriteriaBuilderImpl criteriaBuilder, Expression<String> string1, String string2) {
        this(criteriaBuilder, string1, ConcatExpression.wrap(criteriaBuilder, string2));
    }

    private static Expression<String> wrap(CriteriaBuilderImpl criteriaBuilder, String string) {
        return new LiteralExpression<String>(criteriaBuilder, string);
    }

    public ConcatExpression(CriteriaBuilderImpl criteriaBuilder, String string1, Expression<String> string2) {
        this(criteriaBuilder, ConcatExpression.wrap(criteriaBuilder, string1), string2);
    }

    public Expression<String> getString1() {
        return this.string1;
    }

    public Expression<String> getString2() {
        return this.string2;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getString1(), registry);
        ParameterContainer.Helper.possibleParameter(this.getString2(), registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return '(' + ((Renderable)this.getString1()).render(renderingContext) + " || " + ((Renderable)this.getString2()).render(renderingContext) + ')';
    }
}

