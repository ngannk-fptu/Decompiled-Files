/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmIndexManyToAnyType;
import org.hibernate.boot.model.JavaTypeDescriptor;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceMapImpl;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.AnyDiscriminatorSource;
import org.hibernate.boot.model.source.spi.AnyKeySource;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeIndexNature;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeyManyToAnySource;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.spi.MetadataBuildingContext;

public class PluralAttributeMapKeyManyToAnySourceImpl
implements PluralAttributeMapKeyManyToAnySource {
    private static final HibernateTypeSource UNKNOWN = new HibernateTypeSource(){

        @Override
        public String getName() {
            return null;
        }

        @Override
        public Map<String, String> getParameters() {
            return null;
        }

        @Override
        public JavaTypeDescriptor getJavaType() {
            return null;
        }
    };
    private final AnyDiscriminatorSource discriminatorSource;
    private final AnyKeySource keySource;

    public PluralAttributeMapKeyManyToAnySourceImpl(final MappingDocument mappingDocument, final PluralAttributeSourceMapImpl pluralAttributeSource, final JaxbHbmIndexManyToAnyType jaxbMapKeyManyToAnyMapping) {
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
                return jaxbMapKeyManyToAnyMapping.getColumn();
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
                this.discriminatorTypeSource = new HibernateTypeSourceImpl(jaxbMapKeyManyToAnyMapping.getMetaType());
                this.discriminatorRelationalValueSource = (RelationalValueSource)relationalValueSources.get(0);
                this.discriminatorValueMapping = Collections.emptyMap();
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
                this.fkTypeSource = new HibernateTypeSourceImpl(jaxbMapKeyManyToAnyMapping.getIdType());
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
    public PluralAttributeMapKeySource.Nature getMapKeyNature() {
        return PluralAttributeMapKeySource.Nature.ANY;
    }

    @Override
    public boolean isReferencedEntityAttribute() {
        return false;
    }

    @Override
    public PluralAttributeIndexNature getNature() {
        return PluralAttributeIndexNature.MANY_TO_ANY;
    }

    @Override
    public HibernateTypeSource getTypeInformation() {
        return UNKNOWN;
    }

    @Override
    public String getXmlNodeName() {
        return null;
    }
}

