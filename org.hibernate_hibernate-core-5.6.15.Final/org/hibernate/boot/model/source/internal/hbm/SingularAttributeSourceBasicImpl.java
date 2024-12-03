/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicAttributeType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.BasicAttributeColumnsAndFormulasSource;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceBasic;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.tuple.GenerationTiming;

class SingularAttributeSourceBasicImpl
extends AbstractHbmSourceNode
implements SingularAttributeSourceBasic {
    private final JaxbHbmBasicAttributeType propertyElement;
    private final HibernateTypeSourceImpl typeSource;
    private final NaturalIdMutability naturalIdMutability;
    private final List<RelationalValueSource> relationalValueSources;
    private final AttributeRole attributeRole;
    private final AttributePath attributePath;
    private ToolingHintContext toolingHintContext;

    SingularAttributeSourceBasicImpl(MappingDocument sourceMappingDocument, AttributeSourceContainer container, JaxbHbmBasicAttributeType propertyElement, String logicalTableName, NaturalIdMutability naturalIdMutability) {
        super(sourceMappingDocument);
        this.propertyElement = propertyElement;
        this.typeSource = new HibernateTypeSourceImpl(propertyElement);
        this.naturalIdMutability = naturalIdMutability;
        this.relationalValueSources = RelationalValueSourceHelper.buildValueSources(sourceMappingDocument, logicalTableName, new BasicAttributeColumnsAndFormulasSource(propertyElement));
        this.attributeRole = container.getAttributeRoleBase().append(this.getName());
        this.attributePath = container.getAttributePathBase().append(this.getName());
        this.toolingHintContext = Helper.collectToolingHints(container.getToolingHintContext(), propertyElement);
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.BASIC;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.PROPERTY;
    }

    @Override
    public String getName() {
        return this.propertyElement.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.propertyElement.getNode();
    }

    @Override
    public AttributePath getAttributePath() {
        return this.attributePath;
    }

    @Override
    public boolean isCollectionElement() {
        return false;
    }

    @Override
    public AttributeRole getAttributeRole() {
        return this.attributeRole;
    }

    @Override
    public HibernateTypeSourceImpl getTypeInformation() {
        return this.typeSource;
    }

    @Override
    public String getPropertyAccessorName() {
        return this.propertyElement.getAccess();
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return this.propertyElement.getGenerated();
    }

    @Override
    public Boolean isInsertable() {
        return this.propertyElement.isInsert() == null ? Boolean.TRUE : this.propertyElement.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return this.propertyElement.isUpdate() == null ? Boolean.TRUE : this.propertyElement.isUpdate();
    }

    @Override
    public boolean isBytecodeLazy() {
        return Helper.getValue(this.propertyElement.isLazy(), false);
    }

    @Override
    public NaturalIdMutability getNaturalIdMutability() {
        return this.naturalIdMutability;
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return Helper.getValue(this.propertyElement.isOptimisticLock(), true);
    }

    @Override
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public List<RelationalValueSource> getRelationalValueSources() {
        return this.relationalValueSources;
    }

    @Override
    public boolean areValuesIncludedInInsertByDefault() {
        return Helper.getValue(this.propertyElement.isInsert(), true);
    }

    @Override
    public boolean areValuesIncludedInUpdateByDefault() {
        return Helper.getValue(this.propertyElement.isUpdate(), true);
    }

    @Override
    public boolean areValuesNullableByDefault() {
        return Helper.getValue(this.propertyElement.isNotNull(), this.naturalIdMutability != NaturalIdMutability.NOT_NATURAL_ID) == false;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public MetadataBuildingContext getBuildingContext() {
        return this.sourceMappingDocument();
    }
}

