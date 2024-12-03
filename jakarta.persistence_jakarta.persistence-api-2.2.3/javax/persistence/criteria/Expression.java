/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Collection;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Selection;

public interface Expression<T>
extends Selection<T> {
    public Predicate isNull();

    public Predicate isNotNull();

    public Predicate in(Object ... var1);

    public Predicate in(Expression<?> ... var1);

    public Predicate in(Collection<?> var1);

    public Predicate in(Expression<Collection<?>> var1);

    public <X> Expression<X> as(Class<X> var1);
}

