/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.List;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToManyCollectionElementType;
import org.hibernate.boot.model.source.internal.hbm.AbstractPluralAssociationElementSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FetchCharacteristicsSingularAssociationImpl;
import org.hibernate.boot.model.source.internal.hbm.FilterSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.RelationalValueSourceHelper;
import org.hibernate.boot.model.source.internal.hbm.XmlElementMetadata;
import org.hibernate.boot.model.source.spi.FetchCharacteristics;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.PluralAttributeElementNature;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToMany;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.internal.util.StringHelper;

public class PluralAttributeElementSourceManyToManyImpl
extends AbstractPluralAssociationElementSourceImpl
implements PluralAttributeElementSourceManyToMany {
    private static final FilterSource[] NO_FILTER_SOURCES = new FilterSource[0];
    private final JaxbHbmManyToManyCollectionElementType jaxbManyToManyElement;
    private final String referencedEntityName;
    private final FetchCharacteristics fetchCharacteristics;
    private final List<RelationalValueSource> valueSources;
    private final FilterSource[] filterSources;

    public PluralAttributeElementSourceManyToManyImpl(MappingDocument mappingDocument, PluralAttributeSource pluralAttributeSource, final JaxbHbmManyToManyCollectionElementType jaxbManyToManyElement) {
        super(mappingDocument, pluralAttributeSource);
        this.jaxbManyToManyElement = jaxbManyToManyElement;
        this.referencedEntityName = StringHelper.isNotEmpty(jaxbManyToManyElement.getEntityName()) ? jaxbManyToManyElement.getEntityName() : mappingDocument.qualifyClassName(jaxbManyToManyElement.getClazz());
        this.fetchCharacteristics = FetchCharacteristicsSingularAssociationImpl.interpretManyToManyElement(mappingDocument.getMappingDefaults(), jaxbManyToManyElement.getFetch(), jaxbManyToManyElement.getOuterJoin(), jaxbManyToManyElement.getLazy());
        this.valueSources = RelationalValueSourceHelper.buildValueSources(this.sourceMappingDocument(), null, new RelationalValueSourceHelper.AbstractColumnsAndFormulasSource(){

            @Override
            public XmlElementMetadata getSourceType() {
                return XmlElementMetadata.MANY_TO_MANY;
            }

            @Override
            public String getSourceName() {
                return null;
            }

            @Override
            public boolean isUnique() {
                return jaxbManyToManyElement.isUnique();
            }

            @Override
            public String getFormulaAttribute() {
                return jaxbManyToManyElement.getFormulaAttribute();
            }

            @Override
            public String getColumnAttribute() {
                return jaxbManyToManyElement.getColumnAttribute();
            }

            @Override
            public List getColumnOrFormulaElements() {
                return jaxbManyToManyElement.getColumnOrFormula();
            }
        });
        this.filterSources = this.buildFilterSources();
    }

    private FilterSource[] buildFilterSources() {
        int size = this.jaxbManyToManyElement.getFilter().size();
        if (size == 0) {
            return NO_FILTER_SOURCES;
        }
        FilterSource[] results = new FilterSource[size];
        for (int i = 0; i < size; ++i) {
            JaxbHbmFilterType element = this.jaxbManyToManyElement.getFilter().get(i);
            results[i] = new FilterSourceImpl(this.sourceMappingDocument(), element);
        }
        return results;
    }

    @Override
    public PluralAttributeElementNature getNature() {
        return PluralAttributeElementNature.MANY_TO_MANY;
    }

    @Override
    public String getReferencedEntityName() {
        return this.referencedEntityName;
    }

    @Override
    public FilterSource[] getFilterSources() {
        return this.filterSources;
    }

    @Override
    public String getReferencedEntityAttributeName() {
        return this.jaxbManyToManyElement.getPropertyRef();
    }

    @Override
    public List<RelationalValueSource> getRelationalValueSources() {
        return this.valueSources;
    }

    @Override
    public boolean isIgnoreNotFound() {
        return this.jaxbManyToManyElement.getNotFound() != null && "ignore".equalsIgnoreCase(this.jaxbManyToManyElement.getNotFound().value());
    }

    @Override
    public String getExplicitForeignKeyName() {
        return this.jaxbManyToManyElement.getForeignKey();
    }

    @Override
    public boolean isCascadeDeleteEnabled() {
        return false;
    }

    @Override
    public boolean isUnique() {
        return this.jaxbManyToManyElement.isUnique();
    }

    @Override
    public String getWhere() {
        return this.jaxbManyToManyElement.getWhere();
    }

    @Override
    public FetchCharacteristics getFetchCharacteristics() {
        return this.fetchCharacteristics;
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
    public boolean isOrdered() {
        return StringHelper.isNotEmpty(this.getOrder());
    }

    @Override
    public String getOrder() {
        return this.jaxbManyToManyElement.getOrderBy();
    }

    @Override
    public boolean createForeignKeyConstraint() {
        return true;
    }
}

