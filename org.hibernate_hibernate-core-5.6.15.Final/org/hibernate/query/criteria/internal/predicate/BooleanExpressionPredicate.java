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

public class BooleanExpressionPredicate
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<Boolean> expression;

    public BooleanExpressionPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<Boolean> expression) {
        super(criteriaBuilder);
        this.expression = expression;
    }

    public Expression<Boolean> getExpression() {
        return this.expression;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.expression, registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return ((Renderable)this.getExpression()).render(renderingContext);
    }
}

