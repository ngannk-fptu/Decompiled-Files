/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import java.util.Set;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

public interface Subquery<T>
extends AbstractQuery<T>,
Expression<T> {
    public Subquery<T> select(Expression<T> var1);

    @Override
    public Subquery<T> where(Expression<Boolean> var1);

    @Override
    public Subquery<T> where(Predicate ... var1);

    @Override
    public Subquery<T> groupBy(Expression<?> ... var1);

    @Override
    public Subquery<T> groupBy(List<Expression<?>> var1);

    @Override
    public Subquery<T> having(Expression<Boolean> var1);

    @Override
    public Subquery<T> having(Predicate ... var1);

    @Override
    public Subquery<T> distinct(boolean var1);

    public <Y> Root<Y> correlate(Root<Y> var1);

    public <X, Y> Join<X, Y> correlate(Join<X, Y> var1);

    public <X, Y> CollectionJoin<X, Y> correlate(CollectionJoin<X, Y> var1);

    public <X, Y> SetJoin<X, Y> correlate(SetJoin<X, Y> var1);

    public <X, Y> ListJoin<X, Y> correlate(ListJoin<X, Y> var1);

    public <X, K, V> MapJoin<X, K, V> correlate(MapJoin<X, K, V> var1);

    public AbstractQuery<?> getParent();

    public CommonAbstractCriteria getContainingQuery();

    @Override
    public Expression<T> getSelection();

    public Set<Join<?, ?>> getCorrelatedJoins();
}

