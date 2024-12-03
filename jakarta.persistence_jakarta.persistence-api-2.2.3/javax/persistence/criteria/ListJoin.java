/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import java.util.List;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.PluralJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.ListAttribute;

public interface ListJoin<Z, E>
extends PluralJoin<Z, List<E>, E> {
    @Override
    public ListJoin<Z, E> on(Expression<Boolean> var1);

    @Override
    public ListJoin<Z, E> on(Predicate ... var1);

    @Override
    public ListAttribute<? super Z, E> getModel();

    public Expression<Integer> index();
}

