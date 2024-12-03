/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.FetchMode;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.jaxb.Origin;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedNativeQueryType;
import org.hibernate.boot.jaxb.hbm.spi.JaxbHbmNamedQueryType;
import org.hibernate.boot.model.Caching;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TruthValue;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitBasicColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitCollectionTableNameSource;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.ImplicitIdentifierColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitJoinColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitMapKeyColumnNameSource;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
import org.hibernate.boot.model.naming.ObjectNameNormalizer;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
import org.hibernate.boot.model.source.internal.hbm.AbstractEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityHierarchySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.EntityNamingSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.Helper;
import org.hibernate.boot.model.source.internal.hbm.HibernateTypeSourceImpl;
import org.hibernate.boot.model.source.internal.hbm.IndexedPluralAttributeSource;
import org.hibernate.boot.model.source.internal.hbm.JoinedSubclassEntitySourceImpl;
import org.hibernate.boot.model.source.internal.hbm.MappingDocument;
import org.hibernate.boot.model.source.internal.hbm.NamedQueryBinder;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceArrayImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceBagImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceIdBagImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceListImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceMapImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourcePrimitiveArrayImpl;
import org.hibernate.boot.model.source.internal.hbm.PluralAttributeSourceSetImpl;
import org.hibernate.boot.model.source.internal.hbm.RelationalObjectBinder;
import org.hibernate.boot.model.source.internal.hbm.SubclassEntitySourceImpl;
import org.hibernate.boot.model.source.spi.AnyMappingSource;
import org.hibernate.boot.model.source.spi.AttributePath;
import org.hibernate.boot.model.source.spi.AttributeRole;
import org.hibernate.boot.model.source.spi.AttributeSource;
import org.hibernate.boot.model.source.spi.CascadeStyleSource;
import org.hibernate.boot.model.source.spi.CollectionIdSource;
import org.hibernate.boot.model.source.spi.ColumnSource;
import org.hibernate.boot.model.source.spi.CompositeIdentifierSource;
import org.hibernate.boot.model.source.spi.EmbeddableSource;
import org.hibernate.boot.model.source.spi.EntitySource;
import org.hibernate.boot.model.source.spi.FilterSource;
import org.hibernate.boot.model.source.spi.HibernateTypeSource;
import org.hibernate.boot.model.source.spi.IdentifiableTypeSource;
import org.hibernate.boot.model.source.spi.IdentifierSourceAggregatedComposite;
import org.hibernate.boot.model.source.spi.IdentifierSourceNonAggregatedComposite;
import org.hibernate.boot.model.source.spi.IdentifierSourceSimple;
import org.hibernate.boot.model.source.spi.InLineViewSource;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.model.source.spi.NaturalIdMutability;
import org.hibernate.boot.model.source.spi.Orderable;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceBasic;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceEmbedded;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToAny;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceManyToMany;
import org.hibernate.boot.model.source.spi.PluralAttributeElementSourceOneToMany;
import org.hibernate.boot.model.source.spi.PluralAttributeKeySource;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeyManyToAnySource;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeyManyToManySource;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySourceBasic;
import org.hibernate.boot.model.source.spi.PluralAttributeMapKeySourceEmbedded;
import org.hibernate.boot.model.source.spi.PluralAttributeSequentialIndexSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSource;
import org.hibernate.boot.model.source.spi.PluralAttributeSourceArray;
import org.hibernate.boot.model.source.spi.RelationalValueSource;
import org.hibernate.boot.model.source.spi.RelationalValueSourceContainer;
import org.hibernate.boot.model.source.spi.SecondaryTableSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSource;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceAny;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceBasic;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceEmbedded;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceManyToOne;
import org.hibernate.boot.model.source.spi.SingularAttributeSourceOneToOne;
import org.hibernate.boot.model.source.spi.Sortable;
import org.hibernate.boot.model.source.spi.TableSource;
import org.hibernate.boot.model.source.spi.TableSpecificationSource;
import org.hibernate.boot.model.source.spi.VersionAttributeSource;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.cfg.SecondPass;
import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Array;
import org.hibernate.mapping.AttributeContainer;
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.Bag;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DenormalizedTable;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.IdentifierBag;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.IndexBackref;
import org.hibernate.mapping.IndexedCollection;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.JoinedSubclass;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.List;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Map;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.OneToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.PrimitiveArray;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.Set;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.SyntheticProperty;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UnionSubclass;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.mapping.Value;
import org.hibernate.tuple.GeneratedValueGeneration;
import org.hibernate.tuple.GenerationTiming;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.BasicType;
import org.hibernate.type.BlobType;
import org.hibernate.type.ClobType;
import org.hibernate.type.DiscriminatorType;
import org.hibernate.type.ForeignKeyDirection;
import org.hibernate.type.NClobType;
import org.hibernate.type.TypeResolver;

public class ModelBinder {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(ModelBinder.class);
    private final MetadataBuildingContext metadataBuildingContext;
    private final Database database;
    private final ObjectNameNormalizer objectNameNormalizer;
    private final ImplicitNamingStrategy implicitNamingStrategy;
    private final RelationalObjectBinder relationalObjectBinder;
    private static final String ID_MAPPER_PATH_PART = "<_identifierMapper>";

    public static ModelBinder prepare(MetadataBuildingContext context) {
        return new ModelBinder(context);
    }

    public ModelBinder(final MetadataBuildingContext context) {
        this.metadataBuildingContext = context;
        this.database = context.getMetadataCollector().getDatabase();
        this.objectNameNormalizer = new ObjectNameNormalizer(){

            @Override
            protected MetadataBuildingContext getBuildingContext() {
                return context;
            }
        };
        this.implicitNamingStrategy = context.getBuildingOptions().getImplicitNamingStrategy();
        this.relationalObjectBinder = new RelationalObjectBinder(context);
    }

    public void finishUp(MetadataBuildingContext context) {
    }

    public void bindEntityHierarchy(EntityHierarchySourceImpl hierarchySource) {
        RootClass rootEntityDescriptor = new RootClass(this.metadataBuildingContext);
        this.bindRootEntity(hierarchySource, rootEntityDescriptor);
        hierarchySource.getRoot().getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding(rootEntityDescriptor);
        switch (hierarchySource.getHierarchyInheritanceType()) {
            case NO_INHERITANCE: {
                break;
            }
            case DISCRIMINATED: {
                this.bindDiscriminatorSubclassEntities(hierarchySource.getRoot(), rootEntityDescriptor);
                break;
            }
            case JOINED: {
                this.bindJoinedSubclassEntities(hierarchySource.getRoot(), rootEntityDescriptor);
                break;
            }
            case UNION: {
                this.bindUnionSubclassEntities(hierarchySource.getRoot(), rootEntityDescriptor);
            }
        }
    }

    private void bindRootEntity(EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        MappingDocument mappingDocument = hierarchySource.getRoot().sourceMappingDocument();
        this.bindBasicEntityValues(mappingDocument, hierarchySource.getRoot(), rootEntityDescriptor);
        Table primaryTable = this.bindEntityTableSpecification(mappingDocument, hierarchySource.getRoot().getPrimaryTable(), null, hierarchySource.getRoot(), rootEntityDescriptor);
        rootEntityDescriptor.setTable(primaryTable);
        if (log.isDebugEnabled()) {
            log.debugf("Mapping class: %s -> %s", rootEntityDescriptor.getEntityName(), primaryTable.getName());
        }
        rootEntityDescriptor.setOptimisticLockStyle(hierarchySource.getOptimisticLockStyle());
        rootEntityDescriptor.setMutable(hierarchySource.isMutable());
        rootEntityDescriptor.setWhere(hierarchySource.getWhere());
        rootEntityDescriptor.setExplicitPolymorphism(hierarchySource.isExplicitPolymorphism());
        this.bindEntityIdentifier(mappingDocument, hierarchySource, rootEntityDescriptor);
        if (hierarchySource.getVersionAttributeSource() != null) {
            this.bindEntityVersion(mappingDocument, hierarchySource, rootEntityDescriptor);
        }
        if (hierarchySource.getDiscriminatorSource() != null) {
            this.bindEntityDiscriminator(mappingDocument, hierarchySource, rootEntityDescriptor);
        }
        this.applyCaching(mappingDocument, hierarchySource.getCaching(), rootEntityDescriptor);
        rootEntityDescriptor.createPrimaryKey();
        this.bindAllEntityAttributes(mappingDocument, hierarchySource.getRoot(), rootEntityDescriptor);
        if (hierarchySource.getNaturalIdCaching() != null && hierarchySource.getNaturalIdCaching().getRequested() == TruthValue.TRUE) {
            rootEntityDescriptor.setNaturalIdCacheRegionName(hierarchySource.getNaturalIdCaching().getRegion());
        }
    }

    private void applyCaching(MappingDocument mappingDocument, Caching caching, RootClass rootEntityDescriptor) {
        if (caching == null || caching.getRequested() == TruthValue.UNKNOWN) {
            switch (mappingDocument.getBuildingOptions().getSharedCacheMode()) {
                case ALL: {
                    caching = new Caching(null, mappingDocument.getBuildingOptions().getImplicitCacheAccessType(), false, TruthValue.UNKNOWN);
                    break;
                }
                case NONE: {
                    break;
                }
                case ENABLE_SELECTIVE: {
                    break;
                }
                case DISABLE_SELECTIVE: {
                    break;
                }
            }
        }
        if (caching == null || caching.getRequested() == TruthValue.FALSE) {
            return;
        }
        if (caching.getAccessType() != null) {
            rootEntityDescriptor.setCacheConcurrencyStrategy(caching.getAccessType().getExternalName());
        } else {
            rootEntityDescriptor.setCacheConcurrencyStrategy(mappingDocument.getBuildingOptions().getImplicitCacheAccessType().getExternalName());
        }
        rootEntityDescriptor.setCacheRegionName(caching.getRegion());
        rootEntityDescriptor.setLazyPropertiesCacheable(caching.isCacheLazyProperties());
        rootEntityDescriptor.setCached(caching.getRequested() != TruthValue.UNKNOWN);
    }

    private void bindEntityIdentifier(MappingDocument mappingDocument, EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        switch (hierarchySource.getIdentifierSource().getNature()) {
            case SIMPLE: {
                this.bindSimpleEntityIdentifier(mappingDocument, hierarchySource, rootEntityDescriptor);
                break;
            }
            case AGGREGATED_COMPOSITE: {
                this.bindAggregatedCompositeEntityIdentifier(mappingDocument, hierarchySource, rootEntityDescriptor);
                break;
            }
            case NON_AGGREGATED_COMPOSITE: {
                this.bindNonAggregatedCompositeEntityIdentifier(mappingDocument, hierarchySource, rootEntityDescriptor);
                break;
            }
            default: {
                throw new MappingException(String.format(Locale.ENGLISH, "Unexpected entity identifier nature [%s] for entity %s", new Object[]{hierarchySource.getIdentifierSource().getNature(), hierarchySource.getRoot().getEntityNamingSource().getEntityName()}), mappingDocument.getOrigin());
            }
        }
    }

    private void bindBasicEntityValues(MappingDocument sourceDocument, AbstractEntitySourceImpl entitySource, PersistentClass entityDescriptor) {
        entityDescriptor.setEntityName(entitySource.getEntityNamingSource().getEntityName());
        entityDescriptor.setJpaEntityName(entitySource.getEntityNamingSource().getJpaEntityName());
        entityDescriptor.setClassName(entitySource.getEntityNamingSource().getClassName());
        entityDescriptor.setDiscriminatorValue(entitySource.getDiscriminatorMatchValue() != null ? entitySource.getDiscriminatorMatchValue() : entityDescriptor.getEntityName());
        if (StringHelper.isNotEmpty(entitySource.getProxy())) {
            String qualifiedProxyName = sourceDocument.qualifyClassName(entitySource.getProxy());
            entityDescriptor.setProxyInterfaceName(qualifiedProxyName);
            entityDescriptor.setLazy(true);
        } else if (entitySource.isLazy()) {
            entityDescriptor.setProxyInterfaceName(entityDescriptor.getClassName());
            entityDescriptor.setLazy(true);
        } else {
            entityDescriptor.setProxyInterfaceName(null);
            entityDescriptor.setLazy(false);
        }
        entityDescriptor.setAbstract(entitySource.isAbstract());
        sourceDocument.getMetadataCollector().addImport(entitySource.getEntityNamingSource().getEntityName(), entitySource.getEntityNamingSource().getEntityName());
        if (sourceDocument.getMappingDefaults().isAutoImportEnabled() && entitySource.getEntityNamingSource().getEntityName().indexOf(46) > 0) {
            sourceDocument.getMetadataCollector().addImport(StringHelper.unqualify(entitySource.getEntityNamingSource().getEntityName()), entitySource.getEntityNamingSource().getEntityName());
        }
        if (entitySource.getTuplizerClassMap() != null) {
            if (entitySource.getTuplizerClassMap().size() > 1) {
                DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
            }
            for (Map.Entry entry : entitySource.getTuplizerClassMap().entrySet()) {
                entityDescriptor.addTuplizer((EntityMode)((Object)entry.getKey()), (String)entry.getValue());
            }
        }
        if (StringHelper.isNotEmpty(entitySource.getXmlNodeName())) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
        }
        entityDescriptor.setDynamicInsert(entitySource.isDynamicInsert());
        entityDescriptor.setDynamicUpdate(entitySource.isDynamicUpdate());
        entityDescriptor.setBatchSize(entitySource.getBatchSize());
        entityDescriptor.setSelectBeforeUpdate(entitySource.isSelectBeforeUpdate());
        if (StringHelper.isNotEmpty(entitySource.getCustomPersisterClassName())) {
            try {
                entityDescriptor.setEntityPersisterClass(sourceDocument.getBootstrapContext().getClassLoaderAccess().classForName(entitySource.getCustomPersisterClassName()));
            }
            catch (ClassLoadingException e) {
                throw new MappingException(String.format(Locale.ENGLISH, "Unable to load specified persister class : %s", entitySource.getCustomPersisterClassName()), (Throwable)((Object)e), sourceDocument.getOrigin());
            }
        }
        ModelBinder.bindCustomSql(sourceDocument, entitySource, entityDescriptor);
        JdbcEnvironment jdbcEnvironment = sourceDocument.getMetadataCollector().getDatabase().getJdbcEnvironment();
        for (String tableName : entitySource.getSynchronizedTableNames()) {
            Identifier physicalTableName = sourceDocument.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(jdbcEnvironment.getIdentifierHelper().toIdentifier(tableName), jdbcEnvironment);
            entityDescriptor.addSynchronizedTable(physicalTableName.render(jdbcEnvironment.getDialect()));
        }
        for (FilterSource filterSource : entitySource.getFilterSources()) {
            FilterDefinition filterDefinition;
            String condition = filterSource.getCondition();
            if (condition == null && (filterDefinition = sourceDocument.getMetadataCollector().getFilterDefinition(filterSource.getName())) != null) {
                condition = filterDefinition.getDefaultFilterCondition();
            }
            entityDescriptor.addFilter(filterSource.getName(), condition, filterSource.shouldAutoInjectAliases(), filterSource.getAliasToTableMap(), filterSource.getAliasToEntityMap());
        }
        for (JaxbHbmNamedQueryType namedQuery : entitySource.getNamedQueries()) {
            NamedQueryBinder.processNamedQuery(sourceDocument, namedQuery, entitySource.getEntityNamingSource().getEntityName() + ".");
        }
        for (JaxbHbmNamedNativeQueryType namedQuery : entitySource.getNamedNativeQueries()) {
            NamedQueryBinder.processNamedNativeQuery(sourceDocument, namedQuery, entitySource.getEntityNamingSource().getEntityName() + ".");
        }
        entityDescriptor.setMetaAttributes(entitySource.getToolingHintContext().getMetaAttributeMap());
    }

    private void bindDiscriminatorSubclassEntities(AbstractEntitySourceImpl entitySource, PersistentClass superEntityDescriptor) {
        for (IdentifiableTypeSource subType : entitySource.getSubTypes()) {
            SingleTableSubclass subEntityDescriptor = new SingleTableSubclass(superEntityDescriptor, this.metadataBuildingContext);
            subEntityDescriptor.setCached(superEntityDescriptor.isCached());
            this.bindDiscriminatorSubclassEntity((SubclassEntitySourceImpl)subType, subEntityDescriptor);
            superEntityDescriptor.addSubclass(subEntityDescriptor);
            entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding(subEntityDescriptor);
        }
    }

    private void bindDiscriminatorSubclassEntity(SubclassEntitySourceImpl entitySource, SingleTableSubclass entityDescriptor) {
        this.bindBasicEntityValues(entitySource.sourceMappingDocument(), entitySource, entityDescriptor);
        String superEntityName = ((EntitySource)entitySource.getSuperType()).getEntityNamingSource().getEntityName();
        InFlightMetadataCollector.EntityTableXref superEntityTableXref = entitySource.getLocalMetadataBuildingContext().getMetadataCollector().getEntityTableXref(superEntityName);
        if (superEntityTableXref == null) {
            throw new MappingException(String.format(Locale.ENGLISH, "Unable to locate entity table xref for entity [%s] super-type [%s]", entityDescriptor.getEntityName(), superEntityName), entitySource.origin());
        }
        entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityTableXref(entitySource.getEntityNamingSource().getEntityName(), this.database.toIdentifier(entitySource.getLocalMetadataBuildingContext().getMetadataCollector().getLogicalTableName(entityDescriptor.getTable())), entityDescriptor.getTable(), superEntityTableXref);
        this.bindAllEntityAttributes(entitySource.sourceMappingDocument(), entitySource, entityDescriptor);
        this.bindDiscriminatorSubclassEntities(entitySource, entityDescriptor);
    }

    private void bindJoinedSubclassEntities(AbstractEntitySourceImpl entitySource, PersistentClass superEntityDescriptor) {
        for (IdentifiableTypeSource subType : entitySource.getSubTypes()) {
            JoinedSubclass subEntityDescriptor = new JoinedSubclass(superEntityDescriptor, this.metadataBuildingContext);
            subEntityDescriptor.setCached(superEntityDescriptor.isCached());
            this.bindJoinedSubclassEntity((JoinedSubclassEntitySourceImpl)subType, subEntityDescriptor);
            superEntityDescriptor.addSubclass(subEntityDescriptor);
            entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding(subEntityDescriptor);
        }
    }

    private void bindJoinedSubclassEntity(JoinedSubclassEntitySourceImpl entitySource, JoinedSubclass entityDescriptor) {
        MappingDocument mappingDocument = entitySource.sourceMappingDocument();
        this.bindBasicEntityValues(mappingDocument, entitySource, entityDescriptor);
        final Table primaryTable = this.bindEntityTableSpecification(mappingDocument, entitySource.getPrimaryTable(), null, entitySource, entityDescriptor);
        entityDescriptor.setTable(primaryTable);
        if (log.isDebugEnabled()) {
            log.debugf("Mapping joined-subclass: %s -> %s", entityDescriptor.getEntityName(), primaryTable.getName());
        }
        DependantValue keyBinding = new DependantValue(mappingDocument, primaryTable, entityDescriptor.getIdentifier());
        if (mappingDocument.getBuildingOptions().useNationalizedCharacterData()) {
            keyBinding.makeNationalized();
        }
        entityDescriptor.setKey(keyBinding);
        keyBinding.setCascadeDeleteEnabled(entitySource.isCascadeDeleteEnabled());
        this.relationalObjectBinder.bindColumns(mappingDocument, entitySource.getPrimaryKeyColumnSources(), keyBinding, false, new RelationalObjectBinder.ColumnNamingDelegate(){
            int count = 0;

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                Column column = primaryTable.getPrimaryKey().getColumn(this.count++);
                return ModelBinder.this.database.toIdentifier(column.getQuotedName());
            }
        });
        keyBinding.setForeignKeyName(entitySource.getExplicitForeignKeyName());
        entityDescriptor.createPrimaryKey();
        entityDescriptor.createForeignKey();
        this.bindAllEntityAttributes(entitySource.sourceMappingDocument(), entitySource, entityDescriptor);
        this.bindJoinedSubclassEntities(entitySource, entityDescriptor);
    }

    private void bindUnionSubclassEntities(EntitySource entitySource, PersistentClass superEntityDescriptor) {
        for (IdentifiableTypeSource subType : entitySource.getSubTypes()) {
            UnionSubclass subEntityDescriptor = new UnionSubclass(superEntityDescriptor, this.metadataBuildingContext);
            subEntityDescriptor.setCached(superEntityDescriptor.isCached());
            this.bindUnionSubclassEntity((SubclassEntitySourceImpl)subType, subEntityDescriptor);
            superEntityDescriptor.addSubclass(subEntityDescriptor);
            entitySource.getLocalMetadataBuildingContext().getMetadataCollector().addEntityBinding(subEntityDescriptor);
        }
    }

    private void bindUnionSubclassEntity(SubclassEntitySourceImpl entitySource, UnionSubclass entityDescriptor) {
        MappingDocument mappingDocument = entitySource.sourceMappingDocument();
        this.bindBasicEntityValues(mappingDocument, entitySource, entityDescriptor);
        Table primaryTable = this.bindEntityTableSpecification(mappingDocument, entitySource.getPrimaryTable(), entityDescriptor.getSuperclass().getTable(), entitySource, entityDescriptor);
        entityDescriptor.setTable(primaryTable);
        if (log.isDebugEnabled()) {
            log.debugf("Mapping union-subclass: %s -> %s", entityDescriptor.getEntityName(), primaryTable.getName());
        }
        this.bindAllEntityAttributes(entitySource.sourceMappingDocument(), entitySource, entityDescriptor);
        this.bindUnionSubclassEntities(entitySource, entityDescriptor);
    }

    private void bindSimpleEntityIdentifier(MappingDocument sourceDocument, final EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        final IdentifierSourceSimple idSource = (IdentifierSourceSimple)hierarchySource.getIdentifierSource();
        SimpleValue idValue = new SimpleValue(sourceDocument, rootEntityDescriptor.getTable());
        rootEntityDescriptor.setIdentifier(idValue);
        ModelBinder.bindSimpleValueType(sourceDocument, idSource.getIdentifierAttributeSource().getTypeInformation(), idValue);
        final String propertyName = idSource.getIdentifierAttributeSource().getName();
        if (propertyName == null || !rootEntityDescriptor.hasPojoRepresentation()) {
            if (!idValue.isTypeSpecified()) {
                throw new MappingException("must specify an identifier type: " + rootEntityDescriptor.getEntityName(), sourceDocument.getOrigin());
            }
        } else {
            idValue.setTypeUsingReflection(rootEntityDescriptor.getClassName(), propertyName);
        }
        this.relationalObjectBinder.bindColumnsAndFormulas(sourceDocument, ((RelationalValueSourceContainer)((Object)idSource.getIdentifierAttributeSource())).getRelationalValueSources(), idValue, false, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
                context.getBuildingOptions().getImplicitNamingStrategy().determineIdentifierColumnName(new ImplicitIdentifierColumnNameSource(){

                    @Override
                    public EntityNaming getEntityNaming() {
                        return hierarchySource.getRoot().getEntityNamingSource();
                    }

                    @Override
                    public AttributePath getIdentifierAttributePath() {
                        return idSource.getIdentifierAttributeSource().getAttributePath();
                    }

                    @Override
                    public MetadataBuildingContext getBuildingContext() {
                        return context;
                    }
                });
                return ModelBinder.this.database.toIdentifier(propertyName);
            }
        });
        if (propertyName != null) {
            Property prop = new Property();
            prop.setValue(idValue);
            this.bindProperty(sourceDocument, idSource.getIdentifierAttributeSource(), prop);
            rootEntityDescriptor.setIdentifierProperty(prop);
            rootEntityDescriptor.setDeclaredIdentifierProperty(prop);
        }
        this.makeIdentifier(sourceDocument, idSource.getIdentifierGeneratorDescriptor(), idSource.getUnsavedValue(), idValue);
    }

    private void makeIdentifier(MappingDocument sourceDocument, IdentifierGeneratorDefinition generator, String unsavedValue, SimpleValue identifierValue) {
        if (generator != null) {
            String generatorName = generator.getStrategy();
            Properties params = new Properties();
            IdentifierGeneratorDefinition generatorDef = sourceDocument.getMetadataCollector().getIdentifierGenerator(generatorName);
            if (generatorDef != null) {
                generatorName = generatorDef.getStrategy();
                params.putAll(generatorDef.getParameters());
            }
            identifierValue.setIdentifierGeneratorStrategy(generatorName);
            params.put("identifier_normalizer", this.objectNameNormalizer);
            params.putAll(generator.getParameters());
            identifierValue.setIdentifierGeneratorProperties(params);
        }
        identifierValue.getTable().setIdentifierValue(identifierValue);
        if (StringHelper.isNotEmpty(unsavedValue)) {
            identifierValue.setNullValue(unsavedValue);
        } else if ("assigned".equals(identifierValue.getIdentifierGeneratorStrategy())) {
            identifierValue.setNullValue("undefined");
        } else {
            identifierValue.setNullValue(null);
        }
    }

    private void bindAggregatedCompositeEntityIdentifier(MappingDocument mappingDocument, EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        IdentifierSourceAggregatedComposite identifierSource = (IdentifierSourceAggregatedComposite)hierarchySource.getIdentifierSource();
        Component cid = new Component((MetadataBuildingContext)mappingDocument, (PersistentClass)rootEntityDescriptor);
        cid.setKey(true);
        rootEntityDescriptor.setIdentifier(cid);
        String idClassName = this.extractIdClassName(identifierSource);
        String idPropertyName = identifierSource.getIdentifierAttributeSource().getName();
        String pathPart = idPropertyName == null ? "<id>" : idPropertyName;
        this.bindComponent(mappingDocument, hierarchySource.getRoot().getAttributeRoleBase().append(pathPart).getFullPath(), identifierSource.getEmbeddableSource(), cid, idClassName, rootEntityDescriptor.getClassName(), idPropertyName, idClassName == null && idPropertyName == null, identifierSource.getEmbeddableSource().isDynamic(), identifierSource.getIdentifierAttributeSource().getXmlNodeName());
        this.finishBindingCompositeIdentifier(mappingDocument, rootEntityDescriptor, identifierSource, cid, idPropertyName);
    }

    private String extractIdClassName(IdentifierSourceAggregatedComposite identifierSource) {
        if (identifierSource.getEmbeddableSource().getTypeDescriptor() == null) {
            return null;
        }
        return identifierSource.getEmbeddableSource().getTypeDescriptor().getName();
    }

    private void bindNonAggregatedCompositeEntityIdentifier(MappingDocument mappingDocument, EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        IdentifierSourceNonAggregatedComposite identifierSource = (IdentifierSourceNonAggregatedComposite)hierarchySource.getIdentifierSource();
        Component cid = new Component((MetadataBuildingContext)mappingDocument, (PersistentClass)rootEntityDescriptor);
        cid.setKey(true);
        rootEntityDescriptor.setIdentifier(cid);
        String idClassName = this.extractIdClassName(identifierSource);
        this.bindComponent(mappingDocument, hierarchySource.getRoot().getAttributeRoleBase().append("<id>").getFullPath(), identifierSource.getEmbeddableSource(), cid, idClassName, rootEntityDescriptor.getClassName(), null, idClassName == null, false, null);
        if (idClassName != null) {
            Component mapper = new Component((MetadataBuildingContext)mappingDocument, (PersistentClass)rootEntityDescriptor);
            this.bindComponent(mappingDocument, hierarchySource.getRoot().getAttributeRoleBase().append(ID_MAPPER_PATH_PART).getFullPath(), identifierSource.getEmbeddableSource(), mapper, rootEntityDescriptor.getClassName(), null, null, true, false, null);
            rootEntityDescriptor.setIdentifierMapper(mapper);
            Property property = new Property();
            property.setName("_identifierMapper");
            property.setUpdateable(false);
            property.setInsertable(false);
            property.setValue(mapper);
            property.setPropertyAccessorName("embedded");
            rootEntityDescriptor.addProperty(property);
        }
        this.finishBindingCompositeIdentifier(mappingDocument, rootEntityDescriptor, identifierSource, cid, null);
    }

    private String extractIdClassName(IdentifierSourceNonAggregatedComposite identifierSource) {
        if (identifierSource.getIdClassSource() == null) {
            return null;
        }
        if (identifierSource.getIdClassSource().getTypeDescriptor() == null) {
            return null;
        }
        return identifierSource.getIdClassSource().getTypeDescriptor().getName();
    }

    private void finishBindingCompositeIdentifier(MappingDocument sourceDocument, RootClass rootEntityDescriptor, CompositeIdentifierSource identifierSource, Component cid, String propertyName) {
        if (propertyName == null) {
            rootEntityDescriptor.setEmbeddedIdentifier(cid.isEmbedded());
            if (cid.isEmbedded()) {
                cid.setDynamic(!rootEntityDescriptor.hasPojoRepresentation());
            }
        } else {
            Property prop = new Property();
            prop.setValue(cid);
            this.bindProperty(sourceDocument, ((IdentifierSourceAggregatedComposite)identifierSource).getIdentifierAttributeSource(), prop);
            rootEntityDescriptor.setIdentifierProperty(prop);
            rootEntityDescriptor.setDeclaredIdentifierProperty(prop);
        }
        this.makeIdentifier(sourceDocument, identifierSource.getIdentifierGeneratorDescriptor(), null, cid);
    }

    private void bindEntityVersion(MappingDocument sourceDocument, EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        final VersionAttributeSource versionAttributeSource = hierarchySource.getVersionAttributeSource();
        SimpleValue versionValue = new SimpleValue(sourceDocument, rootEntityDescriptor.getTable());
        versionValue.makeVersion();
        ModelBinder.bindSimpleValueType(sourceDocument, versionAttributeSource.getTypeInformation(), versionValue);
        this.relationalObjectBinder.bindColumnsAndFormulas(sourceDocument, versionAttributeSource.getRelationalValueSources(), versionValue, false, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                return ModelBinder.this.implicitNamingStrategy.determineBasicColumnName(versionAttributeSource);
            }
        });
        Property prop = new Property();
        prop.setValue(versionValue);
        this.bindProperty(sourceDocument, versionAttributeSource, prop);
        if (prop.getValueGenerationStrategy() != null && prop.getValueGenerationStrategy().getGenerationTiming() == GenerationTiming.INSERT) {
            throw new MappingException("'generated' attribute cannot be 'insert' for version/timestamp property", sourceDocument.getOrigin());
        }
        if (versionAttributeSource.getUnsavedValue() != null) {
            versionValue.setNullValue(versionAttributeSource.getUnsavedValue());
        } else {
            versionValue.setNullValue("undefined");
        }
        rootEntityDescriptor.setVersion(prop);
        rootEntityDescriptor.setDeclaredVersion(prop);
        rootEntityDescriptor.addProperty(prop);
    }

    private void bindEntityDiscriminator(MappingDocument sourceDocument, final EntityHierarchySourceImpl hierarchySource, RootClass rootEntityDescriptor) {
        SimpleValue discriminatorValue = new SimpleValue(sourceDocument, rootEntityDescriptor.getTable());
        rootEntityDescriptor.setDiscriminator(discriminatorValue);
        String typeName = hierarchySource.getDiscriminatorSource().getExplicitHibernateTypeName();
        if (typeName == null) {
            typeName = "string";
        }
        ModelBinder.bindSimpleValueType(sourceDocument, new HibernateTypeSourceImpl(typeName), discriminatorValue);
        this.relationalObjectBinder.bindColumnOrFormula(sourceDocument, hierarchySource.getDiscriminatorSource().getDiscriminatorRelationalValueSource(), discriminatorValue, false, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                return ModelBinder.this.implicitNamingStrategy.determineDiscriminatorColumnName(hierarchySource.getDiscriminatorSource());
            }
        });
        rootEntityDescriptor.setPolymorphic(true);
        rootEntityDescriptor.setDiscriminatorInsertable(hierarchySource.getDiscriminatorSource().isInserted());
        boolean force = hierarchySource.getDiscriminatorSource().isForced() || sourceDocument.getBuildingOptions().shouldImplicitlyForceDiscriminatorInSelect();
        rootEntityDescriptor.setForceDiscriminator(force);
    }

    private void bindAllEntityAttributes(MappingDocument mappingDocument, EntitySource entitySource, PersistentClass entityDescriptor) {
        InFlightMetadataCollector.EntityTableXref entityTableXref = mappingDocument.getMetadataCollector().getEntityTableXref(entityDescriptor.getEntityName());
        if (entityTableXref == null) {
            throw new AssertionFailure(String.format(Locale.ENGLISH, "Unable to locate EntityTableXref for entity [%s] : %s", entityDescriptor.getEntityName(), mappingDocument.getOrigin()));
        }
        for (SecondaryTableSource secondaryTableSource : entitySource.getSecondaryTableMap().values()) {
            Join secondaryTableJoin = new Join();
            secondaryTableJoin.setPersistentClass(entityDescriptor);
            this.bindSecondaryTable(mappingDocument, secondaryTableSource, secondaryTableJoin, entityTableXref);
            entityDescriptor.addJoin(secondaryTableJoin);
        }
        for (AttributeSource attributeSource : entitySource.attributeSources()) {
            Property attribute;
            AttributeContainer attributeContainer;
            Table table;
            Join secondaryTableJoin;
            Identifier tableName;
            if (PluralAttributeSource.class.isInstance(attributeSource)) {
                Property attribute2 = this.createPluralAttribute(mappingDocument, (PluralAttributeSource)attributeSource, entityDescriptor);
                entityDescriptor.addProperty(attribute2);
                continue;
            }
            if (SingularAttributeSourceBasic.class.isInstance(attributeSource)) {
                SingularAttributeSourceBasic basicAttributeSource = (SingularAttributeSourceBasic)attributeSource;
                tableName = this.determineTable(mappingDocument, basicAttributeSource.getName(), basicAttributeSource);
                secondaryTableJoin = entityTableXref.locateJoin(tableName);
                if (secondaryTableJoin == null) {
                    table = entityDescriptor.getTable();
                    attributeContainer = entityDescriptor;
                } else {
                    table = secondaryTableJoin.getTable();
                    attributeContainer = secondaryTableJoin;
                }
                attribute = this.createBasicAttribute(mappingDocument, basicAttributeSource, new SimpleValue(mappingDocument, table), entityDescriptor.getClassName());
                if (secondaryTableJoin != null) {
                    attribute.setOptional(secondaryTableJoin.isOptional());
                }
                attributeContainer.addProperty(attribute);
                this.handleNaturalIdBinding(mappingDocument, entityDescriptor, attribute, basicAttributeSource.getNaturalIdMutability());
                continue;
            }
            if (SingularAttributeSourceEmbedded.class.isInstance(attributeSource)) {
                SingularAttributeSourceEmbedded embeddedAttributeSource = (SingularAttributeSourceEmbedded)attributeSource;
                tableName = this.determineTable(mappingDocument, embeddedAttributeSource);
                secondaryTableJoin = entityTableXref.locateJoin(tableName);
                if (secondaryTableJoin == null) {
                    table = entityDescriptor.getTable();
                    attributeContainer = entityDescriptor;
                } else {
                    table = secondaryTableJoin.getTable();
                    attributeContainer = secondaryTableJoin;
                }
                attribute = this.createEmbeddedAttribute(mappingDocument, (SingularAttributeSourceEmbedded)attributeSource, new Component(mappingDocument, table, entityDescriptor), entityDescriptor.getClassName());
                if (secondaryTableJoin != null) {
                    attribute.setOptional(secondaryTableJoin.isOptional());
                }
                attributeContainer.addProperty(attribute);
                this.handleNaturalIdBinding(mappingDocument, entityDescriptor, attribute, embeddedAttributeSource.getNaturalIdMutability());
                continue;
            }
            if (SingularAttributeSourceManyToOne.class.isInstance(attributeSource)) {
                SingularAttributeSourceManyToOne manyToOneAttributeSource = (SingularAttributeSourceManyToOne)attributeSource;
                tableName = this.determineTable(mappingDocument, manyToOneAttributeSource.getName(), manyToOneAttributeSource);
                secondaryTableJoin = entityTableXref.locateJoin(tableName);
                if (secondaryTableJoin == null) {
                    table = entityDescriptor.getTable();
                    attributeContainer = entityDescriptor;
                } else {
                    table = secondaryTableJoin.getTable();
                    attributeContainer = secondaryTableJoin;
                }
                attribute = this.createManyToOneAttribute(mappingDocument, manyToOneAttributeSource, new ManyToOne(mappingDocument, table), entityDescriptor.getClassName());
                if (secondaryTableJoin != null) {
                    attribute.setOptional(secondaryTableJoin.isOptional());
                }
                attributeContainer.addProperty(attribute);
                this.handleNaturalIdBinding(mappingDocument, entityDescriptor, attribute, manyToOneAttributeSource.getNaturalIdMutability());
                continue;
            }
            if (SingularAttributeSourceOneToOne.class.isInstance(attributeSource)) {
                SingularAttributeSourceOneToOne oneToOneAttributeSource = (SingularAttributeSourceOneToOne)attributeSource;
                Table table2 = entityDescriptor.getTable();
                Property attribute3 = this.createOneToOneAttribute(mappingDocument, oneToOneAttributeSource, new OneToOne(mappingDocument, table2, entityDescriptor), entityDescriptor.getClassName());
                entityDescriptor.addProperty(attribute3);
                this.handleNaturalIdBinding(mappingDocument, entityDescriptor, attribute3, oneToOneAttributeSource.getNaturalIdMutability());
                continue;
            }
            if (!SingularAttributeSourceAny.class.isInstance(attributeSource)) continue;
            SingularAttributeSourceAny anyAttributeSource = (SingularAttributeSourceAny)attributeSource;
            tableName = this.determineTable(mappingDocument, anyAttributeSource.getName(), anyAttributeSource.getKeySource().getRelationalValueSources());
            secondaryTableJoin = entityTableXref.locateJoin(tableName);
            if (secondaryTableJoin == null) {
                table = entityDescriptor.getTable();
                attributeContainer = entityDescriptor;
            } else {
                table = secondaryTableJoin.getTable();
                attributeContainer = secondaryTableJoin;
            }
            attribute = this.createAnyAssociationAttribute(mappingDocument, anyAttributeSource, new Any(mappingDocument, table), entityDescriptor.getEntityName());
            if (secondaryTableJoin != null) {
                attribute.setOptional(secondaryTableJoin.isOptional());
            }
            attributeContainer.addProperty(attribute);
            this.handleNaturalIdBinding(mappingDocument, entityDescriptor, attribute, anyAttributeSource.getNaturalIdMutability());
        }
    }

    private void handleNaturalIdBinding(MappingDocument mappingDocument, PersistentClass entityBinding, Property attributeBinding, NaturalIdMutability naturalIdMutability) {
        NaturalIdUniqueKeyBinder ukBinder;
        if (naturalIdMutability == NaturalIdMutability.NOT_NATURAL_ID) {
            return;
        }
        attributeBinding.setNaturalIdentifier(true);
        if (naturalIdMutability == NaturalIdMutability.IMMUTABLE) {
            attributeBinding.setUpdateable(false);
        }
        if ((ukBinder = mappingDocument.getMetadataCollector().locateNaturalIdUniqueKeyBinder(entityBinding.getEntityName())) == null) {
            ukBinder = new NaturalIdUniqueKeyBinderImpl(mappingDocument, entityBinding);
            mappingDocument.getMetadataCollector().registerNaturalIdUniqueKeyBinder(entityBinding.getEntityName(), ukBinder);
        }
        ukBinder.addAttributeBinding(attributeBinding);
    }

    private Property createPluralAttribute(MappingDocument sourceDocument, PluralAttributeSource attributeSource, PersistentClass entityDescriptor) {
        Collection collectionBinding;
        if (attributeSource instanceof PluralAttributeSourceListImpl) {
            collectionBinding = new List(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributeListSecondPass(sourceDocument, (IndexedPluralAttributeSource)attributeSource, (List)collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourceSetImpl) {
            collectionBinding = new Set(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributeSetSecondPass(sourceDocument, attributeSource, collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourceMapImpl) {
            collectionBinding = new Map(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributeMapSecondPass(sourceDocument, (IndexedPluralAttributeSource)attributeSource, (Map)collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourceBagImpl) {
            collectionBinding = new Bag(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributeBagSecondPass(sourceDocument, attributeSource, collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourceIdBagImpl) {
            collectionBinding = new IdentifierBag(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributeIdBagSecondPass(sourceDocument, attributeSource, collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourceArrayImpl) {
            PluralAttributeSourceArray arraySource = (PluralAttributeSourceArray)attributeSource;
            collectionBinding = new Array(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            ((Array)collectionBinding).setElementClassName(sourceDocument.qualifyClassName(arraySource.getElementClass()));
            this.registerSecondPass(new PluralAttributeArraySecondPass(sourceDocument, arraySource, (Array)collectionBinding), sourceDocument);
        } else if (attributeSource instanceof PluralAttributeSourcePrimitiveArrayImpl) {
            collectionBinding = new PrimitiveArray(sourceDocument, entityDescriptor);
            this.bindCollectionMetadata(sourceDocument, attributeSource, collectionBinding);
            this.registerSecondPass(new PluralAttributePrimitiveArraySecondPass(sourceDocument, (IndexedPluralAttributeSource)attributeSource, (PrimitiveArray)collectionBinding), sourceDocument);
        } else {
            throw new AssertionFailure("Unexpected PluralAttributeSource type : " + attributeSource.getClass().getName());
        }
        sourceDocument.getMetadataCollector().addCollectionBinding(collectionBinding);
        Property attribute = new Property();
        attribute.setValue(collectionBinding);
        this.bindProperty(sourceDocument, attributeSource, attribute);
        return attribute;
    }

    private void bindCollectionMetadata(MappingDocument mappingDocument, PluralAttributeSource source, Collection binding) {
        String cascadeStyle;
        binding.setRole(source.getAttributeRole().getFullPath());
        binding.setInverse(source.isInverse());
        binding.setMutable(source.isMutable());
        binding.setOptimisticLocked(source.isIncludedInOptimisticLocking());
        if (source.getCustomPersisterClassName() != null) {
            binding.setCollectionPersisterClass(mappingDocument.getBootstrapContext().getClassLoaderAccess().classForName(mappingDocument.qualifyClassName(source.getCustomPersisterClassName())));
        }
        this.applyCaching(mappingDocument, source.getCaching(), binding);
        String typeName = source.getTypeInformation().getName();
        HashMap<String, String> typeParameters = new HashMap<String, String>();
        if (typeName != null) {
            String[] typeDef = mappingDocument.getMetadataCollector().getTypeDefinition(typeName);
            if (typeDef != null) {
                typeName = typeDef.getTypeImplementorClass().getName();
                if (typeDef.getParameters() != null) {
                    typeParameters.putAll(typeDef.getParameters());
                }
            } else {
                typeName = mappingDocument.qualifyClassName(typeName);
            }
        }
        if (source.getTypeInformation().getParameters() != null) {
            typeParameters.putAll(source.getTypeInformation().getParameters());
        }
        binding.setTypeName(typeName);
        binding.setTypeParameters(typeParameters);
        if (source.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED) {
            binding.setLazy(true);
            binding.setExtraLazy(source.getFetchCharacteristics().isExtraLazy());
        } else {
            binding.setLazy(false);
        }
        switch (source.getFetchCharacteristics().getFetchStyle()) {
            case SELECT: {
                binding.setFetchMode(FetchMode.SELECT);
                break;
            }
            case JOIN: {
                binding.setFetchMode(FetchMode.JOIN);
                break;
            }
            case BATCH: {
                binding.setFetchMode(FetchMode.SELECT);
                binding.setBatchSize(source.getFetchCharacteristics().getBatchSize());
                break;
            }
            case SUBSELECT: {
                binding.setFetchMode(FetchMode.SELECT);
                binding.setSubselectLoadable(true);
                binding.getOwner().setSubselectLoadableCollections(true);
                break;
            }
            default: {
                throw new AssertionFailure("Unexpected FetchStyle : " + source.getFetchCharacteristics().getFetchStyle().name());
            }
        }
        for (String name : source.getSynchronizedTableNames()) {
            binding.getSynchronizedTables().add(name);
        }
        binding.setLoaderName(source.getCustomLoaderName());
        if (source.getCustomSqlInsert() != null) {
            binding.setCustomSQLInsert(source.getCustomSqlInsert().getSql(), source.getCustomSqlInsert().isCallable(), source.getCustomSqlInsert().getCheckStyle());
        }
        if (source.getCustomSqlUpdate() != null) {
            binding.setCustomSQLUpdate(source.getCustomSqlUpdate().getSql(), source.getCustomSqlUpdate().isCallable(), source.getCustomSqlUpdate().getCheckStyle());
        }
        if (source.getCustomSqlDelete() != null) {
            binding.setCustomSQLDelete(source.getCustomSqlDelete().getSql(), source.getCustomSqlDelete().isCallable(), source.getCustomSqlDelete().getCheckStyle());
        }
        if (source.getCustomSqlDeleteAll() != null) {
            binding.setCustomSQLDeleteAll(source.getCustomSqlDeleteAll().getSql(), source.getCustomSqlDeleteAll().isCallable(), source.getCustomSqlDeleteAll().getCheckStyle());
        }
        if (source instanceof Sortable) {
            Sortable sortable = (Sortable)((Object)source);
            if (sortable.isSorted()) {
                binding.setSorted(true);
                if (!sortable.getComparatorName().equals("natural")) {
                    binding.setComparatorClassName(sortable.getComparatorName());
                }
            } else {
                binding.setSorted(false);
            }
        }
        if (source instanceof Orderable && ((Orderable)((Object)source)).isOrdered()) {
            binding.setOrderBy(((Orderable)((Object)source)).getOrder());
        }
        if ((cascadeStyle = source.getCascadeStyleName()) != null && cascadeStyle.contains("delete-orphan")) {
            binding.setOrphanDelete(true);
        }
        for (FilterSource filterSource : source.getFilterSources()) {
            FilterDefinition filterDefinition;
            String condition = filterSource.getCondition();
            if (condition == null && (filterDefinition = mappingDocument.getMetadataCollector().getFilterDefinition(filterSource.getName())) != null) {
                condition = filterDefinition.getDefaultFilterCondition();
            }
            binding.addFilter(filterSource.getName(), condition, filterSource.shouldAutoInjectAliases(), filterSource.getAliasToTableMap(), filterSource.getAliasToEntityMap());
        }
    }

    private void applyCaching(MappingDocument mappingDocument, Caching caching, Collection collection) {
        if (caching == null || caching.getRequested() == TruthValue.UNKNOWN) {
            switch (mappingDocument.getBuildingOptions().getSharedCacheMode()) {
                case ALL: {
                    caching = new Caching(null, mappingDocument.getBuildingOptions().getImplicitCacheAccessType(), false, TruthValue.UNKNOWN);
                    break;
                }
                case NONE: {
                    break;
                }
                case ENABLE_SELECTIVE: {
                    break;
                }
                case DISABLE_SELECTIVE: {
                    break;
                }
            }
        }
        if (caching == null || caching.getRequested() == TruthValue.FALSE) {
            return;
        }
        if (caching.getAccessType() != null) {
            collection.setCacheConcurrencyStrategy(caching.getAccessType().getExternalName());
        } else {
            collection.setCacheConcurrencyStrategy(mappingDocument.getBuildingOptions().getImplicitCacheAccessType().getExternalName());
        }
        collection.setCacheRegionName(caching.getRegion());
    }

    private Identifier determineTable(MappingDocument sourceDocument, String attributeName, RelationalValueSourceContainer relationalValueSourceContainer) {
        return this.determineTable(sourceDocument, attributeName, relationalValueSourceContainer.getRelationalValueSources());
    }

    private Identifier determineTable(MappingDocument mappingDocument, SingularAttributeSourceEmbedded embeddedAttributeSource) {
        Identifier tableName = null;
        for (AttributeSource attributeSource : embeddedAttributeSource.getEmbeddableSource().attributeSources()) {
            Identifier determinedName;
            if (RelationalValueSourceContainer.class.isInstance(attributeSource)) {
                determinedName = this.determineTable(mappingDocument, embeddedAttributeSource.getAttributeRole().getFullPath(), (RelationalValueSourceContainer)((Object)attributeSource));
            } else if (SingularAttributeSourceEmbedded.class.isInstance(attributeSource)) {
                determinedName = this.determineTable(mappingDocument, (SingularAttributeSourceEmbedded)attributeSource);
            } else {
                if (!SingularAttributeSourceAny.class.isInstance(attributeSource)) continue;
                determinedName = this.determineTable(mappingDocument, attributeSource.getAttributeRole().getFullPath(), ((SingularAttributeSourceAny)attributeSource).getKeySource().getRelationalValueSources());
            }
            if (Objects.equals(tableName, determinedName)) continue;
            if (tableName != null) {
                throw new MappingException(String.format(Locale.ENGLISH, "Attribute [%s] referenced columns from multiple tables: %s, %s", embeddedAttributeSource.getAttributeRole().getFullPath(), tableName, determinedName), mappingDocument.getOrigin());
            }
            tableName = determinedName;
        }
        return tableName;
    }

    private Identifier determineTable(MappingDocument mappingDocument, String attributeName, java.util.List<RelationalValueSource> relationalValueSources) {
        String tableName = null;
        for (RelationalValueSource relationalValueSource : relationalValueSources) {
            if (Objects.equals(tableName, relationalValueSource.getContainingTableName())) continue;
            if (tableName != null) {
                throw new MappingException(String.format(Locale.ENGLISH, "Attribute [%s] referenced columns from multiple tables: %s, %s", attributeName, tableName, relationalValueSource.getContainingTableName()), mappingDocument.getOrigin());
            }
            tableName = relationalValueSource.getContainingTableName();
        }
        return this.database.toIdentifier(tableName);
    }

    private void bindSecondaryTable(MappingDocument mappingDocument, SecondaryTableSource secondaryTableSource, Join secondaryTableJoin, final InFlightMetadataCollector.EntityTableXref entityTableXref) {
        Table secondaryTable;
        Identifier logicalTableName;
        PersistentClass persistentClass = secondaryTableJoin.getPersistentClass();
        Identifier catalogName = this.determineCatalogName(secondaryTableSource.getTableSource());
        Identifier schemaName = this.determineSchemaName(secondaryTableSource.getTableSource());
        Namespace namespace = this.database.locateNamespace(catalogName, schemaName);
        if (TableSource.class.isInstance(secondaryTableSource.getTableSource())) {
            TableSource tableSource = (TableSource)secondaryTableSource.getTableSource();
            logicalTableName = this.database.toIdentifier(tableSource.getExplicitTableName());
            secondaryTable = namespace.locateTable(logicalTableName);
            if (secondaryTable == null) {
                secondaryTable = namespace.createTable(logicalTableName, false);
            } else {
                secondaryTable.setAbstract(false);
            }
            secondaryTable.setComment(tableSource.getComment());
        } else {
            InLineViewSource inLineViewSource = (InLineViewSource)secondaryTableSource.getTableSource();
            secondaryTable = new Table(namespace, inLineViewSource.getSelectStatement(), false);
            logicalTableName = Identifier.toIdentifier(inLineViewSource.getLogicalName());
        }
        secondaryTableJoin.setTable(secondaryTable);
        entityTableXref.addSecondaryTable(mappingDocument, logicalTableName, secondaryTableJoin);
        ModelBinder.bindCustomSql(mappingDocument, secondaryTableSource, secondaryTableJoin);
        secondaryTableJoin.setSequentialSelect(secondaryTableSource.getFetchStyle() == FetchStyle.SELECT);
        secondaryTableJoin.setInverse(secondaryTableSource.isInverse());
        secondaryTableJoin.setOptional(secondaryTableSource.isOptional());
        if (log.isDebugEnabled()) {
            log.debugf("Mapping entity secondary-table: %s -> %s", persistentClass.getEntityName(), secondaryTable.getName());
        }
        DependantValue keyBinding = new DependantValue(mappingDocument, secondaryTable, persistentClass.getIdentifier());
        if (mappingDocument.getBuildingOptions().useNationalizedCharacterData()) {
            keyBinding.makeNationalized();
        }
        secondaryTableJoin.setKey(keyBinding);
        keyBinding.setCascadeDeleteEnabled(secondaryTableSource.isCascadeDeleteEnabled());
        this.relationalObjectBinder.bindColumns(mappingDocument, secondaryTableSource.getPrimaryKeyColumnSources(), keyBinding, secondaryTableSource.isOptional(), new RelationalObjectBinder.ColumnNamingDelegate(){
            int count = 0;

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                Column correspondingColumn = entityTableXref.getPrimaryTable().getPrimaryKey().getColumn(this.count++);
                return ModelBinder.this.database.toIdentifier(correspondingColumn.getQuotedName());
            }
        });
        keyBinding.setForeignKeyName(secondaryTableSource.getExplicitForeignKeyName());
        if (secondaryTable.getSubselect() == null) {
            secondaryTableJoin.createPrimaryKey();
            secondaryTableJoin.createForeignKey();
        }
    }

    private Property createEmbeddedAttribute(MappingDocument sourceDocument, SingularAttributeSourceEmbedded embeddedSource, Component componentBinding, String containingClassName) {
        String attributeName = embeddedSource.getName();
        this.bindComponent(sourceDocument, embeddedSource.getEmbeddableSource(), componentBinding, containingClassName, attributeName, embeddedSource.getXmlNodeName(), embeddedSource.isVirtualAttribute());
        this.prepareValueTypeViaReflection(sourceDocument, componentBinding, componentBinding.getComponentClassName(), attributeName, embeddedSource.getAttributeRole());
        componentBinding.createForeignKey();
        Property attribute = embeddedSource.isVirtualAttribute() ? new SyntheticProperty(){

            @Override
            public String getPropertyAccessorName() {
                return "embedded";
            }
        } : new Property();
        attribute.setValue(componentBinding);
        this.bindProperty(sourceDocument, embeddedSource, attribute);
        if (StringHelper.isNotEmpty(embeddedSource.getXmlNodeName())) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
        }
        return attribute;
    }

    private Property createBasicAttribute(MappingDocument sourceDocument, final SingularAttributeSourceBasic attributeSource, SimpleValue value, String containingClassName) {
        String attributeName = attributeSource.getName();
        ModelBinder.bindSimpleValueType(sourceDocument, attributeSource.getTypeInformation(), value);
        this.relationalObjectBinder.bindColumnsAndFormulas(sourceDocument, attributeSource.getRelationalValueSources(), value, attributeSource.areValuesNullableByDefault(), new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                return ModelBinder.this.implicitNamingStrategy.determineBasicColumnName(attributeSource);
            }
        });
        this.prepareValueTypeViaReflection(sourceDocument, value, containingClassName, attributeName, attributeSource.getAttributeRole());
        this.resolveLob(attributeSource, value);
        value.createForeignKey();
        Property property = new Property();
        property.setValue(value);
        property.setLob(value.isLob());
        this.bindProperty(sourceDocument, attributeSource, property);
        return property;
    }

    private void resolveLob(SingularAttributeSourceBasic attributeSource, SimpleValue value) {
        TypeResolver typeResolver;
        BasicType basicType;
        if (!value.isLob() && value.getTypeName() != null && (basicType = (typeResolver = attributeSource.getBuildingContext().getMetadataCollector().getTypeResolver()).basic(value.getTypeName())) instanceof AbstractSingleColumnStandardBasicType && ModelBinder.isLob(((AbstractSingleColumnStandardBasicType)basicType).getSqlTypeDescriptor().getSqlType(), null)) {
            value.makeLob();
        }
        if (!value.isLob()) {
            for (RelationalValueSource relationalValueSource : attributeSource.getRelationalValueSources()) {
                if (!ColumnSource.class.isInstance(relationalValueSource) || !ModelBinder.isLob(null, ((ColumnSource)relationalValueSource).getSqlType())) continue;
                value.makeLob();
            }
        }
    }

    private static boolean isLob(Integer sqlType, String sqlTypeName) {
        if (sqlType != null) {
            return ClobType.INSTANCE.getSqlTypeDescriptor().getSqlType() == sqlType.intValue() || BlobType.INSTANCE.getSqlTypeDescriptor().getSqlType() == sqlType.intValue() || NClobType.INSTANCE.getSqlTypeDescriptor().getSqlType() == sqlType.intValue();
        }
        if (sqlTypeName != null) {
            return ClobType.INSTANCE.getName().equalsIgnoreCase(sqlTypeName) || BlobType.INSTANCE.getName().equalsIgnoreCase(sqlTypeName) || NClobType.INSTANCE.getName().equalsIgnoreCase(sqlTypeName);
        }
        return false;
    }

    private Property createOneToOneAttribute(MappingDocument sourceDocument, SingularAttributeSourceOneToOne oneToOneSource, OneToOne oneToOneBinding, String containingClassName) {
        this.bindOneToOne(sourceDocument, oneToOneSource, oneToOneBinding);
        this.prepareValueTypeViaReflection(sourceDocument, oneToOneBinding, containingClassName, oneToOneSource.getName(), oneToOneSource.getAttributeRole());
        String propertyRef = oneToOneBinding.getReferencedPropertyName();
        if (propertyRef != null) {
            this.handlePropertyReference(sourceDocument, oneToOneBinding.getReferencedEntityName(), propertyRef, true, "<one-to-one name=\"" + oneToOneSource.getName() + "\"/>");
        }
        oneToOneBinding.createForeignKey();
        Property prop = new Property();
        prop.setValue(oneToOneBinding);
        this.bindProperty(sourceDocument, oneToOneSource, prop);
        return prop;
    }

    private void handlePropertyReference(MappingDocument mappingDocument, String referencedEntityName, String referencedPropertyName, boolean isUnique, String sourceElementSynopsis) {
        PersistentClass entityBinding = mappingDocument.getMetadataCollector().getEntityBinding(referencedEntityName);
        if (entityBinding == null) {
            this.registerDelayedPropertyReferenceHandler(new DelayedPropertyReferenceHandlerImpl(referencedEntityName, referencedPropertyName, isUnique, sourceElementSynopsis, mappingDocument.getOrigin()), mappingDocument);
        } else {
            Property propertyBinding = entityBinding.getReferencedProperty(referencedPropertyName);
            if (propertyBinding == null) {
                this.registerDelayedPropertyReferenceHandler(new DelayedPropertyReferenceHandlerImpl(referencedEntityName, referencedPropertyName, isUnique, sourceElementSynopsis, mappingDocument.getOrigin()), mappingDocument);
            } else {
                log.tracef("Property [%s.%s] referenced by property-ref [%s] was available - no need for delayed handling", referencedEntityName, referencedPropertyName, sourceElementSynopsis);
                if (isUnique) {
                    ((SimpleValue)propertyBinding.getValue()).setAlternateUniqueKey(true);
                }
            }
        }
    }

    private void registerDelayedPropertyReferenceHandler(DelayedPropertyReferenceHandlerImpl handler, MetadataBuildingContext buildingContext) {
        log.tracef("Property [%s.%s] referenced by property-ref [%s] was not yet available - creating delayed handler", handler.referencedEntityName, handler.referencedPropertyName, handler.sourceElementSynopsis);
        buildingContext.getMetadataCollector().addDelayedPropertyReferenceHandler(handler);
    }

    public void bindOneToOne(MappingDocument sourceDocument, SingularAttributeSourceOneToOne oneToOneSource, OneToOne oneToOneBinding) {
        oneToOneBinding.setPropertyName(oneToOneSource.getName());
        this.relationalObjectBinder.bindFormulas(sourceDocument, oneToOneSource.getFormulaSources(), oneToOneBinding);
        if (oneToOneSource.isConstrained()) {
            if (oneToOneSource.getCascadeStyleName() != null && oneToOneSource.getCascadeStyleName().contains("delete-orphan")) {
                throw new MappingException(String.format(Locale.ENGLISH, "one-to-one attribute [%s] cannot specify orphan delete cascading as it is constrained", oneToOneSource.getAttributeRole().getFullPath()), sourceDocument.getOrigin());
            }
            oneToOneBinding.setConstrained(true);
            oneToOneBinding.setForeignKeyType(ForeignKeyDirection.FROM_PARENT);
        } else {
            oneToOneBinding.setForeignKeyType(ForeignKeyDirection.TO_PARENT);
        }
        oneToOneBinding.setLazy(oneToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED);
        oneToOneBinding.setFetchMode(oneToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT ? FetchMode.SELECT : FetchMode.JOIN);
        oneToOneBinding.setUnwrapProxy(oneToOneSource.getFetchCharacteristics().isUnwrapProxies());
        if (StringHelper.isNotEmpty(oneToOneSource.getReferencedEntityAttributeName())) {
            oneToOneBinding.setReferencedPropertyName(oneToOneSource.getReferencedEntityAttributeName());
            oneToOneBinding.setReferenceToPrimaryKey(false);
        } else {
            oneToOneBinding.setReferenceToPrimaryKey(true);
        }
        oneToOneBinding.setReferencedEntityName(oneToOneSource.getReferencedEntityName());
        if (oneToOneSource.isEmbedXml() == Boolean.TRUE) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
        }
        if (StringHelper.isNotEmpty(oneToOneSource.getExplicitForeignKeyName())) {
            oneToOneBinding.setForeignKeyName(oneToOneSource.getExplicitForeignKeyName());
        }
        oneToOneBinding.setCascadeDeleteEnabled(oneToOneSource.isCascadeDeleteEnabled());
    }

    private Property createManyToOneAttribute(MappingDocument sourceDocument, SingularAttributeSourceManyToOne manyToOneSource, ManyToOne manyToOneBinding, String containingClassName) {
        String referencedEntityName;
        String attributeName = manyToOneSource.getName();
        if (manyToOneSource.getReferencedEntityName() != null) {
            referencedEntityName = manyToOneSource.getReferencedEntityName();
        } else {
            Class reflectedPropertyClass = Helper.reflectedPropertyClass((MetadataBuildingContext)sourceDocument, containingClassName, attributeName);
            if (reflectedPropertyClass != null) {
                referencedEntityName = reflectedPropertyClass.getName();
            } else {
                this.prepareValueTypeViaReflection(sourceDocument, manyToOneBinding, containingClassName, attributeName, manyToOneSource.getAttributeRole());
                referencedEntityName = manyToOneBinding.getTypeName();
            }
        }
        if (manyToOneSource.isUnique()) {
            manyToOneBinding.markAsLogicalOneToOne();
        }
        this.bindManyToOneAttribute(sourceDocument, manyToOneSource, manyToOneBinding, referencedEntityName);
        String propertyRef = manyToOneBinding.getReferencedPropertyName();
        if (propertyRef != null) {
            this.handlePropertyReference(sourceDocument, manyToOneBinding.getReferencedEntityName(), propertyRef, true, "<many-to-one name=\"" + manyToOneSource.getName() + "\"/>");
        }
        Property prop = new Property();
        prop.setValue(manyToOneBinding);
        this.bindProperty(sourceDocument, manyToOneSource, prop);
        if (StringHelper.isNotEmpty(manyToOneSource.getCascadeStyleName()) && manyToOneSource.getCascadeStyleName().contains("delete-orphan") && !manyToOneBinding.isLogicalOneToOne()) {
            throw new MappingException(String.format(Locale.ENGLISH, "many-to-one attribute [%s] specified delete-orphan but is not specified as unique; remove delete-orphan cascading or specify unique=\"true\"", manyToOneSource.getAttributeRole().getFullPath()), sourceDocument.getOrigin());
        }
        return prop;
    }

    private void bindManyToOneAttribute(MappingDocument sourceDocument, SingularAttributeSourceManyToOne manyToOneSource, ManyToOne manyToOneBinding, String referencedEntityName) {
        ManyToOneColumnBinder columnBinder;
        boolean canBindColumnsImmediately;
        manyToOneBinding.setReferencedEntityName(referencedEntityName);
        if (StringHelper.isNotEmpty(manyToOneSource.getReferencedEntityAttributeName())) {
            manyToOneBinding.setReferencedPropertyName(manyToOneSource.getReferencedEntityAttributeName());
            manyToOneBinding.setReferenceToPrimaryKey(false);
        } else {
            manyToOneBinding.setReferenceToPrimaryKey(true);
        }
        manyToOneBinding.setLazy(manyToOneSource.getFetchCharacteristics().getFetchTiming() == FetchTiming.DELAYED);
        manyToOneBinding.setUnwrapProxy(manyToOneSource.getFetchCharacteristics().isUnwrapProxies());
        manyToOneBinding.setFetchMode(manyToOneSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT ? FetchMode.SELECT : FetchMode.JOIN);
        if (manyToOneSource.isEmbedXml() == Boolean.TRUE) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfEmbedXmlSupport();
        }
        manyToOneBinding.setIgnoreNotFound(manyToOneSource.isIgnoreNotFound());
        if (StringHelper.isNotEmpty(manyToOneSource.getExplicitForeignKeyName())) {
            manyToOneBinding.setForeignKeyName(manyToOneSource.getExplicitForeignKeyName());
        }
        if (canBindColumnsImmediately = (columnBinder = new ManyToOneColumnBinder(sourceDocument, manyToOneSource, manyToOneBinding, referencedEntityName)).canProcessImmediately()) {
            columnBinder.doSecondPass(null);
        } else {
            sourceDocument.getMetadataCollector().addSecondPass(columnBinder);
        }
        if (!manyToOneSource.isIgnoreNotFound()) {
            ManyToOneFkSecondPass fkSecondPass = new ManyToOneFkSecondPass(sourceDocument, manyToOneSource, manyToOneBinding, referencedEntityName);
            if (canBindColumnsImmediately && fkSecondPass.canProcessImmediately()) {
                fkSecondPass.doSecondPass(null);
            } else {
                sourceDocument.getMetadataCollector().addSecondPass(fkSecondPass);
            }
        }
        manyToOneBinding.setCascadeDeleteEnabled(manyToOneSource.isCascadeDeleteEnabled());
    }

    private Property createAnyAssociationAttribute(MappingDocument sourceDocument, SingularAttributeSourceAny anyMapping, Any anyBinding, String entityName) {
        String attributeName = anyMapping.getName();
        this.bindAny(sourceDocument, anyMapping, anyBinding, anyMapping.getAttributeRole(), anyMapping.getAttributePath());
        this.prepareValueTypeViaReflection(sourceDocument, anyBinding, entityName, attributeName, anyMapping.getAttributeRole());
        anyBinding.createForeignKey();
        Property prop = new Property();
        prop.setValue(anyBinding);
        this.bindProperty(sourceDocument, anyMapping, prop);
        return prop;
    }

    private void bindAny(MappingDocument sourceDocument, final AnyMappingSource anyMapping, Any anyBinding, AttributeRole attributeRole, AttributePath attributePath) {
        TypeResolution discriminatorTypeResolution;
        anyBinding.setLazy(anyMapping.isLazy());
        TypeResolution keyTypeResolution = ModelBinder.resolveType(sourceDocument, anyMapping.getKeySource().getTypeSource());
        if (keyTypeResolution != null) {
            anyBinding.setIdentifierType(keyTypeResolution.typeName);
        }
        if ((discriminatorTypeResolution = ModelBinder.resolveType(sourceDocument, anyMapping.getDiscriminatorSource().getTypeSource())) != null) {
            anyBinding.setMetaType(discriminatorTypeResolution.typeName);
            try {
                DiscriminatorType metaType = (DiscriminatorType)sourceDocument.getMetadataCollector().getTypeResolver().heuristicType(discriminatorTypeResolution.typeName);
                HashMap anyValueBindingMap = new HashMap();
                for (Map.Entry<String, String> discriminatorValueMappings : anyMapping.getDiscriminatorSource().getValueMappings().entrySet()) {
                    try {
                        Object discriminatorValue = metaType.stringToObject(discriminatorValueMappings.getKey());
                        String mappedEntityName = sourceDocument.qualifyClassName(discriminatorValueMappings.getValue());
                        anyValueBindingMap.put(discriminatorValue, mappedEntityName);
                    }
                    catch (Exception e) {
                        throw new MappingException(String.format(Locale.ENGLISH, "Unable to interpret <meta-value value=\"%s\" class=\"%s\"/> defined as part of <any/> attribute [%s]", discriminatorValueMappings.getKey(), discriminatorValueMappings.getValue(), attributeRole.getFullPath()), e, sourceDocument.getOrigin());
                    }
                }
                anyBinding.setMetaValues(anyValueBindingMap);
            }
            catch (ClassCastException e) {
                throw new MappingException(String.format(Locale.ENGLISH, "Specified meta-type [%s] for <any/> attribute [%s] did not implement DiscriminatorType", discriminatorTypeResolution.typeName, attributeRole.getFullPath()), e, sourceDocument.getOrigin());
            }
        }
        this.relationalObjectBinder.bindColumnOrFormula(sourceDocument, anyMapping.getDiscriminatorSource().getRelationalValueSource(), anyBinding, true, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                return ModelBinder.this.implicitNamingStrategy.determineAnyDiscriminatorColumnName(anyMapping.getDiscriminatorSource());
            }
        });
        this.relationalObjectBinder.bindColumnsAndFormulas(sourceDocument, anyMapping.getKeySource().getRelationalValueSources(), anyBinding, true, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                return ModelBinder.this.implicitNamingStrategy.determineAnyKeyColumnName(anyMapping.getKeySource());
            }
        });
    }

    private void prepareValueTypeViaReflection(MappingDocument sourceDocument, Value value, String containingClassName, String propertyName, AttributeRole attributeRole) {
        if (StringHelper.isEmpty(propertyName)) {
            throw new MappingException(String.format(Locale.ENGLISH, "Attribute mapping must define a name attribute: containingClassName=[%s], propertyName=[%s], role=[%s]", containingClassName, propertyName, attributeRole.getFullPath()), sourceDocument.getOrigin());
        }
        try {
            value.setTypeUsingReflection(containingClassName, propertyName);
        }
        catch (org.hibernate.MappingException ome) {
            throw new MappingException(String.format(Locale.ENGLISH, "Error calling Value#setTypeUsingReflection: containingClassName=[%s], propertyName=[%s], role=[%s]", containingClassName, propertyName, attributeRole.getFullPath()), (Throwable)((Object)ome), sourceDocument.getOrigin());
        }
    }

    private void bindProperty(MappingDocument mappingDocument, AttributeSource propertySource, Property property) {
        property.setName(propertySource.getName());
        if (StringHelper.isNotEmpty(propertySource.getXmlNodeName())) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
        }
        property.setPropertyAccessorName(StringHelper.isNotEmpty(propertySource.getPropertyAccessorName()) ? propertySource.getPropertyAccessorName() : mappingDocument.getMappingDefaults().getImplicitPropertyAccessorName());
        if (propertySource instanceof CascadeStyleSource) {
            CascadeStyleSource cascadeStyleSource = (CascadeStyleSource)((Object)propertySource);
            property.setCascade(StringHelper.isNotEmpty(cascadeStyleSource.getCascadeStyleName()) ? cascadeStyleSource.getCascadeStyleName() : mappingDocument.getMappingDefaults().getImplicitCascadeStyleName());
        }
        property.setOptimisticLocked(propertySource.isIncludedInOptimisticLocking());
        if (propertySource.isSingular()) {
            SingularAttributeSource singularAttributeSource = (SingularAttributeSource)propertySource;
            property.setInsertable(singularAttributeSource.isInsertable());
            property.setUpdateable(singularAttributeSource.isUpdatable());
            property.setLazy(singularAttributeSource.isBytecodeLazy());
            GenerationTiming generationTiming = singularAttributeSource.getGenerationTiming();
            if (generationTiming == GenerationTiming.ALWAYS || generationTiming == GenerationTiming.INSERT) {
                property.setValueGenerationStrategy(new GeneratedValueGeneration(generationTiming));
                if (property.isInsertable()) {
                    log.debugf("Property [%s] specified %s generation, setting insertable to false : %s", propertySource.getName(), generationTiming.name(), mappingDocument.getOrigin());
                    property.setInsertable(false);
                }
                if (property.isUpdateable() && generationTiming == GenerationTiming.ALWAYS) {
                    log.debugf("Property [%s] specified ALWAYS generation, setting updateable to false : %s", propertySource.getName(), mappingDocument.getOrigin());
                    property.setUpdateable(false);
                }
            }
        }
        property.setMetaAttributes(propertySource.getToolingHintContext().getMetaAttributeMap());
        if (log.isDebugEnabled()) {
            StringBuilder message = new StringBuilder().append("Mapped property: ").append(propertySource.getName()).append(" -> [");
            Iterator<Selectable> itr = property.getValue().getColumnIterator();
            while (itr.hasNext()) {
                message.append(itr.next().getText());
                if (!itr.hasNext()) continue;
                message.append(", ");
            }
            message.append("]");
            log.debug(message.toString());
        }
    }

    private void bindComponent(MappingDocument sourceDocument, EmbeddableSource embeddableSource, Component component, String containingClassName, String propertyName, String xmlNodeName, boolean isVirtual) {
        String fullRole = embeddableSource.getAttributeRoleBase().getFullPath();
        String explicitComponentClassName = this.extractExplicitComponentClassName(embeddableSource);
        this.bindComponent(sourceDocument, fullRole, embeddableSource, component, explicitComponentClassName, containingClassName, propertyName, isVirtual, embeddableSource.isDynamic(), xmlNodeName);
    }

    private String extractExplicitComponentClassName(EmbeddableSource embeddableSource) {
        if (embeddableSource.getTypeDescriptor() == null) {
            return null;
        }
        return embeddableSource.getTypeDescriptor().getName();
    }

    private void bindComponent(MappingDocument sourceDocument, String role, EmbeddableSource embeddableSource, Component componentBinding, String explicitComponentClassName, String containingClassName, String propertyName, boolean isVirtual, boolean isDynamic, String xmlNodeName) {
        componentBinding.setMetaAttributes(embeddableSource.getToolingHintContext().getMetaAttributeMap());
        componentBinding.setRoleName(role);
        componentBinding.setEmbedded(isVirtual);
        if (isDynamic) {
            log.debugf("Binding dynamic-component [%s]", role);
            componentBinding.setDynamic(true);
        } else if (isVirtual) {
            if (componentBinding.getOwner().hasPojoRepresentation()) {
                log.debugf("Binding virtual component [%s] to owner class [%s]", role, componentBinding.getOwner().getClassName());
                componentBinding.setComponentClassName(componentBinding.getOwner().getClassName());
            } else {
                log.debugf("Binding virtual component [%s] as dynamic", role);
                componentBinding.setDynamic(true);
            }
        } else {
            log.debugf("Binding component [%s]", role);
            if (StringHelper.isNotEmpty(explicitComponentClassName)) {
                log.debugf("Binding component [%s] to explicitly specified class", role, explicitComponentClassName);
                componentBinding.setComponentClassName(explicitComponentClassName);
            } else if (componentBinding.getOwner().hasPojoRepresentation()) {
                log.tracef("Attempting to determine component class by reflection %s", role);
                Class reflectedComponentClass = StringHelper.isNotEmpty(containingClassName) && StringHelper.isNotEmpty(propertyName) ? Helper.reflectedPropertyClass((MetadataBuildingContext)sourceDocument, containingClassName, propertyName) : null;
                if (reflectedComponentClass == null) {
                    log.debugf("Unable to determine component class name via reflection, and explicit class name not given; role=[%s]", role);
                } else {
                    componentBinding.setComponentClassName(reflectedComponentClass.getName());
                }
            } else {
                componentBinding.setDynamic(true);
            }
        }
        String nodeName = xmlNodeName;
        if (StringHelper.isNotEmpty(nodeName)) {
            DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfDomEntityModeSupport();
        }
        this.bindAllCompositeAttributes(sourceDocument, embeddableSource, componentBinding);
        if (embeddableSource.getParentReferenceAttributeName() != null) {
            componentBinding.setParentProperty(embeddableSource.getParentReferenceAttributeName());
        }
        if (embeddableSource.isUnique()) {
            ArrayList<Column> cols = new ArrayList<Column>();
            Iterator<Selectable> itr = componentBinding.getColumnIterator();
            while (itr.hasNext()) {
                Selectable selectable = itr.next();
                if (!Column.class.isInstance(selectable)) continue;
                cols.add((Column)selectable);
            }
            componentBinding.getOwner().getTable().createUniqueKey(cols);
        }
        if (embeddableSource.getTuplizerClassMap() != null) {
            if (embeddableSource.getTuplizerClassMap().size() > 1) {
                DeprecationLogger.DEPRECATION_LOGGER.logDeprecationOfMultipleEntityModeSupport();
            }
            for (Map.Entry<EntityMode, String> tuplizerEntry : embeddableSource.getTuplizerClassMap().entrySet()) {
                componentBinding.addTuplizer(tuplizerEntry.getKey(), tuplizerEntry.getValue());
            }
        }
    }

    private void prepareComponentType(MappingDocument sourceDocument, String fullRole, Component componentBinding, String explicitComponentClassName, String containingClassName, String propertyName, boolean isVirtual, boolean isDynamic) {
    }

    private void bindAllCompositeAttributes(MappingDocument sourceDocument, EmbeddableSource embeddableSource, Component component) {
        for (AttributeSource attributeSource : embeddableSource.attributeSources()) {
            Property attribute = null;
            if (SingularAttributeSourceBasic.class.isInstance(attributeSource)) {
                attribute = this.createBasicAttribute(sourceDocument, (SingularAttributeSourceBasic)attributeSource, new SimpleValue(sourceDocument, component.getTable()), component.getComponentClassName());
            } else if (SingularAttributeSourceEmbedded.class.isInstance(attributeSource)) {
                attribute = this.createEmbeddedAttribute(sourceDocument, (SingularAttributeSourceEmbedded)attributeSource, new Component((MetadataBuildingContext)sourceDocument, component), component.getComponentClassName());
            } else if (SingularAttributeSourceManyToOne.class.isInstance(attributeSource)) {
                attribute = this.createManyToOneAttribute(sourceDocument, (SingularAttributeSourceManyToOne)attributeSource, new ManyToOne(sourceDocument, component.getTable()), component.getComponentClassName());
            } else if (SingularAttributeSourceOneToOne.class.isInstance(attributeSource)) {
                attribute = this.createOneToOneAttribute(sourceDocument, (SingularAttributeSourceOneToOne)attributeSource, new OneToOne(sourceDocument, component.getTable(), component.getOwner()), component.getComponentClassName());
            } else if (SingularAttributeSourceAny.class.isInstance(attributeSource)) {
                attribute = this.createAnyAssociationAttribute(sourceDocument, (SingularAttributeSourceAny)attributeSource, new Any(sourceDocument, component.getTable()), component.getComponentClassName());
            } else if (PluralAttributeSource.class.isInstance(attributeSource)) {
                attribute = this.createPluralAttribute(sourceDocument, (PluralAttributeSource)attributeSource, component.getOwner());
            } else {
                throw new AssertionFailure(String.format(Locale.ENGLISH, "Unexpected AttributeSource sub-type [%s] as part of composite [%s]", attributeSource.getClass().getName(), attributeSource.getAttributeRole().getFullPath()));
            }
            component.addProperty(attribute);
        }
    }

    private static void bindSimpleValueType(MappingDocument mappingDocument, HibernateTypeSource typeSource, SimpleValue simpleValue) {
        TypeResolution typeResolution;
        if (mappingDocument.getBuildingOptions().useNationalizedCharacterData()) {
            simpleValue.makeNationalized();
        }
        if ((typeResolution = ModelBinder.resolveType(mappingDocument, typeSource)) == null) {
            return;
        }
        if (CollectionHelper.isNotEmpty(typeResolution.parameters)) {
            simpleValue.setTypeParameters(typeResolution.parameters);
        }
        if (typeResolution.typeName != null) {
            simpleValue.setTypeName(typeResolution.typeName);
        }
    }

    private static TypeResolution resolveType(MappingDocument sourceDocument, HibernateTypeSource typeSource) {
        if (StringHelper.isEmpty(typeSource.getName())) {
            return null;
        }
        String typeName = typeSource.getName();
        Properties typeParameters = new Properties();
        TypeDefinition typeDefinition = sourceDocument.getMetadataCollector().getTypeDefinition(typeName);
        if (typeDefinition != null) {
            typeName = typeDefinition.getTypeImplementorClass().getName();
            if (typeDefinition.getParameters() != null) {
                typeParameters.putAll(typeDefinition.getParameters());
            }
        }
        if (typeSource.getParameters() != null) {
            typeParameters.putAll(typeSource.getParameters());
        }
        return new TypeResolution(typeName, typeParameters);
    }

    private Table bindEntityTableSpecification(final MappingDocument mappingDocument, TableSpecificationSource tableSpecSource, Table denormalizedSuperTable, final EntitySource entitySource, PersistentClass entityDescriptor) {
        Table table;
        Identifier logicalTableName;
        boolean isAbstract;
        Namespace namespace = this.database.locateNamespace(this.determineCatalogName(tableSpecSource), this.determineSchemaName(tableSpecSource));
        boolean isTable = TableSource.class.isInstance(tableSpecSource);
        boolean bl = isAbstract = entityDescriptor.isAbstract() == null ? false : entityDescriptor.isAbstract();
        if (isTable) {
            TableSource tableSource = (TableSource)tableSpecSource;
            if (StringHelper.isNotEmpty(tableSource.getExplicitTableName())) {
                logicalTableName = this.database.toIdentifier(tableSource.getExplicitTableName());
            } else {
                ImplicitEntityNameSource implicitNamingSource = new ImplicitEntityNameSource(){

                    @Override
                    public EntityNaming getEntityNaming() {
                        return entitySource.getEntityNamingSource();
                    }

                    @Override
                    public MetadataBuildingContext getBuildingContext() {
                        return mappingDocument;
                    }
                };
                logicalTableName = mappingDocument.getBuildingOptions().getImplicitNamingStrategy().determinePrimaryTableName(implicitNamingSource);
            }
            table = denormalizedSuperTable == null ? namespace.createTable(logicalTableName, isAbstract) : namespace.createDenormalizedTable(logicalTableName, isAbstract, denormalizedSuperTable);
        } else {
            InLineViewSource inLineViewSource = (InLineViewSource)tableSpecSource;
            String subselect = inLineViewSource.getSelectStatement();
            logicalTableName = this.database.toIdentifier(inLineViewSource.getLogicalName());
            table = denormalizedSuperTable == null ? new Table(namespace, subselect, isAbstract) : new DenormalizedTable(namespace, subselect, isAbstract, denormalizedSuperTable);
            table.setName(logicalTableName.render());
        }
        InFlightMetadataCollector.EntityTableXref superEntityTableXref = null;
        if (entitySource.getSuperType() != null) {
            String superEntityName = ((EntitySource)entitySource.getSuperType()).getEntityNamingSource().getEntityName();
            superEntityTableXref = mappingDocument.getMetadataCollector().getEntityTableXref(superEntityName);
            if (superEntityTableXref == null) {
                throw new MappingException(String.format(Locale.ENGLISH, "Unable to locate entity table xref for entity [%s] super-type [%s]", entityDescriptor.getEntityName(), superEntityName), mappingDocument.getOrigin());
            }
        }
        mappingDocument.getMetadataCollector().addEntityTableXref(entitySource.getEntityNamingSource().getEntityName(), logicalTableName, table, superEntityTableXref);
        if (isTable) {
            TableSource tableSource = (TableSource)tableSpecSource;
            table.setRowId(tableSource.getRowId());
            if (StringHelper.isNotEmpty(tableSource.getCheckConstraint())) {
                table.addCheckConstraint(tableSource.getCheckConstraint());
            }
        }
        table.setComment(tableSpecSource.getComment());
        mappingDocument.getMetadataCollector().addTableNameBinding(logicalTableName, table);
        return table;
    }

    private Identifier determineCatalogName(TableSpecificationSource tableSpecSource) {
        if (StringHelper.isNotEmpty(tableSpecSource.getExplicitCatalogName())) {
            return this.database.toIdentifier(tableSpecSource.getExplicitCatalogName());
        }
        return null;
    }

    private Identifier determineSchemaName(TableSpecificationSource tableSpecSource) {
        if (StringHelper.isNotEmpty(tableSpecSource.getExplicitSchemaName())) {
            return this.database.toIdentifier(tableSpecSource.getExplicitSchemaName());
        }
        return null;
    }

    private static void bindCustomSql(MappingDocument sourceDocument, EntitySource entitySource, PersistentClass entityDescriptor) {
        if (entitySource.getCustomSqlInsert() != null) {
            entityDescriptor.setCustomSQLInsert(entitySource.getCustomSqlInsert().getSql(), entitySource.getCustomSqlInsert().isCallable(), entitySource.getCustomSqlInsert().getCheckStyle());
        }
        if (entitySource.getCustomSqlUpdate() != null) {
            entityDescriptor.setCustomSQLUpdate(entitySource.getCustomSqlUpdate().getSql(), entitySource.getCustomSqlUpdate().isCallable(), entitySource.getCustomSqlUpdate().getCheckStyle());
        }
        if (entitySource.getCustomSqlDelete() != null) {
            entityDescriptor.setCustomSQLDelete(entitySource.getCustomSqlDelete().getSql(), entitySource.getCustomSqlDelete().isCallable(), entitySource.getCustomSqlDelete().getCheckStyle());
        }
        entityDescriptor.setLoaderName(entitySource.getCustomLoaderName());
    }

    private static void bindCustomSql(MappingDocument sourceDocument, SecondaryTableSource secondaryTableSource, Join secondaryTable) {
        if (secondaryTableSource.getCustomSqlInsert() != null) {
            secondaryTable.setCustomSQLInsert(secondaryTableSource.getCustomSqlInsert().getSql(), secondaryTableSource.getCustomSqlInsert().isCallable(), secondaryTableSource.getCustomSqlInsert().getCheckStyle());
        }
        if (secondaryTableSource.getCustomSqlUpdate() != null) {
            secondaryTable.setCustomSQLUpdate(secondaryTableSource.getCustomSqlUpdate().getSql(), secondaryTableSource.getCustomSqlUpdate().isCallable(), secondaryTableSource.getCustomSqlUpdate().getCheckStyle());
        }
        if (secondaryTableSource.getCustomSqlDelete() != null) {
            secondaryTable.setCustomSQLDelete(secondaryTableSource.getCustomSqlDelete().getSql(), secondaryTableSource.getCustomSqlDelete().isCallable(), secondaryTableSource.getCustomSqlDelete().getCheckStyle());
        }
    }

    private void registerSecondPass(SecondPass secondPass, MetadataBuildingContext context) {
        context.getMetadataCollector().addSecondPass(secondPass);
    }

    private boolean useEntityWhereClauseForCollections() {
        return ConfigurationHelper.getBoolean("hibernate.use_entity_where_clause_for_collections", this.metadataBuildingContext.getBuildingOptions().getServiceRegistry().getService(ConfigurationService.class).getSettings(), true);
    }

    private void createIndexBackRef(MappingDocument mappingDocument, IndexedPluralAttributeSource pluralAttributeSource, IndexedCollection collectionBinding) {
        if (collectionBinding.isOneToMany() && !collectionBinding.getKey().isNullable() && !collectionBinding.isInverse()) {
            String entityName = ((OneToMany)collectionBinding.getElement()).getReferencedEntityName();
            PersistentClass referenced = mappingDocument.getMetadataCollector().getEntityBinding(entityName);
            IndexBackref ib = new IndexBackref();
            ib.setName('_' + collectionBinding.getOwnerEntityName() + "." + pluralAttributeSource.getName() + "IndexBackref");
            ib.setUpdateable(false);
            ib.setSelectable(false);
            ib.setCollectionRole(collectionBinding.getRole());
            ib.setEntityName(collectionBinding.getOwner().getEntityName());
            ib.setValue(collectionBinding.getIndex());
            referenced.addProperty(ib);
        }
    }

    public void bindListOrArrayIndex(MappingDocument mappingDocument, final IndexedPluralAttributeSource attributeSource, List collectionBinding) {
        PluralAttributeSequentialIndexSource indexSource = (PluralAttributeSequentialIndexSource)attributeSource.getIndexSource();
        SimpleValue indexBinding = new SimpleValue(mappingDocument, collectionBinding.getCollectionTable());
        ModelBinder.bindSimpleValueType(mappingDocument, indexSource.getTypeInformation(), indexBinding);
        this.relationalObjectBinder.bindColumnsAndFormulas(mappingDocument, indexSource.getRelationalValueSources(), indexBinding, attributeSource.getElementSource() instanceof PluralAttributeElementSourceOneToMany, new RelationalObjectBinder.ColumnNamingDelegate(){

            @Override
            public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
                return context.getBuildingOptions().getImplicitNamingStrategy().determineListIndexColumnName(new ImplicitIndexColumnNameSource(){

                    @Override
                    public AttributePath getPluralAttributePath() {
                        return attributeSource.getAttributePath();
                    }

                    @Override
                    public MetadataBuildingContext getBuildingContext() {
                        return context;
                    }
                });
            }
        });
        collectionBinding.setIndex(indexBinding);
        collectionBinding.setBaseIndex(indexSource.getBase());
    }

    private void bindMapKey(MappingDocument mappingDocument, final IndexedPluralAttributeSource pluralAttributeSource, Map collectionBinding) {
        if (pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeySourceBasic) {
            PluralAttributeMapKeySourceBasic mapKeySource = (PluralAttributeMapKeySourceBasic)pluralAttributeSource.getIndexSource();
            SimpleValue value = new SimpleValue(mappingDocument, collectionBinding.getCollectionTable());
            ModelBinder.bindSimpleValueType(mappingDocument, mapKeySource.getTypeInformation(), value);
            if (!value.isTypeSpecified()) {
                throw new MappingException("map index element must specify a type: " + pluralAttributeSource.getAttributeRole().getFullPath(), mappingDocument.getOrigin());
            }
            this.relationalObjectBinder.bindColumnsAndFormulas(mappingDocument, mapKeySource.getRelationalValueSources(), value, true, new RelationalObjectBinder.ColumnNamingDelegate(){

                @Override
                public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                    return ModelBinder.this.database.toIdentifier("idx");
                }
            });
            collectionBinding.setIndex(value);
        } else if (pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeySourceEmbedded) {
            PluralAttributeMapKeySourceEmbedded mapKeySource = (PluralAttributeMapKeySourceEmbedded)pluralAttributeSource.getIndexSource();
            Component componentBinding = new Component((MetadataBuildingContext)mappingDocument, (Collection)collectionBinding);
            this.bindComponent(mappingDocument, mapKeySource.getEmbeddableSource(), componentBinding, null, pluralAttributeSource.getName(), mapKeySource.getXmlNodeName(), false);
            collectionBinding.setIndex(componentBinding);
        } else if (pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeyManyToManySource) {
            PluralAttributeMapKeyManyToManySource mapKeySource = (PluralAttributeMapKeyManyToManySource)pluralAttributeSource.getIndexSource();
            ManyToOne mapKeyBinding = new ManyToOne(mappingDocument, collectionBinding.getCollectionTable());
            mapKeyBinding.setReferencedEntityName(mapKeySource.getReferencedEntityName());
            this.relationalObjectBinder.bindColumnsAndFormulas(mappingDocument, mapKeySource.getRelationalValueSources(), mapKeyBinding, true, new RelationalObjectBinder.ColumnNamingDelegate(){

                @Override
                public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
                    return ModelBinder.this.implicitNamingStrategy.determineMapKeyColumnName(new ImplicitMapKeyColumnNameSource(){

                        @Override
                        public AttributePath getPluralAttributePath() {
                            return pluralAttributeSource.getAttributePath();
                        }

                        @Override
                        public MetadataBuildingContext getBuildingContext() {
                            return context;
                        }
                    });
                }
            });
            collectionBinding.setIndex(mapKeyBinding);
        } else if (pluralAttributeSource.getIndexSource() instanceof PluralAttributeMapKeyManyToAnySource) {
            PluralAttributeMapKeyManyToAnySource mapKeySource = (PluralAttributeMapKeyManyToAnySource)pluralAttributeSource.getIndexSource();
            Any mapKeyBinding = new Any(mappingDocument, collectionBinding.getCollectionTable());
            this.bindAny(mappingDocument, mapKeySource, mapKeyBinding, pluralAttributeSource.getAttributeRole().append("key"), pluralAttributeSource.getAttributePath().append("key"));
            collectionBinding.setIndex(mapKeyBinding);
        }
    }

    private static class NaturalIdUniqueKeyBinderImpl
    implements NaturalIdUniqueKeyBinder {
        private final MappingDocument mappingDocument;
        private final PersistentClass entityBinding;
        private final java.util.List<Property> attributeBindings = new ArrayList<Property>();

        public NaturalIdUniqueKeyBinderImpl(MappingDocument mappingDocument, PersistentClass entityBinding) {
            this.mappingDocument = mappingDocument;
            this.entityBinding = entityBinding;
        }

        @Override
        public void addAttributeBinding(Property attributeBinding) {
            this.attributeBindings.add(attributeBinding);
        }

        @Override
        public void process() {
            log.debugf("Binding natural-id UniqueKey for entity : " + this.entityBinding.getEntityName(), new Object[0]);
            final ArrayList<Identifier> columnNames = new ArrayList<Identifier>();
            final UniqueKey uk = new UniqueKey();
            uk.setTable(this.entityBinding.getTable());
            for (Property attributeBinding : this.attributeBindings) {
                Iterator itr = attributeBinding.getColumnIterator();
                while (itr.hasNext()) {
                    Object selectable = itr.next();
                    if (!Column.class.isInstance(selectable)) continue;
                    Column column = (Column)selectable;
                    uk.addColumn(column);
                    columnNames.add(this.mappingDocument.getMetadataCollector().getDatabase().toIdentifier(column.getQuotedName()));
                }
                uk.addColumns(attributeBinding.getColumnIterator());
            }
            Identifier ukName = this.mappingDocument.getBuildingOptions().getImplicitNamingStrategy().determineUniqueKeyName(new ImplicitUniqueKeyNameSource(){

                @Override
                public Identifier getTableName() {
                    return entityBinding.getTable().getNameIdentifier();
                }

                @Override
                public java.util.List<Identifier> getColumnNames() {
                    return columnNames;
                }

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return mappingDocument;
                }

                @Override
                public Identifier getUserProvidedIdentifier() {
                    return uk.getName() != null ? Identifier.toIdentifier(uk.getName()) : null;
                }
            });
            uk.setName(ukName.render(this.mappingDocument.getMetadataCollector().getDatabase().getDialect()));
            this.entityBinding.getTable().addUniqueKey(uk);
        }
    }

    private static class ManyToOneFkSecondPass
    extends FkSecondPass {
        private final MappingDocument mappingDocument;
        private final ManyToOne manyToOneBinding;
        private final String referencedEntityName;
        private final String referencedEntityAttributeName;

        public ManyToOneFkSecondPass(MappingDocument mappingDocument, SingularAttributeSourceManyToOne manyToOneSource, ManyToOne manyToOneBinding, String referencedEntityName) {
            super(manyToOneBinding, null);
            if (referencedEntityName == null) {
                throw new MappingException("entity name referenced by many-to-one required [" + manyToOneSource.getAttributeRole().getFullPath() + "]", mappingDocument.getOrigin());
            }
            this.mappingDocument = mappingDocument;
            this.manyToOneBinding = manyToOneBinding;
            this.referencedEntityName = referencedEntityName;
            this.referencedEntityAttributeName = manyToOneSource.getReferencedEntityAttributeName();
        }

        @Override
        public String getReferencedEntityName() {
            return this.referencedEntityName;
        }

        @Override
        public boolean isInPrimaryKey() {
            return false;
        }

        @Override
        public void doSecondPass(java.util.Map persistentClasses) throws org.hibernate.MappingException {
            if (this.referencedEntityAttributeName == null) {
                this.manyToOneBinding.createForeignKey();
            } else {
                this.manyToOneBinding.createPropertyRefConstraints(this.mappingDocument.getMetadataCollector().getEntityBindingMap());
            }
        }

        public boolean canProcessImmediately() {
            PersistentClass referencedEntityBinding = this.mappingDocument.getMetadataCollector().getEntityBinding(this.referencedEntityName);
            return referencedEntityBinding != null && this.referencedEntityAttributeName != null;
        }
    }

    private class ManyToOneColumnBinder
    implements ImplicitColumnNamingSecondPass {
        private final MappingDocument mappingDocument;
        private final SingularAttributeSourceManyToOne manyToOneSource;
        private final ManyToOne manyToOneBinding;
        private final String referencedEntityName;
        private final boolean allColumnsNamed;

        public ManyToOneColumnBinder(MappingDocument mappingDocument, SingularAttributeSourceManyToOne manyToOneSource, ManyToOne manyToOneBinding, String referencedEntityName) {
            this.mappingDocument = mappingDocument;
            this.manyToOneSource = manyToOneSource;
            this.manyToOneBinding = manyToOneBinding;
            this.referencedEntityName = referencedEntityName;
            boolean allNamed = true;
            for (RelationalValueSource relationalValueSource : manyToOneSource.getRelationalValueSources()) {
                if (!(relationalValueSource instanceof ColumnSource) || ((ColumnSource)relationalValueSource).getName() != null) continue;
                allNamed = false;
                break;
            }
            this.allColumnsNamed = allNamed;
        }

        public boolean canProcessImmediately() {
            if (this.allColumnsNamed) {
                return true;
            }
            PersistentClass referencedEntityBinding = this.mappingDocument.getMetadataCollector().getEntityBinding(this.referencedEntityName);
            if (referencedEntityBinding == null) {
                return false;
            }
            return this.manyToOneSource.getReferencedEntityAttributeName() == null;
        }

        @Override
        public void doSecondPass(java.util.Map persistentClasses) throws org.hibernate.MappingException {
            if (this.allColumnsNamed) {
                ModelBinder.this.relationalObjectBinder.bindColumnsAndFormulas(this.mappingDocument, this.manyToOneSource.getRelationalValueSources(), this.manyToOneBinding, this.manyToOneSource.areValuesNullableByDefault(), new RelationalObjectBinder.ColumnNamingDelegate(){

                    @Override
                    public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                        throw new AssertionFailure("Argh!!!");
                    }
                });
            } else {
                PersistentClass referencedEntityBinding = this.mappingDocument.getMetadataCollector().getEntityBinding(this.referencedEntityName);
                if (referencedEntityBinding == null) {
                    throw new AssertionFailure("Unable to locate referenced entity mapping [" + this.referencedEntityName + "] in order to process many-to-one FK : " + this.manyToOneSource.getAttributeRole().getFullPath());
                }
                ModelBinder.this.relationalObjectBinder.bindColumnsAndFormulas(this.mappingDocument, this.manyToOneSource.getRelationalValueSources(), this.manyToOneBinding, this.manyToOneSource.areValuesNullableByDefault(), new RelationalObjectBinder.ColumnNamingDelegate(){

                    @Override
                    public Identifier determineImplicitName(final LocalMetadataBuildingContext context) {
                        return ModelBinder.this.implicitNamingStrategy.determineBasicColumnName(new ImplicitBasicColumnNameSource(){

                            @Override
                            public AttributePath getAttributePath() {
                                return ManyToOneColumnBinder.this.manyToOneSource.getAttributePath();
                            }

                            @Override
                            public boolean isCollectionElement() {
                                return false;
                            }

                            @Override
                            public MetadataBuildingContext getBuildingContext() {
                                return context;
                            }
                        });
                    }
                });
            }
        }
    }

    private class PluralAttributePrimitiveArraySecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributePrimitiveArraySecondPass(MappingDocument sourceDocument, IndexedPluralAttributeSource attributeSource, PrimitiveArray collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }

        @Override
        public IndexedPluralAttributeSource getPluralAttributeSource() {
            return (IndexedPluralAttributeSource)super.getPluralAttributeSource();
        }

        @Override
        public PrimitiveArray getCollectionBinding() {
            return (PrimitiveArray)super.getCollectionBinding();
        }

        @Override
        protected void bindCollectionIndex() {
            ModelBinder.this.bindListOrArrayIndex(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }

        @Override
        protected void createBackReferences() {
            super.createBackReferences();
            ModelBinder.this.createIndexBackRef(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }
    }

    private class PluralAttributeArraySecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeArraySecondPass(MappingDocument sourceDocument, IndexedPluralAttributeSource attributeSource, Array collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }

        @Override
        public IndexedPluralAttributeSource getPluralAttributeSource() {
            return (IndexedPluralAttributeSource)super.getPluralAttributeSource();
        }

        @Override
        public Array getCollectionBinding() {
            return (Array)super.getCollectionBinding();
        }

        @Override
        protected void bindCollectionIndex() {
            ModelBinder.this.bindListOrArrayIndex(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }

        @Override
        protected void createBackReferences() {
            super.createBackReferences();
            ModelBinder.this.createIndexBackRef(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }
    }

    private class PluralAttributeIdBagSecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeIdBagSecondPass(MappingDocument sourceDocument, PluralAttributeSource attributeSource, Collection collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }
    }

    private class PluralAttributeBagSecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeBagSecondPass(MappingDocument sourceDocument, PluralAttributeSource attributeSource, Collection collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }
    }

    private class PluralAttributeMapSecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeMapSecondPass(MappingDocument sourceDocument, IndexedPluralAttributeSource attributeSource, Map collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }

        @Override
        public IndexedPluralAttributeSource getPluralAttributeSource() {
            return (IndexedPluralAttributeSource)super.getPluralAttributeSource();
        }

        @Override
        public Map getCollectionBinding() {
            return (Map)super.getCollectionBinding();
        }

        @Override
        protected void bindCollectionIndex() {
            ModelBinder.this.bindMapKey(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }

        @Override
        protected void createBackReferences() {
            super.createBackReferences();
            boolean indexIsFormula = false;
            Iterator<Selectable> itr = this.getCollectionBinding().getIndex().getColumnIterator();
            while (itr.hasNext()) {
                if (!itr.next().isFormula()) continue;
                indexIsFormula = true;
            }
            if (this.getCollectionBinding().isOneToMany() && !this.getCollectionBinding().getKey().isNullable() && !this.getCollectionBinding().isInverse() && !indexIsFormula) {
                String entityName = ((OneToMany)this.getCollectionBinding().getElement()).getReferencedEntityName();
                PersistentClass referenced = this.getMappingDocument().getMetadataCollector().getEntityBinding(entityName);
                IndexBackref ib = new IndexBackref();
                ib.setName('_' + this.getCollectionBinding().getOwnerEntityName() + "." + this.getPluralAttributeSource().getName() + "IndexBackref");
                ib.setUpdateable(false);
                ib.setSelectable(false);
                ib.setCollectionRole(this.getCollectionBinding().getRole());
                ib.setEntityName(this.getCollectionBinding().getOwner().getEntityName());
                ib.setValue(this.getCollectionBinding().getIndex());
                referenced.addProperty(ib);
            }
        }
    }

    private class PluralAttributeSetSecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeSetSecondPass(MappingDocument sourceDocument, PluralAttributeSource attributeSource, Collection collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }
    }

    private class PluralAttributeListSecondPass
    extends AbstractPluralAttributeSecondPass {
        public PluralAttributeListSecondPass(MappingDocument sourceDocument, IndexedPluralAttributeSource attributeSource, List collectionBinding) {
            super(sourceDocument, attributeSource, collectionBinding);
        }

        @Override
        public IndexedPluralAttributeSource getPluralAttributeSource() {
            return (IndexedPluralAttributeSource)super.getPluralAttributeSource();
        }

        @Override
        public List getCollectionBinding() {
            return (List)super.getCollectionBinding();
        }

        @Override
        protected void bindCollectionIndex() {
            ModelBinder.this.bindListOrArrayIndex(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }

        @Override
        protected void createBackReferences() {
            super.createBackReferences();
            ModelBinder.this.createIndexBackRef(this.getMappingDocument(), this.getPluralAttributeSource(), this.getCollectionBinding());
        }
    }

    private abstract class AbstractPluralAttributeSecondPass
    implements SecondPass {
        private final MappingDocument mappingDocument;
        private final PluralAttributeSource pluralAttributeSource;
        private final Collection collectionBinding;

        protected AbstractPluralAttributeSecondPass(MappingDocument mappingDocument, PluralAttributeSource pluralAttributeSource, Collection collectionBinding) {
            this.mappingDocument = mappingDocument;
            this.pluralAttributeSource = pluralAttributeSource;
            this.collectionBinding = collectionBinding;
        }

        public MappingDocument getMappingDocument() {
            return this.mappingDocument;
        }

        public PluralAttributeSource getPluralAttributeSource() {
            return this.pluralAttributeSource;
        }

        public Collection getCollectionBinding() {
            return this.collectionBinding;
        }

        @Override
        public void doSecondPass(java.util.Map persistentClasses) throws org.hibernate.MappingException {
            this.bindCollectionTable();
            this.bindCollectionKey();
            this.bindCollectionIdentifier();
            this.bindCollectionIndex();
            this.bindCollectionElement();
            this.createBackReferences();
            this.collectionBinding.createAllKeys();
            if (log.isDebugEnabled()) {
                log.debugf("Mapped collection : " + this.getPluralAttributeSource().getAttributeRole().getFullPath(), new Object[0]);
                log.debugf("   + table -> " + this.getCollectionBinding().getTable().getName(), new Object[0]);
                log.debugf("   + key -> " + this.columns(this.getCollectionBinding().getKey()), new Object[0]);
                if (this.getCollectionBinding().isIndexed()) {
                    log.debugf("   + index -> " + this.columns(((IndexedCollection)this.getCollectionBinding()).getIndex()), new Object[0]);
                }
                if (this.getCollectionBinding().isOneToMany()) {
                    log.debugf("   + one-to-many -> " + ((OneToMany)this.getCollectionBinding().getElement()).getReferencedEntityName(), new Object[0]);
                } else {
                    log.debugf("   + element -> " + this.columns(this.getCollectionBinding().getElement()), new Object[0]);
                }
            }
        }

        private String columns(Value value) {
            StringBuilder builder = new StringBuilder();
            Iterator<Selectable> selectableItr = value.getColumnIterator();
            while (selectableItr.hasNext()) {
                builder.append(selectableItr.next().getText());
                if (!selectableItr.hasNext()) continue;
                builder.append(", ");
            }
            return builder.toString();
        }

        private void bindCollectionTable() {
            if (this.pluralAttributeSource.getElementSource() instanceof PluralAttributeElementSourceOneToMany) {
                PluralAttributeElementSourceOneToMany elementSource = (PluralAttributeElementSourceOneToMany)this.pluralAttributeSource.getElementSource();
                PersistentClass persistentClass = this.mappingDocument.getMetadataCollector().getEntityBinding(elementSource.getReferencedEntityName());
                if (persistentClass == null) {
                    throw new MappingException(String.format(Locale.ENGLISH, "Association [%s] references an unmapped entity [%s]", this.pluralAttributeSource.getAttributeRole().getFullPath(), this.pluralAttributeSource.getAttributeRole().getFullPath()), this.mappingDocument.getOrigin());
                }
                this.collectionBinding.setCollectionTable(persistentClass.getTable());
            } else {
                Table collectionTable;
                TableSpecificationSource tableSpecSource = this.pluralAttributeSource.getCollectionTableSpecificationSource();
                Identifier logicalCatalogName = ModelBinder.this.determineCatalogName(tableSpecSource);
                Identifier logicalSchemaName = ModelBinder.this.determineSchemaName(tableSpecSource);
                Namespace namespace = ModelBinder.this.database.locateNamespace(logicalCatalogName, logicalSchemaName);
                if (tableSpecSource instanceof TableSource) {
                    Identifier logicalName;
                    TableSource tableSource = (TableSource)tableSpecSource;
                    if (StringHelper.isNotEmpty(tableSource.getExplicitTableName())) {
                        logicalName = Identifier.toIdentifier(tableSource.getExplicitTableName(), this.mappingDocument.getMappingDefaults().shouldImplicitlyQuoteIdentifiers());
                    } else {
                        final EntityNamingSourceImpl ownerEntityNaming = new EntityNamingSourceImpl(this.collectionBinding.getOwner().getEntityName(), this.collectionBinding.getOwner().getClassName(), this.collectionBinding.getOwner().getJpaEntityName());
                        ImplicitCollectionTableNameSource implicitNamingSource = new ImplicitCollectionTableNameSource(){

                            @Override
                            public Identifier getOwningPhysicalTableName() {
                                return AbstractPluralAttributeSecondPass.this.collectionBinding.getOwner().getTable().getNameIdentifier();
                            }

                            @Override
                            public EntityNaming getOwningEntityNaming() {
                                return ownerEntityNaming;
                            }

                            @Override
                            public AttributePath getOwningAttributePath() {
                                return AbstractPluralAttributeSecondPass.this.pluralAttributeSource.getAttributePath();
                            }

                            @Override
                            public MetadataBuildingContext getBuildingContext() {
                                return AbstractPluralAttributeSecondPass.this.mappingDocument;
                            }
                        };
                        logicalName = this.mappingDocument.getBuildingOptions().getImplicitNamingStrategy().determineCollectionTableName(implicitNamingSource);
                    }
                    collectionTable = namespace.createTable(logicalName, false);
                } else {
                    collectionTable = new Table(namespace, ((InLineViewSource)tableSpecSource).getSelectStatement(), false);
                }
                this.collectionBinding.setCollectionTable(collectionTable);
            }
            if (log.isDebugEnabled()) {
                log.debugf("Mapping collection: %s -> %s", this.collectionBinding.getRole(), this.collectionBinding.getCollectionTable().getName());
            }
            if (this.pluralAttributeSource.getCollectionTableComment() != null) {
                this.collectionBinding.getCollectionTable().setComment(this.pluralAttributeSource.getCollectionTableComment());
            }
            if (this.pluralAttributeSource.getCollectionTableCheck() != null) {
                this.collectionBinding.getCollectionTable().addCheckConstraint(this.pluralAttributeSource.getCollectionTableCheck());
            }
        }

        protected void createBackReferences() {
            if (this.collectionBinding.isOneToMany() && !this.collectionBinding.isInverse() && !this.collectionBinding.getKey().isNullable()) {
                String entityName = ((OneToMany)this.collectionBinding.getElement()).getReferencedEntityName();
                PersistentClass referenced = this.mappingDocument.getMetadataCollector().getEntityBinding(entityName);
                Backref prop = new Backref();
                prop.setName('_' + this.collectionBinding.getOwnerEntityName() + "." + this.pluralAttributeSource.getName() + "Backref");
                prop.setUpdateable(false);
                prop.setSelectable(false);
                prop.setCollectionRole(this.collectionBinding.getRole());
                prop.setEntityName(this.collectionBinding.getOwner().getEntityName());
                prop.setValue(this.collectionBinding.getKey());
                referenced.addProperty(prop);
                log.debugf("Added virtual backref property [%s] : %s", prop.getName(), this.pluralAttributeSource.getAttributeRole().getFullPath());
            }
        }

        protected void bindCollectionKey() {
            PluralAttributeKeySource keySource = this.getPluralAttributeSource().getKeySource();
            String propRef = keySource.getReferencedPropertyName();
            this.getCollectionBinding().setReferencedPropertyName(propRef);
            KeyValue keyVal = propRef == null ? this.getCollectionBinding().getOwner().getIdentifier() : (KeyValue)this.getCollectionBinding().getOwner().getRecursiveProperty(propRef).getValue();
            DependantValue key = new DependantValue(this.mappingDocument, this.getCollectionBinding().getCollectionTable(), keyVal);
            key.setForeignKeyName(keySource.getExplicitForeignKeyName());
            key.setCascadeDeleteEnabled(this.getPluralAttributeSource().getKeySource().isCascadeDeleteEnabled());
            if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToMany || this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceOneToMany) {
                ImplicitJoinColumnNameSource.Nature implicitNamingNature = ImplicitJoinColumnNameSource.Nature.ENTITY_COLLECTION;
            } else {
                ImplicitJoinColumnNameSource.Nature implicitNamingNature = ImplicitJoinColumnNameSource.Nature.ELEMENT_COLLECTION;
            }
            ModelBinder.this.relationalObjectBinder.bindColumnsAndFormulas(this.mappingDocument, this.getPluralAttributeSource().getKeySource().getRelationalValueSources(), key, this.getPluralAttributeSource().getKeySource().areValuesNullableByDefault(), new RelationalObjectBinder.ColumnNamingDelegate(){

                @Override
                public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                    return context.getMetadataCollector().getDatabase().toIdentifier("id");
                }
            });
            key.createForeignKey();
            this.getCollectionBinding().setKey(key);
            key.setNullable(this.getPluralAttributeSource().getKeySource().areValuesNullableByDefault());
            key.setUpdateable(this.getPluralAttributeSource().getKeySource().areValuesIncludedInUpdateByDefault());
        }

        protected void bindCollectionIdentifier() {
            CollectionIdSource idSource = this.getPluralAttributeSource().getCollectionIdSource();
            if (idSource != null) {
                IdentifierCollection idBagBinding = (IdentifierCollection)this.getCollectionBinding();
                SimpleValue idBinding = new SimpleValue(this.mappingDocument, idBagBinding.getCollectionTable());
                ModelBinder.bindSimpleValueType(this.mappingDocument, idSource.getTypeInformation(), idBinding);
                ModelBinder.this.relationalObjectBinder.bindColumn(this.mappingDocument, idSource.getColumnSource(), idBinding, false, new RelationalObjectBinder.ColumnNamingDelegate(){

                    @Override
                    public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                        return ModelBinder.this.database.toIdentifier("id");
                    }
                });
                idBagBinding.setIdentifier(idBinding);
                ModelBinder.this.makeIdentifier(this.mappingDocument, new IdentifierGeneratorDefinition(idSource.getGeneratorName(), idSource.getParameters()), null, idBinding);
            }
        }

        protected void bindCollectionIndex() {
        }

        protected void bindCollectionElement() {
            log.debugf("Binding [%s] element type for a [%s]", (Object)this.getPluralAttributeSource().getElementSource().getNature(), (Object)this.getPluralAttributeSource().getNature());
            if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceBasic) {
                PluralAttributeElementSourceBasic elementSource = (PluralAttributeElementSourceBasic)this.getPluralAttributeSource().getElementSource();
                SimpleValue elementBinding = new SimpleValue(this.getMappingDocument(), this.getCollectionBinding().getCollectionTable());
                ModelBinder.bindSimpleValueType(this.getMappingDocument(), elementSource.getExplicitHibernateTypeSource(), elementBinding);
                ModelBinder.this.relationalObjectBinder.bindColumnsAndFormulas(this.mappingDocument, elementSource.getRelationalValueSources(), elementBinding, elementSource.areValuesNullableByDefault(), new RelationalObjectBinder.ColumnNamingDelegate(){

                    @Override
                    public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                        return context.getMetadataCollector().getDatabase().toIdentifier("elt");
                    }
                });
                this.getCollectionBinding().setElement(elementBinding);
                this.getCollectionBinding().setWhere(this.getPluralAttributeSource().getWhere());
            } else if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceEmbedded) {
                PluralAttributeElementSourceEmbedded elementSource = (PluralAttributeElementSourceEmbedded)this.getPluralAttributeSource().getElementSource();
                Component elementBinding = new Component((MetadataBuildingContext)this.getMappingDocument(), this.getCollectionBinding());
                EmbeddableSource embeddableSource = elementSource.getEmbeddableSource();
                ModelBinder.this.bindComponent(this.mappingDocument, embeddableSource, elementBinding, null, embeddableSource.getAttributePathBase().getProperty(), this.getPluralAttributeSource().getXmlNodeName(), false);
                this.getCollectionBinding().setElement(elementBinding);
                this.getCollectionBinding().setWhere(this.getPluralAttributeSource().getWhere());
            } else if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceOneToMany) {
                PluralAttributeElementSourceOneToMany elementSource = (PluralAttributeElementSourceOneToMany)this.getPluralAttributeSource().getElementSource();
                OneToMany elementBinding = new OneToMany(this.getMappingDocument(), this.getCollectionBinding().getOwner());
                this.collectionBinding.setElement(elementBinding);
                PersistentClass referencedEntityBinding = this.mappingDocument.getMetadataCollector().getEntityBinding(elementSource.getReferencedEntityName());
                if (ModelBinder.this.useEntityWhereClauseForCollections()) {
                    this.collectionBinding.setWhere(StringHelper.getNonEmptyOrConjunctionIfBothNonEmpty(referencedEntityBinding.getWhere(), this.getPluralAttributeSource().getWhere()));
                } else {
                    this.collectionBinding.setWhere(this.getPluralAttributeSource().getWhere());
                }
                elementBinding.setReferencedEntityName(referencedEntityBinding.getEntityName());
                elementBinding.setAssociatedClass(referencedEntityBinding);
                elementBinding.setIgnoreNotFound(elementSource.isIgnoreNotFound());
            } else if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToMany) {
                PluralAttributeElementSourceManyToMany elementSource = (PluralAttributeElementSourceManyToMany)this.getPluralAttributeSource().getElementSource();
                ManyToOne elementBinding = new ManyToOne(this.getMappingDocument(), this.getCollectionBinding().getCollectionTable());
                ModelBinder.this.relationalObjectBinder.bindColumnsAndFormulas(this.getMappingDocument(), elementSource.getRelationalValueSources(), elementBinding, false, new RelationalObjectBinder.ColumnNamingDelegate(){

                    @Override
                    public Identifier determineImplicitName(LocalMetadataBuildingContext context) {
                        return context.getMetadataCollector().getDatabase().toIdentifier("elt");
                    }
                });
                elementBinding.setLazy(elementSource.getFetchCharacteristics().getFetchTiming() != FetchTiming.IMMEDIATE);
                elementBinding.setFetchMode(elementSource.getFetchCharacteristics().getFetchStyle() == FetchStyle.SELECT ? FetchMode.SELECT : FetchMode.JOIN);
                elementBinding.setForeignKeyName(elementSource.getExplicitForeignKeyName());
                elementBinding.setReferencedEntityName(elementSource.getReferencedEntityName());
                if (StringHelper.isNotEmpty(elementSource.getReferencedEntityAttributeName())) {
                    elementBinding.setReferencedPropertyName(elementSource.getReferencedEntityAttributeName());
                    elementBinding.setReferenceToPrimaryKey(false);
                } else {
                    elementBinding.setReferenceToPrimaryKey(true);
                }
                this.getCollectionBinding().setElement(elementBinding);
                PersistentClass referencedEntityBinding = this.mappingDocument.getMetadataCollector().getEntityBinding(elementSource.getReferencedEntityName());
                this.getCollectionBinding().setWhere(this.getPluralAttributeSource().getWhere());
                if (ModelBinder.this.useEntityWhereClauseForCollections()) {
                    this.getCollectionBinding().setManyToManyWhere(StringHelper.getNonEmptyOrConjunctionIfBothNonEmpty(referencedEntityBinding.getWhere(), elementSource.getWhere()));
                } else {
                    this.getCollectionBinding().setManyToManyWhere(elementSource.getWhere());
                }
                this.getCollectionBinding().setManyToManyOrdering(elementSource.getOrder());
                if (!(CollectionHelper.isEmpty(elementSource.getFilterSources()) && elementSource.getWhere() == null || this.getCollectionBinding().getFetchMode() != FetchMode.JOIN || elementBinding.getFetchMode() == FetchMode.JOIN)) {
                    throw new MappingException(String.format(Locale.ENGLISH, "many-to-many defining filter or where without join fetching is not valid within collection [%s] using join fetching", this.getPluralAttributeSource().getAttributeRole().getFullPath()), this.getMappingDocument().getOrigin());
                }
                for (FilterSource filterSource : elementSource.getFilterSources()) {
                    if (filterSource.getName() == null) {
                        log.debugf("Encountered filter with no name associated with many-to-many [%s]; skipping", this.getPluralAttributeSource().getAttributeRole().getFullPath());
                        continue;
                    }
                    if (filterSource.getCondition() == null) {
                        throw new MappingException(String.format(Locale.ENGLISH, "No filter condition found for filter [%s] associated with many-to-many [%s]", filterSource.getName(), this.getPluralAttributeSource().getAttributeRole().getFullPath()), this.getMappingDocument().getOrigin());
                    }
                    if (log.isDebugEnabled()) {
                        log.debugf("Applying many-to-many filter [%s] as [%s] to collection [%s]", filterSource.getName(), filterSource.getCondition(), this.getPluralAttributeSource().getAttributeRole().getFullPath());
                    }
                    this.getCollectionBinding().addManyToManyFilter(filterSource.getName(), filterSource.getCondition(), filterSource.shouldAutoInjectAliases(), filterSource.getAliasToTableMap(), filterSource.getAliasToEntityMap());
                }
            } else if (this.getPluralAttributeSource().getElementSource() instanceof PluralAttributeElementSourceManyToAny) {
                PluralAttributeElementSourceManyToAny elementSource = (PluralAttributeElementSourceManyToAny)this.getPluralAttributeSource().getElementSource();
                Any elementBinding = new Any(this.getMappingDocument(), this.getCollectionBinding().getCollectionTable());
                ModelBinder.this.bindAny(this.mappingDocument, elementSource, elementBinding, this.getPluralAttributeSource().getAttributeRole().append("element"), this.getPluralAttributeSource().getAttributePath().append("element"));
                this.getCollectionBinding().setElement(elementBinding);
                this.getCollectionBinding().setWhere(this.getPluralAttributeSource().getWhere());
            }
        }
    }

    public static final class DelayedPropertyReferenceHandlerImpl
    implements InFlightMetadataCollector.DelayedPropertyReferenceHandler {
        public final String referencedEntityName;
        public final String referencedPropertyName;
        public final boolean isUnique;
        private final String sourceElementSynopsis;
        public final Origin propertyRefOrigin;

        public DelayedPropertyReferenceHandlerImpl(String referencedEntityName, String referencedPropertyName, boolean isUnique, String sourceElementSynopsis, Origin propertyRefOrigin) {
            this.referencedEntityName = referencedEntityName;
            this.referencedPropertyName = referencedPropertyName;
            this.isUnique = isUnique;
            this.sourceElementSynopsis = sourceElementSynopsis;
            this.propertyRefOrigin = propertyRefOrigin;
        }

        @Override
        public void process(InFlightMetadataCollector metadataCollector) {
            log.tracef("Performing delayed property-ref handling [%s, %s, %s]", this.referencedEntityName, this.referencedPropertyName, this.sourceElementSynopsis);
            PersistentClass entityBinding = metadataCollector.getEntityBinding(this.referencedEntityName);
            if (entityBinding == null) {
                throw new MappingException(String.format(Locale.ENGLISH, "property-ref [%s] referenced an unmapped entity [%s]", this.sourceElementSynopsis, this.referencedEntityName), this.propertyRefOrigin);
            }
            Property propertyBinding = entityBinding.getReferencedProperty(this.referencedPropertyName);
            if (propertyBinding == null) {
                throw new MappingException(String.format(Locale.ENGLISH, "property-ref [%s] referenced an unknown entity property [%s#%s]", this.sourceElementSynopsis, this.referencedEntityName, this.referencedPropertyName), this.propertyRefOrigin);
            }
            if (this.isUnique) {
                ((SimpleValue)propertyBinding.getValue()).setAlternateUniqueKey(true);
            }
        }
    }

    private static class TypeResolution {
        private final String typeName;
        private final Properties parameters;

        public TypeResolution(String typeName, Properties parameters) {
            this.typeName = typeName;
            this.parameters = parameters;
        }
    }
}

