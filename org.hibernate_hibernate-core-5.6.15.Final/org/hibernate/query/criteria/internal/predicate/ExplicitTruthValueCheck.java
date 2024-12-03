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
import org.hibernate.query.criteria.internal.predicate.TruthValue;

public class ExplicitTruthValueCheck
extends AbstractSimplePredicate
implements Serializable {
    private final Expression<Boolean> booleanExpression;
    private final TruthValue truthValue;

    public ExplicitTruthValueCheck(CriteriaBuilderImpl criteriaBuilder, Expression<Boolean> booleanExpression, TruthValue truthValue) {
        super(criteriaBuilder);
        this.booleanExpression = booleanExpression;
        this.truthValue = truthValue;
    }

    public Expression<Boolean> getBooleanExpression() {
        return this.booleanExpression;
    }

    public TruthValue getTruthValue() {
        return this.truthValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        ParameterContainer.Helper.possibleParameter(this.getBooleanExpression(), registry);
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return ((Renderable)this.getBooleanExpression()).render(renderingContext) + (isNegated ? " <> " : " = ") + (this.getTruthValue() == TruthValue.TRUE ? "true" : "false");
    }
}

