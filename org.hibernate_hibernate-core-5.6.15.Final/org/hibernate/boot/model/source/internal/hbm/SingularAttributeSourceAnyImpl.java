/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyAssociationType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyValueMappingType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AnyDiscriminatorSource;
import org.hibernate.boot.model.source.spi.AnyKeySource;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceAny;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.tuple.GenerationTiming;

public class SingularAttributeSourceAnyImpl
extends AbstractHbmSourceNode
implements SingularAttributeSourceAny {
    private final JaxbHbmAnyAssociationType jaxbAnyMapping;
    private final NaturalIdMutability naturalIdMutability;
    private final AttributePath attributePath;
    private final AttributeRole attributeRole;
    private final HibernateTypeSource attributeTypeSource = new HibernateTypeSourceImpl((String)null);
    private final AnyDiscriminatorSource discriminatorSource;
    private final AnyKeySource keySource;
    private final ToolingHintContext toolingHintContext;

    public SingularAttributeSourceAnyImpl(final MappingDocument sourceMappingDocument, AttributeSourceContainer container, final JaxbHbmAnyAssociationType jaxbAnyMapping, String logicalTableName, NaturalIdMutability naturalIdMutability) {
        super(sourceMappingDocument);
        this.jaxbAnyMapping = jaxbAnyMapping;
        this.naturalIdMutability = naturalIdMutability;
        this.attributePath = container.getAttributePathBase().append(jaxbAnyMapping.getName());
        this.attributeRole = container.getAttributeRoleBase().append(jaxbAnyMapping.getName());
        final List<RelationalValueSource> relationalValueSources = RelationalValueSourceHelper.buildValueSources(sourceMappingDocument, logicalTableName, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.ANY;
            }

            @Override
            public String getSourceName() {
                return jaxbAnyMapping.getName();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbAnyMapping.getColumn();
            }
        });
        if (relationalValueSources.size() < 2) {
            throw new MappingException(String.format(Locale.ENGLISH, "<any name=\"%s\" /> mapping needs to specify 2 or more columns", jaxbAnyMapping.getName()), this.origin());
        }
        this.discriminatorSource = new AnyDiscriminatorSource(){
            private final HibernateTypeSource typeSource;
            private final RelationalValueSource relationalValueSource;
            private final Map<String, String> valueMappings;
            {
                this.typeSource = new HibernateTypeSourceImpl(jaxbAnyMapping.getMetaType());
                this.relationalValueSource = (RelationalValueSource)relationalValueSources.get(0);
                this.valueMappings = new HashMap<String, String>();
                for (JaxbHbmAnyValueMappingType valueMapping : jaxbAnyMapping.getMetaValue()) {
                    this.valueMappings.put(valueMapping.getValue(), sourceMappingDocument.qualifyClassName(valueMapping.getClazz()));
                }
            }

            @Override
            public HibernateTypeSource getTypeSource() {
                return this.typeSource;
            }

            @Override
            public RelationalValueSource getRelationalValueSource() {
                return this.relationalValueSource;
            }

            @Override
            public Map<String, String> getValueMappings() {
                return this.valueMappings;
            }

            @Override
            public AttributePath getAttributePath() {
                return SingularAttributeSourceAnyImpl.this.attributePath;
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return sourceMappingDocument;
            }
        };
        this.keySource = new AnyKeySource(){
            private final HibernateTypeSource fkTypeSource;
            private final List<RelationalValueSource> fkRelationalValueSources;
            {
                this.fkTypeSource = new HibernateTypeSourceImpl(jaxbAnyMapping.getIdType());
                this.fkRelationalValueSources = relationalValueSources.subList(1, relationalValueSources.size());
            }

            @Override
            public HibernateTypeSource getTypeSource() {
                return this.fkTypeSource;
            }

            @Override
            public List<RelationalValueSource> getRelationalValueSources() {
                return this.fkRelationalValueSources;
            }

            @Override
            public AttributePath getAttributePath() {
                return SingularAttributeSourceAnyImpl.this.attributePath;
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return sourceMappingDocument;
            }
        };
        this.toolingHintContext = Helper.collectToolingHints(sourceMappingDocument.getToolingHintContext(), jaxbAnyMapping);
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.ANY;
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.ANY;
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public String getName() {
        return this.jaxbAnyMapping.getName();
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbAnyMapping.getNode();
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
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return GenerationTiming.NEVER;
    }

    @Override
    public Boolean isInsertable() {
        return this.jaxbAnyMapping.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return this.jaxbAnyMapping.isUpdate();
    }

    @Override
    public boolean isBytecodeLazy() {
        return this.jaxbAnyMapping.isLazy();
    }

    @Override
    public NaturalIdMutability getNaturalIdMutability() {
        return this.naturalIdMutability;
    }

    @Override
    public HibernateTypeSource getTypeInformation() {
        return this.attributeTypeSource;
    }

    @Override
    public String getPropertyAccessorName() {
        return this.jaxbAnyMapping.getAccess();
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return this.jaxbAnyMapping.isOptimisticLock();
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public AnyDiscriminatorSource getDiscriminatorSource() {
        return this.discriminatorSource;
    }

    @Override
    public AnyKeySource getKeySource() {
        return this.keySource;
    }

    @Override
    public String getCascadeStyleName() {
        return this.jaxbAnyMapping.getCascade();
    }

    @Override
    public boolean isLazy() {
        return this.isBytecodeLazy();
    }
}

