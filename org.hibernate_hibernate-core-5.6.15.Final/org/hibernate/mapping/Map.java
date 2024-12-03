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

public class Map
extends IndexedCollection {
    @Deprecated
    public Map(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public Map(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public CollectionType getDefaultCollectionType() {
        if (this.isSorted()) {
            return this.getMetadata().getTypeResolver().getTypeFactory().sortedMap(this.getRole(), this.getReferencedPropertyName(), this.getComparator());
        }
        if (this.hasOrder()) {
            return this.getMetadata().getTypeResolver().getTypeFactory().orderedMap(this.getRole(), this.getReferencedPropertyName());
        }
        return this.getMetadata().getTypeResolver().getTypeFactory().map(this.getRole(), this.getReferencedPropertyName());
    }

    @Override
    public void createAllKeys() throws MappingException {
        super.createAllKeys();
        if (!this.isInverse()) {
            this.getIndex().createForeignKey();
        }
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

