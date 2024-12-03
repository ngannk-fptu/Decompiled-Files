/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ValueVisitor;

public class PrimitiveArray
extends Array {
    @Deprecated
    public PrimitiveArray(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public PrimitiveArray(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public boolean isPrimitiveArray() {
        return true;
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

