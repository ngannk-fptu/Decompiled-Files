/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Collection;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.CollectionAttribute;

public interface CollectionJoin<Z, E>
extends PluralJoin<Z, Collection<E>, E> {
    @Override
    public CollectionJoin<Z, E> on(Expression<Boolean> var1);

    @Override
    public CollectionJoin<Z, E> on(Predicate ... var1);

    @Override
    public CollectionAttribute<? super Z, E> getModel();
}

