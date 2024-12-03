/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyBasicAttributeType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintContainer;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.CompositeIdentifierSingularAttributeSourceBasicImpl;
import org.hibernate.boot.model.source.internal.hbm.CompositeIdentifierSingularAttributeSourceManyToOneImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.ToolingHintContext;

class IdClassSource
implements EmbeddableSource {
    private final JavaTypeDescriptor idClassDescriptor;
    private final RootEntitySourceImpl rootEntitySource;
    private final MappingDocument sourceMappingDocument;
    private final AttributePath attributePathBase;
    private final AttributeRole attributeRoleBase;
    private final List<AttributeSource> attributeSources;

    IdClassSource(JavaTypeDescriptor idClassDescriptor, RootEntitySourceImpl rootEntitySource, MappingDocument sourceMappingDocument) {
        this.idClassDescriptor = idClassDescriptor;
        this.rootEntitySource = rootEntitySource;
        this.sourceMappingDocument = sourceMappingDocument;
        this.attributePathBase = rootEntitySource.getAttributePathBase().append("<IdClass>");
        this.attributeRoleBase = rootEntitySource.getAttributeRoleBase().append("<IdClass>");
        this.attributeSources = new ArrayList<AttributeSource>();
        for (JaxbHbmToolingHintContainer attribute : rootEntitySource.jaxbEntityMapping().getCompositeId().getKeyPropertyOrKeyManyToOne()) {
            if (JaxbHbmCompositeKeyBasicAttributeType.class.isInstance(attribute)) {
                this.attributeSources.add(new CompositeIdentifierSingularAttributeSourceBasicImpl(sourceMappingDocument, this, (JaxbHbmCompositeKeyBasicAttributeType)attribute));
                continue;
            }
            this.attributeSources.add(new CompositeIdentifierSingularAttributeSourceManyToOneImpl(sourceMappingDocument, this, (JaxbHbmCompositeKeyManyToOneType)attribute));
        }
    }

    @Override
    public JavaTypeDescriptor getTypeDescriptor() {
        return this.idClassDescriptor;
    }

    @Override
    public String getParentReferenceAttributeName() {
        return null;
    }

    @Override
    public Map<EntityMode, String> getTuplizerClassMap() {
        return null;
    }

    @Override
    public boolean isDynamic() {
        return false;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public AttributePath getAttributePathBase() {
        return this.attributePathBase;
    }

    @Override
    public AttributeRole getAttributeRoleBase() {
        return this.attributeRoleBase;
    }

    @Override
    public List<AttributeSource> attributeSources() {
        return this.attributeSources;
    }

    @Override
    public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
        return this.rootEntitySource.getLocalMetadataBuildingContext();
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.rootEntitySource.getToolingHintContext();
    }
}

