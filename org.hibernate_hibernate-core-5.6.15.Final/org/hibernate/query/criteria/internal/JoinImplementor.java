/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Fetch
 *  javax.persistence.criteria.Join
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.FromImplementor;

public interface JoinImplementor<Z, X>
extends Join<Z, X>,
Fetch<Z, X>,
FromImplementor<Z, X> {
    @Override
    public JoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl var1);

    public JoinImplementor<Z, X> on(Expression<Boolean> var1);

    public JoinImplementor<Z, X> on(Predicate ... var1);

    public <T extends X> JoinImplementor<Z, T> treatAs(Class<T> var1);
}

