/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AbstractToOneAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FetchCharacteristicsSingularAssociationImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.ManyToOneAttributeColumnsAndFormulasSource;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceManyToOne;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.type.ForeignKeyDirection;

class SingularAttributeSourceManyToOneImpl
extends AbstractToOneAttributeSourceImpl
implements SingularAttributeSourceManyToOne,
RelationalValueSourceContainer {
    private final JaxbHbmManyToOneType manyToOneElement;
    private final HibernateTypeSourceImpl typeSource;
    private final String referencedTypeName;
    private final List<RelationalValueSource> relationalValueSources;
    private final AttributeRole attributeRole;
    private final AttributePath attributePath;
    private final ToolingHintContext toolingHintContext;
    private final FetchCharacteristicsSingularAssociationImpl fetchCharacteristics;

    SingularAttributeSourceManyToOneImpl(MappingDocument mappingDocument, AttributeSourceContainer container, JaxbHbmManyToOneType manyToOneElement, String logicalTableName, NaturalIdMutability naturalIdMutability) {
        super(mappingDocument, naturalIdMutability);
        this.manyToOneElement = manyToOneElement;
        this.referencedTypeName = manyToOneElement.getClazz() != null ? mappingDocument.qualifyClassName(manyToOneElement.getClazz()) : manyToOneElement.getEntityName();
        JavaTypeDescriptor referencedTypeDescriptor = new JavaTypeDescriptor(){

            @Override
            public String getName() {
                return SingularAttributeSourceManyToOneImpl.this.referencedTypeName;
            }
        };
        this.typeSource = new HibernateTypeSourceImpl(referencedTypeDescriptor);
        this.relationalValueSources = RelationalValueSourceHelper.buildValueSources(mappingDocument, logicalTableName, new ManyToOneAttributeColumnsAndFormulasSource(manyToOneElement));
        this.attributeRole = container.getAttributeRoleBase().append(manyToOneElement.getName());
        this.attributePath = container.getAttributePathBase().append(manyToOneElement.getName());
        this.fetchCharacteristics = FetchCharacteristicsSingularAssociationImpl.interpretManyToOne(mappingDocument.getMappingDefaults(), manyToOneElement.getFetch(), manyToOneElement.getOuterJoin(), manyToOneElement.getLazy());
        this.toolingHintContext = Helper.collectToolingHints(container.getToolingHintContext(), manyToOneElement);
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.MANY_TO_ONE;
    }

    @Override
    public String getName() {
        return this.manyToOneElement.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.manyToOneElement.getNode();
    }

    @Override
    public AttributePath getAttributePath() {
        return this.attributePath;
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
        return this.manyToOneElement.getAccess();
    }

    @Override
    public FetchCharacteristicsSingularAssociationImpl getFetchCharacteristics() {
        return this.fetchCharacteristics;
    }

    @Override
    public boolean isIgnoreNotFound() {
        return this.manyToOneElement.getNotFound() != null && "ignore".equalsIgnoreCase(this.manyToOneElement.getNotFound().value());
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return this.manyToOneElement.isOptimisticLock();
    }

    @Override
    public String getCascadeStyleName() {
        return this.manyToOneElement.getCascade() == null ? "" : this.manyToOneElement.getCascade();
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.MANY_TO_ONE;
    }

    @Override
    public Boolean isInsertable() {
        return this.manyToOneElement.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return this.manyToOneElement.isUpdate();
    }

    @Override
    public boolean isBytecodeLazy() {
        return false;
    }

    @Override
    public String getReferencedEntityAttributeName() {
        return this.manyToOneElement.getPropertyRef();
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedTypeName;
    }

    @Override
    public Boolean isEmbedXml() {
        return this.manyToOneElement.isEmbedXml();
    }

    @Override
    public boolean isUnique() {
        return this.manyToOneElement.isUnique();
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.manyToOneElement.getForeignKey();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return JaxbHbmOnDeleteEnum.CASCADE.equals((Object)this.manyToOneElement.getOnDelete());
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return ForeignKeyDirection.TO_PARENT;
    }

    @Override
    public List<RelationalValueSource> getRelationalValueSources() {
        return this.relationalValueSources;
    }

    @Override
    public boolean areValuesIncludedInInsertByDefault() {
        return true;
    }

    @Override
    public boolean areValuesIncludedInUpdateByDefault() {
        return true;
    }

    @Override
    public boolean areValuesNullableByDefault() {
        return this.getNaturalIdMutability() == NaturalIdMutability.NOT_NATURAL_ID;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }
}

