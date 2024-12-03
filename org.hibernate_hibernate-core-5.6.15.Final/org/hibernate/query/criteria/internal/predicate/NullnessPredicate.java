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
import org.hibernate.query.criteria.internal.expression.UnaryOperatorExpression;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class NullnessPredicate
extends AbstractSimplePredicate
implements UnaryOperatorExpression<Boolean>,
Serializable {
    private final Expression<?> operand;

    public NullnessPredicate(CriteriaBuilderImpl criteriaBuilder, Expression<?> operand) {
        super(criteriaBuilder);
        this.operand = operand;
    }

    @Override
    public Expression<?> getOperand() {
        return this.operand;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getOperand(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return ((Renderable)this.operand).render(renderingContext) + this.check(isNegated);
    }

    private String check(boolean negated) {
        return negated ? " is not null" : " is null";
    }
}

