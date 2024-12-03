/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.CollectionType;

public class List
extends IndexedCollection {
    private int baseIndex;

    @Override
    public boolean isList() {
        return true;
    }

    @Deprecated
    public List(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public List(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public CollectionType getDefaultCollectionType() throws MappingException {
        return this.getMetadata().getTypeResolver().getTypeFactory().list(this.getRole(), this.getReferencedPropertyName());
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }

    public int getBaseIndex() {
        return this.baseIndex;
    }

    public void setBaseIndex(int baseIndex) {
        this.baseIndex = baseIndex;
    }
}

