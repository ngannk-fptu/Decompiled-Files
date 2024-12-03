/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderFactory;
import org.hibernate.boot.spi.SessionFactoryBuilderImplementor;
import org.hibernate.boot.spi.SessionFactoryBuilderService;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.MappedSuperclass;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Table;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.spi.TypeConfiguration;

public class MetadataImpl
implements MetadataImplementor,
Serializable {
    private static final Pattern LISTENER_SEPARATION_PATTERN = Pattern.compile(" ,");
    private final UUID uuid;
    private final MetadataBuildingOptions metadataBuildingOptions;
    private final BootstrapContext bootstrapContext;
    private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
    private final Map<String, PersistentClass> entityBindingMap;
    private final Map<Class, MappedSuperclass> mappedSuperclassMap;
    private final Map<String, org.hibernate.mapping.Collection> collectionBindingMap;
    private final Map<String, TypeDefinition> typeDefinitionMap;
    private final Map<String, FilterDefinition> filterDefinitionMap;
    private final Map<String, FetchProfile> fetchProfileMap;
    private final Map<String, String> imports;
    private final Map<String, IdentifierGeneratorDefinition> idGeneratorDefinitionMap;
    private final Map<String, NamedQueryDefinition> namedQueryMap;
    private final Map<String, NamedSQLQueryDefinition> namedNativeQueryMap;
    private final Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
    private final Map<String, ResultSetMappingDefinition> sqlResultSetMappingMap;
    private final Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
    private final Map<String, SQLFunction> sqlFunctionMap;
    private final Database database;

    public MetadataImpl(UUID uuid, MetadataBuildingOptions metadataBuildingOptions, MutableIdentifierGeneratorFactory identifierGeneratorFactory, Map<String, PersistentClass> entityBindingMap, Map<Class, MappedSuperclass> mappedSuperclassMap, Map<String, org.hibernate.mapping.Collection> collectionBindingMap, Map<String, TypeDefinition> typeDefinitionMap, Map<String, FilterDefinition> filterDefinitionMap, Map<String, FetchProfile> fetchProfileMap, Map<String, String> imports, Map<String, IdentifierGeneratorDefinition> idGeneratorDefinitionMap, Map<String, NamedQueryDefinition> namedQueryMap, Map<String, NamedSQLQueryDefinition> namedNativeQueryMap, Map<String, NamedProcedureCallDefinition> namedProcedureCallMap, Map<String, ResultSetMappingDefinition> sqlResultSetMappingMap, Map<String, NamedEntityGraphDefinition> namedEntityGraphMap, Map<String, SQLFunction> sqlFunctionMap, Database database, BootstrapContext bootstrapContext) {
        this.uuid = uuid;
        this.metadataBuildingOptions = metadataBuildingOptions;
        this.identifierGeneratorFactory = identifierGeneratorFactory;
        this.entityBindingMap = entityBindingMap;
        this.mappedSuperclassMap = mappedSuperclassMap;
        this.collectionBindingMap = collectionBindingMap;
        this.typeDefinitionMap = typeDefinitionMap;
        this.filterDefinitionMap = filterDefinitionMap;
        this.fetchProfileMap = fetchProfileMap;
        this.imports = imports;
        this.idGeneratorDefinitionMap = idGeneratorDefinitionMap;
        this.namedQueryMap = namedQueryMap;
        this.namedNativeQueryMap = namedNativeQueryMap;
        this.namedProcedureCallMap = namedProcedureCallMap;
        this.sqlResultSetMappingMap = sqlResultSetMappingMap;
        this.namedEntityGraphMap = namedEntityGraphMap;
        this.sqlFunctionMap = sqlFunctionMap;
        this.database = database;
        this.bootstrapContext = bootstrapContext;
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.metadataBuildingOptions;
    }

    @Override
    public TypeConfiguration getTypeConfiguration() {
        return this.bootstrapContext.getTypeConfiguration();
    }

    @Override
    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.bootstrapContext.getTypeConfiguration().getTypeResolver();
    }

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder() {
        SessionFactoryBuilderService factoryBuilderService = this.metadataBuildingOptions.getServiceRegistry().getService(SessionFactoryBuilderService.class);
        SessionFactoryBuilderImplementor defaultBuilder = factoryBuilderService.createSessionFactoryBuilder(this, this.bootstrapContext);
        ClassLoaderService cls = this.metadataBuildingOptions.getServiceRegistry().getService(ClassLoaderService.class);
        Collection<SessionFactoryBuilderFactory> discoveredBuilderFactories = cls.loadJavaServices(SessionFactoryBuilderFactory.class);
        SessionFactoryBuilder builder = null;
        ArrayList<String> activeFactoryNames = null;
        for (SessionFactoryBuilderFactory discoveredBuilderFactory : discoveredBuilderFactories) {
            SessionFactoryBuilder returnedBuilder = discoveredBuilderFactory.getSessionFactoryBuilder(this, defaultBuilder);
            if (returnedBuilder == null) continue;
            if (activeFactoryNames == null) {
                activeFactoryNames = new ArrayList<String>();
            }
            activeFactoryNames.add(discoveredBuilderFactory.getClass().getName());
            builder = returnedBuilder;
        }
        if (activeFactoryNames != null && activeFactoryNames.size() > 1) {
            throw new HibernateException("Multiple active SessionFactoryBuilderFactory definitions were discovered : " + String.join((CharSequence)", ", (Iterable<? extends CharSequence>)activeFactoryNames));
        }
        if (builder != null) {
            return builder;
        }
        return defaultBuilder;
    }

    @Override
    public SessionFactory buildSessionFactory() {
        return this.getSessionFactoryBuilder().build();
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public Database getDatabase() {
        return this.database;
    }

    @Override
    public MutableIdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return this.identifierGeneratorFactory;
    }

    @Override
    public Collection<PersistentClass> getEntityBindings() {
        return this.entityBindingMap.values();
    }

    @Override
    public PersistentClass getEntityBinding(String entityName) {
        return this.entityBindingMap.get(entityName);
    }

    @Override
    public Collection<org.hibernate.mapping.Collection> getCollectionBindings() {
        return this.collectionBindingMap.values();
    }

    @Override
    public org.hibernate.mapping.Collection getCollectionBinding(String role) {
        return this.collectionBindingMap.get(role);
    }

    @Override
    public Map<String, String> getImports() {
        return this.imports;
    }

    @Override
    public NamedQueryDefinition getNamedQueryDefinition(String name) {
        return this.namedQueryMap.get(name);
    }

    @Override
    public Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
        return this.namedQueryMap.values();
    }

    @Override
    public NamedSQLQueryDefinition getNamedNativeQueryDefinition(String name) {
        return this.namedNativeQueryMap.get(name);
    }

    @Override
    public Collection<NamedSQLQueryDefinition> getNamedNativeQueryDefinitions() {
        return this.namedNativeQueryMap.values();
    }

    @Override
    public Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
        return this.namedProcedureCallMap.values();
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.sqlResultSetMappingMap.get(name);
    }

    @Override
    public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
        return this.sqlResultSetMappingMap;
    }

    @Override
    public TypeDefinition getTypeDefinition(String typeName) {
        return this.typeDefinitionMap.get(typeName);
    }

    @Override
    public Map<String, FilterDefinition> getFilterDefinitions() {
        return this.filterDefinitionMap;
    }

    @Override
    public FilterDefinition getFilterDefinition(String name) {
        return this.filterDefinitionMap.get(name);
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return this.fetchProfileMap.get(name);
    }

    @Override
    public Collection<FetchProfile> getFetchProfiles() {
        return this.fetchProfileMap.values();
    }

    @Override
    public NamedEntityGraphDefinition getNamedEntityGraph(String name) {
        return this.namedEntityGraphMap.get(name);
    }

    @Override
    public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphs() {
        return this.namedEntityGraphMap;
    }

    @Override
    public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
        return this.idGeneratorDefinitionMap.get(name);
    }

    @Override
    public Map<String, SQLFunction> getSqlFunctionMap() {
        return this.sqlFunctionMap;
    }

    @Override
    public Collection<Table> collectTableMappings() {
        ArrayList<Table> tables = new ArrayList<Table>();
        for (Namespace namespace : this.database.getNamespaces()) {
            tables.addAll(namespace.getTables());
        }
        return tables;
    }

    @Override
    public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
        return new NamedQueryRepository(this.namedQueryMap, this.namedNativeQueryMap, this.sqlResultSetMappingMap, this.buildProcedureCallMementos(sessionFactory));
    }

    private Map<String, ProcedureCallMemento> buildProcedureCallMementos(SessionFactoryImpl sessionFactory) {
        HashMap<String, ProcedureCallMemento> rtn = new HashMap<String, ProcedureCallMemento>();
        if (this.namedProcedureCallMap != null) {
            for (NamedProcedureCallDefinition procedureCallDefinition : this.namedProcedureCallMap.values()) {
                rtn.put(procedureCallDefinition.getRegisteredName(), procedureCallDefinition.toMemento(sessionFactory, this.sqlResultSetMappingMap));
            }
        }
        return rtn;
    }

    @Override
    public void validate() throws MappingException {
        for (PersistentClass entityBinding : this.getEntityBindings()) {
            entityBinding.validate(this);
        }
        for (org.hibernate.mapping.Collection collectionBinding : this.getCollectionBindings()) {
            collectionBinding.validate(this);
        }
    }

    @Override
    public Set<MappedSuperclass> getMappedSuperclassMappingsCopy() {
        return this.mappedSuperclassMap == null ? Collections.emptySet() : new HashSet<MappedSuperclass>(this.mappedSuperclassMap.values());
    }

    @Override
    public void initSessionFactory(SessionFactoryImplementor sessionFactory) {
        ServiceRegistryImplementor sessionFactoryServiceRegistry = sessionFactory.getServiceRegistry();
        assert (sessionFactoryServiceRegistry != null);
        EventListenerRegistry eventListenerRegistry = sessionFactoryServiceRegistry.getService(EventListenerRegistry.class);
        ConfigurationService cfgService = sessionFactoryServiceRegistry.getService(ConfigurationService.class);
        ClassLoaderService classLoaderService = sessionFactoryServiceRegistry.getService(ClassLoaderService.class);
        for (Map.Entry entry : cfgService.getSettings().entrySet()) {
            if (!String.class.isInstance(entry.getKey())) continue;
            String propertyName = (String)entry.getKey();
            String listenerPrefix = (String)NullnessHelper.coalesceSuppliedValues(() -> propertyName.startsWith("hibernate.event.listener") ? "hibernate.event.listener" : null, () -> {
                if (propertyName.startsWith("hibernate.ejb.event")) {
                    DeprecationLogger.DEPRECATION_LOGGER.deprecatedSetting("hibernate.ejb.event", "hibernate.event.listener");
                    return "hibernate.ejb.event";
                }
                return null;
            }, () -> null);
            if (listenerPrefix == null) continue;
            String eventTypeName = propertyName.substring(listenerPrefix.length() + 1);
            EventType eventType = EventType.resolveEventTypeByName(eventTypeName);
            EventListenerGroup<Object> eventListenerGroup = eventListenerRegistry.getEventListenerGroup(eventType);
            for (String listenerImpl : LISTENER_SEPARATION_PATTERN.split((String)entry.getValue())) {
                eventListenerGroup.appendListener(this.instantiate(listenerImpl, classLoaderService));
            }
        }
    }

    private Object instantiate(String listenerImpl, ClassLoaderService classLoaderService) {
        try {
            return classLoaderService.classForName(listenerImpl).newInstance();
        }
        catch (Exception e) {
            throw new HibernateException("Could not instantiate requested listener [" + listenerImpl + "]", e);
        }
    }

    @Override
    public Type getIdentifierType(String entityName) throws MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new MappingException("persistent class not known: " + entityName);
        }
        return pc.getIdentifier().getType();
    }

    @Override
    public String getIdentifierPropertyName(String entityName) throws MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new MappingException("persistent class not known: " + entityName);
        }
        if (!pc.hasIdentifierProperty()) {
            return null;
        }
        return pc.getIdentifierProperty().getName();
    }

    @Override
    public Type getReferencedPropertyType(String entityName, String propertyName) throws MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new MappingException("persistent class not known: " + entityName);
        }
        Property prop = pc.getReferencedProperty(propertyName);
        if (prop == null) {
            throw new MappingException("property not known: " + entityName + '.' + propertyName);
        }
        return prop.getType();
    }

    public Map<String, PersistentClass> getEntityBindingMap() {
        return this.entityBindingMap;
    }

    public Map<String, org.hibernate.mapping.Collection> getCollectionBindingMap() {
        return this.collectionBindingMap;
    }

    public Map<String, TypeDefinition> getTypeDefinitionMap() {
        return this.typeDefinitionMap;
    }

    public Map<String, FetchProfile> getFetchProfileMap() {
        return this.fetchProfileMap;
    }

    public Map<Class, MappedSuperclass> getMappedSuperclassMap() {
        return this.mappedSuperclassMap;
    }

    public Map<String, IdentifierGeneratorDefinition> getIdGeneratorDefinitionMap() {
        return this.idGeneratorDefinitionMap;
    }

    public Map<String, NamedQueryDefinition> getNamedQueryMap() {
        return this.namedQueryMap;
    }

    public Map<String, NamedSQLQueryDefinition> getNamedNativeQueryMap() {
        return this.namedNativeQueryMap;
    }

    public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
        return this.namedProcedureCallMap;
    }

    public Map<String, ResultSetMappingDefinition> getSqlResultSetMappingMap() {
        return this.sqlResultSetMappingMap;
    }

    public Map<String, NamedEntityGraphDefinition> getNamedEntityGraphMap() {
        return this.namedEntityGraphMap;
    }

    public BootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
    }
}

