/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate$BooleanOperator
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractPredicateImpl;

public abstract class AbstractSimplePredicate
extends AbstractPredicateImpl
implements Serializable {
    private static final List<Expression<Boolean>> NO_EXPRESSIONS = Collections.emptyList();

    public AbstractSimplePredicate(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder);
    }

    @Override
    public boolean isJunction() {
        return false;
    }

    public Predicate.BooleanOperator getOperator() {
        return Predicate.BooleanOperator.AND;
    }

    public final List<Expression<Boolean>> getExpressions() {
        return NO_EXPRESSIONS;
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return this.render(this.isNegated(), renderingContext);
    }
}

