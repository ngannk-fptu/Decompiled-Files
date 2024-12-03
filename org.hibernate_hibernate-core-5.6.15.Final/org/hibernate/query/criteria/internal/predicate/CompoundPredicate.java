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
import java.util.Arrays;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterContainer;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractPredicateImpl;
import org.hibernate.query.criteria.internal.predicate.NegatedPredicateWrapper;
import org.hibernate.query.criteria.internal.predicate.PredicateImplementor;

public class CompoundPredicate
extends AbstractPredicateImpl
implements Serializable {
    private Predicate.BooleanOperator operator;
    private final List<Expression<Boolean>> expressions = new ArrayList<Expression<Boolean>>();

    public CompoundPredicate(CriteriaBuilderImpl criteriaBuilder, Predicate.BooleanOperator operator) {
        super(criteriaBuilder);
        this.operator = operator;
    }

    public CompoundPredicate(CriteriaBuilderImpl criteriaBuilder, Predicate.BooleanOperator operator, Expression<Boolean> ... expressions) {
        this(criteriaBuilder, operator);
        this.applyExpressions(expressions);
    }

    public CompoundPredicate(CriteriaBuilderImpl criteriaBuilder, Predicate.BooleanOperator operator, List<Expression<Boolean>> expressions) {
        this(criteriaBuilder, operator);
        this.applyExpressions(expressions);
    }

    private void applyExpressions(Expression<Boolean> ... expressions) {
        this.applyExpressions(Arrays.asList(expressions));
    }

    private void applyExpressions(List<Expression<Boolean>> expressions) {
        this.expressions.clear();
        CriteriaBuilderImpl criteriaBuilder = this.criteriaBuilder();
        for (Expression<Boolean> expression : expressions) {
            this.expressions.add((Expression<Boolean>)criteriaBuilder.wrap(expression));
        }
    }

    public Predicate.BooleanOperator getOperator() {
        return this.operator;
    }

    public List<Expression<Boolean>> getExpressions() {
        return this.expressions;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        for (Expression<Boolean> expression : this.getExpressions()) {
            ParameterContainer.Helper.possibleParameter(expression, registry);
        }
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.render(this.isNegated(), renderingContext);
    }

    @Override
    public boolean isJunction() {
        return true;
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return CompoundPredicate.render(this, renderingContext);
    }

    @Override
    public Predicate not() {
        return new NegatedPredicateWrapper(this);
    }

    private void toggleOperator() {
        this.operator = CompoundPredicate.reverseOperator(this.operator);
    }

    public static Predicate.BooleanOperator reverseOperator(Predicate.BooleanOperator operator) {
        return operator == Predicate.BooleanOperator.AND ? Predicate.BooleanOperator.OR : Predicate.BooleanOperator.AND;
    }

    public static String render(PredicateImplementor predicate, RenderingContext renderingContext) {
        if (!predicate.isJunction()) {
            throw new IllegalStateException("CompoundPredicate.render should only be used to render junctions");
        }
        if (predicate.getExpressions().isEmpty()) {
            boolean implicitTrue = predicate.getOperator() == Predicate.BooleanOperator.AND;
            return implicitTrue ? "1=1" : "0=1";
        }
        if (predicate.getExpressions().size() == 1) {
            return ((Renderable)predicate.getExpressions().get(0)).render(renderingContext);
        }
        StringBuilder buffer = new StringBuilder();
        String sep = "";
        for (Expression expression : predicate.getExpressions()) {
            buffer.append(sep).append("( ").append(((Renderable)expression).render(renderingContext)).append(" )");
            sep = CompoundPredicate.operatorTextWithSeparator(predicate.getOperator());
        }
        return buffer.toString();
    }

    private static String operatorTextWithSeparator(Predicate.BooleanOperator operator) {
        return operator == Predicate.BooleanOperator.AND ? " and " : " or ";
    }
}

