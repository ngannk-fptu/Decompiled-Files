/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNaturalIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.model.source.internal.hbm.AbstractEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;

public class RootEntitySourceImpl
extends AbstractEntitySourceImpl {
    private final TableSpecificationSource primaryTable;

    protected RootEntitySourceImpl(MappingDocument sourceMappingDocument, JaxbHbmRootEntityType entityElement) {
        super(sourceMappingDocument, entityElement);
        this.primaryTable = Helper.createTableSource(this.sourceMappingDocument(), entityElement, this, entityElement.getRowid(), entityElement.getComment(), entityElement.getCheck());
        this.afterInstantiation();
    }

    @Override
    protected void buildAttributeSources(AttributesHelper.Callback attributeBuildingCallback) {
        JaxbHbmNaturalIdType naturalId = this.jaxbEntityMapping().getNaturalId();
        if (naturalId != null) {
            NaturalIdMutability naturalIdMutability = naturalId.isMutable() ? NaturalIdMutability.MUTABLE : NaturalIdMutability.IMMUTABLE;
            AttributesHelper.processAttributes(this.sourceMappingDocument(), attributeBuildingCallback, naturalId.getAttributes(), null, naturalIdMutability);
        }
        super.buildAttributeSources(attributeBuildingCallback);
    }

    @Override
    protected JaxbHbmRootEntityType jaxbEntityMapping() {
        return (JaxbHbmRootEntityType)super.jaxbEntityMapping();
    }

    @Override
    public TableSpecificationSource getPrimaryTable() {
        return this.primaryTable;
    }

    @Override
    public String getDiscriminatorMatchValue() {
        return this.jaxbEntityMapping().getDiscriminatorValue();
    }

    @Override
    public IdentifiableTypeSource getSuperType() {
        return null;
    }
}

