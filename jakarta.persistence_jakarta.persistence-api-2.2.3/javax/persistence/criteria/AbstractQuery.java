/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import java.util.Set;
import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.persistence.metamodel.EntityType;

public interface AbstractQuery<T>
extends CommonAbstractCriteria {
    public <X> Root<X> from(Class<X> var1);

    public <X> Root<X> from(EntityType<X> var1);

    public AbstractQuery<T> where(Expression<Boolean> var1);

    public AbstractQuery<T> where(Predicate ... var1);

    public AbstractQuery<T> groupBy(Expression<?> ... var1);

    public AbstractQuery<T> groupBy(List<Expression<?>> var1);

    public AbstractQuery<T> having(Expression<Boolean> var1);

    public AbstractQuery<T> having(Predicate ... var1);

    public AbstractQuery<T> distinct(boolean var1);

    public Set<Root<?>> getRoots();

    public Selection<T> getSelection();

    public List<Expression<?>> getGroupList();

    public Predicate getGroupRestriction();

    public boolean isDistinct();

    public Class<T> getResultType();
}

