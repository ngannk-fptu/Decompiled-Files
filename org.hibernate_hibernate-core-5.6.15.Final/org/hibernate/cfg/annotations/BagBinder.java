/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations;

import org.hibernate.cfg.annotations.CollectionBinder;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;

public class BagBinder
extends CollectionBinder {
    public BagBinder() {
        super(false);
    }

    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new Bag(this.getBuildingContext(), persistentClass);
    }
}

