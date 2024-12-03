/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.CollectionJoin
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.JoinImplementor;

public interface CollectionJoinImplementor<Z, X>
extends JoinImplementor<Z, X>,
CollectionJoin<Z, X> {
    @Override
    public CollectionJoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl var1);

    @Override
    public CollectionJoinImplementor<Z, X> on(Expression<Boolean> var1);

    @Override
    public CollectionJoinImplementor<Z, X> on(Predicate ... var1);

    @Override
    public <T extends X> CollectionJoinImplementor<Z, T> treatAs(Class<T> var1);
}

