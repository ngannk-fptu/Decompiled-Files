/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CriteriaDelete
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.AbstractManipulationCriteriaQuery;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.compile.RenderingContext;

public class CriteriaDeleteImpl<T>
extends AbstractManipulationCriteriaQuery<T>
implements CriteriaDelete<T> {
    protected CriteriaDeleteImpl(CriteriaBuilderImpl criteriaBuilder) {
        super(criteriaBuilder);
    }

    public CriteriaDelete<T> where(Expression<Boolean> restriction) {
        this.setRestriction(restriction);
        return this;
    }

    public CriteriaDelete<T> where(Predicate ... restrictions) {
        this.setRestriction(restrictions);
        return this;
    }

    @Override
    protected String renderQuery(RenderingContext renderingContext) {
        StringBuilder jpaql = new StringBuilder("delete ");
        this.renderRoot(jpaql, renderingContext);
        this.renderRestrictions(jpaql, renderingContext);
        return jpaql.toString();
    }
}

