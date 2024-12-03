/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Access
 *  javax.persistence.Cacheable
 *  javax.persistence.ConstraintMode
 *  javax.persistence.Entity
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinTable
 *  javax.persistence.NamedEntityGraph
 *  javax.persistence.NamedEntityGraphs
 *  javax.persistence.PrimaryKeyJoinColumn
 *  javax.persistence.SecondaryTable
 *  javax.persistence.SecondaryTables
 *  javax.persistence.SharedCacheMode
 *  javax.persistence.Table
 *  org.hibernate.annotations.common.reflection.ReflectionManager
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.Access;
import javax.persistence.Cacheable;
import javax.persistence.ConstraintMode;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.SharedCacheMode;
import javax.persistence.Table;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.EntityMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.OptimisticLockType;
import org.hibernate.annotations.OptimisticLocking;
import org.hibernate.annotations.Persister;
import org.hibernate.annotations.Polymorphism;
import org.hibernate.annotations.PolymorphismType;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.RowId;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import org.hibernate.annotations.Tables;
import org.hibernate.annotations.Tuplizer;
import org.hibernate.annotations.Tuplizers;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.common.reflection.ReflectionManager;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.model.naming.EntityNaming;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitEntityNameSource;
import org.hibernate.boot.model.naming.NamingStrategyHelper;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.ObjectNameSource;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.UniqueConstraintHolder;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.engine.OptimisticLockStyle;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.SingleTableSubclass;
import org.hibernate.mapping.TableOwner;
import org.hibernate.mapping.Value;
import org.jboss.logging.Logger;

public class EntityBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)EntityBinder.class.getName());
    private static final String NATURAL_ID_CACHE_SUFFIX = "##NaturalId";
    private MetadataBuildingContext context;
    private String name;
    private XClass annotatedClass;
    private PersistentClass persistentClass;
    private String discriminatorValue = "";
    private Boolean forceDiscriminator;
    private Boolean insertableDiscriminator;
    private boolean dynamicInsert;
    private boolean dynamicUpdate;
    private boolean explicitHibernateEntityAnnotation;
    private OptimisticLockType optimisticLockType;
    private PolymorphismType polymorphismType;
    private boolean selectBeforeUpdate;
    private int batchSize;
    private boolean lazy;
    private XClass proxyClass;
    private String where;
    private Map<String, Join> secondaryTables = new HashMap<String, Join>();
    private Map<String, Object> secondaryTableJoins = new HashMap<String, Object>();
    private List<Filter> filters = new ArrayList<Filter>();
    private InheritanceState inheritanceState;
    private boolean ignoreIdAnnotations;
    private AccessType propertyAccessType = AccessType.DEFAULT;
    private boolean wrapIdsInEmbeddedComponents;
    private String subselect;
    private boolean isCached;
    private String cacheConcurrentStrategy;
    private String cacheRegion;
    private boolean cacheLazyProperty;
    private String naturalIdCacheRegion;
    private static SecondaryTableNamingStrategyHelper SEC_TBL_NS_HELPER = new SecondaryTableNamingStrategyHelper();

    public boolean wrapIdsInEmbeddedComponents() {
        return this.wrapIdsInEmbeddedComponents;
    }

    public EntityBinder() {
    }

    public EntityBinder(Entity ejb3Ann, org.hibernate.annotations.Entity hibAnn, XClass annotatedClass, PersistentClass persistentClass, MetadataBuildingContext context) {
        this.context = context;
        this.persistentClass = persistentClass;
        this.annotatedClass = annotatedClass;
        this.bindEjb3Annotation(ejb3Ann);
        this.bindHibernateAnnotation(hibAnn);
    }

    public boolean isPropertyDefinedInSuperHierarchy(String name) {
        if (this.persistentClass == null) {
            return false;
        }
        return this.persistentClass.isPropertyDefinedInSuperHierarchy(name);
    }

    private void bindHibernateAnnotation(org.hibernate.annotations.Entity hibAnn) {
        DynamicInsert dynamicInsertAnn = (DynamicInsert)this.annotatedClass.getAnnotation(DynamicInsert.class);
        this.dynamicInsert = dynamicInsertAnn == null ? (hibAnn == null ? false : hibAnn.dynamicInsert()) : dynamicInsertAnn.value();
        DynamicUpdate dynamicUpdateAnn = (DynamicUpdate)this.annotatedClass.getAnnotation(DynamicUpdate.class);
        this.dynamicUpdate = dynamicUpdateAnn == null ? (hibAnn == null ? false : hibAnn.dynamicUpdate()) : dynamicUpdateAnn.value();
        SelectBeforeUpdate selectBeforeUpdateAnn = (SelectBeforeUpdate)this.annotatedClass.getAnnotation(SelectBeforeUpdate.class);
        this.selectBeforeUpdate = selectBeforeUpdateAnn == null ? (hibAnn == null ? false : hibAnn.selectBeforeUpdate()) : selectBeforeUpdateAnn.value();
        OptimisticLocking optimisticLockingAnn = (OptimisticLocking)this.annotatedClass.getAnnotation(OptimisticLocking.class);
        this.optimisticLockType = optimisticLockingAnn == null ? (hibAnn == null ? OptimisticLockType.VERSION : hibAnn.optimisticLock()) : optimisticLockingAnn.type();
        Polymorphism polymorphismAnn = (Polymorphism)this.annotatedClass.getAnnotation(Polymorphism.class);
        PolymorphismType polymorphismType = polymorphismAnn == null ? (hibAnn == null ? PolymorphismType.IMPLICIT : hibAnn.polymorphism()) : (this.polymorphismType = polymorphismAnn.type());
        if (hibAnn != null) {
            this.explicitHibernateEntityAnnotation = true;
        }
    }

    private void bindEjb3Annotation(Entity ejb3Ann) {
        if (ejb3Ann == null) {
            throw new AssertionFailure("@Entity should always be not null");
        }
        this.name = BinderHelper.isEmptyAnnotationValue(ejb3Ann.name()) ? StringHelper.unqualify(this.annotatedClass.getName()) : ejb3Ann.name();
    }

    public boolean isRootEntity() {
        return this.persistentClass instanceof RootClass;
    }

    public void setDiscriminatorValue(String discriminatorValue) {
        this.discriminatorValue = discriminatorValue;
    }

    public void setForceDiscriminator(boolean forceDiscriminator) {
        this.forceDiscriminator = forceDiscriminator;
    }

    public void setInsertableDiscriminator(boolean insertableDiscriminator) {
        this.insertableDiscriminator = insertableDiscriminator;
    }

    public void bindEntity() {
        this.persistentClass.setAbstract(this.annotatedClass.isAbstract());
        this.persistentClass.setClassName(this.annotatedClass.getName());
        this.persistentClass.setJpaEntityName(this.name);
        this.persistentClass.setEntityName(this.annotatedClass.getName());
        this.bindDiscriminatorValue();
        this.persistentClass.setLazy(this.lazy);
        if (this.proxyClass != null) {
            this.persistentClass.setProxyInterfaceName(this.proxyClass.getName());
        }
        this.persistentClass.setDynamicInsert(this.dynamicInsert);
        this.persistentClass.setDynamicUpdate(this.dynamicUpdate);
        if (this.persistentClass instanceof RootClass) {
            RootClass rootClass = (RootClass)this.persistentClass;
            boolean mutable = true;
            if (this.annotatedClass.isAnnotationPresent(Immutable.class)) {
                mutable = false;
            } else {
                org.hibernate.annotations.Entity entityAnn = (org.hibernate.annotations.Entity)this.annotatedClass.getAnnotation(org.hibernate.annotations.Entity.class);
                if (entityAnn != null) {
                    mutable = entityAnn.mutable();
                }
            }
            rootClass.setMutable(mutable);
            rootClass.setExplicitPolymorphism(this.isExplicitPolymorphism(this.polymorphismType));
            if (StringHelper.isNotEmpty(this.where)) {
                rootClass.setWhere(this.where);
            }
            if (this.cacheConcurrentStrategy != null) {
                rootClass.setCacheConcurrencyStrategy(this.cacheConcurrentStrategy);
                rootClass.setCacheRegionName(this.cacheRegion);
                rootClass.setLazyPropertiesCacheable(this.cacheLazyProperty);
            }
            rootClass.setNaturalIdCacheRegionName(this.naturalIdCacheRegion);
            boolean forceDiscriminatorInSelects = this.forceDiscriminator == null ? this.context.getBuildingOptions().shouldImplicitlyForceDiscriminatorInSelect() : this.forceDiscriminator.booleanValue();
            rootClass.setForceDiscriminator(forceDiscriminatorInSelects);
            if (this.insertableDiscriminator != null) {
                rootClass.setDiscriminatorInsertable(this.insertableDiscriminator);
            }
        } else {
            if (this.explicitHibernateEntityAnnotation) {
                LOG.entityAnnotationOnNonRoot(this.annotatedClass.getName());
            }
            if (this.annotatedClass.isAnnotationPresent(Immutable.class)) {
                LOG.immutableAnnotationOnNonRoot(this.annotatedClass.getName());
            }
        }
        this.persistentClass.setCached(this.isCached);
        this.persistentClass.setOptimisticLockStyle(this.getVersioning(this.optimisticLockType));
        this.persistentClass.setSelectBeforeUpdate(this.selectBeforeUpdate);
        Persister persisterAnn = (Persister)this.annotatedClass.getAnnotation(Persister.class);
        Class<Object> persister = null;
        if (persisterAnn != null) {
            persister = persisterAnn.impl();
        } else {
            org.hibernate.annotations.Entity entityAnn = (org.hibernate.annotations.Entity)this.annotatedClass.getAnnotation(org.hibernate.annotations.Entity.class);
            if (entityAnn != null && !BinderHelper.isEmptyAnnotationValue(entityAnn.persister())) {
                try {
                    persister = this.context.getBootstrapContext().getClassLoaderAccess().classForName(entityAnn.persister());
                }
                catch (ClassLoadingException e) {
                    throw new AnnotationException("Could not find persister class: " + entityAnn.persister(), (Throwable)((Object)e));
                }
            }
        }
        if (persister != null) {
            this.persistentClass.setEntityPersisterClass(persister);
        }
        this.persistentClass.setBatchSize(this.batchSize);
        SQLInsert sqlInsert = (SQLInsert)this.annotatedClass.getAnnotation(SQLInsert.class);
        SQLUpdate sqlUpdate = (SQLUpdate)this.annotatedClass.getAnnotation(SQLUpdate.class);
        SQLDelete sqlDelete = (SQLDelete)this.annotatedClass.getAnnotation(SQLDelete.class);
        SQLDeleteAll sqlDeleteAll = (SQLDeleteAll)this.annotatedClass.getAnnotation(SQLDeleteAll.class);
        Loader loader = (Loader)this.annotatedClass.getAnnotation(Loader.class);
        if (sqlInsert != null) {
            this.persistentClass.setCustomSQLInsert(sqlInsert.sql().trim(), sqlInsert.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlInsert.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlUpdate != null) {
            this.persistentClass.setCustomSQLUpdate(sqlUpdate.sql(), sqlUpdate.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlUpdate.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlDelete != null) {
            this.persistentClass.setCustomSQLDelete(sqlDelete.sql(), sqlDelete.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlDelete.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlDeleteAll != null) {
            this.persistentClass.setCustomSQLDelete(sqlDeleteAll.sql(), sqlDeleteAll.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (loader != null) {
            this.persistentClass.setLoaderName(loader.namedQuery());
        }
        JdbcEnvironment jdbcEnvironment = this.context.getMetadataCollector().getDatabase().getJdbcEnvironment();
        if (this.annotatedClass.isAnnotationPresent(Synchronize.class)) {
            String[] tables;
            Synchronize synchronizedWith = (Synchronize)this.annotatedClass.getAnnotation(Synchronize.class);
            for (String table : tables = synchronizedWith.value()) {
                this.persistentClass.addSynchronizedTable(this.context.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(jdbcEnvironment.getIdentifierHelper().toIdentifier(table), jdbcEnvironment).render(jdbcEnvironment.getDialect()));
            }
        }
        if (this.annotatedClass.isAnnotationPresent(Subselect.class)) {
            Tuplizer[] subselect = (Tuplizer[])this.annotatedClass.getAnnotation(Subselect.class);
            this.subselect = subselect.value();
        }
        if (this.annotatedClass.isAnnotationPresent(Tuplizers.class)) {
            for (Tuplizer tuplizer : ((Tuplizers)this.annotatedClass.getAnnotation(Tuplizers.class)).value()) {
                EntityMode mode = EntityMode.parse(tuplizer.entityMode());
                this.persistentClass.addTuplizer(mode, tuplizer.impl().getName());
            }
        }
        if (this.annotatedClass.isAnnotationPresent(Tuplizer.class)) {
            Tuplizer tuplizer = (Tuplizer)this.annotatedClass.getAnnotation(Tuplizer.class);
            EntityMode mode = EntityMode.parse(tuplizer.entityMode());
            this.persistentClass.addTuplizer(mode, tuplizer.impl().getName());
        }
        for (Filter filter : this.filters) {
            String filterName = filter.name();
            String cond = filter.condition();
            if (BinderHelper.isEmptyAnnotationValue(cond)) {
                FilterDefinition definition = this.context.getMetadataCollector().getFilterDefinition(filterName);
                String string = cond = definition == null ? null : definition.getDefaultFilterCondition();
                if (StringHelper.isEmpty(cond)) {
                    throw new AnnotationException("no filter condition found for filter " + filterName + " in " + this.name);
                }
            }
            this.persistentClass.addFilter(filterName, cond, filter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(filter.aliases()), BinderHelper.toAliasEntityMap(filter.aliases()));
        }
        LOG.debugf("Import with entity name %s", this.name);
        try {
            this.context.getMetadataCollector().addImport(this.name, this.persistentClass.getEntityName());
            String entityName = this.persistentClass.getEntityName();
            if (!entityName.equals(this.name)) {
                this.context.getMetadataCollector().addImport(entityName, entityName);
            }
        }
        catch (MappingException me) {
            throw new AnnotationException("Use of the same entity name twice: " + this.name, (Throwable)((Object)me));
        }
        this.processNamedEntityGraphs();
    }

    private void processNamedEntityGraphs() {
        this.processNamedEntityGraph((NamedEntityGraph)this.annotatedClass.getAnnotation(NamedEntityGraph.class));
        NamedEntityGraphs graphs = (NamedEntityGraphs)this.annotatedClass.getAnnotation(NamedEntityGraphs.class);
        if (graphs != null) {
            for (NamedEntityGraph graph : graphs.value()) {
                this.processNamedEntityGraph(graph);
            }
        }
    }

    private void processNamedEntityGraph(NamedEntityGraph annotation) {
        if (annotation == null) {
            return;
        }
        this.context.getMetadataCollector().addNamedEntityGraph(new NamedEntityGraphDefinition(annotation, this.name, this.persistentClass.getEntityName()));
    }

    public void bindDiscriminatorValue() {
        if (StringHelper.isEmpty(this.discriminatorValue)) {
            Value discriminator = this.persistentClass.getDiscriminator();
            if (discriminator == null) {
                this.persistentClass.setDiscriminatorValue(this.name);
            } else {
                if ("character".equals(discriminator.getType().getName())) {
                    throw new AnnotationException("Using default @DiscriminatorValue for a discriminator of type CHAR is not safe");
                }
                if ("integer".equals(discriminator.getType().getName())) {
                    this.persistentClass.setDiscriminatorValue(String.valueOf(this.name.hashCode()));
                } else {
                    this.persistentClass.setDiscriminatorValue(this.name);
                }
            }
        } else {
            this.persistentClass.setDiscriminatorValue(this.discriminatorValue);
        }
    }

    OptimisticLockStyle getVersioning(OptimisticLockType type) {
        switch (type) {
            case VERSION: {
                return OptimisticLockStyle.VERSION;
            }
            case NONE: {
                return OptimisticLockStyle.NONE;
            }
            case DIRTY: {
                return OptimisticLockStyle.DIRTY;
            }
            case ALL: {
                return OptimisticLockStyle.ALL;
            }
        }
        throw new AssertionFailure("optimistic locking not supported: " + (Object)((Object)type));
    }

    private boolean isExplicitPolymorphism(PolymorphismType type) {
        switch (type) {
            case IMPLICIT: {
                return false;
            }
            case EXPLICIT: {
                return true;
            }
        }
        throw new AssertionFailure("Unknown polymorphism type: " + (Object)((Object)type));
    }

    public void setBatchSize(BatchSize sizeAnn) {
        this.batchSize = sizeAnn != null ? sizeAnn.size() : -1;
    }

    public void setProxy(Proxy proxy) {
        if (proxy != null) {
            ReflectionManager reflectionManager;
            this.lazy = proxy.lazy();
            this.proxyClass = !this.lazy ? null : (AnnotationBinder.isDefault((reflectionManager = this.context.getBootstrapContext().getReflectionManager()).toXClass(proxy.proxyClass()), this.context) ? this.annotatedClass : reflectionManager.toXClass(proxy.proxyClass()));
        } else {
            this.lazy = true;
            this.proxyClass = this.annotatedClass;
        }
    }

    public void setWhere(Where whereAnn) {
        if (whereAnn != null) {
            this.where = whereAnn.clause();
        }
    }

    public void setWrapIdsInEmbeddedComponents(boolean wrapIdsInEmbeddedComponents) {
        this.wrapIdsInEmbeddedComponents = wrapIdsInEmbeddedComponents;
    }

    public void applyCaching(XClass clazzToProcess, SharedCacheMode sharedCacheMode, MetadataBuildingContext context) {
        Cache explicitCacheAnn = (Cache)clazzToProcess.getAnnotation(Cache.class);
        Cacheable explicitCacheableAnn = (Cacheable)clazzToProcess.getAnnotation(Cacheable.class);
        this.isCached = false;
        this.cacheConcurrentStrategy = null;
        this.cacheRegion = null;
        this.cacheLazyProperty = true;
        if (this.persistentClass instanceof RootClass) {
            Cache effectiveCacheAnn = explicitCacheAnn;
            if (explicitCacheAnn != null) {
                this.isCached = true;
            } else {
                effectiveCacheAnn = EntityBinder.buildCacheMock(clazzToProcess.getName(), context);
                switch (sharedCacheMode) {
                    case ALL: {
                        this.isCached = true;
                        break;
                    }
                    case ENABLE_SELECTIVE: {
                        if (explicitCacheableAnn == null || !explicitCacheableAnn.value()) break;
                        this.isCached = true;
                        break;
                    }
                    case DISABLE_SELECTIVE: {
                        if (explicitCacheableAnn != null && !explicitCacheableAnn.value()) break;
                        this.isCached = true;
                        break;
                    }
                    default: {
                        this.isCached = false;
                    }
                }
            }
            this.cacheConcurrentStrategy = EntityBinder.resolveCacheConcurrencyStrategy(effectiveCacheAnn.usage());
            this.cacheRegion = effectiveCacheAnn.region();
            switch (effectiveCacheAnn.include().toLowerCase(Locale.ROOT)) {
                case "all": {
                    this.cacheLazyProperty = true;
                    break;
                }
                case "non-lazy": {
                    this.cacheLazyProperty = false;
                    break;
                }
                default: {
                    throw new AnnotationException("Unknown @Cache.include value [" + effectiveCacheAnn.include() + "] : " + this.annotatedClass.getName());
                }
            }
        } else if (explicitCacheAnn != null) {
            LOG.cacheOrCacheableAnnotationOnNonRoot(this.persistentClass.getClassName() == null ? this.annotatedClass.getName() : this.persistentClass.getClassName());
        } else if (explicitCacheableAnn == null && this.persistentClass.getSuperclass() != null) {
            this.isCached = this.persistentClass.getSuperclass().isCached();
        } else {
            switch (sharedCacheMode) {
                case ALL: {
                    this.isCached = true;
                    break;
                }
                case ENABLE_SELECTIVE: {
                    if (explicitCacheableAnn == null || !explicitCacheableAnn.value()) break;
                    this.isCached = true;
                    break;
                }
                case DISABLE_SELECTIVE: {
                    if (explicitCacheableAnn != null && explicitCacheableAnn.value()) break;
                    this.isCached = true;
                    break;
                }
                default: {
                    this.isCached = false;
                }
            }
        }
        this.naturalIdCacheRegion = null;
        NaturalIdCache naturalIdCacheAnn = (NaturalIdCache)clazzToProcess.getAnnotation(NaturalIdCache.class);
        if (naturalIdCacheAnn != null) {
            this.naturalIdCacheRegion = BinderHelper.isEmptyAnnotationValue(naturalIdCacheAnn.region()) ? (explicitCacheAnn != null && StringHelper.isNotEmpty(explicitCacheAnn.region()) ? explicitCacheAnn.region() + NATURAL_ID_CACHE_SUFFIX : clazzToProcess.getName() + NATURAL_ID_CACHE_SUFFIX) : naturalIdCacheAnn.region();
        }
    }

    private static String resolveCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
        org.hibernate.cache.spi.access.AccessType accessType = strategy.toAccessType();
        return accessType == null ? null : accessType.getExternalName();
    }

    private static Cache buildCacheMock(String region, MetadataBuildingContext context) {
        return new LocalCacheAnnotationStub(region, EntityBinder.determineCacheConcurrencyStrategy(context));
    }

    private static CacheConcurrencyStrategy determineCacheConcurrencyStrategy(MetadataBuildingContext context) {
        return CacheConcurrencyStrategy.fromAccessType(context.getBuildingOptions().getImplicitCacheAccessType());
    }

    public void bindTableForDiscriminatedSubclass(InFlightMetadataCollector.EntityTableXref superTableXref) {
        if (!SingleTableSubclass.class.isInstance(this.persistentClass)) {
            throw new AssertionFailure("Was expecting a discriminated subclass [" + SingleTableSubclass.class.getName() + "] but found [" + this.persistentClass.getClass().getName() + "] for entity [" + this.persistentClass.getEntityName() + "]");
        }
        this.context.getMetadataCollector().addEntityTableXref(this.persistentClass.getEntityName(), this.context.getMetadataCollector().getDatabase().toIdentifier(this.context.getMetadataCollector().getLogicalTableName(superTableXref.getPrimaryTable())), superTableXref.getPrimaryTable(), superTableXref);
    }

    public void bindTable(String schema, String catalog, String tableName, List<UniqueConstraintHolder> uniqueConstraints, String constraints, InFlightMetadataCollector.EntityTableXref denormalizedSuperTableXref) {
        EntityTableNamingStrategyHelper namingStrategyHelper = new EntityTableNamingStrategyHelper(this.persistentClass.getClassName(), this.persistentClass.getEntityName(), this.name);
        Identifier logicalName = StringHelper.isNotEmpty(tableName) ? namingStrategyHelper.handleExplicitName(tableName, this.context) : namingStrategyHelper.determineImplicitName(this.context);
        org.hibernate.mapping.Table table = TableBinder.buildAndFillTable(schema, catalog, logicalName, this.persistentClass.isAbstract(), uniqueConstraints, null, constraints, this.context, this.subselect, denormalizedSuperTableXref);
        RowId rowId = (RowId)this.annotatedClass.getAnnotation(RowId.class);
        if (rowId != null) {
            table.setRowId(rowId.value());
        }
        this.context.getMetadataCollector().addEntityTableXref(this.persistentClass.getEntityName(), logicalName, table, denormalizedSuperTableXref);
        if (!(this.persistentClass instanceof TableOwner)) {
            throw new AssertionFailure("binding a table for a subclass");
        }
        LOG.debugf("Bind entity %s on table %s", this.persistentClass.getEntityName(), table.getName());
        ((TableOwner)((Object)this.persistentClass)).setTable(table);
    }

    public void finalSecondaryTableBinding(PropertyHolder propertyHolder) {
        Iterator<Join> joins = this.secondaryTables.values().iterator();
        Iterator<Object> joinColumns = this.secondaryTableJoins.values().iterator();
        while (joins.hasNext()) {
            Object uncastedColumn = joinColumns.next();
            Join join = joins.next();
            this.createPrimaryColumnsToSecondaryTable(uncastedColumn, propertyHolder, join);
        }
    }

    private void createPrimaryColumnsToSecondaryTable(Object uncastedColumn, PropertyHolder propertyHolder, Join join) {
        Ejb3JoinColumn[] ejb3JoinColumns;
        PrimaryKeyJoinColumn[] pkColumnsAnn = null;
        JoinColumn[] joinColumnsAnn = null;
        if (uncastedColumn instanceof PrimaryKeyJoinColumn[]) {
            pkColumnsAnn = (PrimaryKeyJoinColumn[])uncastedColumn;
        }
        if (uncastedColumn instanceof JoinColumn[]) {
            joinColumnsAnn = (JoinColumn[])uncastedColumn;
        }
        if (pkColumnsAnn == null && joinColumnsAnn == null) {
            ejb3JoinColumns = new Ejb3JoinColumn[]{Ejb3JoinColumn.buildJoinColumn(null, null, this.persistentClass.getIdentifier(), this.secondaryTables, propertyHolder, this.context)};
        } else {
            int nbrOfJoinColumns;
            int n = nbrOfJoinColumns = pkColumnsAnn != null ? pkColumnsAnn.length : joinColumnsAnn.length;
            if (nbrOfJoinColumns == 0) {
                ejb3JoinColumns = new Ejb3JoinColumn[]{Ejb3JoinColumn.buildJoinColumn(null, null, this.persistentClass.getIdentifier(), this.secondaryTables, propertyHolder, this.context)};
            } else {
                int colIndex;
                ejb3JoinColumns = new Ejb3JoinColumn[nbrOfJoinColumns];
                if (pkColumnsAnn != null) {
                    for (colIndex = 0; colIndex < nbrOfJoinColumns; ++colIndex) {
                        ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(pkColumnsAnn[colIndex], null, this.persistentClass.getIdentifier(), this.secondaryTables, propertyHolder, this.context);
                    }
                } else {
                    for (colIndex = 0; colIndex < nbrOfJoinColumns; ++colIndex) {
                        ejb3JoinColumns[colIndex] = Ejb3JoinColumn.buildJoinColumn(null, joinColumnsAnn[colIndex], this.persistentClass.getIdentifier(), this.secondaryTables, propertyHolder, this.context);
                    }
                }
            }
        }
        for (Ejb3JoinColumn joinColumn : ejb3JoinColumns) {
            joinColumn.forceNotNull();
        }
        this.bindJoinToPersistentClass(join, ejb3JoinColumns, this.context);
    }

    private void bindJoinToPersistentClass(Join join, Ejb3JoinColumn[] ejb3JoinColumns, MetadataBuildingContext buildingContext) {
        DependantValue key = new DependantValue(buildingContext, join.getTable(), this.persistentClass.getIdentifier());
        join.setKey(key);
        this.setFKNameIfDefined(join);
        key.setCascadeDeleteEnabled(false);
        TableBinder.bindFk(this.persistentClass, null, ejb3JoinColumns, key, false, buildingContext);
        join.createPrimaryKey();
        join.createForeignKey();
        this.persistentClass.addJoin(join);
    }

    private void setFKNameIfDefined(Join join) {
        org.hibernate.annotations.Table matchingTable = this.findMatchingComplimentTableAnnotation(join);
        if (matchingTable != null && !BinderHelper.isEmptyAnnotationValue(matchingTable.foreignKey().name())) {
            ((SimpleValue)join.getKey()).setForeignKeyName(matchingTable.foreignKey().name());
        } else {
            SecondaryTable jpaSecondaryTable = this.findMatchingSecondaryTable(join);
            if (jpaSecondaryTable != null) {
                boolean noConstraintByDefault = this.context.getBuildingOptions().isNoConstraintByDefault();
                if (jpaSecondaryTable.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || jpaSecondaryTable.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault) {
                    ((SimpleValue)join.getKey()).setForeignKeyName("none");
                } else {
                    ((SimpleValue)join.getKey()).setForeignKeyName(StringHelper.nullIfEmpty(jpaSecondaryTable.foreignKey().name()));
                    ((SimpleValue)join.getKey()).setForeignKeyDefinition(StringHelper.nullIfEmpty(jpaSecondaryTable.foreignKey().foreignKeyDefinition()));
                }
            }
        }
    }

    private SecondaryTable findMatchingSecondaryTable(Join join) {
        String nameToMatch = join.getTable().getQuotedName();
        SecondaryTable secondaryTable = (SecondaryTable)this.annotatedClass.getAnnotation(SecondaryTable.class);
        if (secondaryTable != null && nameToMatch.equals(secondaryTable.name())) {
            return secondaryTable;
        }
        SecondaryTables secondaryTables = (SecondaryTables)this.annotatedClass.getAnnotation(SecondaryTables.class);
        if (secondaryTables != null) {
            for (SecondaryTable secondaryTablesEntry : secondaryTables.value()) {
                if (secondaryTablesEntry == null || !nameToMatch.equals(secondaryTablesEntry.name())) continue;
                return secondaryTablesEntry;
            }
        }
        return null;
    }

    private org.hibernate.annotations.Table findMatchingComplimentTableAnnotation(Join join) {
        String tableName = join.getTable().getQuotedName();
        org.hibernate.annotations.Table table = (org.hibernate.annotations.Table)this.annotatedClass.getAnnotation(org.hibernate.annotations.Table.class);
        org.hibernate.annotations.Table matchingTable = null;
        if (table != null && tableName.equals(table.appliesTo())) {
            matchingTable = table;
        } else {
            Tables tables = (Tables)this.annotatedClass.getAnnotation(Tables.class);
            if (tables != null) {
                for (org.hibernate.annotations.Table current : tables.value()) {
                    if (!tableName.equals(current.appliesTo())) continue;
                    matchingTable = current;
                    break;
                }
            }
        }
        return matchingTable;
    }

    public void firstLevelSecondaryTablesBinding(SecondaryTable secTable, SecondaryTables secTables) {
        if (secTables != null) {
            for (SecondaryTable tab : secTables.value()) {
                this.addJoin(tab, null, null, false);
            }
        } else if (secTable != null) {
            this.addJoin(secTable, null, null, false);
        }
    }

    public Join addJoin(JoinTable joinTable, PropertyHolder holder, boolean noDelayInPkColumnCreation) {
        return this.addJoin(null, joinTable, holder, noDelayInPkColumnCreation);
    }

    private Join addJoin(SecondaryTable secondaryTable, JoinTable joinTable, PropertyHolder propertyHolder, boolean noDelayInPkColumnCreation) {
        List<UniqueConstraintHolder> uniqueConstraintHolders;
        PrimaryKeyJoinColumn[] joinColumns;
        QualifiedTableName logicalName;
        String catalog;
        String schema;
        Join join = new Join();
        join.setPersistentClass(this.persistentClass);
        if (secondaryTable != null) {
            schema = secondaryTable.schema();
            catalog = secondaryTable.catalog();
            logicalName = new QualifiedTableName(Identifier.toIdentifier(catalog), Identifier.toIdentifier(schema), this.context.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(secondaryTable.name()));
            joinColumns = secondaryTable.pkJoinColumns();
            uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders(secondaryTable.uniqueConstraints());
        } else if (joinTable != null) {
            schema = joinTable.schema();
            catalog = joinTable.catalog();
            logicalName = new QualifiedTableName(Identifier.toIdentifier(catalog), Identifier.toIdentifier(schema), this.context.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(joinTable.name()));
            joinColumns = joinTable.joinColumns();
            uniqueConstraintHolders = TableBinder.buildUniqueConstraintHolders(joinTable.uniqueConstraints());
        } else {
            throw new AssertionFailure("Both JoinTable and SecondaryTable are null");
        }
        org.hibernate.mapping.Table table = TableBinder.buildAndFillTable(schema, catalog, logicalName.getTableName(), false, uniqueConstraintHolders, null, null, this.context, null, null);
        InFlightMetadataCollector.EntityTableXref tableXref = this.context.getMetadataCollector().getEntityTableXref(this.persistentClass.getEntityName());
        assert (tableXref != null) : "Could not locate EntityTableXref for entity [" + this.persistentClass.getEntityName() + "]";
        tableXref.addSecondaryTable(logicalName, join);
        if (secondaryTable != null) {
            TableBinder.addIndexes(table, secondaryTable.indexes(), this.context);
        }
        join.setTable(table);
        LOG.debugf("Adding secondary table to entity %s -> %s", this.persistentClass.getEntityName(), join.getTable().getName());
        org.hibernate.annotations.Table matchingTable = this.findMatchingComplimentTableAnnotation(join);
        if (matchingTable != null) {
            join.setSequentialSelect(FetchMode.JOIN != matchingTable.fetch());
            join.setInverse(matchingTable.inverse());
            join.setOptional(matchingTable.optional());
            if (!BinderHelper.isEmptyAnnotationValue(matchingTable.sqlInsert().sql())) {
                join.setCustomSQLInsert(matchingTable.sqlInsert().sql().trim(), matchingTable.sqlInsert().callable(), ExecuteUpdateResultCheckStyle.fromExternalName(matchingTable.sqlInsert().check().toString().toLowerCase(Locale.ROOT)));
            }
            if (!BinderHelper.isEmptyAnnotationValue(matchingTable.sqlUpdate().sql())) {
                join.setCustomSQLUpdate(matchingTable.sqlUpdate().sql().trim(), matchingTable.sqlUpdate().callable(), ExecuteUpdateResultCheckStyle.fromExternalName(matchingTable.sqlUpdate().check().toString().toLowerCase(Locale.ROOT)));
            }
            if (!BinderHelper.isEmptyAnnotationValue(matchingTable.sqlDelete().sql())) {
                join.setCustomSQLDelete(matchingTable.sqlDelete().sql().trim(), matchingTable.sqlDelete().callable(), ExecuteUpdateResultCheckStyle.fromExternalName(matchingTable.sqlDelete().check().toString().toLowerCase(Locale.ROOT)));
            }
        } else {
            join.setSequentialSelect(false);
            join.setInverse(false);
            join.setOptional(true);
        }
        if (noDelayInPkColumnCreation) {
            this.createPrimaryColumnsToSecondaryTable(joinColumns, propertyHolder, join);
        } else {
            this.secondaryTables.put(table.getQuotedName(), join);
            this.secondaryTableJoins.put(table.getQuotedName(), joinColumns);
        }
        return join;
    }

    public Map<String, Join> getSecondaryTables() {
        return this.secondaryTables;
    }

    public static String getCacheConcurrencyStrategy(CacheConcurrencyStrategy strategy) {
        org.hibernate.cache.spi.access.AccessType accessType = strategy.toAccessType();
        return accessType == null ? null : accessType.getExternalName();
    }

    public void addFilter(Filter filter) {
        this.filters.add(filter);
    }

    public void setInheritanceState(InheritanceState inheritanceState) {
        this.inheritanceState = inheritanceState;
    }

    public boolean isIgnoreIdAnnotations() {
        return this.ignoreIdAnnotations;
    }

    public void setIgnoreIdAnnotations(boolean ignoreIdAnnotations) {
        this.ignoreIdAnnotations = ignoreIdAnnotations;
    }

    public void processComplementaryTableDefinitions(Table table) {
        if (table == null) {
            return;
        }
        TableBinder.addIndexes(this.persistentClass.getTable(), table.indexes(), this.context);
    }

    public void processComplementaryTableDefinitions(org.hibernate.annotations.Table table) {
        if (table == null) {
            return;
        }
        String appliedTable = table.appliesTo();
        Iterator tables = this.persistentClass.getTableClosureIterator();
        org.hibernate.mapping.Table hibTable = null;
        while (tables.hasNext()) {
            org.hibernate.mapping.Table pcTable = (org.hibernate.mapping.Table)tables.next();
            if (pcTable.getQuotedName().equals(appliedTable)) {
                hibTable = pcTable;
                break;
            }
            hibTable = null;
        }
        if (hibTable == null) {
            for (Join join : this.secondaryTables.values()) {
                if (!join.getTable().getQuotedName().equals(appliedTable)) continue;
                hibTable = join.getTable();
                break;
            }
        }
        if (hibTable == null) {
            throw new AnnotationException("@org.hibernate.annotations.Table references an unknown table: " + appliedTable);
        }
        if (!BinderHelper.isEmptyAnnotationValue(table.comment())) {
            hibTable.setComment(table.comment());
        }
        TableBinder.addIndexes(hibTable, table.indexes(), this.context);
    }

    public void processComplementaryTableDefinitions(Tables tables) {
        if (tables == null) {
            return;
        }
        for (org.hibernate.annotations.Table table : tables.value()) {
            this.processComplementaryTableDefinitions(table);
        }
    }

    public AccessType getPropertyAccessType() {
        return this.propertyAccessType;
    }

    public void setPropertyAccessType(AccessType propertyAccessor) {
        this.propertyAccessType = this.getExplicitAccessType((XAnnotatedElement)this.annotatedClass);
        if (this.propertyAccessType == null) {
            this.propertyAccessType = propertyAccessor;
        }
    }

    public AccessType getPropertyAccessor(XAnnotatedElement element) {
        AccessType accessType = this.getExplicitAccessType(element);
        if (accessType == null) {
            accessType = this.propertyAccessType;
        }
        return accessType;
    }

    public AccessType getExplicitAccessType(XAnnotatedElement element) {
        Access access;
        AccessType accessType = null;
        AccessType hibernateAccessType = null;
        AccessType jpaAccessType = null;
        org.hibernate.annotations.AccessType accessTypeAnnotation = (org.hibernate.annotations.AccessType)element.getAnnotation(org.hibernate.annotations.AccessType.class);
        if (accessTypeAnnotation != null) {
            hibernateAccessType = AccessType.getAccessStrategy(accessTypeAnnotation.value());
        }
        if ((access = (Access)element.getAnnotation(Access.class)) != null) {
            jpaAccessType = AccessType.getAccessStrategy(access.value());
        }
        if (hibernateAccessType != null && jpaAccessType != null && hibernateAccessType != jpaAccessType) {
            throw new MappingException("Found @Access and @AccessType with conflicting values on a property in class " + this.annotatedClass.toString());
        }
        if (hibernateAccessType != null) {
            accessType = hibernateAccessType;
        } else if (jpaAccessType != null) {
            accessType = jpaAccessType;
        }
        return accessType;
    }

    private static class SecondaryTableNamingStrategyHelper
    implements NamingStrategyHelper {
        private SecondaryTableNamingStrategyHelper() {
        }

        @Override
        public Identifier determineImplicitName(MetadataBuildingContext buildingContext) {
            return null;
        }

        @Override
        public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
            return buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(explicitName);
        }

        @Override
        public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
            return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(logicalName, buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment());
        }
    }

    private static class EntityTableNamingStrategyHelper
    implements NamingStrategyHelper {
        private final String className;
        private final String entityName;
        private final String jpaEntityName;

        private EntityTableNamingStrategyHelper(String className, String entityName, String jpaEntityName) {
            this.className = className;
            this.entityName = entityName;
            this.jpaEntityName = jpaEntityName;
        }

        @Override
        public Identifier determineImplicitName(final MetadataBuildingContext buildingContext) {
            return buildingContext.getBuildingOptions().getImplicitNamingStrategy().determinePrimaryTableName(new ImplicitEntityNameSource(){
                private final EntityNaming entityNaming = new EntityNaming(){

                    @Override
                    public String getClassName() {
                        return className;
                    }

                    @Override
                    public String getEntityName() {
                        return entityName;
                    }

                    @Override
                    public String getJpaEntityName() {
                        return jpaEntityName;
                    }
                };

                @Override
                public EntityNaming getEntityNaming() {
                    return this.entityNaming;
                }

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return buildingContext;
                }
            });
        }

        @Override
        public Identifier handleExplicitName(String explicitName, MetadataBuildingContext buildingContext) {
            return buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment().getIdentifierHelper().toIdentifier(explicitName);
        }

        @Override
        public Identifier toPhysicalName(Identifier logicalName, MetadataBuildingContext buildingContext) {
            return buildingContext.getBuildingOptions().getPhysicalNamingStrategy().toPhysicalTableName(logicalName, buildingContext.getMetadataCollector().getDatabase().getJdbcEnvironment());
        }
    }

    private static class EntityTableObjectNameSource
    implements ObjectNameSource {
        private final String explicitName;
        private final String logicalName;

        private EntityTableObjectNameSource(String explicitName, String entityName) {
            this.explicitName = explicitName;
            this.logicalName = StringHelper.isNotEmpty(explicitName) ? explicitName : StringHelper.unqualify(entityName);
        }

        @Override
        public String getExplicitName() {
            return this.explicitName;
        }

        @Override
        public String getLogicalName() {
            return this.logicalName;
        }
    }

    private static class LocalCacheAnnotationStub
    implements Cache {
        private final String region;
        private final CacheConcurrencyStrategy usage;

        private LocalCacheAnnotationStub(String region, CacheConcurrencyStrategy usage) {
            this.region = region;
            this.usage = usage;
        }

        @Override
        public CacheConcurrencyStrategy usage() {
            return this.usage;
        }

        @Override
        public String region() {
            return this.region;
        }

        @Override
        public String include() {
            return "all";
        }

        @Override
        public Class<? extends Annotation> annotationType() {
            return Cache.class;
        }
    }
}

