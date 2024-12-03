/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.mapping.List;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.ValueVisitor;
import org.hibernate.type.CollectionType;
import org.hibernate.type.PrimitiveType;
import org.hibernate.type.Type;

public class Array
extends List {
    private String elementClassName;

    @Deprecated
    public Array(MetadataImplementor metadata, PersistentClass owner) {
        super(metadata, owner);
    }

    public Array(MetadataBuildingContext buildingContext, PersistentClass owner) {
        super(buildingContext, owner);
    }

    public Class getElementClass() throws MappingException {
        if (this.elementClassName == null) {
            Type elementType = this.getElement().getType();
            return this.isPrimitiveArray() ? ((PrimitiveType)((Object)elementType)).getPrimitiveClass() : elementType.getReturnedClass();
        }
        try {
            return this.getMetadata().getMetadataBuildingOptions().getServiceRegistry().getService(ClassLoaderService.class).classForName(this.elementClassName);
        }
        catch (ClassLoadingException e) {
            throw new MappingException((Throwable)((Object)e));
        }
    }

    @Override
    public CollectionType getDefaultCollectionType() throws MappingException {
        return this.getMetadata().getTypeResolver().getTypeFactory().array(this.getRole(), this.getReferencedPropertyName(), this.getElementClass());
    }

    @Override
    public boolean isArray() {
        return true;
    }

    public String getElementClassName() {
        return this.elementClassName;
    }

    public void setElementClassName(String elementClassName) {
        this.elementClassName = elementClassName;
    }

    @Override
    public Object accept(ValueVisitor visitor) {
        return visitor.accept(this);
    }
}

