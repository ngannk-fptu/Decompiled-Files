/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

public interface CriteriaUpdate<T>
extends CommonAbstractCriteria {
    public Root<T> from(Class<T> var1);

    public Root<T> from(EntityType<T> var1);

    public Root<T> getRoot();

    public <Y, X extends Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> var1, X var2);

    public <Y> CriteriaUpdate<T> set(SingularAttribute<? super T, Y> var1, Expression<? extends Y> var2);

    public <Y, X extends Y> CriteriaUpdate<T> set(Path<Y> var1, X var2);

    public <Y> CriteriaUpdate<T> set(Path<Y> var1, Expression<? extends Y> var2);

    public CriteriaUpdate<T> set(String var1, Object var2);

    public CriteriaUpdate<T> where(Expression<Boolean> var1);

    public CriteriaUpdate<T> where(Predicate ... var1);
}

