/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeIdType;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.IdClassSource;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.IdentifierSourceNonAggregatedComposite;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.id.EntityIdentifierNature;
import org.hibernate.internal.util.StringHelper;

class IdentifierSourceNonAggregatedCompositeImpl
implements IdentifierSourceNonAggregatedComposite,
EmbeddableSource {
    private final RootEntitySourceImpl rootEntitySource;
    private final AttributePath attributePathBase;
    private final AttributeRole attributeRoleBase;
    private final IdentifierGeneratorDefinition generatorDefinition;
    private final List attributeSources;
    private final EmbeddableSource idClassSource;
    private final ToolingHintContext toolingHintContext;

    IdentifierSourceNonAggregatedCompositeImpl(RootEntitySourceImpl rootEntitySource) {
        this.rootEntitySource = rootEntitySource;
        this.attributePathBase = rootEntitySource.getAttributePathBase().append("<id>");
        this.attributeRoleBase = rootEntitySource.getAttributeRoleBase().append("<id>");
        this.generatorDefinition = EntityHierarchySourceImpl.interpretGeneratorDefinition(rootEntitySource.sourceMappingDocument(), rootEntitySource.getEntityNamingSource(), rootEntitySource.jaxbEntityMapping().getCompositeId().getGenerator());
        this.attributeSources = new ArrayList();
        AttributesHelper.processCompositeKeySubAttributes(rootEntitySource.sourceMappingDocument(), new AttributesHelper.Callback(){

            @Override
            public AttributeSourceContainer getAttributeSourceContainer() {
                return IdentifierSourceNonAggregatedCompositeImpl.this;
            }

            @Override
            public void addAttributeSource(AttributeSource attributeSource) {
                IdentifierSourceNonAggregatedCompositeImpl.this.attributeSources.add(attributeSource);
            }
        }, rootEntitySource.jaxbEntityMapping().getCompositeId().getKeyPropertyOrKeyManyToOne());
        this.idClassSource = this.interpretIdClass(rootEntitySource.sourceMappingDocument(), rootEntitySource.jaxbEntityMapping().getCompositeId());
        this.toolingHintContext = Helper.collectToolingHints(rootEntitySource.getToolingHintContext(), rootEntitySource.jaxbEntityMapping().getCompositeId());
    }

    private EmbeddableSource interpretIdClass(MappingDocument mappingDocument, JaxbHbmCompositeIdType jaxbHbmCompositeIdMapping) {
        if (!jaxbHbmCompositeIdMapping.isMapped()) {
            return null;
        }
        String className = jaxbHbmCompositeIdMapping.getClazz();
        if (StringHelper.isEmpty(className)) {
            return null;
        }
        final String idClassQualifiedName = mappingDocument.qualifyClassName(className);
        JavaTypeDescriptor idClassTypeDescriptor = new JavaTypeDescriptor(){

            @Override
            public String getName() {
                return idClassQualifiedName;
            }
        };
        return new IdClassSource(idClassTypeDescriptor, this.rootEntitySource, mappingDocument);
    }

    @Override
    public List<SingularAttributeSource> getAttributeSourcesMakingUpIdentifier() {
        return this.attributeSources;
    }

    @Override
    public EmbeddableSource getIdClassSource() {
        return this.idClassSource;
    }

    @Override
    public IdentifierGeneratorDefinition getIndividualAttributeIdGenerator(String identifierAttributeName) {
        return null;
    }

    @Override
    public IdentifierGeneratorDefinition getIdentifierGeneratorDescriptor() {
        return this.generatorDefinition;
    }

    @Override
    public EntityIdentifierNature getNature() {
        return EntityIdentifierNature.NON_AGGREGATED_COMPOSITE;
    }

    @Override
    public JavaTypeDescriptor getTypeDescriptor() {
        return null;
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
        return this.rootEntitySource.metadataBuildingContext();
    }

    @Override
    public EmbeddableSource getEmbeddableSource() {
        return this;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }
}

