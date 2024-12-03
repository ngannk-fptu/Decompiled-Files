/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.CollectionType;

public class IdentifierBag
extends IdentifierCollection {
    @Deprecated
    public IdentifierBag(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public IdentifierBag(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    @Override
    public CollectionType getDefaultCollectionType() {
        return this.getMetadata().getTypeResolver().getTypeFactory().idbag(this.getRole(), this.getReferencedPropertyName());
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

