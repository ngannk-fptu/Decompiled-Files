/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmPropertiesType;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.hibernate.boot.model.source.internal.hbm.EmbeddableSourceContainer;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.ToolingHintContext;

public class EmbeddableSourceVirtualImpl
extends AbstractHbmSourceNode
implements EmbeddableSource {
    private final JavaTypeDescriptor typeDescriptor = new JavaTypeDescriptor(){

        @Override
        public String getName() {
            return null;
        }
    };
    private final AttributeRole attributeRoleBase;
    private final AttributePath attributePathBase;
    private final List<AttributeSource> attributeSources;
    private final boolean isUnique;
    private final ToolingHintContext toolingHintContext;

    public EmbeddableSourceVirtualImpl(MappingDocument mappingDocument, AttributesHelper.Callback containingCallback, EmbeddableSourceContainer container, List attributeJaxbMappings, String logicalTableName, NaturalIdMutability naturalIdMutability, JaxbHbmPropertiesType jaxbPropertiesGroup) {
        super(mappingDocument);
        this.attributeRoleBase = container.getAttributeRoleBase();
        this.attributePathBase = container.getAttributePathBase();
        this.attributeSources = new ArrayList<AttributeSource>();
        AttributesHelper.processAttributes(mappingDocument, new AttributesHelper.Callback(){

            @Override
            public AttributeSourceContainer getAttributeSourceContainer() {
                return EmbeddableSourceVirtualImpl.this;
            }

            @Override
            public void addAttributeSource(AttributeSource attributeSource) {
                EmbeddableSourceVirtualImpl.this.attributeSources.add(attributeSource);
            }
        }, attributeJaxbMappings, logicalTableName, naturalIdMutability);
        this.isUnique = jaxbPropertiesGroup.isUnique();
        this.toolingHintContext = container.getToolingHintContextBaselineForEmbeddable();
    }

    @Override
    public JavaTypeDescriptor getTypeDescriptor() {
        return this.typeDescriptor;
    }

    @Override
    public String getParentReferenceAttributeName() {
        return null;
    }

    @Override
    public Map<EntityMode, String> getTuplizerClassMap() {
        return Collections.emptyMap();
    }

    @Override
    public boolean isDynamic() {
        return false;
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

