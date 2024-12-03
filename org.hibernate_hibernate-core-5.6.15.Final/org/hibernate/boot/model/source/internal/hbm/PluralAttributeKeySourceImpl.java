/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmKeyType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeKeySourceImpl
extends AbstractHbmSourceNode
implements PluralAttributeKeySource,
RelationalValueSourceContainer {
    private final String explicitFkName;
    private final String referencedPropertyName;
    private final boolean cascadeDeletesAtFkLevel;
    private final boolean nullable;
    private final boolean updateable;
    private final List<RelationalValueSource> valueSources;

    public PluralAttributeKeySourceImpl(MappingDocument mappingDocument, final JaxbHbmKeyType jaxbKey, AttributeSourceContainer container) {
        super(mappingDocument);
        this.explicitFkName = StringHelper.nullIfEmpty(jaxbKey.getForeignKey());
        this.referencedPropertyName = StringHelper.nullIfEmpty(jaxbKey.getPropertyRef());
        this.cascadeDeletesAtFkLevel = jaxbKey.getOnDelete() != null && "cascade".equals(jaxbKey.getOnDelete().value());
        this.nullable = jaxbKey.isNotNull() == null || jaxbKey.isNotNull() == false;
        this.updateable = jaxbKey.isUpdate() == null || jaxbKey.isUpdate() != false;
        this.valueSources = RelationalValueSourceHelper.buildValueSources(this.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.KEY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return StringHelper.nullIfEmpty(jaxbKey.getColumnAttribute());
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbKey.getColumn();
            }
        });
    }

    public PluralAttributeKeySourceImpl(MappingDocument mappingDocument, final JaxbHbmKeyType jaxbKey, JaxbHbmManyToOneType jaxbManyToOne, AttributeSourceContainer container) {
        super(mappingDocument);
        this.explicitFkName = StringHelper.nullIfEmpty(jaxbManyToOne.getForeignKey());
        this.referencedPropertyName = StringHelper.nullIfEmpty(jaxbManyToOne.getPropertyRef());
        this.cascadeDeletesAtFkLevel = jaxbKey.getOnDelete() == null ? jaxbManyToOne.getOnDelete() != null && "cascade".equals(jaxbManyToOne.getOnDelete().value()) : "cascade".equals(jaxbKey.getOnDelete().value());
        this.nullable = jaxbKey.isNotNull() == null ? jaxbManyToOne.isNotNull() == null || jaxbManyToOne.isNotNull() == false : jaxbKey.isNotNull() == false;
        this.updateable = jaxbKey.isUpdate() == null ? jaxbManyToOne.isUpdate() : jaxbKey.isUpdate().booleanValue();
        this.valueSources = RelationalValueSourceHelper.buildValueSources(this.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.KEY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return StringHelper.nullIfEmpty(jaxbKey.getColumnAttribute());
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbKey.getColumn();
            }
        });
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.explicitFkName;
    }

    @Override
    public boolean createForeignKeyConstraint() {
        return true;
    }

    @Override
    public String getReferencedPropertyName() {
        return this.referencedPropertyName;
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return this.cascadeDeletesAtFkLevel;
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
        return this.updateable;
    }

    @Override
    public boolean areValuesNullableByDefault() {
        return this.nullable;
    }
}

