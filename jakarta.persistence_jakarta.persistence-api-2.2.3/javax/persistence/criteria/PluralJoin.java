/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.Join;
import javax.persistence.metamodel.PluralAttribute;

public interface PluralJoin<Z, C, E>
extends Join<Z, E> {
    public PluralAttribute<? super Z, C, E> getModel();
}

