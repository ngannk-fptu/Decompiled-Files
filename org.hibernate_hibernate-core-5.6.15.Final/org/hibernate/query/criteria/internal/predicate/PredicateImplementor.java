/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal.predicate;

import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.Renderable;
import org.hibernate.query.criteria.internal.compile.RenderingContext;

public interface PredicateImplementor
extends Predicate,
Renderable {
    public CriteriaBuilderImpl criteriaBuilder();

    public boolean isJunction();

    public String render(boolean var1, RenderingContext var2);
}

