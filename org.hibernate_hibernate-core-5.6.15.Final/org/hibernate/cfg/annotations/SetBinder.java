/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations;

import org.hibernate.annotations.OrderBy;
import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Set;

public class SetBinder
extends CollectionBinder {
    public SetBinder(boolean sorted) {
        super(sorted);
    }

    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new Set(this.getBuildingContext(), persistentClass);
    }

    @Override
    public void setSqlOrderBy(OrderBy orderByAnn) {
        if (orderByAnn != null) {
            super.setSqlOrderBy(orderByAnn);
        }
    }
}

