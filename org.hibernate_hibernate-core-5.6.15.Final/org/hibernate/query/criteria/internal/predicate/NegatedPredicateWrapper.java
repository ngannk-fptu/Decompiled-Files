/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Predicate$BooleanOperator
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.predicate.CompoundPredicate;
import org.hibernate.query.criteria.internal.predicate.PredicateImplementor;

public class NegatedPredicateWrapper
extends ExpressionImpl<Boolean>
implements PredicateImplementor,
Serializable {
    private final PredicateImplementor predicate;
    private final Predicate.BooleanOperator negatedOperator;
    private final List<Expression<Boolean>> negatedExpressions;

    public NegatedPredicateWrapper(PredicateImplementor predicate) {
        super(predicate.criteriaBuilder(), Boolean.class);
        this.predicate = predicate;
        this.negatedOperator = predicate.isJunction() ? CompoundPredicate.reverseOperator(predicate.getOperator()) : predicate.getOperator();
        this.negatedExpressions = NegatedPredicateWrapper.negateCompoundExpressions(predicate.getExpressions(), predicate.criteriaBuilder());
    }

    private static List<Expression<Boolean>> negateCompoundExpressions(List<Expression<Boolean>> expressions, CriteriaBuilderImpl criteriaBuilder) {
        if (expressions == null || expressions.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Expression<Boolean>> negatedExpressions = new ArrayList<Expression<Boolean>>();
        for (Expression<Boolean> expression : expressions) {
            if (Predicate.class.isInstance(expression)) {
                negatedExpressions.add((Expression<Boolean>)((Predicate)expression).not());
                continue;
            }
            negatedExpressions.add((Expression<Boolean>)criteriaBuilder.not(expression));
        }
        return negatedExpressions;
    }

    public Predicate.BooleanOperator getOperator() {
        return this.negatedOperator;
    }

    @Override
    public boolean isJunction() {
        return this.predicate.isJunction();
    }

    public boolean isNegated() {
        return !this.predicate.isNegated();
    }

    public List<Expression<Boolean>> getExpressions() {
        return this.negatedExpressions;
    }

    public Predicate not() {
        return new NegatedPredicateWrapper(this);
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        if (ParameterContainer.class.isInstance(this.predicate)) {
            ((ParameterContainer)((Object)this.predicate)).registerParameters(registry);
        }
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        if (this.isJunction()) {
            return CompoundPredicate.render(this, renderingContext);
        }
        return this.predicate.render(isNegated, renderingContext);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.render(this.isNegated(), renderingContext);
    }
}

