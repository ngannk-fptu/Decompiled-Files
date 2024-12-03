/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.EntityMode;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.hbm.spi.EntityInfo;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmEntityBaseDefinition;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFetchProfileType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmFilterType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmRootEntityType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmSecondaryTableType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmTuplizerType;
import org.hibernate.boot.jaxb.hbm.spi.SecondaryTableContainer;
import org.hibernate.boot.model.CustomSql;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.source.internal.hbm.AbstractHbmSourceNode;
import org.hibernate.boot.model.source.internal.hbm.AttributesHelper;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityNamingSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.FetchProfileBinder;
import org.hibernate.boot.model.source.internal.hbm.FilterSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.SecondaryTableSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.SubclassEntitySourceImpl;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.AttributeSourceContainer;
import org.hibernate.boot.model.source.spi.EntityHierarchySource;
import org.hibernate.boot.model.source.spi.EntityNamingSource;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
import org.hibernate.boot.model.source.spi.JpaCallbackSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.SecondaryTableSource;
import org.hibernate.boot.model.source.spi.SubclassEntitySource;
import org.hibernate.boot.model.source.spi.ToolingHintContext;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;

public abstract class AbstractEntitySourceImpl
extends AbstractHbmSourceNode
implements EntitySource,
Helper.InLineViewNameInferrer {
    private static final FilterSource[] NO_FILTER_SOURCES = new FilterSource[0];
    private final JaxbHbmEntityBaseDefinition jaxbEntityMapping;
    private final EntityNamingSource entityNamingSource;
    private final AttributeRole attributeRoleBase;
    private final AttributePath attributePathBase;
    private List<IdentifiableTypeSource> subclassEntitySources = new ArrayList<IdentifiableTypeSource>();
    private int inLineViewCount = 0;
    private List<AttributeSource> attributeSources;
    private Map<String, SecondaryTableSource> secondaryTableMap;
    private final FilterSource[] filterSources;
    private final Map<EntityMode, String> tuplizerClassMap;
    private final ToolingHintContext toolingHintContext;
    private EntityHierarchySourceImpl entityHierarchy;

    protected AbstractEntitySourceImpl(MappingDocument sourceMappingDocument, JaxbHbmEntityBaseDefinition jaxbEntityMapping) {
        super(sourceMappingDocument);
        this.jaxbEntityMapping = jaxbEntityMapping;
        this.entityNamingSource = AbstractEntitySourceImpl.extractEntityNamingSource(sourceMappingDocument, jaxbEntityMapping);
        this.attributePathBase = new AttributePath();
        this.attributeRoleBase = new AttributeRole(this.entityNamingSource.getEntityName());
        this.tuplizerClassMap = AbstractEntitySourceImpl.extractTuplizers(jaxbEntityMapping);
        this.filterSources = this.buildFilterSources();
        for (JaxbHbmFetchProfileType jaxbFetchProfile : jaxbEntityMapping.getFetchProfile()) {
            FetchProfileBinder.processFetchProfile(sourceMappingDocument, jaxbFetchProfile, this.entityNamingSource.getClassName() != null ? this.entityNamingSource.getClassName() : this.entityNamingSource.getEntityName());
        }
        this.toolingHintContext = Helper.collectToolingHints(sourceMappingDocument.getToolingHintContext(), jaxbEntityMapping);
    }

    public static EntityNamingSourceImpl extractEntityNamingSource(MappingDocument sourceMappingDocument, EntityInfo jaxbEntityMapping) {
        String jpaEntityName;
        String entityName;
        String className = sourceMappingDocument.qualifyClassName(jaxbEntityMapping.getName());
        if (StringHelper.isNotEmpty(jaxbEntityMapping.getEntityName())) {
            entityName = jaxbEntityMapping.getEntityName();
            jpaEntityName = jaxbEntityMapping.getEntityName();
        } else {
            entityName = className;
            jpaEntityName = StringHelper.unqualify(className);
        }
        return new EntityNamingSourceImpl(entityName, className, jpaEntityName);
    }

    private static Map<EntityMode, String> extractTuplizers(JaxbHbmEntityBaseDefinition entityElement) {
        if (entityElement.getTuplizer() == null) {
            return Collections.emptyMap();
        }
        HashMap<EntityMode, String> tuplizers = new HashMap<EntityMode, String>();
        for (JaxbHbmTuplizerType tuplizerElement : entityElement.getTuplizer()) {
            tuplizers.put(tuplizerElement.getEntityMode(), tuplizerElement.getClazz());
        }
        return tuplizers;
    }

    private FilterSource[] buildFilterSources() {
        if (JaxbHbmRootEntityType.class.isInstance(this.jaxbEntityMapping())) {
            JaxbHbmRootEntityType jaxbClassElement = (JaxbHbmRootEntityType)this.jaxbEntityMapping();
            int size = jaxbClassElement.getFilter().size();
            if (size == 0) {
                return NO_FILTER_SOURCES;
            }
            FilterSource[] results = new FilterSource[size];
            for (int i = 0; i < size; ++i) {
                JaxbHbmFilterType element = jaxbClassElement.getFilter().get(i);
                results[i] = new FilterSourceImpl(this.sourceMappingDocument(), element);
            }
            return results;
        }
        return NO_FILTER_SOURCES;
    }

    @Override
    public String getXmlNodeName() {
        return this.jaxbEntityMapping.getNode();
    }

    @Override
    public LocalMetadataBuildingContext getLocalMetadataBuildingContext() {
        return super.metadataBuildingContext();
    }

    @Override
    public String getTypeName() {
        return this.entityNamingSource.getTypeName();
    }

    @Override
    public AttributePath getAttributePathBase() {
        return this.attributePathBase;
    }

    @Override
    public AttributeRole getAttributeRoleBase() {
        return this.attributeRoleBase;
    }

    @Override
    public Collection<IdentifiableTypeSource> getSubTypes() {
        return this.subclassEntitySources;
    }

    @Override
    public FilterSource[] getFilterSources() {
        return this.filterSources;
    }

    @Override
    public String inferInLineViewName() {
        return this.entityNamingSource.getEntityName() + '#' + ++this.inLineViewCount;
    }

    protected void afterInstantiation() {
        this.attributeSources = this.buildAttributeSources();
        this.secondaryTableMap = this.buildSecondaryTableMap();
    }

    protected List<AttributeSource> buildAttributeSources() {
        final ArrayList<AttributeSource> attributeSources = new ArrayList<AttributeSource>();
        AttributesHelper.Callback attributeBuildingCallback = new AttributesHelper.Callback(){

            @Override
            public AttributeSourceContainer getAttributeSourceContainer() {
                return AbstractEntitySourceImpl.this;
            }

            @Override
            public void addAttributeSource(AttributeSource attributeSource) {
                attributeSources.add(attributeSource);
            }
        };
        this.buildAttributeSources(attributeBuildingCallback);
        return attributeSources;
    }

    protected void buildAttributeSources(AttributesHelper.Callback attributeBuildingCallback) {
        AttributesHelper.processAttributes(this.sourceMappingDocument(), attributeBuildingCallback, this.jaxbEntityMapping.getAttributes(), null, NaturalIdMutability.NOT_NATURAL_ID);
    }

    private Map<String, SecondaryTableSource> buildSecondaryTableMap() {
        if (!SecondaryTableContainer.class.isInstance(this.jaxbEntityMapping)) {
            return Collections.emptyMap();
        }
        HashMap<String, SecondaryTableSource> secondaryTableSourcesMap = new HashMap<String, SecondaryTableSource>();
        for (JaxbHbmSecondaryTableType joinElement : ((SecondaryTableContainer)((Object)this.jaxbEntityMapping)).getJoin()) {
            SecondaryTableSourceImpl secondaryTableSource = new SecondaryTableSourceImpl(this.sourceMappingDocument(), joinElement, this.getEntityNamingSource(), this);
            String logicalTableName = secondaryTableSource.getLogicalTableNameForContainedColumns();
            secondaryTableSourcesMap.put(logicalTableName, secondaryTableSource);
            AttributesHelper.processAttributes(this.sourceMappingDocument(), new AttributesHelper.Callback(){

                @Override
                public AttributeSourceContainer getAttributeSourceContainer() {
                    return AbstractEntitySourceImpl.this;
                }

                @Override
                public void addAttributeSource(AttributeSource attributeSource) {
                    AbstractEntitySourceImpl.this.attributeSources.add(attributeSource);
                }
            }, joinElement.getAttributes(), logicalTableName, NaturalIdMutability.NOT_NATURAL_ID);
        }
        return secondaryTableSourcesMap;
    }

    protected JaxbHbmEntityBaseDefinition jaxbEntityMapping() {
        return this.jaxbEntityMapping;
    }

    @Override
    public Origin getOrigin() {
        return this.origin();
    }

    @Override
    public EntityNamingSource getEntityNamingSource() {
        return this.entityNamingSource;
    }

    @Override
    public Boolean isAbstract() {
        return this.jaxbEntityMapping().isAbstract();
    }

    @Override
    public boolean isLazy() {
        if (this.jaxbEntityMapping.isLazy() == null) {
            return this.metadataBuildingContext().getMappingDefaults().areEntitiesImplicitlyLazy();
        }
        return this.jaxbEntityMapping().isLazy();
    }

    @Override
    public String getProxy() {
        return this.jaxbEntityMapping.getProxy();
    }

    @Override
    public int getBatchSize() {
        return this.jaxbEntityMapping.getBatchSize();
    }

    @Override
    public boolean isDynamicInsert() {
        return this.jaxbEntityMapping.isDynamicInsert();
    }

    @Override
    public boolean isDynamicUpdate() {
        return this.jaxbEntityMapping.isDynamicUpdate();
    }

    @Override
    public boolean isSelectBeforeUpdate() {
        return this.jaxbEntityMapping.isSelectBeforeUpdate();
    }

    protected EntityMode determineEntityMode() {
        return StringHelper.isNotEmpty(this.entityNamingSource.getClassName()) ? EntityMode.POJO : EntityMode.MAP;
    }

    @Override
    public Map<EntityMode, String> getTuplizerClassMap() {
        return this.tuplizerClassMap;
    }

    @Override
    public String getCustomPersisterClassName() {
        return this.metadataBuildingContext().qualifyClassName(this.jaxbEntityMapping.getPersister());
    }

    @Override
    public String getCustomLoaderName() {
        return this.jaxbEntityMapping.getLoader() != null ? this.jaxbEntityMapping.getLoader().getQueryRef() : null;
    }

    @Override
    public CustomSql getCustomSqlInsert() {
        return Helper.buildCustomSql(this.jaxbEntityMapping.getSqlInsert());
    }

    @Override
    public CustomSql getCustomSqlUpdate() {
        return Helper.buildCustomSql(this.jaxbEntityMapping.getSqlUpdate());
    }

    @Override
    public CustomSql getCustomSqlDelete() {
        return Helper.buildCustomSql(this.jaxbEntityMapping.getSqlDelete());
    }

    @Override
    public String[] getSynchronizedTableNames() {
        if (CollectionHelper.isEmpty(this.jaxbEntityMapping.getSynchronize())) {
            return StringHelper.EMPTY_STRINGS;
        }
        int size = this.jaxbEntityMapping.getSynchronize().size();
        String[] synchronizedTableNames = new String[size];
        for (int i = 0; i < size; ++i) {
            synchronizedTableNames[i] = this.jaxbEntityMapping.getSynchronize().get(i).getTable();
        }
        return synchronizedTableNames;
    }

    @Override
    public ToolingHintContext getToolingHintContext() {
        return this.toolingHintContext;
    }

    @Override
    public List<AttributeSource> attributeSources() {
        return this.attributeSources;
    }

    public void injectHierarchy(EntityHierarchySourceImpl entityHierarchy) {
        this.entityHierarchy = entityHierarchy;
    }

    @Override
    public EntityHierarchySource getHierarchy() {
        return this.entityHierarchy;
    }

    void add(SubclassEntitySource subclassEntitySource) {
        this.add((SubclassEntitySourceImpl)subclassEntitySource);
    }

    void add(SubclassEntitySourceImpl subclassEntitySource) {
        subclassEntitySource.injectHierarchy(this.entityHierarchy);
        this.entityHierarchy.processSubclass(subclassEntitySource);
        this.subclassEntitySources.add(subclassEntitySource);
    }

    @Override
    public Map<String, SecondaryTableSource> getSecondaryTableMap() {
        return this.secondaryTableMap;
    }

    @Override
    public List<JpaCallbackSource> getJpaCallbackClasses() {
        return Collections.emptyList();
    }

    @Override
    public List<JaxbHbmNamedQueryType> getNamedQueries() {
        return this.jaxbEntityMapping.getQuery();
    }

    @Override
    public List<JaxbHbmNamedNativeQueryType> getNamedNativeQueries() {
        return this.jaxbEntityMapping.getSqlQuery();
    }

    @Override
    public TruthValue quoteIdentifiersLocalToEntity() {
        return TruthValue.UNKNOWN;
    }
}

