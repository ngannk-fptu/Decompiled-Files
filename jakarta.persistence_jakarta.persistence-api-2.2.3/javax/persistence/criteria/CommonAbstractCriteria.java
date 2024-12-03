/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Subquery;

public interface CommonAbstractCriteria {
    public <U> Subquery<U> subquery(Class<U> var1);

    public Predicate getRestriction();
}

