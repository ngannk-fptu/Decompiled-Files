/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeIdType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmToolingHintType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.source.internal.hbm.AbstractSingularAttributeSourceEmbeddedImpl;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceContainer;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.EmbeddableMapping;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.EmbeddedAttributeMapping;
import org.hibernate.boot.model.source.spi.IdentifierSourceAggregatedComposite;
import org.hibernate.boot.model.source.spi.MapsIdSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceEmbedded;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.id.EntityIdentifierNature;

class IdentifierSourceAggregatedCompositeImpl
implements IdentifierSourceAggregatedComposite {
    private final SingularAttributeSourceAggregatedCompositeIdentifierImpl attributeSource;
    private final IdentifierGeneratorDefinition generatorDefinition;
    private final ToolingHintContext toolingHintContext;

    public IdentifierSourceAggregatedCompositeImpl(RootEntitySourceImpl rootEntitySource) {
        EmbeddedAttributeMappingAdapterAggregatedCompositeId compositeIdAdapter = new EmbeddedAttributeMappingAdapterAggregatedCompositeId(rootEntitySource);
        this.attributeSource = new SingularAttributeSourceAggregatedCompositeIdentifierImpl(rootEntitySource.sourceMappingDocument(), compositeIdAdapter);
        this.generatorDefinition = EntityHierarchySourceImpl.interpretGeneratorDefinition(rootEntitySource.sourceMappingDocument(), rootEntitySource.getEntityNamingSource(), rootEntitySource.jaxbEntityMapping().getCompositeId().getGenerator());
        this.toolingHintContext = Helper.collectToolingHints(rootEntitySource.getToolingHintContext(), rootEntitySource.jaxbEntityMapping().getCompositeId());
    }

    @Override
    public SingularAttributeSourceEmbedded getIdentifierAttributeSource() {
        return this.attributeSource;
    }

    @Override
    public List<MapsIdSource> getMapsIdSources() {
        return Collections.emptyList();
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
        return EntityIdentifierNature.AGGREGATED_COMPOSITE;
    }

    @Override
    public EmbeddableSource getEmbeddableSource() {
        return this.attributeSource.getEmbeddableSource();
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    private static class EmbeddedAttributeMappingAdapterAggregatedCompositeId
    implements EmbeddedAttributeMapping,
    EmbeddableSourceContainer,
    EmbeddableMapping {
        private final RootEntitySourceImpl rootEntitySource;
        private final JaxbHbmCompositeIdType jaxbCompositeIdMapping;
        private final AttributeRole idAttributeRole;
        private final AttributePath idAttributePath;
        private final ToolingHintContext toolingHintContext;

        private EmbeddedAttributeMappingAdapterAggregatedCompositeId(RootEntitySourceImpl rootEntitySource) {
            this.rootEntitySource = rootEntitySource;
            this.jaxbCompositeIdMapping = rootEntitySource.jaxbEntityMapping().getCompositeId();
            this.idAttributeRole = rootEntitySource.getAttributeRoleBase().append(this.jaxbCompositeIdMapping.getName());
            this.idAttributePath = rootEntitySource.getAttributePathBase().append(this.jaxbCompositeIdMapping.getName());
            this.toolingHintContext = Helper.collectToolingHints(rootEntitySource.getToolingHintContext(), this.jaxbCompositeIdMapping);
        }

        @Override
        public String getName() {
            return this.jaxbCompositeIdMapping.getName();
        }

        @Override
        public String getAccess() {
            return this.jaxbCompositeIdMapping.getAccess();
        }

        @Override
        public String getClazz() {
            return this.jaxbCompositeIdMapping.getClazz();
        }

        @Override
        public List<JaxbHbmTuplizerType> getTuplizer() {
            return Collections.emptyList();
        }

        @Override
        public String getParent() {
            return null;
        }

        @Override
        public List<JaxbHbmToolingHintType> getToolingHints() {
            return this.jaxbCompositeIdMapping.getToolingHints();
        }

        @Override
        public AttributeRole getAttributeRoleBase() {
            return this.idAttributeRole;
        }

        @Override
        public AttributePath getAttributePathBase() {
            return this.idAttributePath;
        }

        @Override
        public ToolingHintContext getToolingHintContextBaselineForEmbeddable() {
            return this.toolingHintContext;
        }

        public List getAttributes() {
            return this.jaxbCompositeIdMapping.getKeyPropertyOrKeyManyToOne();
        }

        public String getXmlNodeName() {
            return this.jaxbCompositeIdMapping.getNode();
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public EmbeddableMapping getEmbeddableMapping() {
            return this;
        }
    }

    private static class SingularAttributeSourceAggregatedCompositeIdentifierImpl
    extends AbstractSingularAttributeSourceEmbeddedImpl {
        private final EmbeddedAttributeMappingAdapterAggregatedCompositeId compositeIdAdapter;

        protected SingularAttributeSourceAggregatedCompositeIdentifierImpl(MappingDocument mappingDocument, EmbeddedAttributeMappingAdapterAggregatedCompositeId compositeIdAdapter) {
            super(mappingDocument, compositeIdAdapter, new EmbeddableSourceImpl(mappingDocument, compositeIdAdapter, compositeIdAdapter, compositeIdAdapter.getAttributes(), false, false, null, NaturalIdMutability.NOT_NATURAL_ID), NaturalIdMutability.NOT_NATURAL_ID);
            this.compositeIdAdapter = compositeIdAdapter;
        }

        @Override
        public Boolean isInsertable() {
            return true;
        }

        @Override
        public Boolean isUpdatable() {
            return false;
        }

        @Override
        public boolean isBytecodeLazy() {
            return false;
        }

        @Override
        public XmlElementMetadata getSourceType() {
            return XmlElementMetadata.COMPOSITE_ID;
        }

        @Override
        public String getXmlNodeName() {
            return this.compositeIdAdapter.getXmlNodeName();
        }

        @Override
        public AttributePath getAttributePath() {
            return this.getEmbeddableSource().getAttributePathBase();
        }

        @Override
        public AttributeRole getAttributeRole() {
            return this.getEmbeddableSource().getAttributeRoleBase();
        }

        @Override
        public boolean isIncludedInOptimisticLocking() {
            return false;
        }

        @Override
        public ToolingHintContext getToolingHintContext() {
            return this.compositeIdAdapter.toolingHintContext;
        }
    }
}

