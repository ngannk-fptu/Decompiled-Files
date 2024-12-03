/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.ToolingHintContainer;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceContainer;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EmbeddableMapping;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.internal.log.DeprecationLogger;

public class EmbeddableSourceImpl
extends AbstractHbmSourceNode
implements EmbeddableSource {
    private final EmbeddableMapping jaxbEmbeddableMapping;
    private final JavaTypeDescriptor typeDescriptor;
    private final AttributeRole attributeRoleBase;
    private final AttributePath attributePathBase;
    private final ToolingHintContext toolingHintContext;
    private final boolean isDynamic;
    private final boolean isUnique;
    private final Map<EntityMode, String> tuplizerClassMap;
    private final List<AttributeSource> attributeSources;

    public EmbeddableSourceImpl(MappingDocument mappingDocument, EmbeddableSourceContainer container, EmbeddableMapping jaxbEmbeddableMapping, List attributeMappings, boolean isDynamic, boolean isUnique, String logicalTableName, NaturalIdMutability naturalIdMutability) {
        super(mappingDocument);
        this.attributeRoleBase = container.getAttributeRoleBase();
        this.attributePathBase = container.getAttributePathBase();
        this.toolingHintContext = ToolingHintContainer.class.isInstance(jaxbEmbeddableMapping) ? Helper.collectToolingHints(container.getToolingHintContextBaselineForEmbeddable(), (ToolingHintContainer)((Object)jaxbEmbeddableMapping)) : container.getToolingHintContextBaselineForEmbeddable();
        this.jaxbEmbeddableMapping = jaxbEmbeddableMapping;
        this.isDynamic = isDynamic;
        this.isUnique = isUnique;
        final String typeName = isDynamic ? jaxbEmbeddableMapping.getClazz() : mappingDocument.qualifyClassName(jaxbEmbeddableMapping.getClazz());
        this.typeDescriptor = new JavaTypeDescriptor(){

            @Override
            public String getName() {
                return typeName;
            }
        };
        if (jaxbEmbeddableMapping.getTuplizer().isEmpty()) {
            this.tuplizerClassMap = Collections.emptyMap();
        } else {
            if (jaxbEmbeddableMapping.getTuplizer().size() > 1) {
                DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
            }
            this.tuplizerClassMap = new HashMap<EntityMode, String>();
            for (JaxbHbmTuplizerType tuplizerBinding : jaxbEmbeddableMapping.getTuplizer()) {
                this.tuplizerClassMap.put(tuplizerBinding.getEntityMode(), tuplizerBinding.getClazz());
            }
        }
        this.attributeSources = new ArrayList<AttributeSource>();
        AttributesHelper.processAttributes(mappingDocument, new AttributesHelper.Callback(){

            @Override
            public AttributeSourceContainer getAttributeSourceContainer() {
                return EmbeddableSourceImpl.this;
            }

            @Override
            public void addAttributeSource(AttributeSource attributeSource) {
                EmbeddableSourceImpl.this.attributeSources.add(attributeSource);
            }
        }, attributeMappings, logicalTableName, naturalIdMutability);
    }

    @Override
    public JavaTypeDescriptor getTypeDescriptor() {
        return this.typeDescriptor;
    }

    @Override
    public String getParentReferenceAttributeName() {
        return this.jaxbEmbeddableMapping.getParent();
    }

    @Override
    public Map<EntityMode, String> getTuplizerClassMap() {
        return this.tuplizerClassMap;
    }

    @Override
    public boolean isDynamic() {
        return this.isDynamic;
    }

    @Override
    public boolean isUnique() {
        return this.isUnique;
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
        return this.metadataBuildingContext();
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }
}

