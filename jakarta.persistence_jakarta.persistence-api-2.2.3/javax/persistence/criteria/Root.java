/*
 * Decompiled with CFR 0.152.
 */
package javax.persistence.criteria;

import javax.persistence.criteria.From;
import javax.persistence.metamodel.EntityType;

public interface Root<X>
extends From<X, X> {
    @Override
    public EntityType<X> getModel();
}

