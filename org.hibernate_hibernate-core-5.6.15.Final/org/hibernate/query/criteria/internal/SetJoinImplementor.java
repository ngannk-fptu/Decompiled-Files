/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.Predicate
 *  javax.persistence.criteria.SetJoin
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.JoinImplementor;

public interface SetJoinImplementor<Z, X>
extends JoinImplementor<Z, X>,
SetJoin<Z, X> {
    @Override
    public SetJoinImplementor<Z, X> correlateTo(CriteriaSubqueryImpl var1);

    @Override
    public SetJoinImplementor<Z, X> on(Expression<Boolean> var1);

    @Override
    public SetJoinImplementor<Z, X> on(Predicate ... var1);

    @Override
    public <T extends X> SetJoinImplementor<Z, T> treatAs(Class<T> var1);
}

