/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import java.util.Set;
import javax.persistence.criteria.AbstractQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

public interface CriteriaQuery<T>
extends AbstractQuery<T> {
    public CriteriaQuery<T> select(Selection<? extends T> var1);

    public CriteriaQuery<T> multiselect(Selection<?> ... var1);

    public CriteriaQuery<T> multiselect(List<Selection<?>> var1);

    @Override
    public CriteriaQuery<T> where(Expression<Boolean> var1);

    @Override
    public CriteriaQuery<T> where(Predicate ... var1);

    @Override
    public CriteriaQuery<T> groupBy(Expression<?> ... var1);

    @Override
    public CriteriaQuery<T> groupBy(List<Expression<?>> var1);

    @Override
    public CriteriaQuery<T> having(Expression<Boolean> var1);

    @Override
    public CriteriaQuery<T> having(Predicate ... var1);

    public CriteriaQuery<T> orderBy(Order ... var1);

    public CriteriaQuery<T> orderBy(List<Order> var1);

    @Override
    public CriteriaQuery<T> distinct(boolean var1);

    public List<Order> getOrderList();

    public Set<ParameterExpression<?>> getParameters();
}

