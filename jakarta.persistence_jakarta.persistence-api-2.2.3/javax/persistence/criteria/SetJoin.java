/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.Set;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.SetAttribute;

public interface SetJoin<Z, E>
extends PluralJoin<Z, Set<E>, E> {
    @Override
    public SetJoin<Z, E> on(Expression<Boolean> var1);

    @Override
    public SetJoin<Z, E> on(Predicate ... var1);

    @Override
    public SetAttribute<? super Z, E> getModel();
}

