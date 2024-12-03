/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmListIndexType;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexNature;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.SizeSource;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeSequentialIndexSourceImpl
extends AbstractHbmSourceNode
implements PluralAttributeSequentialIndexSource {
    private final int base;
    private final String xmlNodeName;
    private final HibernateTypeSourceImpl typeSource;
    private final List<RelationalValueSource> valueSources;

    public PluralAttributeSequentialIndexSourceImpl(MappingDocument sourceMappingDocument, final JaxbHbmListIndexType jaxbListIndex) {
        super(sourceMappingDocument);
        this.base = Integer.parseInt(jaxbListIndex.getBase());
        this.xmlNodeName = null;
        this.typeSource = new HibernateTypeSourceImpl("integer");
        this.valueSources = RelationalValueSourceHelper.buildValueSources(sourceMappingDocument, null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){
            final List<JaxbHbmColumnType> columnElements;
            {
                this.columnElements = jaxbListIndex.getColumn() == null ? Collections.emptyList() : Collections.singletonList(jaxbListIndex.getColumn());
            }

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.LIST_INDEX;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return jaxbListIndex.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return this.columnElements;
            }
        });
    }

    public PluralAttributeSequentialIndexSourceImpl(MappingDocument sourceMappingDocument, final JaxbHbmIndexType jaxbIndex) {
        super(sourceMappingDocument);
        this.base = 0;
        this.xmlNodeName = null;
        this.typeSource = StringHelper.isEmpty(jaxbIndex.getType()) ? new HibernateTypeSourceImpl("integer") : new HibernateTypeSourceImpl(jaxbIndex.getType());
        this.valueSources = RelationalValueSourceHelper.buildValueSources(sourceMappingDocument, null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.INDEX;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public String getColumnAttribute() {
                return jaxbIndex.getColumnAttribute();
            }

            @Override
            public SizeSource getSizeSource() {
                return Helper.interpretSizeSource(jaxbIndex.getLength(), (Integer)null, null);
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbIndex.getColumn();
            }
        });
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
    public boolean areValuesNullableByDefault() {
        return false;
    }

    @Override
    public int getBase() {
        return this.base;
    }

    @Override
    public PluralAttributeIndexNature getNature() {
        return PluralAttributeIndexNature.SEQUENTIAL;
    }

    @Override
    public HibernateTypeSourceImpl getTypeInformation() {
        return this.typeSource;
    }

    @Override
    public String getXmlNodeName() {
        return this.xmlNodeName;
    }

    @Override
    public List<RelationalValueSource> getRelationalValueSources() {
        return this.valueSources;
    }
}

