/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmBasicCollectionElementType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.PluralAttributeElementNature;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceBasic;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.boot.spi.MetadataBuildingContext;

public class PluralAttributeElementSourceBasicImpl
extends AbstractHbmSourceNode
implements PluralAttributeElementSourceBasic,
RelationalValueSourceContainer {
    private final PluralAttributeSource pluralAttributeSource;
    private final HibernateTypeSourceImpl typeSource;
    private final List<RelationalValueSource> valueSources;

    public PluralAttributeElementSourceBasicImpl(MappingDocument sourceMappingDocument, PluralAttributeSource pluralAttributeSource, final JaxbHbmBasicCollectionElementType jaxbElement) {
        super(sourceMappingDocument);
        this.pluralAttributeSource = pluralAttributeSource;
        this.typeSource = new HibernateTypeSourceImpl(jaxbElement);
        this.valueSources = RelationalValueSourceHelper.buildValueSources(this.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.ELEMENT;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return jaxbElement.getColumnAttribute();
            }

            @Override
            public String getFormulaAttribute() {
                return jaxbElement.getFormulaAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbElement.getColumnOrFormula();
            }

            @Override
            public Boolean isNullable() {
                return !jaxbElement.isNotNull();
            }

            @Override
            public boolean isUnique() {
                return jaxbElement.isUnique();
            }

            @Override
            public SizeSource getSizeSource() {
                return Helper.interpretSizeSource(jaxbElement.getLength(), jaxbElement.getScale(), jaxbElement.getPrecision());
            }
        });
    }

    @Override
    public PluralAttributeElementNature getNature() {
        return PluralAttributeElementNature.BASIC;
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
        return true;
    }

    @Override
    public boolean areValuesNullableByDefault() {
        return true;
    }

    @Override
    public HibernateTypeSourceImpl getExplicitHibernateTypeSource() {
        return this.typeSource;
    }

    @Override
    public AttributePath getAttributePath() {
        return this.pluralAttributeSource.getAttributePath();
    }

    @Override
    public boolean isCollectionElement() {
        return true;
    }

    @Override
    public MetadataBuildingContext getBuildingContext() {
        return this.metadataBuildingContext();
    }
}

