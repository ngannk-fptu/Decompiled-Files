/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmCompositeKeyManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyEnum;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmLazyWithNoProxyEnum;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.AbstractToOneAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FetchCharacteristicsSingularAssociationImpl;
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
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceManyToOne;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.type.ForeignKeyDirection;

public class CompositeIdentifierSingularAttributeSourceManyToOneImpl
extends AbstractToOneAttributeSourceImpl
implements SingularAttributeSourceManyToOne,
RelationalValueSourceContainer {
    private final JaxbHbmCompositeKeyManyToOneType keyManyToOneElement;
    private final String referencedEntityName;
    private final HibernateTypeSourceImpl typeSource;
    private final List<RelationalValueSource> valueSources;
    private final AttributePath attributePath;
    private final AttributeRole attributeRole;
    private final FetchCharacteristicsSingularAssociationImpl fetchCharacteristics;
    private final ToolingHintContext toolingHintContext;

    public CompositeIdentifierSingularAttributeSourceManyToOneImpl(MappingDocument mappingDocument, AttributeSourceContainer container, final JaxbHbmCompositeKeyManyToOneType keyManyToOneElement) {
        super(mappingDocument, NaturalIdMutability.NOT_NATURAL_ID);
        this.keyManyToOneElement = keyManyToOneElement;
        this.referencedEntityName = keyManyToOneElement.getClazz() != null ? mappingDocument.qualifyClassName(keyManyToOneElement.getClazz()) : keyManyToOneElement.getEntityName();
        JavaTypeDescriptor referencedTypeDescriptor = new JavaTypeDescriptor(){

            @Override
            public String getName() {
                return CompositeIdentifierSingularAttributeSourceManyToOneImpl.this.referencedEntityName;
            }
        };
        this.typeSource = new HibernateTypeSourceImpl(referencedTypeDescriptor);
        this.valueSources = RelationalValueSourceHelper.buildValueSources(mappingDocument, null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.KEY_MANY_TO_ONE;
            }

            @Override
            public String getSourceName() {
                return keyManyToOneElement.getName();
            }

            @Override
            public String getColumnAttribute() {
                return keyManyToOneElement.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return keyManyToOneElement.getColumn();
            }
        });
        this.attributePath = container.getAttributePathBase().append(this.getName());
        this.attributeRole = container.getAttributeRoleBase().append(this.getName());
        this.fetchCharacteristics = FetchCharacteristicsSingularAssociationImpl.interpretManyToOne(mappingDocument.getMappingDefaults(), null, null, CompositeIdentifierSingularAttributeSourceManyToOneImpl.interpretLazy(mappingDocument, keyManyToOneElement));
        this.toolingHintContext = Helper.collectToolingHints(container.getToolingHintContext(), keyManyToOneElement);
    }

    private static JaxbHbmLazyWithNoProxyEnum interpretLazy(MappingDocument mappingDocument, JaxbHbmCompositeKeyManyToOneType keyManyToOne) {
        if (keyManyToOne.getLazy() == null) {
            return null;
        }
        if (keyManyToOne.getLazy() == JaxbHbmLazyEnum.FALSE) {
            return JaxbHbmLazyWithNoProxyEnum.FALSE;
        }
        if (keyManyToOne.getLazy() == JaxbHbmLazyEnum.PROXY) {
            return JaxbHbmLazyWithNoProxyEnum.PROXY;
        }
        throw new MappingException("Unrecognized lazy value [" + keyManyToOne.getLazy().name() + "] specified for key-many-to-one [" + keyManyToOne.getName() + "]", mappingDocument.getOrigin());
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.MANY_TO_ONE;
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
        return XmlElementMetadata.KEY_MANY_TO_ONE;
    }

    @Override
    public String getName() {
        return this.keyManyToOneElement.getName();
    }

    @Override
    public String getXmlNodeName() {
        return null;
    }

    @Override
    public AttributePath getAttributePath() {
        return this.attributePath;
    }

    @Override
    public HibernateTypeSourceImpl getTypeInformation() {
        return this.typeSource;
    }

    @Override
    public String getPropertyAccessorName() {
        return this.keyManyToOneElement.getAccess();
    }

    @Override
    public AttributeRole getAttributeRole() {
        return this.attributeRole;
    }

    @Override
    public List<RelationalValueSource> getRelationalValueSources() {
        return this.valueSources;
    }

    @Override
    public boolean areValuesIncludedInInsertByDefault() {
        return true;
    }

    @Override
    public boolean areValuesIncludedInUpdateByDefault() {
        return false;
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
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
    public boolean areValuesNullableByDefault() {
        return false;
    }

    @Override
    public String getReferencedEntityAttributeName() {
        return null;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    @Override
    public Boolean isEmbedXml() {
        return null;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public ForeignKeyDirection getForeignKeyDirection() {
        return ForeignKeyDirection.TO_PARENT;
    }

    @Override
    public String getCascadeStyleName() {
        return "";
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.keyManyToOneElement.getForeignKey();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return "cascade".equals(this.keyManyToOneElement.getOnDelete().value());
    }

    protected String getClassName() {
        return this.sourceMappingDocument().qualifyClassName(this.keyManyToOneElement.getClazz());
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }
}

