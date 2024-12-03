/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import javax.persistence.criteria.Expression;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class BetweenPredicate<Y>
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<? extends Y> expression;
    private final Expression<? extends Y> lowerBound;
    private final Expression<? extends Y> upperBound;

    public BetweenPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Y> expression, Y lowerBound, Y upperBound) {
        this(criteriaBuilder, expression, criteriaBuilder.literal(lowerBound), criteriaBuilder.literal(upperBound));
    }

    public BetweenPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<? extends Y> expression, Expression<? extends Y> lowerBound, Expression<? extends Y> upperBound) {
        super(criteriaBuilder);
        this.expression = expression;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public Expression<? extends Y> getExpression() {
        return this.expression;
    }

    public Expression<? extends Y> getLowerBound() {
        return this.lowerBound;
    }

    public Expression<? extends Y> getUpperBound() {
        return this.upperBound;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getExpression(), registry);
        ParameterContainer.Helper.possibleParameter(this.getLowerBound(), registry);
        ParameterContainer.Helper.possibleParameter(this.getUpperBound(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        String operator = isNegated ? " not between " : " between ";
        return ((Renderable)this.getExpression()).render(renderingContext) + operator + ((Renderable)this.getLowerBound()).render(renderingContext) + " and " + ((Renderable)this.getUpperBound()).render(renderingContext);
    }
}

