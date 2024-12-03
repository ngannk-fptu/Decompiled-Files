/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.spi;

import java.util.Collection;
import java.util.List;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EntityHierarchySource;
import org.hibernate.boot.model.source.spi.JpaCallbackSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;

public interface IdentifiableTypeSource
extends AttributeSourceContainer {
    public Origin getOrigin();

    public EntityHierarchySource getHierarchy();

    @Override
    public LocalMetadataBuildingContext getLocalMetadataBuildingContext();

    public String getTypeName();

    public IdentifiableTypeSource getSuperType();

    public Collection<IdentifiableTypeSource> getSubTypes();

    public List<JpaCallbackSource> getJpaCallbackClasses();
}

