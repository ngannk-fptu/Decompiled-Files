/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Subquery
 */
package org.hibernate.query.criteria.internal.predicate;

import java.io.Serializable;
import javax.persistence.criteria.Subquery;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.ParameterRegistry;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;
import org.hibernate.query.criteria.internal.predicate.AbstractSimplePredicate;

public class ExistsPredicate
extends AbstractSimplePredicate
implements Serializable {
    private final Subquery<?> subquery;

    public ExistsPredicate(CriteriaBuilderImpl criteriaBuilder, Subquery<?> subquery) {
        super(criteriaBuilder);
        this.subquery = subquery;
    }

    public Subquery<?> getSubquery() {
        return this.subquery;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
    }

    @Override
    public String render(boolean isNegated, RenderingContext renderingContext) {
        return (isNegated ? "not " : "") + "exists " + ((Renderable)this.getSubquery()).render(renderingContext);
    }
}

