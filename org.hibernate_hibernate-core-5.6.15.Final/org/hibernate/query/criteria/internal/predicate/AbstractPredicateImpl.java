/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.Selection
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import java.util.List;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.expression.ExpressionImpl;
import org.hibernate.query.criteria.internal.predicate.NegatedPredicateWrapper;
import org.hibernate.query.criteria.internal.predicate.PredicateImplementor;

public abstract class AbstractPredicateImpl
extends ExpressionImpl<Boolean>
implements PredicateImplementor,
Serializable {
    protected AbstractPredicateImpl(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder, Boolean.class);
    }

    public boolean isNegated() {
        return false;
    }

    public Predicate not() {
        return new NegatedPredicateWrapper(this);
    }

    @Override
    public final boolean isCompoundSelection() {
        return super.isCompoundSelection();
    }

    @Override
    public final List<Selection<?>> getCompoundSelectionItems() {
        return super.getCompoundSelectionItems();
    }
}

