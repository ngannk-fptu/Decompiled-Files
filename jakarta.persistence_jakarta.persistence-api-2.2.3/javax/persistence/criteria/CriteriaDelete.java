/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.CommonAbstractCriteria;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

public interface CriteriaDelete<T>
extends CommonAbstractCriteria {
    public Root<T> from(Class<T> var1);

    public Root<T> from(EntityType<T> var1);

    public Root<T> getRoot();

    public CriteriaDelete<T> where(Expression<Boolean> var1);

    public CriteriaDelete<T> where(Predicate ... var1);
}

