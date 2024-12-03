/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmColumnType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmManyToOneType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSynchronizeType;
import org.hibernate.boot.jaxb.hbm.spi.PluralAttributeInfo;
import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.FetchCharacteristicsPluralAttributeImpl;
import org.hibernate.boot.model.source.internal.hbm.FilterSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeElementSourceBasicImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeElementSourceEmbeddedImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeElementSourceManyToAnyImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeElementSourceManyToManyImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeElementSourceOneToManyImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeKeySourceImpl;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.CollectionIdSource;
import org.hibernate.boot.model.source.spi.FetchCharacteristicsPluralAttribute;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSource;
import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.cfg.NotYetImplementedException;

public abstract class AbstractPluralAttributeSourceImpl
extends AbstractHbmSourceNode
implements PluralAttributeSource,
Helper.InLineViewNameInferrer {
    private static final FilterSource[] NO_FILTER_SOURCES = new FilterSource[0];
    private final PluralAttributeInfo pluralAttributeJaxbMapping;
    private final AttributeSourceContainer container;
    private final AttributeRole attributeRole;
    private final AttributePath attributePath;
    private final HibernateTypeSource typeInformation;
    private final PluralAttributeKeySource keySource;
    private final PluralAttributeElementSource elementSource;
    private final FetchCharacteristicsPluralAttributeImpl fetchCharacteristics;
    private final Caching caching;
    private final FilterSource[] filterSources;
    private final String[] synchronizedTableNames;
    private final ToolingHintContext toolingHintContext;

    protected AbstractPluralAttributeSourceImpl(MappingDocument mappingDocument, PluralAttributeInfo pluralAttributeJaxbMapping, AttributeSourceContainer container) {
        super(mappingDocument);
        String childClass;
        this.pluralAttributeJaxbMapping = pluralAttributeJaxbMapping;
        this.container = container;
        this.attributeRole = container.getAttributeRoleBase().append(pluralAttributeJaxbMapping.getName());
        this.attributePath = container.getAttributePathBase().append(pluralAttributeJaxbMapping.getName());
        Optional<JaxbHbmManyToOneType> jaxbHbmManyToOneTypeOptional = Optional.empty();
        if (pluralAttributeJaxbMapping.isInverse() && pluralAttributeJaxbMapping.getOneToMany() != null && pluralAttributeJaxbMapping.getKey().getPropertyRef() == null && (childClass = pluralAttributeJaxbMapping.getOneToMany().getClazz()) != null) {
            ArrayList<String> keyColumnNames;
            if (pluralAttributeJaxbMapping.getKey().getColumnAttribute() == null) {
                keyColumnNames = new ArrayList(pluralAttributeJaxbMapping.getKey().getColumn().size());
                for (JaxbHbmColumnType jaxbHbmColumnType : pluralAttributeJaxbMapping.getKey().getColumn()) {
                    keyColumnNames.add(jaxbHbmColumnType.getName());
                }
            } else {
                keyColumnNames = new ArrayList<String>(1);
                keyColumnNames.add(pluralAttributeJaxbMapping.getKey().getColumnAttribute());
            }
            jaxbHbmManyToOneTypeOptional = mappingDocument.getDocumentRoot().getClazz().stream().filter(entityType -> childClass.equals(entityType.getName())).flatMap(jaxbHbmRootEntityType -> jaxbHbmRootEntityType.getAttributes().stream()).filter(attribute -> {
                if (attribute instanceof JaxbHbmManyToOneType) {
                    JaxbHbmManyToOneType manyToOneType = (JaxbHbmManyToOneType)attribute;
                    String manyToOneTypeClass = manyToOneType.getClazz();
                    String containerClass = container.getAttributeRoleBase().getFullPath();
                    if (manyToOneTypeClass == null || manyToOneTypeClass.equals(containerClass)) {
                        if (manyToOneType.getColumnAttribute() == null) {
                            List<Serializable> columns = manyToOneType.getColumnOrFormula();
                            if (columns.size() != keyColumnNames.size()) {
                                return false;
                            }
                            for (int i = 0; i < columns.size(); ++i) {
                                Serializable column = columns.get(i);
                                String keyColumn = (String)keyColumnNames.get(i);
                                if (column instanceof JaxbHbmColumnType && ((JaxbHbmColumnType)column).getName().equals(keyColumn)) continue;
                                return false;
                            }
                        } else {
                            return keyColumnNames.size() == 1 && ((String)keyColumnNames.get(0)).equals(manyToOneType.getColumnAttribute());
                        }
                        return true;
                    }
                }
                return false;
            }).map(JaxbHbmManyToOneType.class::cast).findFirst();
        }
        this.keySource = jaxbHbmManyToOneTypeOptional.map(jaxbHbmManyToOneType -> new PluralAttributeKeySourceImpl(this.sourceMappingDocument(), pluralAttributeJaxbMapping.getKey(), (JaxbHbmManyToOneType)jaxbHbmManyToOneType, container)).orElseGet(() -> new PluralAttributeKeySourceImpl(this.sourceMappingDocument(), pluralAttributeJaxbMapping.getKey(), container));
        this.typeInformation = new HibernateTypeSourceImpl(pluralAttributeJaxbMapping.getCollectionType());
        this.caching = Helper.createCaching(pluralAttributeJaxbMapping.getCache());
        this.filterSources = AbstractPluralAttributeSourceImpl.buildFilterSources(mappingDocument, pluralAttributeJaxbMapping);
        this.synchronizedTableNames = AbstractPluralAttributeSourceImpl.extractSynchronizedTableNames(pluralAttributeJaxbMapping);
        this.toolingHintContext = Helper.collectToolingHints(container.getToolingHintContext(), pluralAttributeJaxbMapping);
        this.elementSource = this.interpretElementType();
        this.fetchCharacteristics = FetchCharacteristicsPluralAttributeImpl.interpret(mappingDocument.getMappingDefaults(), pluralAttributeJaxbMapping.getFetch(), pluralAttributeJaxbMapping.getOuterJoin(), pluralAttributeJaxbMapping.getLazy(), pluralAttributeJaxbMapping.getBatchSize());
    }

    private static String[] extractSynchronizedTableNames(PluralAttributeInfo pluralAttributeElement) {
        if (pluralAttributeElement.getSynchronize().isEmpty()) {
            return new String[0];
        }
        String[] names = new String[pluralAttributeElement.getSynchronize().size()];
        int i = 0;
        for (JaxbHbmSynchronizeType jaxbHbmSynchronizeType : pluralAttributeElement.getSynchronize()) {
            names[i++] = jaxbHbmSynchronizeType.getTable();
        }
        return names;
    }

    private static FilterSource[] buildFilterSources(MappingDocument mappingDocument, PluralAttributeInfo pluralAttributeElement) {
        int size = pluralAttributeElement.getFilter().size();
        if (size == 0) {
            return null;
        }
        FilterSource[] results = new FilterSource[size];
        for (int i = 0; i < size; ++i) {
            JaxbHbmFilterType element = pluralAttributeElement.getFilter().get(i);
            results[i] = new FilterSourceImpl(mappingDocument, element);
        }
        return results;
    }

    private PluralAttributeElementSource interpretElementType() {
        if (this.pluralAttributeJaxbMapping.getElement() != null) {
            return new PluralAttributeElementSourceBasicImpl(this.sourceMappingDocument(), this, this.pluralAttributeJaxbMapping.getElement());
        }
        if (this.pluralAttributeJaxbMapping.getCompositeElement() != null) {
            return new PluralAttributeElementSourceEmbeddedImpl(this.sourceMappingDocument(), this, this.pluralAttributeJaxbMapping.getCompositeElement());
        }
        if (this.pluralAttributeJaxbMapping.getOneToMany() != null) {
            return new PluralAttributeElementSourceOneToManyImpl(this.sourceMappingDocument(), this, this.pluralAttributeJaxbMapping.getOneToMany(), this.pluralAttributeJaxbMapping.getCascade());
        }
        if (this.pluralAttributeJaxbMapping.getManyToMany() != null) {
            return new PluralAttributeElementSourceManyToManyImpl(this.sourceMappingDocument(), this, this.pluralAttributeJaxbMapping.getManyToMany());
        }
        if (this.pluralAttributeJaxbMapping.getManyToAny() != null) {
            return new PluralAttributeElementSourceManyToAnyImpl(this.sourceMappingDocument(), this, this.pluralAttributeJaxbMapping.getManyToAny(), this.pluralAttributeJaxbMapping.getCascade());
        }
        throw new MappingException("Unexpected collection element type : " + this.pluralAttributeJaxbMapping.getName(), this.sourceMappingDocument().getOrigin());
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
    public boolean usesJoinTable() {
        switch (this.elementSource.getNature()) {
            case BASIC: 
            case AGGREGATE: 
            case ONE_TO_MANY: {
                return false;
            }
            case MANY_TO_MANY: {
                return true;
            }
            case MANY_TO_ANY: {
                throw new NotYetImplementedException(String.format("%s is not implemented yet.", new Object[]{this.elementSource.getNature()}));
            }
        }
        throw new AssertionFailure(String.format("Unexpected plural attribute element source nature: %s", new Object[]{this.elementSource.getNature()}));
    }

    protected AttributeSourceContainer container() {
        return this.container;
    }

    @Override
    public FilterSource[] getFilterSources() {
        return this.filterSources == null ? NO_FILTER_SOURCES : this.filterSources;
    }

    @Override
    public PluralAttributeKeySource getKeySource() {
        return this.keySource;
    }

    @Override
    public PluralAttributeElementSource getElementSource() {
        return this.elementSource;
    }

    @Override
    public String getCascadeStyleName() {
        return this.pluralAttributeJaxbMapping.getCascade();
    }

    @Override
    public boolean isMutable() {
        return this.pluralAttributeJaxbMapping.isMutable();
    }

    @Override
    public String getMappedBy() {
        return null;
    }

    @Override
    public String inferInLineViewName() {
        return this.getAttributeRole().getFullPath();
    }

    @Override
    public CollectionIdSource getCollectionIdSource() {
        return null;
    }

    @Override
    public TableSpecificationSource getCollectionTableSpecificationSource() {
        return this.pluralAttributeJaxbMapping.getOneToMany() == null ? Helper.createTableSource(this.sourceMappingDocument(), this.pluralAttributeJaxbMapping, this) : null;
    }

    @Override
    public String getCollectionTableComment() {
        return this.pluralAttributeJaxbMapping.getComment();
    }

    @Override
    public String getCollectionTableCheck() {
        return this.pluralAttributeJaxbMapping.getCheck();
    }

    @Override
    public String[] getSynchronizedTableNames() {
        return this.synchronizedTableNames;
    }

    @Override
    public Caching getCaching() {
        return this.caching;
    }

    @Override
    public String getWhere() {
        return this.pluralAttributeJaxbMapping.getWhere();
    }

    @Override
    public String getName() {
        return this.pluralAttributeJaxbMapping.getName();
    }

    @Override
    public boolean isSingular() {
        return false;
    }

    @Override
    public HibernateTypeSource getTypeInformation() {
        return this.typeInformation;
    }

    @Override
    public String getPropertyAccessorName() {
        return this.pluralAttributeJaxbMapping.getAccess();
    }

    @Override
    public boolean isIncludedInOptimisticLocking() {
        return this.pluralAttributeJaxbMapping.isOptimisticLock();
    }

    @Override
    public boolean isInverse() {
        return this.pluralAttributeJaxbMapping.isInverse();
    }

    @Override
    public String getCustomPersisterClassName() {
        return this.pluralAttributeJaxbMapping.getPersister();
    }

    @Override
    public String getCustomLoaderName() {
        return this.pluralAttributeJaxbMapping.getLoader() == null ? null : this.pluralAttributeJaxbMapping.getLoader().getQueryRef();
    }

    @Override
    public CustomSql getCustomSqlInsert() {
        return Helper.buildCustomSql(this.pluralAttributeJaxbMapping.getSqlInsert());
    }

    @Override
    public CustomSql getCustomSqlUpdate() {
        return Helper.buildCustomSql(this.pluralAttributeJaxbMapping.getSqlUpdate());
    }

    @Override
    public CustomSql getCustomSqlDelete() {
        return Helper.buildCustomSql(this.pluralAttributeJaxbMapping.getSqlDelete());
    }

    @Override
    public CustomSql getCustomSqlDeleteAll() {
        return Helper.buildCustomSql(this.pluralAttributeJaxbMapping.getSqlDeleteAll());
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public FetchCharacteristicsPluralAttribute getFetchCharacteristics() {
        return this.fetchCharacteristics;
    }
}

