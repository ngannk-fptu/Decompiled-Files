/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.criteria.Expression
 *  javax.persistence.criteria.MapJoin
 *  javax.persistence.criteria.Predicate
 */
package org.hibernate.query.criteria.internal;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import org.hibernate.query.criteria.internal.CriteriaSubqueryImpl;
import org.hibernate.query.criteria.internal.JoinImplementor;

public interface MapJoinImplementor<Z, K, V>
extends JoinImplementor<Z, V>,
MapJoin<Z, K, V> {
    @Override
    public MapJoinImplementor<Z, K, V> correlateTo(CriteriaSubqueryImpl var1);

    public MapJoinImplementor<Z, K, V> on(Expression<Boolean> var1);

    @Override
    public MapJoinImplementor<Z, K, V> on(Predicate ... var1);

    public <T extends V> MapJoinImplementor<Z, K, T> treatAs(Class<T> var1);
}

