/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.CollectionType;

public class Bag
extends Collection {
    @Deprecated
    public Bag(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public Bag(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public CollectionType getDefaultCollectionType() {
        return this.getMetadata().getTypeResolver().getTypeFactory().bag(this.getRole(), this.getReferencedPropertyName());
    }

    @Override
    void createPrimaryKey() {
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

