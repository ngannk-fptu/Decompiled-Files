/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.ListJoin
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.JoinImplementor;

public interface ListJoinImplementor<Z, X>
extends JoinImplementor<Z, X>,
ListJoin<Z, X> {
    @Override
    public ListJoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl var1);

    @Override
    public ListJoinImplementor<Z, X> on(Expression<Boolean> var1);

    @Override
    public ListJoinImplementor<Z, X> on(Predicate ... var1);

    @Override
    public <T extends X> ListJoinImplementor<Z, T> treatAs(Class<T> var1);
}

