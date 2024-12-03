/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeOverride
 *  javax.persistence.AttributeOverrides
 *  javax.persistence.CollectionTable
 *  javax.persistence.ConstraintMode
 *  javax.persistence.ElementCollection
 *  javax.persistence.Embeddable
 *  javax.persistence.FetchType
 *  javax.persistence.ForeignKey
 *  javax.persistence.JoinColumn
 *  javax.persistence.JoinColumns
 *  javax.persistence.JoinTable
 *  javax.persistence.ManyToMany
 *  javax.persistence.MapKey
 *  javax.persistence.MapKeyColumn
 *  javax.persistence.OneToMany
 *  javax.persistence.OrderBy
 *  org.hibernate.annotations.common.reflection.XClass
 *  org.hibernate.annotations.common.reflection.XProperty
 *  org.jboss.logging.Logger
 */
package org.hibernate.cfg.annotations;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.ConstraintMode;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapKey;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.FetchMode;
import org.hibernate.MappingException;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CollectionId;
import org.hibernate.annotations.CollectionType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterJoinTable;
import org.hibernate.annotations.FilterJoinTables;
import org.hibernate.annotations.Filters;
import org.hibernate.annotations.ForeignKey;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyGroup;
import org.hibernate.annotations.Loader;
import org.hibernate.annotations.ManyToAny;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.OptimisticLock;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Persister;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortComparator;
import org.hibernate.annotations.SortNatural;
import org.hibernate.annotations.SortType;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.cfg.AccessType;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.AnnotationBinder;
import org.hibernate.cfg.BinderHelper;
import org.hibernate.cfg.CollectionPropertyHolder;
import org.hibernate.cfg.CollectionSecondPass;
import org.hibernate.cfg.Ejb3Column;
import org.hibernate.cfg.Ejb3JoinColumn;
import org.hibernate.cfg.IndexColumn;
import org.hibernate.cfg.InheritanceState;
import org.hibernate.cfg.PropertyHolder;
import org.hibernate.cfg.PropertyHolderBuilder;
import org.hibernate.cfg.PropertyInferredData;
import org.hibernate.cfg.PropertyPreloadedData;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.annotations.ArrayBinder;
import org.hibernate.cfg.annotations.BagBinder;
import org.hibernate.cfg.annotations.EntityBinder;
import org.hibernate.cfg.annotations.IdBagBinder;
import org.hibernate.cfg.annotations.ListBinder;
import org.hibernate.cfg.annotations.MapBinder;
import org.hibernate.cfg.annotations.Nullability;
import org.hibernate.cfg.annotations.PrimitiveArrayBinder;
import org.hibernate.cfg.annotations.PropertyBinder;
import org.hibernate.cfg.annotations.SetBinder;
import org.hibernate.cfg.annotations.SimpleValueBinder;
import org.hibernate.cfg.annotations.TableBinder;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.ExecuteUpdateResultCheckStyle;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.mapping.Any;
import org.hibernate.mapping.Backref;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.DependantValue;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Selectable;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Value;
import org.jboss.logging.Logger;

public abstract class CollectionBinder {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)CollectionBinder.class.getName());
    private static final List<Class<?>> INFERRED_CLASS_PRIORITY = Collections.unmodifiableList(Arrays.asList(List.class, SortedSet.class, Set.class, SortedMap.class, Map.class, Collection.class));
    private MetadataBuildingContext buildingContext;
    protected org.hibernate.mapping.Collection collection;
    protected String propertyName;
    PropertyHolder propertyHolder;
    private int batchSize;
    private String mappedBy;
    private XClass collectionType;
    private XClass targetEntity;
    private Ejb3JoinColumn[] inverseJoinColumns;
    private String cascadeStrategy;
    private String cacheConcurrencyStrategy;
    private String cacheRegionName;
    private boolean oneToMany;
    protected IndexColumn indexColumn;
    protected boolean cascadeDeleteEnabled;
    protected String mapKeyPropertyName;
    private boolean insertable = true;
    private boolean updatable = true;
    private Ejb3JoinColumn[] fkJoinColumns;
    private boolean isExplicitAssociationTable;
    private Ejb3Column[] elementColumns;
    private boolean isEmbedded;
    private XProperty property;
    private NotFoundAction notFoundAction;
    private TableBinder tableBinder;
    private Ejb3Column[] mapKeyColumns;
    private Ejb3JoinColumn[] mapKeyManyToManyColumns;
    protected HashMap<String, IdentifierGeneratorDefinition> localGenerators;
    protected Map<XClass, InheritanceState> inheritanceStatePerClass;
    private XClass declaringClass;
    private boolean declaringClassSet;
    private AccessType accessType;
    private boolean hibernateExtensionMapping;
    private boolean isSortedCollection;
    private OrderBy jpaOrderBy;
    private org.hibernate.annotations.OrderBy sqlOrderBy;
    private Sort deprecatedSort;
    private SortNatural naturalSort;
    private SortComparator comparatorSort;
    private String explicitType;
    private final Properties explicitTypeParameters = new Properties();
    private Ejb3JoinColumn[] joinColumns;

    protected CollectionBinder(boolean isSortedCollection) {
        this.isSortedCollection = isSortedCollection;
    }

    protected MetadataBuildingContext getBuildingContext() {
        return this.buildingContext;
    }

    public void setBuildingContext(MetadataBuildingContext buildingContext) {
        this.buildingContext = buildingContext;
    }

    public boolean isMap() {
        return false;
    }

    protected void setIsHibernateExtensionMapping(boolean hibernateExtensionMapping) {
        this.hibernateExtensionMapping = hibernateExtensionMapping;
    }

    protected boolean isHibernateExtensionMapping() {
        return this.hibernateExtensionMapping;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public void setInheritanceStatePerClass(Map<XClass, InheritanceState> inheritanceStatePerClass) {
        this.inheritanceStatePerClass = inheritanceStatePerClass;
    }

    public void setInsertable(boolean insertable) {
        this.insertable = insertable;
    }

    public void setCascadeStrategy(String cascadeStrategy) {
        this.cascadeStrategy = cascadeStrategy;
    }

    public void setAccessType(AccessType accessType) {
        this.accessType = accessType;
    }

    public void setInverseJoinColumns(Ejb3JoinColumn[] inverseJoinColumns) {
        this.inverseJoinColumns = inverseJoinColumns;
    }

    public void setJoinColumns(Ejb3JoinColumn[] joinColumns) {
        this.joinColumns = joinColumns;
    }

    public void setPropertyHolder(PropertyHolder propertyHolder) {
        this.propertyHolder = propertyHolder;
    }

    public void setBatchSize(BatchSize batchSize) {
        this.batchSize = batchSize == null ? -1 : batchSize.size();
    }

    public void setJpaOrderBy(OrderBy jpaOrderBy) {
        this.jpaOrderBy = jpaOrderBy;
    }

    public void setSqlOrderBy(org.hibernate.annotations.OrderBy sqlOrderBy) {
        this.sqlOrderBy = sqlOrderBy;
    }

    public void setSort(Sort deprecatedSort) {
        this.deprecatedSort = deprecatedSort;
    }

    public void setNaturalSort(SortNatural naturalSort) {
        this.naturalSort = naturalSort;
    }

    public void setComparatorSort(SortComparator comparatorSort) {
        this.comparatorSort = comparatorSort;
    }

    public static CollectionBinder getCollectionBinder(String entityName, XProperty property, boolean isIndexed, boolean isHibernateExtensionMapping, MetadataBuildingContext buildingContext) {
        CollectionBinder result;
        if (property.isArray()) {
            result = property.getElementClass().isPrimitive() ? new PrimitiveArrayBinder() : new ArrayBinder();
        } else if (property.isCollection()) {
            Class<?> inferredClass;
            Class<?> semanticsClass;
            Class returnedClass = property.getCollectionClass();
            CollectionBinder basicBinder = CollectionBinder.getBinderFromBasicCollectionType(returnedClass, property, entityName, isIndexed);
            result = basicBinder != null ? basicBinder : (property.isAnnotationPresent(CollectionType.class) ? ((semanticsClass = ((CollectionType)property.getAnnotation(CollectionType.class)).semantics()) != Void.TYPE ? CollectionBinder.getBinderFromBasicCollectionType(semanticsClass, property, entityName, isIndexed) : ((inferredClass = CollectionBinder.inferCollectionClassFromSubclass(returnedClass)) != null ? CollectionBinder.getBinderFromBasicCollectionType(inferredClass, property, entityName, isIndexed) : null)) : null);
            if (result == null) {
                throw new AnnotationException(returnedClass.getName() + " collection type not supported for property: " + StringHelper.qualify(entityName, property.getName()));
            }
        } else {
            throw new AnnotationException("Illegal attempt to map a non collection as a @OneToMany, @ManyToMany or @CollectionOfElements: " + StringHelper.qualify(entityName, property.getName()));
        }
        result.setIsHibernateExtensionMapping(isHibernateExtensionMapping);
        CollectionType typeAnnotation = (CollectionType)property.getAnnotation(CollectionType.class);
        if (typeAnnotation != null) {
            String typeName = typeAnnotation.type();
            TypeDefinition typeDef = buildingContext.getMetadataCollector().getTypeDefinition(typeName);
            if (typeDef != null) {
                result.explicitType = typeDef.getTypeImplementorClass().getName();
                result.explicitTypeParameters.putAll(typeDef.getParameters());
            } else {
                result.explicitType = typeName;
                for (Parameter param : typeAnnotation.parameters()) {
                    result.explicitTypeParameters.setProperty(param.name(), param.value());
                }
            }
        }
        return result;
    }

    private static CollectionBinder getBinderFromBasicCollectionType(Class<?> clazz, XProperty property, String entityName, boolean isIndexed) {
        if (Set.class.equals(clazz)) {
            if (property.isAnnotationPresent(CollectionId.class)) {
                throw new AnnotationException("Set do not support @CollectionId: " + StringHelper.qualify(entityName, property.getName()));
            }
            return new SetBinder(false);
        }
        if (SortedSet.class.equals(clazz)) {
            if (property.isAnnotationPresent(CollectionId.class)) {
                throw new AnnotationException("Set do not support @CollectionId: " + StringHelper.qualify(entityName, property.getName()));
            }
            return new SetBinder(true);
        }
        if (Map.class.equals(clazz)) {
            if (property.isAnnotationPresent(CollectionId.class)) {
                throw new AnnotationException("Map do not support @CollectionId: " + StringHelper.qualify(entityName, property.getName()));
            }
            return new MapBinder(false);
        }
        if (SortedMap.class.equals(clazz)) {
            if (property.isAnnotationPresent(CollectionId.class)) {
                throw new AnnotationException("Map do not support @CollectionId: " + StringHelper.qualify(entityName, property.getName()));
            }
            return new MapBinder(true);
        }
        if (Collection.class.equals(clazz)) {
            if (property.isAnnotationPresent(CollectionId.class)) {
                return new IdBagBinder();
            }
            return new BagBinder();
        }
        if (List.class.equals(clazz)) {
            if (isIndexed) {
                if (property.isAnnotationPresent(CollectionId.class)) {
                    throw new AnnotationException("List do not support @CollectionId and @OrderColumn (or @IndexColumn) at the same time: " + StringHelper.qualify(entityName, property.getName()));
                }
                return new ListBinder();
            }
            if (property.isAnnotationPresent(CollectionId.class)) {
                return new IdBagBinder();
            }
            return new BagBinder();
        }
        return null;
    }

    private static Class<?> inferCollectionClassFromSubclass(Class<?> clazz) {
        for (Class<?> priorityClass : INFERRED_CLASS_PRIORITY) {
            if (!priorityClass.isAssignableFrom(clazz)) continue;
            return priorityClass;
        }
        return null;
    }

    public void setMappedBy(String mappedBy) {
        this.mappedBy = mappedBy;
    }

    public void setTableBinder(TableBinder tableBinder) {
        this.tableBinder = tableBinder;
    }

    public void setCollectionType(XClass collectionType) {
        this.collectionType = collectionType;
    }

    public void setTargetEntity(XClass targetEntity) {
        this.targetEntity = targetEntity;
    }

    protected abstract org.hibernate.mapping.Collection createCollection(PersistentClass var1);

    public org.hibernate.mapping.Collection getCollection() {
        return this.collection;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public void setDeclaringClass(XClass declaringClass) {
        this.declaringClass = declaringClass;
        this.declaringClassSet = true;
    }

    public void bind() {
        this.collection = this.createCollection(this.propertyHolder.getPersistentClass());
        String role = StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName);
        LOG.debugf("Collection role: %s", role);
        this.collection.setRole(role);
        this.collection.setMappedByProperty(this.mappedBy);
        if (this.property.isAnnotationPresent(MapKeyColumn.class) && this.mapKeyPropertyName != null) {
            throw new AnnotationException("Cannot mix @javax.persistence.MapKey and @MapKeyColumn or @org.hibernate.annotations.MapKey on the same collection: " + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
        }
        InFlightMetadataCollector metadataCollector = this.buildingContext.getMetadataCollector();
        if (this.explicitType != null) {
            TypeDefinition typeDef = metadataCollector.getTypeDefinition(this.explicitType);
            if (typeDef == null) {
                this.collection.setTypeName(this.explicitType);
                this.collection.setTypeParameters(this.explicitTypeParameters);
            } else {
                this.collection.setTypeName(typeDef.getTypeImplementorClass().getName());
                this.collection.setTypeParameters(typeDef.getParameters());
            }
        }
        this.defineFetchingStrategy();
        this.collection.setBatchSize(this.batchSize);
        this.collection.setMutable(!this.property.isAnnotationPresent(Immutable.class));
        boolean isMappedBy = !BinderHelper.isEmptyAnnotationValue(this.mappedBy);
        OptimisticLock lockAnn = (OptimisticLock)this.property.getAnnotation(OptimisticLock.class);
        boolean includeInOptimisticLockChecks = lockAnn != null ? !lockAnn.excluded() : !isMappedBy;
        this.collection.setOptimisticLocked(includeInOptimisticLockChecks);
        Persister persisterAnn = (Persister)this.property.getAnnotation(Persister.class);
        if (persisterAnn != null) {
            this.collection.setCollectionPersisterClass(persisterAnn.impl());
        }
        this.applySortingAndOrdering(this.collection);
        if (StringHelper.isNotEmpty(this.cacheConcurrencyStrategy)) {
            this.collection.setCacheConcurrencyStrategy(this.cacheConcurrencyStrategy);
            this.collection.setCacheRegionName(this.cacheRegionName);
        }
        SQLInsert sqlInsert = (SQLInsert)this.property.getAnnotation(SQLInsert.class);
        SQLUpdate sqlUpdate = (SQLUpdate)this.property.getAnnotation(SQLUpdate.class);
        SQLDelete sqlDelete = (SQLDelete)this.property.getAnnotation(SQLDelete.class);
        SQLDeleteAll sqlDeleteAll = (SQLDeleteAll)this.property.getAnnotation(SQLDeleteAll.class);
        Loader loader = (Loader)this.property.getAnnotation(Loader.class);
        if (sqlInsert != null) {
            this.collection.setCustomSQLInsert(sqlInsert.sql().trim(), sqlInsert.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlInsert.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlUpdate != null) {
            this.collection.setCustomSQLUpdate(sqlUpdate.sql(), sqlUpdate.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlUpdate.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlDelete != null) {
            this.collection.setCustomSQLDelete(sqlDelete.sql(), sqlDelete.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlDelete.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (sqlDeleteAll != null) {
            this.collection.setCustomSQLDeleteAll(sqlDeleteAll.sql(), sqlDeleteAll.callable(), ExecuteUpdateResultCheckStyle.fromExternalName(sqlDeleteAll.check().toString().toLowerCase(Locale.ROOT)));
        }
        if (loader != null) {
            this.collection.setLoaderName(loader.namedQuery());
        }
        if (isMappedBy && (this.property.isAnnotationPresent(JoinColumn.class) || this.property.isAnnotationPresent(JoinColumns.class) || this.propertyHolder.getJoinTable(this.property) != null)) {
            String message = "Associations marked as mappedBy must not define database mappings like @JoinTable or @JoinColumn: ";
            message = message + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName);
            throw new AnnotationException(message);
        }
        if (!isMappedBy && this.oneToMany && this.property.isAnnotationPresent(OnDelete.class) && !this.property.isAnnotationPresent(JoinColumn.class)) {
            String message = "Unidirectional one-to-many associations annotated with @OnDelete must define @JoinColumn: ";
            message = message + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName);
            throw new AnnotationException(message);
        }
        this.collection.setInverse(isMappedBy);
        if (!this.oneToMany && isMappedBy) {
            metadataCollector.addMappedBy(this.getCollectionType().getName(), this.mappedBy, this.propertyName);
        }
        XClass collectionType = this.getCollectionType();
        if (this.inheritanceStatePerClass == null) {
            throw new AssertionFailure("inheritanceStatePerClass not set");
        }
        SecondPass sp = this.getSecondPass(this.fkJoinColumns, this.joinColumns, this.inverseJoinColumns, this.elementColumns, this.mapKeyColumns, this.mapKeyManyToManyColumns, this.isEmbedded, this.property, collectionType, this.notFoundAction, this.oneToMany, this.tableBinder, this.buildingContext);
        if (collectionType.isAnnotationPresent(Embeddable.class) || this.property.isAnnotationPresent(ElementCollection.class)) {
            metadataCollector.addSecondPass(sp, !isMappedBy);
        } else {
            metadataCollector.addSecondPass(sp, !isMappedBy);
        }
        metadataCollector.addCollectionBinding(this.collection);
        PropertyBinder binder = new PropertyBinder();
        binder.setName(this.propertyName);
        binder.setValue(this.collection);
        binder.setCascade(this.cascadeStrategy);
        if (this.cascadeStrategy != null && this.cascadeStrategy.contains("delete-orphan")) {
            this.collection.setOrphanDelete(true);
        }
        binder.setLazy(this.collection.isLazy());
        LazyGroup lazyGroupAnnotation = (LazyGroup)this.property.getAnnotation(LazyGroup.class);
        if (lazyGroupAnnotation != null) {
            binder.setLazyGroup(lazyGroupAnnotation.value());
        }
        binder.setAccessType(this.accessType);
        binder.setProperty(this.property);
        binder.setInsertable(this.insertable);
        binder.setUpdatable(this.updatable);
        Property prop = binder.makeProperty();
        if (!this.declaringClassSet) {
            throw new AssertionFailure("DeclaringClass is not set in CollectionBinder while binding");
        }
        this.propertyHolder.addProperty(prop, this.declaringClass);
    }

    private void applySortingAndOrdering(org.hibernate.mapping.Collection collection) {
        boolean hadOrderBy = false;
        boolean hadExplicitSort = false;
        Class comparatorClass = null;
        if (this.jpaOrderBy == null && this.sqlOrderBy == null) {
            if (this.deprecatedSort != null) {
                LOG.debug("Encountered deprecated @Sort annotation; use @SortNatural or @SortComparator instead.");
                if (this.naturalSort != null || this.comparatorSort != null) {
                    throw this.buildIllegalSortCombination();
                }
                boolean bl = hadExplicitSort = this.deprecatedSort.type() != SortType.UNSORTED;
                if (this.deprecatedSort.type() == SortType.NATURAL) {
                    this.isSortedCollection = true;
                } else if (this.deprecatedSort.type() == SortType.COMPARATOR) {
                    this.isSortedCollection = true;
                    comparatorClass = this.deprecatedSort.comparator();
                }
            } else if (this.naturalSort != null) {
                if (this.comparatorSort != null) {
                    throw this.buildIllegalSortCombination();
                }
                hadExplicitSort = true;
            } else if (this.comparatorSort != null) {
                hadExplicitSort = true;
                comparatorClass = this.comparatorSort.value();
            }
        } else {
            if (this.jpaOrderBy != null && this.sqlOrderBy != null) {
                throw new AnnotationException(String.format("Illegal combination of @%s and @%s on %s", OrderBy.class.getName(), org.hibernate.annotations.OrderBy.class.getName(), this.safeCollectionRole()));
            }
            hadOrderBy = true;
            hadExplicitSort = false;
            if (this.sqlOrderBy != null) {
                collection.setOrderBy(this.sqlOrderBy.clause());
            }
        }
        if (this.isSortedCollection && !hadExplicitSort && !hadOrderBy) {
            throw new AnnotationException("A sorted collection must define and ordering or sorting : " + this.safeCollectionRole());
        }
        collection.setSorted(this.isSortedCollection || hadExplicitSort);
        if (comparatorClass != null) {
            try {
                collection.setComparator(comparatorClass.newInstance());
            }
            catch (Exception e) {
                throw new AnnotationException(String.format("Could not instantiate comparator class [%s] for %s", comparatorClass.getName(), this.safeCollectionRole()));
            }
        }
    }

    private AnnotationException buildIllegalSortCombination() {
        return new AnnotationException(String.format("Illegal combination of annotations on %s.  Only one of @%s, @%s and @%s can be used", this.safeCollectionRole(), Sort.class.getName(), SortNatural.class.getName(), SortComparator.class.getName()));
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private void defineFetchingStrategy() {
        FetchType fetchType;
        LazyCollection lazy = (LazyCollection)this.property.getAnnotation(LazyCollection.class);
        Fetch fetch = (Fetch)this.property.getAnnotation(Fetch.class);
        OneToMany oneToMany = (OneToMany)this.property.getAnnotation(OneToMany.class);
        ManyToMany manyToMany = (ManyToMany)this.property.getAnnotation(ManyToMany.class);
        ElementCollection elementCollection = (ElementCollection)this.property.getAnnotation(ElementCollection.class);
        ManyToAny manyToAny = (ManyToAny)this.property.getAnnotation(ManyToAny.class);
        NotFound notFound = (NotFound)this.property.getAnnotation(NotFound.class);
        if (oneToMany != null) {
            fetchType = oneToMany.fetch();
        } else if (manyToMany != null) {
            fetchType = manyToMany.fetch();
        } else if (elementCollection != null) {
            fetchType = elementCollection.fetch();
        } else {
            if (manyToAny == null) throw new AssertionFailure("Define fetch strategy on a property not annotated with @ManyToOne nor @OneToMany nor @CollectionOfElements");
            fetchType = FetchType.LAZY;
        }
        if (notFound != null) {
            this.collection.setLazy(false);
            if (lazy != null) {
                this.collection.setExtraLazy(lazy.value() == LazyCollectionOption.EXTRA);
            }
            if (fetch != null) {
                if (fetch.value() == null) return;
                this.collection.setFetchMode(fetch.value().getHibernateFetchMode());
                if (fetch.value() != org.hibernate.annotations.FetchMode.SUBSELECT) return;
                this.collection.setSubselectLoadable(true);
                this.collection.getOwner().setSubselectLoadableCollections(true);
                return;
            } else {
                this.collection.setFetchMode(AnnotationBinder.getFetchMode(fetchType));
            }
            return;
        } else {
            if (lazy != null) {
                this.collection.setLazy(lazy.value() != LazyCollectionOption.FALSE);
                this.collection.setExtraLazy(lazy.value() == LazyCollectionOption.EXTRA);
            } else {
                this.collection.setLazy(fetchType == FetchType.LAZY);
                this.collection.setExtraLazy(false);
            }
            if (fetch != null) {
                if (fetch.value() == org.hibernate.annotations.FetchMode.JOIN) {
                    this.collection.setFetchMode(FetchMode.JOIN);
                    this.collection.setLazy(false);
                    return;
                } else if (fetch.value() == org.hibernate.annotations.FetchMode.SELECT) {
                    this.collection.setFetchMode(FetchMode.SELECT);
                    return;
                } else {
                    if (fetch.value() != org.hibernate.annotations.FetchMode.SUBSELECT) throw new AssertionFailure("Unknown FetchMode: " + (Object)((Object)fetch.value()));
                    this.collection.setFetchMode(FetchMode.SELECT);
                    this.collection.setSubselectLoadable(true);
                    this.collection.getOwner().setSubselectLoadableCollections(true);
                }
                return;
            } else {
                this.collection.setFetchMode(AnnotationBinder.getFetchMode(fetchType));
            }
        }
    }

    private XClass getCollectionType() {
        if (AnnotationBinder.isDefault(this.targetEntity, this.buildingContext)) {
            if (this.collectionType != null) {
                return this.collectionType;
            }
            String errorMsg = "Collection has neither generic type or OneToMany.targetEntity() defined: " + this.safeCollectionRole();
            throw new AnnotationException(errorMsg);
        }
        return this.targetEntity;
    }

    public SecondPass getSecondPass(final Ejb3JoinColumn[] fkJoinColumns, final Ejb3JoinColumn[] keyColumns, final Ejb3JoinColumn[] inverseColumns, final Ejb3Column[] elementColumns, Ejb3Column[] mapKeyColumns, Ejb3JoinColumn[] mapKeyManyToManyColumns, final boolean isEmbedded, final XProperty property, final XClass collType, final NotFoundAction notFoundAction, final boolean unique, final TableBinder assocTableBinder, final MetadataBuildingContext buildingContext) {
        return new CollectionSecondPass(buildingContext, this.collection){

            @Override
            public void secondPass(Map persistentClasses, Map inheritedMetas) {
                CollectionBinder.this.bindStarToManySecondPass(persistentClasses, collType, fkJoinColumns, keyColumns, inverseColumns, elementColumns, isEmbedded, property, unique, assocTableBinder, notFoundAction, buildingContext);
            }
        };
    }

    protected boolean bindStarToManySecondPass(Map<String, PersistentClass> persistentClasses, XClass collType, Ejb3JoinColumn[] fkJoinColumns, Ejb3JoinColumn[] keyColumns, Ejb3JoinColumn[] inverseColumns, Ejb3Column[] elementColumns, boolean isEmbedded, XProperty property, boolean unique, TableBinder associationTableBinder, NotFoundAction notFoundAction, MetadataBuildingContext buildingContext) {
        PersistentClass persistentClass = persistentClasses.get(collType.getName());
        boolean reversePropertyInJoin = false;
        if (persistentClass != null && StringHelper.isNotEmpty(this.mappedBy)) {
            try {
                reversePropertyInJoin = 0 != persistentClass.getJoinNumber(persistentClass.getRecursiveProperty(this.mappedBy));
            }
            catch (MappingException e) {
                throw new AnnotationException("mappedBy reference an unknown target entity property: " + collType + "." + this.mappedBy + " in " + this.collection.getOwnerEntityName() + "." + property.getName());
            }
        }
        if (persistentClass != null && !reversePropertyInJoin && this.oneToMany && !this.isExplicitAssociationTable && (this.joinColumns[0].isImplicit() && !BinderHelper.isEmptyAnnotationValue(this.mappedBy) || !fkJoinColumns[0].isImplicit())) {
            this.bindOneToManySecondPass(this.getCollection(), persistentClasses, fkJoinColumns, collType, this.cascadeDeleteEnabled, notFoundAction, buildingContext, this.inheritanceStatePerClass);
            return true;
        }
        this.bindManyToManySecondPass(this.collection, persistentClasses, keyColumns, inverseColumns, elementColumns, isEmbedded, collType, notFoundAction, unique, this.cascadeDeleteEnabled, associationTableBinder, property, this.propertyHolder, buildingContext);
        return false;
    }

    protected void bindOneToManySecondPass(org.hibernate.mapping.Collection collection, Map<String, PersistentClass> persistentClasses, Ejb3JoinColumn[] fkJoinColumns, XClass collectionType, boolean cascadeDeleteEnabled, NotFoundAction notFoundAction, MetadataBuildingContext buildingContext, Map<XClass, InheritanceState> inheritanceStatePerClass) {
        String orderByFragment;
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Binding a OneToMany: %s.%s through a foreign key", this.propertyHolder.getEntityName(), this.propertyName);
        }
        if (buildingContext == null) {
            throw new AssertionFailure("CollectionSecondPass for oneToMany should not be called with null mappings");
        }
        org.hibernate.mapping.OneToMany oneToMany = new org.hibernate.mapping.OneToMany(buildingContext, collection.getOwner());
        collection.setElement(oneToMany);
        oneToMany.setReferencedEntityName(collectionType.getName());
        oneToMany.setNotFoundAction(notFoundAction);
        String assocClass = oneToMany.getReferencedEntityName();
        PersistentClass associatedClass = persistentClasses.get(assocClass);
        if (this.jpaOrderBy != null && StringHelper.isNotEmpty(orderByFragment = CollectionBinder.buildOrderByClauseFromHql(this.jpaOrderBy.value(), associatedClass, collection.getRole()))) {
            collection.setOrderBy(orderByFragment);
        }
        Map<String, Join> joins = buildingContext.getMetadataCollector().getJoins(assocClass);
        if (associatedClass == null) {
            throw new MappingException(String.format("Association [%s] for entity [%s] references unmapped class [%s]", this.propertyName, this.propertyHolder.getClassName(), assocClass));
        }
        oneToMany.setAssociatedClass(associatedClass);
        for (Ejb3JoinColumn column : fkJoinColumns) {
            column.setPersistentClass(associatedClass, joins, inheritanceStatePerClass);
            column.setJoins(joins);
            collection.setCollectionTable(column.getTable());
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Mapping collection: %s -> %s", collection.getRole(), collection.getCollectionTable().getName());
        }
        this.bindFilters(false);
        CollectionBinder.bindCollectionSecondPass(collection, null, fkJoinColumns, cascadeDeleteEnabled, this.property, this.propertyHolder, buildingContext);
        if (!collection.isInverse() && !collection.getKey().isNullable()) {
            String entityName = oneToMany.getReferencedEntityName();
            PersistentClass referenced = buildingContext.getMetadataCollector().getEntityBinding(entityName);
            Backref prop = new Backref();
            prop.setName('_' + fkJoinColumns[0].getPropertyName() + '_' + fkJoinColumns[0].getLogicalColumnName() + "Backref");
            prop.setUpdateable(false);
            prop.setSelectable(false);
            prop.setCollectionRole(collection.getRole());
            prop.setEntityName(collection.getOwner().getEntityName());
            prop.setValue(collection.getKey());
            referenced.addProperty(prop);
        }
    }

    private void bindFilters(boolean hasAssociationTable) {
        String whereJoinTableClause;
        Where whereOnClass;
        FilterJoinTables filterJoinTables;
        FilterJoinTable simpleFilterJoinTable;
        Filters filters;
        Filter simpleFilter = (Filter)this.property.getAnnotation(Filter.class);
        if (simpleFilter != null) {
            if (hasAssociationTable) {
                this.collection.addManyToManyFilter(simpleFilter.name(), this.getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(simpleFilter.aliases()), BinderHelper.toAliasEntityMap(simpleFilter.aliases()));
            } else {
                this.collection.addFilter(simpleFilter.name(), this.getCondition(simpleFilter), simpleFilter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(simpleFilter.aliases()), BinderHelper.toAliasEntityMap(simpleFilter.aliases()));
            }
        }
        if ((filters = (Filters)this.property.getAnnotation(Filters.class)) != null) {
            for (Filter filter : filters.value()) {
                if (hasAssociationTable) {
                    this.collection.addManyToManyFilter(filter.name(), this.getCondition(filter), filter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(filter.aliases()), BinderHelper.toAliasEntityMap(filter.aliases()));
                    continue;
                }
                this.collection.addFilter(filter.name(), this.getCondition(filter), filter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(filter.aliases()), BinderHelper.toAliasEntityMap(filter.aliases()));
            }
        }
        if ((simpleFilterJoinTable = (FilterJoinTable)this.property.getAnnotation(FilterJoinTable.class)) != null) {
            if (hasAssociationTable) {
                this.collection.addFilter(simpleFilterJoinTable.name(), simpleFilterJoinTable.condition(), simpleFilterJoinTable.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(simpleFilterJoinTable.aliases()), BinderHelper.toAliasEntityMap(simpleFilterJoinTable.aliases()));
            } else {
                throw new AnnotationException("Illegal use of @FilterJoinTable on an association without join table:" + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
            }
        }
        if ((filterJoinTables = (FilterJoinTables)this.property.getAnnotation(FilterJoinTables.class)) != null) {
            for (FilterJoinTable filter : filterJoinTables.value()) {
                if (!hasAssociationTable) {
                    throw new AnnotationException("Illegal use of @FilterJoinTable on an association without join table:" + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
                }
                this.collection.addFilter(filter.name(), filter.condition(), filter.deduceAliasInjectionPoints(), BinderHelper.toAliasTableMap(filter.aliases()), BinderHelper.toAliasEntityMap(filter.aliases()));
            }
        }
        boolean useEntityWhereClauseForCollections = ConfigurationHelper.getBoolean("hibernate.use_entity_where_clause_for_collections", this.buildingContext.getBuildingOptions().getServiceRegistry().getService(ConfigurationService.class).getSettings(), true);
        String whereOnClassClause = null;
        if (useEntityWhereClauseForCollections && this.property.getElementClass() != null && (whereOnClass = (Where)this.property.getElementClass().getAnnotation(Where.class)) != null) {
            whereOnClassClause = whereOnClass.clause();
        }
        Where whereOnCollection = (Where)this.property.getAnnotation(Where.class);
        String whereOnCollectionClause = null;
        if (whereOnCollection != null) {
            whereOnCollectionClause = whereOnCollection.clause();
        }
        String whereClause = StringHelper.getNonEmptyOrConjunctionIfBothNonEmpty(whereOnClassClause, whereOnCollectionClause);
        if (hasAssociationTable) {
            this.collection.setManyToManyWhere(whereClause);
        } else {
            this.collection.setWhere(whereClause);
        }
        WhereJoinTable whereJoinTable = (WhereJoinTable)this.property.getAnnotation(WhereJoinTable.class);
        String string = whereJoinTableClause = whereJoinTable == null ? null : whereJoinTable.clause();
        if (StringHelper.isNotEmpty(whereJoinTableClause)) {
            if (hasAssociationTable) {
                this.collection.setWhere(whereJoinTableClause);
            } else {
                throw new AnnotationException("Illegal use of @WhereJoinTable on an association without join table:" + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
            }
        }
    }

    private String getCondition(Filter filter) {
        String name = filter.name();
        String cond = filter.condition();
        return this.getCondition(cond, name);
    }

    private String getCondition(String cond, String name) {
        if (BinderHelper.isEmptyAnnotationValue(cond) && StringHelper.isEmpty(cond = this.buildingContext.getMetadataCollector().getFilterDefinition(name).getDefaultFilterCondition())) {
            throw new AnnotationException("no filter condition found for filter " + name + " in " + StringHelper.qualify(this.propertyHolder.getPath(), this.propertyName));
        }
        return cond;
    }

    public void setCache(Cache cacheAnn) {
        if (cacheAnn != null) {
            this.cacheRegionName = BinderHelper.isEmptyAnnotationValue(cacheAnn.region()) ? null : cacheAnn.region();
            this.cacheConcurrencyStrategy = EntityBinder.getCacheConcurrencyStrategy(cacheAnn.usage());
        } else {
            this.cacheConcurrencyStrategy = null;
            this.cacheRegionName = null;
        }
    }

    public void setOneToMany(boolean oneToMany) {
        this.oneToMany = oneToMany;
    }

    public void setIndexColumn(IndexColumn indexColumn) {
        this.indexColumn = indexColumn;
    }

    public void setMapKey(MapKey key) {
        if (key != null) {
            this.mapKeyPropertyName = key.name();
        }
    }

    private static String buildOrderByClauseFromHql(String orderByFragment, PersistentClass associatedClass, String role) {
        if (orderByFragment != null) {
            if (orderByFragment.length() == 0) {
                return "id asc";
            }
            if ("desc".equals(orderByFragment)) {
                return "id desc";
            }
        }
        return orderByFragment;
    }

    public static String adjustUserSuppliedValueCollectionOrderingFragment(String orderByFragment) {
        if (orderByFragment != null) {
            if ((orderByFragment = orderByFragment.trim()).length() == 0 || orderByFragment.equalsIgnoreCase("asc")) {
                return "$element$ asc";
            }
            if (orderByFragment.equalsIgnoreCase("desc")) {
                return "$element$ desc";
            }
        }
        return orderByFragment;
    }

    private static SimpleValue buildCollectionKey(org.hibernate.mapping.Collection collValue, Ejb3JoinColumn[] joinColumns, boolean cascadeDeleteEnabled, boolean noConstraintByDefault, XProperty property, PropertyHolder propertyHolder, MetadataBuildingContext buildingContext) {
        String propRef;
        if (joinColumns.length > 0 && StringHelper.isNotEmpty(joinColumns[0].getMappedBy())) {
            String entityName = joinColumns[0].getManyToManyOwnerSideEntityName() != null ? "inverse__" + joinColumns[0].getManyToManyOwnerSideEntityName() : joinColumns[0].getPropertyHolder().getEntityName();
            String propRef2 = buildingContext.getMetadataCollector().getPropertyReferencedAssociation(entityName, joinColumns[0].getMappedBy());
            if (propRef2 != null) {
                collValue.setReferencedPropertyName(propRef2);
                buildingContext.getMetadataCollector().addPropertyReference(collValue.getOwnerEntityName(), propRef2);
            }
        }
        KeyValue keyVal = (propRef = collValue.getReferencedPropertyName()) == null ? collValue.getOwner().getIdentifier() : (KeyValue)collValue.getOwner().getReferencedProperty(propRef).getValue();
        DependantValue key = new DependantValue(buildingContext, collValue.getCollectionTable(), keyVal);
        key.setTypeName(null);
        Ejb3Column.checkPropertyConsistency(joinColumns, collValue.getOwnerEntityName());
        key.setNullable(joinColumns.length == 0 || joinColumns[0].isNullable());
        key.setUpdateable(joinColumns.length == 0 || joinColumns[0].isUpdatable());
        key.setCascadeDeleteEnabled(cascadeDeleteEnabled);
        collValue.setKey(key);
        if (property != null) {
            ForeignKey fk = (ForeignKey)property.getAnnotation(ForeignKey.class);
            if (fk != null && !BinderHelper.isEmptyAnnotationValue(fk.name())) {
                key.setForeignKeyName(fk.name());
            } else {
                CollectionTable collectionTableAnn = (CollectionTable)property.getAnnotation(CollectionTable.class);
                if (collectionTableAnn != null) {
                    if (collectionTableAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || collectionTableAnn.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault) {
                        key.setForeignKeyName("none");
                    } else {
                        key.setForeignKeyName(StringHelper.nullIfEmpty(collectionTableAnn.foreignKey().name()));
                        key.setForeignKeyDefinition(StringHelper.nullIfEmpty(collectionTableAnn.foreignKey().foreignKeyDefinition()));
                        if (key.getForeignKeyName() == null && key.getForeignKeyDefinition() == null && collectionTableAnn.joinColumns().length == 1) {
                            JoinColumn joinColumn = collectionTableAnn.joinColumns()[0];
                            key.setForeignKeyName(StringHelper.nullIfEmpty(joinColumn.foreignKey().name()));
                            key.setForeignKeyDefinition(StringHelper.nullIfEmpty(joinColumn.foreignKey().foreignKeyDefinition()));
                        }
                    }
                } else {
                    JoinTable joinTableAnn = (JoinTable)property.getAnnotation(JoinTable.class);
                    if (joinTableAnn != null) {
                        String foreignKeyName = joinTableAnn.foreignKey().name();
                        String foreignKeyDefinition = joinTableAnn.foreignKey().foreignKeyDefinition();
                        ConstraintMode foreignKeyValue = joinTableAnn.foreignKey().value();
                        if (joinTableAnn.joinColumns().length != 0) {
                            JoinColumn joinColumnAnn = joinTableAnn.joinColumns()[0];
                            if (foreignKeyName != null && foreignKeyName.isEmpty()) {
                                foreignKeyName = joinColumnAnn.foreignKey().name();
                                foreignKeyDefinition = joinColumnAnn.foreignKey().foreignKeyDefinition();
                            }
                            if (foreignKeyValue != ConstraintMode.NO_CONSTRAINT) {
                                foreignKeyValue = joinColumnAnn.foreignKey().value();
                            }
                        }
                        if (foreignKeyValue == ConstraintMode.NO_CONSTRAINT || foreignKeyValue == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault) {
                            key.setForeignKeyName("none");
                        } else {
                            key.setForeignKeyName(StringHelper.nullIfEmpty(foreignKeyName));
                            key.setForeignKeyDefinition(StringHelper.nullIfEmpty(foreignKeyDefinition));
                        }
                    } else {
                        javax.persistence.ForeignKey fkOverride = propertyHolder.getOverriddenForeignKey(StringHelper.qualify(propertyHolder.getPath(), property.getName()));
                        if (fkOverride != null && (fkOverride.value() == ConstraintMode.NO_CONSTRAINT || fkOverride.value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault)) {
                            key.setForeignKeyName("none");
                        } else if (fkOverride != null) {
                            key.setForeignKeyName(StringHelper.nullIfEmpty(fkOverride.name()));
                            key.setForeignKeyDefinition(StringHelper.nullIfEmpty(fkOverride.foreignKeyDefinition()));
                        } else {
                            OneToMany oneToManyAnn = (OneToMany)property.getAnnotation(OneToMany.class);
                            OnDelete onDeleteAnn = (OnDelete)property.getAnnotation(OnDelete.class);
                            if (!(oneToManyAnn == null || oneToManyAnn.mappedBy().isEmpty() || onDeleteAnn != null && onDeleteAnn.action() == OnDeleteAction.CASCADE)) {
                                key.setForeignKeyName("none");
                            } else {
                                JoinColumn joinColumnAnn = (JoinColumn)property.getAnnotation(JoinColumn.class);
                                if (joinColumnAnn != null) {
                                    if (joinColumnAnn.foreignKey().value() == ConstraintMode.NO_CONSTRAINT || joinColumnAnn.foreignKey().value() == ConstraintMode.PROVIDER_DEFAULT && noConstraintByDefault) {
                                        key.setForeignKeyName("none");
                                    } else {
                                        key.setForeignKeyName(StringHelper.nullIfEmpty(joinColumnAnn.foreignKey().name()));
                                        key.setForeignKeyDefinition(StringHelper.nullIfEmpty(joinColumnAnn.foreignKey().foreignKeyDefinition()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return key;
    }

    private void bindManyToManySecondPass(org.hibernate.mapping.Collection collValue, Map<String, PersistentClass> persistentClasses, Ejb3JoinColumn[] joinColumns, Ejb3JoinColumn[] inverseJoinColumns, Ejb3Column[] elementColumns, boolean isEmbedded, XClass collType, NotFoundAction notFoundAction, boolean unique, boolean cascadeDeleteEnabled, TableBinder associationTableBinder, XProperty property, PropertyHolder parentPropertyHolder, MetadataBuildingContext buildingContext) throws MappingException {
        boolean mappedBy;
        String path;
        if (property == null) {
            throw new IllegalArgumentException("null was passed for argument property");
        }
        PersistentClass collectionEntity = persistentClasses.get(collType.getName());
        String hqlOrderBy = this.extractHqlOrderBy(this.jpaOrderBy);
        boolean isCollectionOfEntities = collectionEntity != null;
        ManyToAny anyAnn = (ManyToAny)property.getAnnotation(ManyToAny.class);
        if (LOG.isDebugEnabled()) {
            path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
            if (isCollectionOfEntities && unique) {
                LOG.debugf("Binding a OneToMany: %s through an association table", path);
            } else if (isCollectionOfEntities) {
                LOG.debugf("Binding as ManyToMany: %s", path);
            } else if (anyAnn != null) {
                LOG.debugf("Binding a ManyToAny: %s", path);
            } else {
                LOG.debugf("Binding a collection of element: %s", path);
            }
        }
        if (!isCollectionOfEntities) {
            if (property.isAnnotationPresent(ManyToMany.class) || property.isAnnotationPresent(OneToMany.class)) {
                path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
                throw new AnnotationException("Use of @OneToMany or @ManyToMany targeting an unmapped class: " + path + "[" + collType + "]");
            }
            if (anyAnn != null) {
                if (parentPropertyHolder.getJoinTable(property) == null) {
                    path = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
                    throw new AnnotationException("@JoinTable is mandatory when @ManyToAny is used: " + path);
                }
            } else {
                JoinTable joinTableAnn = parentPropertyHolder.getJoinTable(property);
                if (joinTableAnn != null && joinTableAnn.inverseJoinColumns().length > 0) {
                    String path2 = collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName();
                    throw new AnnotationException("Use of @JoinTable.inverseJoinColumns targeting an unmapped class: " + path2 + "[" + collType + "]");
                }
            }
        }
        boolean bl = mappedBy = !BinderHelper.isEmptyAnnotationValue(joinColumns[0].getMappedBy());
        if (mappedBy) {
            Property otherSideProperty;
            if (!isCollectionOfEntities) {
                throw new AnnotationException("Collection of elements must not have mappedBy or association reference an unmapped entity: " + collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName());
            }
            try {
                otherSideProperty = collectionEntity.getRecursiveProperty(joinColumns[0].getMappedBy());
            }
            catch (MappingException e) {
                throw new AnnotationException("mappedBy reference an unknown target entity property: " + collType + "." + joinColumns[0].getMappedBy() + " in " + collValue.getOwnerEntityName() + "." + joinColumns[0].getPropertyName());
            }
            Table table = otherSideProperty.getValue() instanceof org.hibernate.mapping.Collection ? ((org.hibernate.mapping.Collection)otherSideProperty.getValue()).getCollectionTable() : otherSideProperty.getValue().getTable();
            collValue.setCollectionTable(table);
            String entityName = collectionEntity.getEntityName();
            for (Ejb3JoinColumn column : joinColumns) {
                column.setManyToManyOwnerSideEntityName(entityName);
            }
        } else {
            for (Ejb3JoinColumn column : joinColumns) {
                String mappedByProperty = buildingContext.getMetadataCollector().getFromMappedBy(collValue.getOwnerEntityName(), column.getPropertyName());
                Table ownerTable = collValue.getOwner().getTable();
                column.setMappedBy(collValue.getOwner().getEntityName(), collValue.getOwner().getJpaEntityName(), buildingContext.getMetadataCollector().getLogicalTableName(ownerTable), mappedByProperty);
            }
            if (StringHelper.isEmpty(associationTableBinder.getName())) {
                associationTableBinder.setDefaultName(collValue.getOwner().getClassName(), collValue.getOwner().getEntityName(), collValue.getOwner().getJpaEntityName(), buildingContext.getMetadataCollector().getLogicalTableName(collValue.getOwner().getTable()), collectionEntity != null ? collectionEntity.getClassName() : null, collectionEntity != null ? collectionEntity.getEntityName() : null, collectionEntity != null ? collectionEntity.getJpaEntityName() : null, collectionEntity != null ? buildingContext.getMetadataCollector().getLogicalTableName(collectionEntity.getTable()) : null, joinColumns[0].getPropertyName());
            }
            associationTableBinder.setJPA2ElementCollection(!isCollectionOfEntities && property.isAnnotationPresent(ElementCollection.class));
            collValue.setCollectionTable(associationTableBinder.bind());
        }
        this.bindFilters(isCollectionOfEntities);
        CollectionBinder.bindCollectionSecondPass(collValue, collectionEntity, joinColumns, cascadeDeleteEnabled, property, this.propertyHolder, buildingContext);
        ManyToOne element = null;
        if (isCollectionOfEntities) {
            ForeignKey fk;
            element = new ManyToOne(buildingContext, collValue.getCollectionTable());
            collValue.setElement(element);
            element.setReferencedEntityName(collType.getName());
            element.setFetchMode(FetchMode.JOIN);
            element.setLazy(false);
            element.setNotFoundAction(notFoundAction);
            if (hqlOrderBy != null) {
                collValue.setManyToManyOrdering(CollectionBinder.buildOrderByClauseFromHql(hqlOrderBy, collectionEntity, collValue.getRole()));
            }
            if ((fk = (ForeignKey)property.getAnnotation(ForeignKey.class)) != null && !BinderHelper.isEmptyAnnotationValue(fk.name())) {
                element.setForeignKeyName(fk.name());
            } else {
                JoinTable joinTableAnn = (JoinTable)property.getAnnotation(JoinTable.class);
                if (joinTableAnn != null) {
                    String foreignKeyName = joinTableAnn.inverseForeignKey().name();
                    String foreignKeyDefinition = joinTableAnn.inverseForeignKey().foreignKeyDefinition();
                    ConstraintMode foreignKeyValue = joinTableAnn.inverseForeignKey().value();
                    if (joinTableAnn.inverseJoinColumns().length != 0) {
                        JoinColumn joinColumnAnn = joinTableAnn.inverseJoinColumns()[0];
                        if (foreignKeyName != null && foreignKeyName.isEmpty()) {
                            foreignKeyName = joinColumnAnn.foreignKey().name();
                            foreignKeyDefinition = joinColumnAnn.foreignKey().foreignKeyDefinition();
                        }
                        if (foreignKeyValue != ConstraintMode.NO_CONSTRAINT) {
                            foreignKeyValue = joinColumnAnn.foreignKey().value();
                        }
                    }
                    if (joinTableAnn.inverseForeignKey().value() == ConstraintMode.NO_CONSTRAINT || joinTableAnn.inverseForeignKey().value() == ConstraintMode.PROVIDER_DEFAULT && buildingContext.getBuildingOptions().isNoConstraintByDefault()) {
                        element.setForeignKeyName("none");
                    } else {
                        element.setForeignKeyName(StringHelper.nullIfEmpty(foreignKeyName));
                        element.setForeignKeyDefinition(StringHelper.nullIfEmpty(foreignKeyDefinition));
                    }
                }
            }
        } else if (anyAnn != null) {
            PropertyInferredData inferredData = new PropertyInferredData(null, property, "unsupported", buildingContext.getBootstrapContext().getReflectionManager());
            for (Ejb3JoinColumn column : inverseJoinColumns) {
                column.setTable(collValue.getCollectionTable());
            }
            Any any = BinderHelper.buildAnyValue(anyAnn.metaDef(), (Ejb3JoinColumn[])inverseJoinColumns, anyAnn.metaColumn(), inferredData, cascadeDeleteEnabled, anyAnn.fetch() == FetchType.LAZY, Nullability.NO_CONSTRAINT, this.propertyHolder, new EntityBinder(), true, buildingContext);
            collValue.setElement(any);
        } else {
            CollectionPropertyHolder holder;
            XClass elementClass;
            AnnotatedClassType classType;
            if (BinderHelper.PRIMITIVE_NAMES.contains(collType.getName())) {
                classType = AnnotatedClassType.NONE;
                elementClass = null;
                holder = PropertyHolderBuilder.buildPropertyHolder(collValue, collValue.getRole(), null, property, parentPropertyHolder, buildingContext);
            } else {
                boolean attributeOverride;
                elementClass = collType;
                classType = buildingContext.getMetadataCollector().getClassType(elementClass);
                holder = PropertyHolderBuilder.buildPropertyHolder(collValue, collValue.getRole(), elementClass, property, parentPropertyHolder, buildingContext);
                parentPropertyHolder.startingProperty(property);
                boolean bl2 = attributeOverride = property.isAnnotationPresent(AttributeOverride.class) || property.isAnnotationPresent(AttributeOverrides.class);
                if (isEmbedded || attributeOverride) {
                    classType = AnnotatedClassType.EMBEDDABLE;
                }
            }
            if (AnnotatedClassType.EMBEDDABLE.equals((Object)classType)) {
                String orderBy;
                boolean isPropertyAnnotated;
                holder.prepare(property);
                EntityBinder entityBinder = new EntityBinder();
                PersistentClass owner = collValue.getOwner();
                if (owner.getIdentifierProperty() != null) {
                    isPropertyAnnotated = owner.getIdentifierProperty().getPropertyAccessorName().equals("property");
                } else if (owner.getIdentifierMapper() != null && owner.getIdentifierMapper().getPropertySpan() > 0) {
                    Property prop = (Property)owner.getIdentifierMapper().getPropertyIterator().next();
                    isPropertyAnnotated = prop.getPropertyAccessorName().equals("property");
                } else {
                    throw new AssertionFailure("Unable to guess collection property accessor name");
                }
                PropertyPreloadedData inferredData = this.isMap() ? (this.isHibernateExtensionMapping() ? new PropertyPreloadedData(AccessType.PROPERTY, "element", elementClass) : new PropertyPreloadedData(AccessType.PROPERTY, "value", elementClass)) : (this.isHibernateExtensionMapping() ? new PropertyPreloadedData(AccessType.PROPERTY, "element", elementClass) : new PropertyPreloadedData(AccessType.PROPERTY, "collection&&element", elementClass));
                boolean isNullable = true;
                Component component = AnnotationBinder.fillComponent(holder, inferredData, isPropertyAnnotated ? AccessType.PROPERTY : AccessType.FIELD, isNullable, entityBinder, false, false, true, buildingContext, this.inheritanceStatePerClass);
                collValue.setElement(component);
                if (StringHelper.isNotEmpty(hqlOrderBy) && (orderBy = CollectionBinder.adjustUserSuppliedValueCollectionOrderingFragment(hqlOrderBy)) != null) {
                    collValue.setOrderBy(orderBy);
                }
            } else {
                holder.prepare(property);
                SimpleValueBinder elementBinder = new SimpleValueBinder();
                elementBinder.setBuildingContext(buildingContext);
                elementBinder.setReturnedClassName(collType.getName());
                if (elementColumns == null || elementColumns.length == 0) {
                    elementColumns = new Ejb3Column[1];
                    Ejb3Column column = new Ejb3Column();
                    column.setImplicit(false);
                    column.setNullable(true);
                    column.setLength(255);
                    column.setLogicalColumnName("elt");
                    column.setJoins(new HashMap<String, Join>());
                    column.setBuildingContext(buildingContext);
                    column.bind();
                    elementColumns[0] = column;
                }
                for (Ejb3Column column : elementColumns) {
                    column.setTable(collValue.getCollectionTable());
                }
                elementBinder.setColumns(elementColumns);
                elementBinder.setType(property, elementClass, collValue.getOwnerEntityName(), holder.resolveElementAttributeConverterDescriptor(property, elementClass));
                elementBinder.setPersistentClassName(this.propertyHolder.getEntityName());
                elementBinder.setAccessType(this.accessType);
                collValue.setElement(elementBinder.make());
                String orderBy = CollectionBinder.adjustUserSuppliedValueCollectionOrderingFragment(hqlOrderBy);
                if (orderBy != null) {
                    collValue.setOrderBy(orderBy);
                }
            }
        }
        CollectionBinder.checkFilterConditions(collValue);
        if (isCollectionOfEntities) {
            CollectionBinder.bindManytoManyInverseFk(collectionEntity, (Ejb3JoinColumn[])inverseJoinColumns, element, unique, buildingContext);
        }
    }

    private String extractHqlOrderBy(OrderBy jpaOrderBy) {
        if (jpaOrderBy != null) {
            return jpaOrderBy.value();
        }
        return null;
    }

    private static void checkFilterConditions(org.hibernate.mapping.Collection collValue) {
        if ((collValue.getFilters().size() != 0 || StringHelper.isNotEmpty(collValue.getWhere())) && collValue.getFetchMode() == FetchMode.JOIN && !(collValue.getElement() instanceof SimpleValue) && collValue.getElement().getFetchMode() != FetchMode.JOIN) {
            throw new MappingException("@ManyToMany or @CollectionOfElements defining filter or where without join fetching not valid within collection using join fetching[" + collValue.getRole() + "]");
        }
    }

    private static void bindCollectionSecondPass(org.hibernate.mapping.Collection collValue, PersistentClass collectionEntity, Ejb3JoinColumn[] joinColumns, boolean cascadeDeleteEnabled, XProperty property, PropertyHolder propertyHolder, MetadataBuildingContext buildingContext) {
        try {
            BinderHelper.createSyntheticPropertyReference(joinColumns, collValue.getOwner(), collectionEntity, collValue, false, buildingContext);
        }
        catch (AnnotationException ex) {
            throw new AnnotationException("Unable to map collection " + collValue.getOwner().getClassName() + "." + property.getName(), (Throwable)((Object)ex));
        }
        SimpleValue key = CollectionBinder.buildCollectionKey(collValue, joinColumns, cascadeDeleteEnabled, buildingContext.getBuildingOptions().isNoConstraintByDefault(), property, propertyHolder, buildingContext);
        if (property.isAnnotationPresent(ElementCollection.class) && joinColumns.length > 0) {
            joinColumns[0].setJPA2ElementCollection(true);
        }
        TableBinder.bindFk(collValue.getOwner(), collectionEntity, joinColumns, key, false, buildingContext);
    }

    public void setCascadeDeleteEnabled(boolean onDeleteCascade) {
        this.cascadeDeleteEnabled = onDeleteCascade;
    }

    private String safeCollectionRole() {
        if (this.propertyHolder != null) {
            return this.propertyHolder.getEntityName() + "." + this.propertyName;
        }
        return "";
    }

    public static void bindManytoManyInverseFk(PersistentClass referencedEntity, Ejb3JoinColumn[] columns, SimpleValue value, boolean unique, MetadataBuildingContext buildingContext) {
        String mappedBy = columns[0].getMappedBy();
        if (StringHelper.isNotEmpty(mappedBy)) {
            Iterator<Selectable> mappedByColumns;
            Property property = referencedEntity.getRecursiveProperty(mappedBy);
            if (property.getValue() instanceof org.hibernate.mapping.Collection) {
                mappedByColumns = ((org.hibernate.mapping.Collection)property.getValue()).getKey().getColumnIterator();
            } else {
                Iterator joinsIt = referencedEntity.getJoinIterator();
                Value key = null;
                while (joinsIt.hasNext()) {
                    Join join = (Join)joinsIt.next();
                    if (!join.containsProperty(property)) continue;
                    key = join.getKey();
                    break;
                }
                if (key == null) {
                    key = property.getPersistentClass().getIdentifier();
                }
                mappedByColumns = key.getColumnIterator();
            }
            while (mappedByColumns.hasNext()) {
                Column column = (Column)mappedByColumns.next();
                columns[0].linkValueUsingAColumnCopy(column, value);
            }
            String referencedPropertyName = buildingContext.getMetadataCollector().getPropertyReferencedAssociation("inverse__" + referencedEntity.getEntityName(), mappedBy);
            if (referencedPropertyName != null) {
                ((ManyToOne)value).setReferencedPropertyName(referencedPropertyName);
                buildingContext.getMetadataCollector().addUniquePropertyReference(referencedEntity.getEntityName(), referencedPropertyName);
            }
            ((ManyToOne)value).setReferenceToPrimaryKey(referencedPropertyName == null);
            value.createForeignKey();
        } else {
            BinderHelper.createSyntheticPropertyReference(columns, referencedEntity, null, value, true, buildingContext);
            TableBinder.bindFk(referencedEntity, null, columns, value, unique, buildingContext);
        }
    }

    public void setFkJoinColumns(Ejb3JoinColumn[] ejb3JoinColumns) {
        this.fkJoinColumns = ejb3JoinColumns;
    }

    public void setExplicitAssociationTable(boolean explicitAssocTable) {
        this.isExplicitAssociationTable = explicitAssocTable;
    }

    public void setElementColumns(Ejb3Column[] elementColumns) {
        this.elementColumns = elementColumns;
    }

    public void setEmbedded(boolean annotationPresent) {
        this.isEmbedded = annotationPresent;
    }

    public void setProperty(XProperty property) {
        this.property = property;
    }

    public NotFoundAction getNotFoundAction() {
        return this.notFoundAction;
    }

    public void setNotFoundAction(NotFoundAction notFoundAction) {
        this.notFoundAction = notFoundAction;
    }

    public void setIgnoreNotFound(boolean ignoreNotFound) {
        if (ignoreNotFound) {
            this.setNotFoundAction(NotFoundAction.IGNORE);
        } else {
            this.setNotFoundAction(null);
        }
    }

    public void setMapKeyColumns(Ejb3Column[] mapKeyColumns) {
        this.mapKeyColumns = mapKeyColumns;
    }

    public void setMapKeyManyToManyColumns(Ejb3JoinColumn[] mapJoinColumns) {
        this.mapKeyManyToManyColumns = mapJoinColumns;
    }

    public void setLocalGenerators(HashMap<String, IdentifierGeneratorDefinition> localGenerators) {
        this.localGenerators = localGenerators;
    }
}

