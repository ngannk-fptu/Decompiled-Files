/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOnDeleteEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmOneToOneType;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AbstractToOneAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FetchCharacteristicsSingularAssociationImpl;
import org.hibernate.boot.model.source.internal.hbm.FormulaImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.DerivedValueSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceOneToOne;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.type.ForeignKeyDirection;

class SingularAttributeSourceOneToOneImpl
extends AbstractToOneAttributeSourceImpl
implements SingularAttributeSourceOneToOne {
    private final JaxbHbmOneToOneType oneToOneElement;
    private final HibernateTypeSourceImpl typeSource;
    private final String referencedTypeName;
    private final List<DerivedValueSource> formulaSources;
    private final AttributeRole attributeRole;
    private final AttributePath attributePath;
    private final FetchCharacteristicsSingularAssociationImpl fetchCharacteristics;
    private ToolingHintContext toolingHintContext;

    SingularAttributeSourceOneToOneImpl(MappingDocument mappingDocument, AttributeSourceContainer container, JaxbHbmOneToOneType oneToOneElement, String logicalTableName, NaturalIdMutability naturalIdMutability) {
        super(mappingDocument, naturalIdMutability);
        this.oneToOneElement = oneToOneElement;
        this.referencedTypeName = oneToOneElement.getClazz() != null ? this.metadataBuildingContext().qualifyClassName(oneToOneElement.getClazz()) : oneToOneElement.getEntityName();
        JavaTypeDescriptor referencedTypeDescriptor = new JavaTypeDescriptor(){

            @Override
            public String getName() {
                return SingularAttributeSourceOneToOneImpl.this.referencedTypeName;
            }
        };
        this.typeSource = new HibernateTypeSourceImpl(referencedTypeDescriptor);
        if (StringHelper.isNotEmpty(oneToOneElement.getFormulaAttribute())) {
            this.formulaSources = Collections.singletonList(new FormulaImpl(mappingDocument, logicalTableName, oneToOneElement.getFormulaAttribute()));
        } else if (!oneToOneElement.getFormula().isEmpty()) {
            this.formulaSources = CollectionHelper.arrayList(oneToOneElement.getFormula().size());
            for (String expression : oneToOneElement.getFormula()) {
                this.formulaSources.add(new FormulaImpl(mappingDocument, logicalTableName, expression));
            }
        } else {
            this.formulaSources = Collections.emptyList();
        }
        this.attributeRole = container.getAttributeRoleBase().append(oneToOneElement.getName());
        this.attributePath = container.getAttributePathBase().append(oneToOneElement.getName());
        this.fetchCharacteristics = FetchCharacteristicsSingularAssociationImpl.interpretOneToOne(mappingDocument.getMappingDefaults(), oneToOneElement.getFetch(), oneToOneElement.getOuterJoin(), oneToOneElement.getLazy(), oneToOneElement.isConstrained());
        this.toolingHintContext = Helper.collectToolingHints(container.getToolingHintContext(), oneToOneElement);
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.ONE_TO_ONE;
    }

    @Override
    public String getName() {
        return this.oneToOneElement.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.oneToOneElement.getNode();
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
        return this.oneToOneElement.getAccess();
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return true;
    }

    @Override
    public String getCascadeStyleName() {
        return this.oneToOneElement.getCascade();
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.ONE_TO_ONE;
    }

    @Override
    public Boolean isInsertable() {
        return false;
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
    public FetchCharacteristicsSingularAssociationImpl getFetchCharacteristics() {
        return this.fetchCharacteristics;
    }

    @Override
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedTypeName;
    }

    @Override
    public boolean isUnique() {
        return true;
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.oneToOneElement.getForeignKey();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return JaxbHbmOnDeleteEnum.CASCADE.equals((Object)this.oneToOneElement.getOnDelete());
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return this.oneToOneElement.isConstrained() ? ForeignKeyDirection.FROM_PARENT : ForeignKeyDirection.TO_PARENT;
    }

    @Override
    public List<DerivedValueSource> getFormulaSources() {
        return this.formulaSources;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public boolean isConstrained() {
        return this.oneToOneElement.isConstrained();
    }

    @Override
    public String getReferencedEntityAttributeName() {
        return this.oneToOneElement.getPropertyRef();
    }

    @Override
    public Boolean isEmbedXml() {
        return this.oneToOneElement.isEmbedXml();
    }
}

