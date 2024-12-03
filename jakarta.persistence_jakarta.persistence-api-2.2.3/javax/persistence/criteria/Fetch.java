/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.JoinType;
import javax.persistence.metamodel.Attribute;

public interface Fetch<Z, X>
extends FetchParent<Z, X> {
    public Attribute<? super Z, ?> getAttribute();

    public FetchParent<?, Z> getParent();

    public JoinType getJoinType();
}

