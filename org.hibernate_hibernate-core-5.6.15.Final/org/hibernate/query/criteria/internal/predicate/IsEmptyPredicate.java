/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.Collection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.expression.UnaryOperatorExpression;
import org.hibernate.query.criteria.internal.path.PluralAttributePath;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class IsEmptyPredicate<C extends Collection>
extends AbstractSimplePredicate
implements UnaryOperatorExpression<Boolean>,
Serializable {
    private final PluralAttributePath<C> collectionPath;

    public IsEmptyPredicate(CriteriaBuilderImpl criteriaBuilder, PluralAttributePath<C> collectionPath) {
        super(criteriaBuilder);
        this.collectionPath = collectionPath;
    }

    @Override
    public PluralAttributePath<C> getOperand() {
        return this.collectionPath;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        String operator = isNegated ? " is not empty" : " is empty";
        return this.getOperand().render(renderingContext) + operator;
    }
}

