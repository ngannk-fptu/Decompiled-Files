/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.metamodel.Attribute;

public interface Join<Z, X>
extends From<Z, X> {
    public Join<Z, X> on(Expression<Boolean> var1);

    public Join<Z, X> on(Predicate ... var1);

    public Predicate getOn();

    public Attribute<? super Z, ?> getAttribute();

    public From<?, Z> getParent();

    public JoinType getJoinType();
}

