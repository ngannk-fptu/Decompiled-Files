/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.cfg.annotations;

import org.hibernate.cfg.annotations.ArrayBinder;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;

public class PrimitiveArrayBinder
extends ArrayBinder {
    @Override
    protected Collection createCollection(PersistentClass persistentClass) {
        return new PrimitiveArray(this.getBuildingContext(), persistentClass);
    }
}

