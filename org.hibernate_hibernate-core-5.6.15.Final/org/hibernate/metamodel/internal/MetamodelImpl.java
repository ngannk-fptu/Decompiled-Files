/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.NamedAttributeNode
 *  javax.persistence.NamedEntityGraph
 *  javax.persistence.NamedSubgraph
 *  javax.persistence.metamodel.Attribute
 *  javax.persistence.metamodel.EmbeddableType
 *  javax.persistence.metamodel.EntityType
 *  javax.persistence.metamodel.ManagedType
 *  javax.persistence.metamodel.MappedSuperclassType
 */
package org.hibernate.metamodel.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.persistence.EntityGraph;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedSubgraph;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EmbeddableType;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MappedSuperclassType;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.UnknownEntityTypeException;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.registry.classloading.spi.ClassLoadingException;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cache.cfg.internal.DomainDataRegionConfigImpl;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CachedDomainDataAccess;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.graph.AttributeNode;
import org.hibernate.graph.SubGraph;
import org.hibernate.graph.internal.RootGraphImpl;
import org.hibernate.graph.spi.GraphImplementor;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.graph.spi.SubGraphImplementor;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.metamodel.internal.JpaMetaModelPopulationSetting;
import org.hibernate.metamodel.internal.MetadataContext;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.metamodel.model.domain.internal.EntityTypeImpl;
import org.hibernate.metamodel.model.domain.internal.MappedSuperclassTypeImpl;
import org.hibernate.metamodel.model.domain.spi.EmbeddedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.EntityTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.MappedSuperclassTypeDescriptor;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Queryable;
import org.hibernate.persister.spi.PersisterCreationContext;
import org.hibernate.persister.spi.PersisterFactory;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.AssociationType;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

public class MetamodelImpl
implements MetamodelImplementor,
Serializable {
    private static final EntityManagerMessageLogger log = HEMLogging.messageLogger(MetamodelImpl.class);
    private static final Object ENTITY_NAME_RESOLVER_MAP_VALUE = new Object();
    private static final String INVALID_IMPORT = "";
    private static final String[] EMPTY_IMPLEMENTORS = new String[0];
    private final SessionFactoryImplementor sessionFactory;
    private final Map<String, String> knownValidImports = new ConcurrentHashMap<String, String>();
    private final Map<String, String> knownInvalidImports = new ConcurrentHashMap<String, String>();
    private final Map<String, EntityPersister> entityPersisterMap = new ConcurrentHashMap<String, EntityPersister>();
    private final Map<Class, String> entityProxyInterfaceMap = new ConcurrentHashMap<Class, String>();
    private final Map<String, CollectionPersister> collectionPersisterMap = new ConcurrentHashMap<String, CollectionPersister>();
    private final Map<String, Set<String>> collectionRolesByEntityParticipant = new ConcurrentHashMap<String, Set<String>>();
    private final ConcurrentMap<EntityNameResolver, Object> entityNameResolvers = new ConcurrentHashMap<EntityNameResolver, Object>();
    private final Map<Class<?>, EntityTypeDescriptor<?>> jpaEntityTypeMap = new ConcurrentHashMap();
    private final Map<String, EntityTypeDescriptor<?>> jpaEntityTypesByEntityName = new ConcurrentHashMap();
    private final Map<Class<?>, MappedSuperclassType<?>> jpaMappedSuperclassTypeMap = new ConcurrentHashMap();
    private final Set<EmbeddedTypeDescriptor<?>> jpaEmbeddableTypes = new CopyOnWriteArraySet();
    private final Map<Class<?>, EmbeddedTypeDescriptor<?>> jpaEmbeddableTypeMap = new ConcurrentHashMap();
    private final transient Map<String, RootGraphImplementor> entityGraphMap = new ConcurrentHashMap<String, RootGraphImplementor>();
    private final TypeConfiguration typeConfiguration;
    private final Map<String, String[]> implementorsCache = new ConcurrentHashMap<String, String[]>();

    public MetamodelImpl(SessionFactoryImplementor sessionFactory, TypeConfiguration typeConfiguration) {
        this.sessionFactory = sessionFactory;
        this.typeConfiguration = typeConfiguration;
    }

    public void initialize(final MetadataImplementor mappingMetadata, JpaMetaModelPopulationSetting jpaMetaModelPopulationSetting) {
        CachedDomainDataAccess accessStrategy;
        this.knownValidImports.putAll(mappingMetadata.getImports());
        this.primeSecondLevelCacheRegions(mappingMetadata);
        PersisterCreationContext persisterCreationContext = new PersisterCreationContext(){

            @Override
            public SessionFactoryImplementor getSessionFactory() {
                return MetamodelImpl.this.sessionFactory;
            }

            @Override
            public MetadataImplementor getMetadata() {
                return mappingMetadata;
            }
        };
        PersisterFactory persisterFactory = this.sessionFactory.getServiceRegistry().getService(PersisterFactory.class);
        for (PersistentClass persistentClass : mappingMetadata.getEntityBindings()) {
            NavigableRole navigableRole = new NavigableRole(persistentClass.getRootClass().getEntityName());
            accessStrategy = this.sessionFactory.getCache().getEntityRegionAccess(navigableRole);
            NaturalIdDataAccess naturalIdAccessStrategy = this.sessionFactory.getCache().getNaturalIdCacheRegionAccessStrategy(navigableRole);
            EntityPersister cp = persisterFactory.createEntityPersister(persistentClass, (EntityDataAccess)accessStrategy, naturalIdAccessStrategy, persisterCreationContext);
            this.entityPersisterMap.put(persistentClass.getEntityName(), cp);
            if (cp.getConcreteProxyClass() == null || !cp.getConcreteProxyClass().isInterface() || Map.class.isAssignableFrom(cp.getConcreteProxyClass()) || cp.getMappedClass() == cp.getConcreteProxyClass()) continue;
            if (cp.getMappedClass().equals(cp.getConcreteProxyClass())) {
                log.debugf("Entity [%s] mapped same interface [%s] as class and proxy", cp.getEntityName(), cp.getMappedClass());
                continue;
            }
            String old = this.entityProxyInterfaceMap.put(cp.getConcreteProxyClass(), cp.getEntityName());
            if (old == null) continue;
            throw new HibernateException(String.format(Locale.ENGLISH, "Multiple entities [%s, %s] named the same interface [%s] as their proxy which is not supported", old, cp.getEntityName(), cp.getConcreteProxyClass().getName()));
        }
        for (org.hibernate.mapping.Collection collection : mappingMetadata.getCollectionBindings()) {
            Type elementType;
            NavigableRole navigableRole = new NavigableRole(collection.getRole());
            accessStrategy = this.sessionFactory.getCache().getCollectionRegionAccess(navigableRole);
            CollectionPersister persister = persisterFactory.createCollectionPersister(collection, (CollectionDataAccess)accessStrategy, persisterCreationContext);
            this.collectionPersisterMap.put(collection.getRole(), persister);
            Type indexType = persister.getIndexType();
            if (indexType != null && indexType.isAssociationType() && !indexType.isAnyType()) {
                String entityName = ((AssociationType)indexType).getAssociatedEntityName(this.sessionFactory);
                Set<String> roles = this.collectionRolesByEntityParticipant.get(entityName);
                if (roles == null) {
                    roles = new HashSet<String>();
                    this.collectionRolesByEntityParticipant.put(entityName, roles);
                }
                roles.add(persister.getRole());
            }
            if (!(elementType = persister.getElementType()).isAssociationType() || elementType.isAnyType()) continue;
            String entityName = ((AssociationType)elementType).getAssociatedEntityName(this.sessionFactory);
            Set<String> roles = this.collectionRolesByEntityParticipant.get(entityName);
            if (roles == null) {
                roles = new HashSet<String>();
                this.collectionRolesByEntityParticipant.put(entityName, roles);
            }
            roles.add(persister.getRole());
        }
        this.entityPersisterMap.values().forEach(EntityPersister::generateEntityDefinition);
        for (EntityPersister entityPersister : this.entityPersisterMap.values()) {
            entityPersister.postInstantiate();
            MetamodelImpl.registerEntityNameResolvers(entityPersister, this.entityNameResolvers);
        }
        this.collectionPersisterMap.values().forEach(CollectionPersister::postInstantiate);
        if (jpaMetaModelPopulationSetting != JpaMetaModelPopulationSetting.DISABLED) {
            MetadataContext context = new MetadataContext(this.sessionFactory, mappingMetadata.getMappedSuperclassMappingsCopy(), jpaMetaModelPopulationSetting);
            for (PersistentClass persistentClass : mappingMetadata.getEntityBindings()) {
                MetamodelImpl.locateOrBuildEntityType(persistentClass, context);
            }
            MetamodelImpl.handleUnusedMappedSuperclasses(context);
            context.wrapUp();
            this.jpaEntityTypeMap.putAll(context.getEntityTypeMap());
            this.jpaEmbeddableTypes.addAll(context.getEmbeddableTypeSet());
            for (EmbeddedTypeDescriptor<?> embeddedTypeDescriptor : this.jpaEmbeddableTypes) {
                this.jpaEmbeddableTypeMap.put(embeddedTypeDescriptor.getJavaType(), embeddedTypeDescriptor);
            }
            this.jpaMappedSuperclassTypeMap.putAll(context.getMappedSuperclassTypeMap());
            this.jpaEntityTypesByEntityName.putAll(context.getEntityTypesByEntityName());
            this.applyNamedEntityGraphs(mappingMetadata.getNamedEntityGraphs().values());
        }
    }

    private void primeSecondLevelCacheRegions(MetadataImplementor mappingMetadata) {
        Set<DomainDataRegionConfig> regionConfigs;
        AccessType accessType;
        ConcurrentHashMap<String, DomainDataRegionConfigImpl.Builder> regionConfigBuilders = new ConcurrentHashMap<String, DomainDataRegionConfigImpl.Builder>();
        for (PersistentClass bootEntityDescriptor : mappingMetadata.getEntityBindings()) {
            accessType = AccessType.fromExternalName(bootEntityDescriptor.getCacheConcurrencyStrategy());
            if (accessType == null) continue;
            if (bootEntityDescriptor.isCached()) {
                regionConfigBuilders.computeIfAbsent(bootEntityDescriptor.getRootClass().getCacheRegionName(), DomainDataRegionConfigImpl.Builder::new).addEntityConfig(bootEntityDescriptor, accessType);
            }
            if (!RootClass.class.isInstance(bootEntityDescriptor) || !bootEntityDescriptor.hasNaturalId() || bootEntityDescriptor.getNaturalIdCacheRegionName() == null) continue;
            regionConfigBuilders.computeIfAbsent(bootEntityDescriptor.getNaturalIdCacheRegionName(), DomainDataRegionConfigImpl.Builder::new).addNaturalIdConfig((RootClass)bootEntityDescriptor, accessType);
        }
        for (org.hibernate.mapping.Collection collection : mappingMetadata.getCollectionBindings()) {
            accessType = AccessType.fromExternalName(collection.getCacheConcurrencyStrategy());
            if (accessType == null) continue;
            regionConfigBuilders.computeIfAbsent(collection.getCacheRegionName(), DomainDataRegionConfigImpl.Builder::new).addCollectionConfig(collection, accessType);
        }
        if (regionConfigBuilders.isEmpty()) {
            regionConfigs = Collections.emptySet();
        } else {
            regionConfigs = new HashSet();
            for (DomainDataRegionConfigImpl.Builder builder : regionConfigBuilders.values()) {
                regionConfigs.add(builder.build());
            }
        }
        this.getSessionFactory().getCache().prime(regionConfigs);
    }

    private void applyNamedEntityGraphs(Collection<NamedEntityGraphDefinition> namedEntityGraphs) {
        for (NamedEntityGraphDefinition definition : namedEntityGraphs) {
            log.debugf("Applying named entity graph [name=%s, entity-name=%s, jpa-entity-name=%s", definition.getRegisteredName(), definition.getEntityName(), definition.getJpaEntityName());
            EntityType entityType = this.entity(definition.getEntityName());
            if (entityType == null) {
                throw new IllegalArgumentException("Attempted to register named entity graph [" + definition.getRegisteredName() + "] for unknown entity [" + definition.getEntityName() + "]");
            }
            RootGraphImpl entityGraph = new RootGraphImpl(definition.getRegisteredName(), entityType, this.getSessionFactory());
            NamedEntityGraph namedEntityGraph = definition.getAnnotation();
            if (namedEntityGraph.includeAllAttributes()) {
                for (Object attributeObject : entityType.getAttributes()) {
                    entityGraph.addAttributeNodes(new Attribute[]{(Attribute)attributeObject});
                }
            }
            if (namedEntityGraph.attributeNodes() != null) {
                this.applyNamedAttributeNodes(namedEntityGraph.attributeNodes(), namedEntityGraph, entityGraph);
            }
            this.entityGraphMap.put(definition.getRegisteredName(), entityGraph);
        }
    }

    private void applyNamedAttributeNodes(NamedAttributeNode[] namedAttributeNodes, NamedEntityGraph namedEntityGraph, GraphImplementor graphNode) {
        for (NamedAttributeNode namedAttributeNode : namedAttributeNodes) {
            SubGraph subgraph;
            String value = namedAttributeNode.value();
            AttributeNode attributeNode = graphNode.addAttributeNode(value);
            if (StringHelper.isNotEmpty(namedAttributeNode.subgraph())) {
                subgraph = attributeNode.makeSubGraph();
                this.applyNamedSubgraphs(namedEntityGraph, namedAttributeNode.subgraph(), (SubGraphImplementor)subgraph);
            }
            if (!StringHelper.isNotEmpty(namedAttributeNode.keySubgraph())) continue;
            subgraph = attributeNode.makeKeySubGraph();
            this.applyNamedSubgraphs(namedEntityGraph, namedAttributeNode.keySubgraph(), (SubGraphImplementor)subgraph);
        }
    }

    private void applyNamedSubgraphs(NamedEntityGraph namedEntityGraph, String subgraphName, SubGraphImplementor subgraph) {
        for (NamedSubgraph namedSubgraph : namedEntityGraph.subgraphs()) {
            if (!subgraphName.equals(namedSubgraph.name())) continue;
            this.applyNamedAttributeNodes(namedSubgraph.attributeNodes(), namedEntityGraph, subgraph);
        }
    }

    @Override
    public Collection<EntityNameResolver> getEntityNameResolvers() {
        return this.entityNameResolvers.keySet();
    }

    private static void registerEntityNameResolvers(EntityPersister persister, Map<EntityNameResolver, Object> entityNameResolvers) {
        if (persister.getEntityMetamodel() == null || persister.getEntityMetamodel().getTuplizer() == null) {
            return;
        }
        MetamodelImpl.registerEntityNameResolvers(persister.getEntityMetamodel().getTuplizer(), entityNameResolvers);
    }

    private static void registerEntityNameResolvers(EntityTuplizer tuplizer, Map<EntityNameResolver, Object> entityNameResolvers) {
        EntityNameResolver[] resolvers = tuplizer.getEntityNameResolvers();
        if (resolvers == null) {
            return;
        }
        for (EntityNameResolver resolver : resolvers) {
            entityNameResolvers.put(resolver, ENTITY_NAME_RESOLVER_MAP_VALUE);
        }
    }

    private static void handleUnusedMappedSuperclasses(MetadataContext context) {
        Set<MappedSuperclass> unusedMappedSuperclasses = context.getUnusedMappedSuperclasses();
        if (!unusedMappedSuperclasses.isEmpty()) {
            for (MappedSuperclass mappedSuperclass : unusedMappedSuperclasses) {
                log.unusedMappedSuperclass(mappedSuperclass.getMappedClass().getName());
                MetamodelImpl.locateOrBuildMappedSuperclassType(mappedSuperclass, context);
            }
        }
    }

    private static EntityTypeDescriptor<?> locateOrBuildEntityType(PersistentClass persistentClass, MetadataContext context) {
        EntityTypeDescriptor<?> entityType = context.locateEntityType(persistentClass);
        if (entityType == null) {
            entityType = MetamodelImpl.buildEntityType(persistentClass, context);
        }
        return entityType;
    }

    private static EntityTypeImpl<?> buildEntityType(PersistentClass persistentClass, MetadataContext context) {
        MappedSuperclassTypeDescriptor<?> superType;
        Class javaType = persistentClass.getMappedClass();
        context.pushEntityWorkedOn(persistentClass);
        MappedSuperclass superMappedSuperclass = persistentClass.getSuperMappedSuperclass();
        MappedSuperclassTypeDescriptor<?> mappedSuperclassTypeDescriptor = superType = superMappedSuperclass == null ? null : MetamodelImpl.locateOrBuildMappedSuperclassType(superMappedSuperclass, context);
        if (superType == null) {
            PersistentClass superPersistentClass = persistentClass.getSuperclass();
            superType = superPersistentClass == null ? null : MetamodelImpl.locateOrBuildEntityType(superPersistentClass, context);
        }
        EntityTypeImpl entityType = new EntityTypeImpl(javaType, superType, persistentClass, context.getSessionFactory());
        context.registerEntityType(persistentClass, entityType);
        context.popEntityWorkedOn(persistentClass);
        return entityType;
    }

    private static MappedSuperclassTypeDescriptor<?> locateOrBuildMappedSuperclassType(MappedSuperclass mappedSuperclass, MetadataContext context) {
        MappedSuperclassTypeDescriptor<?> mappedSuperclassType = context.locateMappedSuperclassType(mappedSuperclass);
        if (mappedSuperclassType == null) {
            mappedSuperclassType = MetamodelImpl.buildMappedSuperclassType(mappedSuperclass, context);
        }
        return mappedSuperclassType;
    }

    private static MappedSuperclassTypeImpl<?> buildMappedSuperclassType(MappedSuperclass mappedSuperclass, MetadataContext context) {
        MappedSuperclassTypeDescriptor<?> superType;
        MappedSuperclass superMappedSuperclass = mappedSuperclass.getSuperMappedSuperclass();
        MappedSuperclassTypeDescriptor<?> mappedSuperclassTypeDescriptor = superType = superMappedSuperclass == null ? null : MetamodelImpl.locateOrBuildMappedSuperclassType(superMappedSuperclass, context);
        if (superType == null) {
            PersistentClass superPersistentClass = mappedSuperclass.getSuperPersistentClass();
            superType = superPersistentClass == null ? null : MetamodelImpl.locateOrBuildEntityType(superPersistentClass, context);
        }
        Class javaType = mappedSuperclass.getMappedClass();
        MappedSuperclassTypeImpl mappedSuperclassType = new MappedSuperclassTypeImpl(javaType, mappedSuperclass, superType, context.getSessionFactory());
        context.registerMappedSuperclassType(mappedSuperclass, mappedSuperclassType);
        return mappedSuperclassType;
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.typeConfiguration;
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public <X> EntityTypeDescriptor<X> entity(Class<X> cls) {
        EntityType entityType = this.jpaEntityTypeMap.get(cls);
        if (entityType == null) {
            throw new IllegalArgumentException("Not an entity: " + cls);
        }
        return (EntityTypeDescriptor)entityType;
    }

    @Override
    public <X> ManagedTypeDescriptor<X> managedType(Class<X> cls) {
        ManagedType type = this.jpaEntityTypeMap.get(cls);
        if (type == null) {
            type = (ManagedType)this.jpaMappedSuperclassTypeMap.get(cls);
        }
        if (type == null) {
            type = this.jpaEmbeddableTypeMap.get(cls);
        }
        if (type == null) {
            throw new IllegalArgumentException("Not a managed type: " + cls);
        }
        return (ManagedTypeDescriptor)type;
    }

    @Override
    public <X> EmbeddedTypeDescriptor<X> embeddable(Class<X> cls) {
        EmbeddedTypeDescriptor<?> embeddableType = this.jpaEmbeddableTypeMap.get(cls);
        if (embeddableType == null) {
            throw new IllegalArgumentException("Not an embeddable: " + cls);
        }
        return embeddableType;
    }

    public Set<ManagedType<?>> getManagedTypes() {
        int setSize = CollectionHelper.determineProperSizing(this.jpaEntityTypeMap.size() + this.jpaMappedSuperclassTypeMap.size() + this.jpaEmbeddableTypes.size());
        HashSet managedTypes = new HashSet(setSize);
        managedTypes.addAll(this.jpaEntityTypesByEntityName.values());
        managedTypes.addAll(this.jpaMappedSuperclassTypeMap.values());
        managedTypes.addAll(this.jpaEmbeddableTypes);
        return managedTypes;
    }

    public Set<EntityType<?>> getEntities() {
        return new HashSet(this.jpaEntityTypesByEntityName.values());
    }

    public Set<EmbeddableType<?>> getEmbeddables() {
        return new HashSet(this.jpaEmbeddableTypes);
    }

    @Override
    public <X> EntityTypeDescriptor<X> entity(String entityName) {
        return this.jpaEntityTypesByEntityName.get(entityName);
    }

    @Override
    public String getImportedClassName(String className) {
        String result = this.knownValidImports.get(className);
        if (result != null) {
            return result;
        }
        if (this.knownInvalidImports.containsKey(className)) {
            return null;
        }
        try {
            this.sessionFactory.getServiceRegistry().getService(ClassLoaderService.class).classForName(className);
            this.knownValidImports.put(className, className);
            return className;
        }
        catch (ClassLoadingException cnfe) {
            if (this.knownInvalidImports.size() < 1000) {
                this.knownInvalidImports.put(className, INVALID_IMPORT);
            }
            return null;
        }
    }

    @Override
    public String[] getImplementors(String className) throws MappingException {
        String[] implementors = this.implementorsCache.get(className);
        if (implementors != null) {
            return Arrays.copyOf(implementors, implementors.length);
        }
        try {
            Class clazz = this.getSessionFactory().getServiceRegistry().getService(ClassLoaderService.class).classForName(className);
            implementors = this.doGetImplementors(clazz);
            if (implementors.length > 0) {
                this.implementorsCache.putIfAbsent(className, implementors);
                return Arrays.copyOf(implementors, implementors.length);
            }
            return EMPTY_IMPLEMENTORS;
        }
        catch (ClassLoadingException e) {
            return new String[]{className};
        }
    }

    @Override
    public Map<String, EntityPersister> entityPersisters() {
        return this.entityPersisterMap;
    }

    @Override
    public CollectionPersister collectionPersister(String role) {
        CollectionPersister persister = this.collectionPersisterMap.get(role);
        if (persister == null) {
            throw new MappingException("Could not locate CollectionPersister for role : " + role);
        }
        return persister;
    }

    @Override
    public Map<String, CollectionPersister> collectionPersisters() {
        return this.collectionPersisterMap;
    }

    @Override
    public EntityPersister entityPersister(Class entityClass) {
        return this.entityPersister(entityClass.getName());
    }

    @Override
    public EntityPersister entityPersister(String entityName) throws MappingException {
        EntityPersister result = this.entityPersisterMap.get(entityName);
        if (result == null) {
            throw new MappingException("Unknown entity: " + entityName);
        }
        return result;
    }

    @Override
    public EntityPersister locateEntityPersister(Class byClass) {
        String mappedEntityName;
        EntityPersister entityPersister = this.entityPersisterMap.get(byClass.getName());
        if (entityPersister == null && (mappedEntityName = this.entityProxyInterfaceMap.get(byClass)) != null) {
            entityPersister = this.entityPersisterMap.get(mappedEntityName);
        }
        if (entityPersister == null) {
            throw new UnknownEntityTypeException("Unable to locate persister: " + byClass.getName());
        }
        return entityPersister;
    }

    @Override
    public EntityPersister locateEntityPersister(String byName) {
        EntityPersister entityPersister = this.entityPersisterMap.get(byName);
        if (entityPersister == null) {
            throw new UnknownEntityTypeException("Unable to locate persister: " + byName);
        }
        return entityPersister;
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return this.collectionRolesByEntityParticipant.get(entityName);
    }

    @Override
    public String[] getAllEntityNames() {
        return ArrayHelper.toStringArray(this.entityPersisterMap.keySet());
    }

    @Override
    public String[] getAllCollectionRoles() {
        return ArrayHelper.toStringArray(this.collectionPersisterMap.keySet());
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, RootGraphImplementor<T> entityGraph) {
        EntityGraph old = this.entityGraphMap.put(graphName, entityGraph.makeImmutableCopy(graphName));
        if (old != null) {
            log.debugf("EntityGraph being replaced on EntityManagerFactory for name %s", graphName);
        }
    }

    @Override
    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.addNamedEntityGraph(graphName, (RootGraphImplementor)entityGraph);
    }

    @Override
    public <T> RootGraphImplementor<T> findEntityGraphByName(String name) {
        return this.entityGraphMap.get(name);
    }

    @Override
    public <T> List<RootGraphImplementor<? super T>> findEntityGraphsByJavaType(Class<T> entityClass) {
        EntityType entityType = this.entity(entityClass);
        if (entityType == null) {
            throw new IllegalArgumentException("Given class is not an entity : " + entityClass.getName());
        }
        ArrayList<RootGraphImplementor<T>> results = new ArrayList<RootGraphImplementor<T>>();
        for (EntityGraph entityGraph : this.entityGraphMap.values()) {
            RootGraphImplementor egi;
            if (!RootGraphImplementor.class.isInstance(entityGraph) || !(egi = (RootGraphImplementor)entityGraph).appliesTo(entityType)) continue;
            results.add(egi);
        }
        return results;
    }

    @Override
    public void close() {
    }

    private String[] doGetImplementors(Class<?> clazz) throws MappingException {
        ArrayList<String> results = new ArrayList<String>();
        for (EntityPersister checkPersister : this.entityPersisters().values()) {
            boolean assignableSuperclass;
            if (!Queryable.class.isInstance(checkPersister)) continue;
            Queryable checkQueryable = (Queryable)Queryable.class.cast(checkPersister);
            String checkQueryableEntityName = checkQueryable.getEntityName();
            boolean isMappedClass = clazz.getName().equals(checkQueryableEntityName);
            if (checkQueryable.isExplicitPolymorphism()) {
                if (!isMappedClass) continue;
                return new String[]{clazz.getName()};
            }
            if (isMappedClass) {
                results.add(checkQueryableEntityName);
                continue;
            }
            Class mappedClass = checkQueryable.getMappedClass();
            if (mappedClass == null || !clazz.isAssignableFrom(mappedClass)) continue;
            if (checkQueryable.isInherited()) {
                Class mappedSuperclass = this.entityPersister(checkQueryable.getMappedSuperclass()).getMappedClass();
                assignableSuperclass = clazz.isAssignableFrom(mappedSuperclass);
            } else {
                assignableSuperclass = false;
            }
            if (assignableSuperclass) continue;
            results.add(checkQueryableEntityName);
        }
        return results.toArray(new String[results.size()]);
    }
}

