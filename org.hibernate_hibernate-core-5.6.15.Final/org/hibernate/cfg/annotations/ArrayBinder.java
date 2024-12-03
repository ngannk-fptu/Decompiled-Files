/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations;

import org.hibernate.cfg.annotations.ListBinder;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;

public class ArrayBinder
extends ListBinder {
    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new Array(this.getBuildingContext(), persistentClass);
    }
}

