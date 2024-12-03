/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class BooleanStaticAssertionPredicate
extends AbstractSimplePredicate
implements Serializable {
    private final Boolean assertedValue;

    public BooleanStaticAssertionPredicate(CriteriaBuilderImpl criteriaBuilder, Boolean assertedValue) {
        super(criteriaBuilder);
        this.assertedValue = assertedValue;
    }

    public Boolean getAssertedValue() {
        return this.assertedValue;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        boolean isTrue = this.getAssertedValue();
        if (isNegated) {
            isTrue = !isTrue;
        }
        return isTrue ? "1=1" : "0=1";
    }
}

