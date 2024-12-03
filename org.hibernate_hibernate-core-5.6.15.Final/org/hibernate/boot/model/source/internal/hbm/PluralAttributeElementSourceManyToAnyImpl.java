/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmAnyValueMappingType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToAnyCollectionElementType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAttributeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AnyDiscriminatorSource;
import org.hibernate.boot.model.source.spi.AnyKeySource;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeElementNature;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToAny;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.spi.MetadataBuildingContext;

public class PluralAttributeElementSourceManyToAnyImpl
implements PluralAttributeElementSourceManyToAny {
    private final String cascade;
    private final AnyDiscriminatorSource discriminatorSource;
    private final AnyKeySource keySource;

    public PluralAttributeElementSourceManyToAnyImpl(final MappingDocument mappingDocument, final AbstractPluralAttributeSourceImpl pluralAttributeSource, final JaxbHbmManyToAnyCollectionElementType jaxbManyToAnyMapping, String cascade) {
        this.cascade = cascade;
        final List<RelationalValueSource> relationalValueSources = RelationalValueSourceHelper.buildValueSources(mappingDocument, null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.MANY_TO_ANY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbManyToAnyMapping.getColumn();
            }
        });
        if (relationalValueSources.size() < 2) {
            throw new MappingException(String.format(Locale.ENGLISH, "<many-to-any /> mapping [%s] needs to specify 2 or more columns", pluralAttributeSource.getAttributeRole().getFullPath()), mappingDocument.getOrigin());
        }
        this.discriminatorSource = new AnyDiscriminatorSource(){
            private final HibernateTypeSource discriminatorTypeSource;
            private final RelationalValueSource discriminatorRelationalValueSource;
            private final Map<String, String> discriminatorValueMapping;
            {
                this.discriminatorTypeSource = new HibernateTypeSourceImpl(jaxbManyToAnyMapping.getMetaType());
                this.discriminatorRelationalValueSource = (RelationalValueSource)relationalValueSources.get(0);
                this.discriminatorValueMapping = new HashMap<String, String>();
                for (JaxbHbmAnyValueMappingType valueMapping : jaxbManyToAnyMapping.getMetaValue()) {
                    this.discriminatorValueMapping.put(valueMapping.getValue(), mappingDocument.qualifyClassName(valueMapping.getClazz()));
                }
            }

            @Override
            public HibernateTypeSource getTypeSource() {
                return this.discriminatorTypeSource;
            }

            @Override
            public RelationalValueSource getRelationalValueSource() {
                return this.discriminatorRelationalValueSource;
            }

            @Override
            public Map<String, String> getValueMappings() {
                return this.discriminatorValueMapping;
            }

            @Override
            public AttributePath getAttributePath() {
                return pluralAttributeSource.getAttributePath();
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return mappingDocument;
            }
        };
        this.keySource = new AnyKeySource(){
            private final HibernateTypeSource fkTypeSource;
            private final List<RelationalValueSource> fkRelationalValueSources;
            {
                this.fkTypeSource = new HibernateTypeSourceImpl(jaxbManyToAnyMapping.getIdType());
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
                return pluralAttributeSource.getAttributePath();
            }

            @Override
            public MetadataBuildingContext getBuildingContext() {
                return mappingDocument;
            }
        };
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
    public PluralAttributeElementNature getNature() {
        return PluralAttributeElementNature.MANY_TO_ANY;
    }
}

