/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmVersionAttributeType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.RootEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SingularAttributeNature;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.boot.model.source.spi.VersionAttributeSource;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.tuple.GenerationTiming;

class VersionAttributeSourceImpl
extends AbstractHbmSourceNode
implements VersionAttributeSource {
    private final JaxbHbmVersionAttributeType versionElement;
    private final HibernateTypeSourceImpl typeSource;
    private final List<RelationalValueSource> relationalValueSources;
    private final AttributePath attributePath;
    private final AttributeRole attributeRole;
    private final ToolingHintContext toolingHints;

    VersionAttributeSourceImpl(MappingDocument mappingDocument, RootEntitySourceImpl rootEntitySource, JaxbHbmVersionAttributeType versionElement) {
        super(mappingDocument);
        this.versionElement = versionElement;
        this.relationalValueSources = RelationalValueSourceHelper.buildValueSources(mappingDocument, null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return VersionAttributeSourceImpl.this.getSourceType();
            }

            @Override
            public String getSourceName() {
                return VersionAttributeSourceImpl.this.versionElement.getName();
            }

            @Override
            public String getColumnAttribute() {
                return VersionAttributeSourceImpl.this.versionElement.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return VersionAttributeSourceImpl.this.versionElement.getColumn();
            }

            @Override
            public Boolean isNullable() {
                return false;
            }
        });
        this.typeSource = new HibernateTypeSourceImpl(versionElement.getType() == null ? "integer" : versionElement.getType());
        this.attributePath = rootEntitySource.getAttributePathBase().append(this.getName());
        this.attributeRole = rootEntitySource.getAttributeRoleBase().append(this.getName());
        this.toolingHints = Helper.collectToolingHints(rootEntitySource.getToolingHintContext(), versionElement);
    }

    @Override
    public XmlElementMetadata getSourceType() {
        return XmlElementMetadata.VERSION;
    }

    @Override
    public String getName() {
        return this.versionElement.getName();
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
        return this.versionElement.getAccess();
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
        return false;
    }

    @Override
    public String getUnsavedValue() {
        return this.versionElement.getUnsavedValue().value();
    }

    @Override
    public GenerationTiming getGenerationTiming() {
        return this.versionElement.getGenerated();
    }

    @Override
    public Boolean isInsertable() {
        return this.versionElement.isInsert() == null ? Boolean.TRUE : this.versionElement.isInsert();
    }

    @Override
    public Boolean isUpdatable() {
        return true;
    }

    @Override
    public boolean isBytecodeLazy() {
        return false;
    }

    @Override
    public NaturalIdMutability getNaturalIdMutability() {
        return NaturalIdMutability.NOT_NATURAL_ID;
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return false;
    }

    @Override
    public SingularAttributeNature getSingularAttributeNature() {
        return SingularAttributeNature.BASIC;
    }

    @Override
    public boolean isVirtualAttribute() {
        return false;
    }

    @Override
    public boolean isSingular() {
        return true;
    }

    @Override
    public String getXmlNodeName() {
        return this.versionElement.getNode();
    }

    @Override
    public MetadataBuildingContext getBuildingContext() {
        return this.metadataBuildingContext();
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHints;
    }
}

