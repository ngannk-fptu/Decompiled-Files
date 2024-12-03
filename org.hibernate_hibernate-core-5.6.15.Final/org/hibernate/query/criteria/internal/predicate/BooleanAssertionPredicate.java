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

public class BooleanAssertionPredicate
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<Boolean> expression;
    private final Boolean assertedValue;

    public BooleanAssertionPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<Boolean> expression, Boolean assertedValue) {
        super(criteriaBuilder);
        this.expression = expression;
        this.assertedValue = assertedValue;
    }

    public Expression<Boolean> getExpression() {
        return this.expression;
    }

    public Boolean getAssertedValue() {
        return this.assertedValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.expression, registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        String operator = isNegated ? " <> " : " = ";
        String assertionLiteral = this.assertedValue != false ? "true" : "false";
        return ((Renderable)this.expression).render(renderingContext) + operator + assertionLiteral;
    }
}

