/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmDiscriminatorSubclassEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.TableInformationContainer;
import org.hibernate.boot.model.source.internal.hbm.AbstractEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
import org.hibernate.boot.model.source.spi.SubclassEntitySource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;

public class SubclassEntitySourceImpl
extends AbstractEntitySourceImpl
implements SubclassEntitySource {
    private final EntitySource container;
    private final TableSpecificationSource primaryTable;

    protected SubclassEntitySourceImpl(MappingDocument sourceMappingDocument, JaxbHbmEntityBaseDefinition entityElement, EntitySource container) {
        super(sourceMappingDocument, entityElement);
        this.container = container;
        this.primaryTable = TableInformationContainer.class.isInstance(entityElement) ? Helper.createTableSource(this.sourceMappingDocument(), (TableInformationContainer)((Object)entityElement), this) : null;
        this.afterInstantiation();
    }

    @Override
    public TableSpecificationSource getPrimaryTable() {
        return this.primaryTable;
    }

    @Override
    public String getDiscriminatorMatchValue() {
        return JaxbHbmDiscriminatorSubclassEntityType.class.isInstance(this.jaxbEntityMapping()) ? ((JaxbHbmDiscriminatorSubclassEntityType)this.jaxbEntityMapping()).getDiscriminatorValue() : null;
    }

    @Override
    public IdentifiableTypeSource getSuperType() {
        return this.container;
    }
}

