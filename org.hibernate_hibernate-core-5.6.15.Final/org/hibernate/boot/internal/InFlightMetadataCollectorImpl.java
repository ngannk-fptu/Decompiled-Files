/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Embeddable
 *  javax.persistence.Entity
 *  javax.persistence.MappedSuperclass
 *  javax.persistence.MapsId
 *  org.hibernate.annotations.common.reflection.XClass
 */
package org.hibernate.boot.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.AttributeConverter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.MapsId;
import org.hibernate.AnnotationException;
import org.hibernate.AssertionFailure;
import org.hibernate.DuplicateMappingException;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.boot.CacheRegionDefinition;
import org.hibernate.boot.MappingException;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.internal.ClassmateContext;
import org.hibernate.boot.internal.MetadataImpl;
import org.hibernate.boot.model.IdentifierGeneratorDefinition;
import org.hibernate.boot.model.TypeDefinition;
import org.hibernate.boot.model.convert.internal.AttributeConverterManager;
import org.hibernate.boot.model.convert.internal.ClassBasedConverterDescriptor;
import org.hibernate.boot.model.convert.spi.ConverterAutoApplyHandler;
import org.hibernate.boot.model.convert.spi.ConverterDescriptor;
import org.hibernate.boot.model.naming.Identifier;
import org.hibernate.boot.model.naming.ImplicitForeignKeyNameSource;
import org.hibernate.boot.model.naming.ImplicitIndexNameSource;
import org.hibernate.boot.model.naming.ImplicitUniqueKeyNameSource;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.model.relational.Database;
import org.hibernate.boot.model.relational.Namespace;
import org.hibernate.boot.model.relational.QualifiedTableName;
import org.hibernate.boot.model.source.internal.ImplicitColumnNamingSecondPass;
import org.hibernate.boot.model.source.spi.LocalMetadataBuildingContext;
import org.hibernate.boot.spi.BootstrapContext;
import org.hibernate.boot.spi.InFlightMetadataCollector;
import org.hibernate.boot.spi.MetadataBuildingContext;
import org.hibernate.boot.spi.MetadataBuildingOptions;
import org.hibernate.boot.spi.NaturalIdUniqueKeyBinder;
import org.hibernate.cfg.AnnotatedClassType;
import org.hibernate.cfg.CreateKeySecondPass;
import org.hibernate.cfg.FkSecondPass;
import org.hibernate.cfg.IdGeneratorResolverSecondPass;
import org.hibernate.cfg.JPAIndexHolder;
import org.hibernate.cfg.PropertyData;
import org.hibernate.cfg.QuerySecondPass;
import org.hibernate.cfg.RecoverableException;
import org.hibernate.cfg.SecondPass;
import org.hibernate.cfg.SecondaryTableSecondPass;
import org.hibernate.cfg.SetSimpleValueTypeSecondPass;
import org.hibernate.cfg.UniqueConstraintHolder;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.id.factory.spi.MutableIdentifierGeneratorFactory;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.CollectionHelper;
import org.hibernate.mapping.Column;
import org.hibernate.mapping.DenormalizedTable;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.ForeignKey;
import org.hibernate.mapping.IdentifierCollection;
import org.hibernate.mapping.Index;
import org.hibernate.mapping.Join;
import org.hibernate.mapping.KeyValue;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.UniqueKey;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.hibernate.type.spi.TypeConfiguration;

public class InFlightMetadataCollectorImpl
implements InFlightMetadataCollector {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(InFlightMetadataCollectorImpl.class);
    private final BootstrapContext bootstrapContext;
    private final MetadataBuildingOptions options;
    private final AttributeConverterManager attributeConverterManager = new AttributeConverterManager();
    private final UUID uuid;
    private final MutableIdentifierGeneratorFactory identifierGeneratorFactory;
    private final Map<String, PersistentClass> entityBindingMap = new HashMap<String, PersistentClass>();
    private final Map<String, org.hibernate.mapping.Collection> collectionBindingMap = new HashMap<String, org.hibernate.mapping.Collection>();
    private final Map<String, TypeDefinition> typeDefinitionMap = new HashMap<String, TypeDefinition>();
    private final Map<String, FilterDefinition> filterDefinitionMap = new HashMap<String, FilterDefinition>();
    private final Map<String, String> imports = new HashMap<String, String>();
    private Database database;
    private final Map<String, NamedQueryDefinition> namedQueryMap = new HashMap<String, NamedQueryDefinition>();
    private final Map<String, NamedSQLQueryDefinition> namedNativeQueryMap = new HashMap<String, NamedSQLQueryDefinition>();
    private final Map<String, NamedProcedureCallDefinition> namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>();
    private final Map<String, ResultSetMappingDefinition> sqlResultSetMappingMap = new HashMap<String, ResultSetMappingDefinition>();
    private final Map<String, NamedEntityGraphDefinition> namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
    private final Map<String, FetchProfile> fetchProfileMap = new HashMap<String, FetchProfile>();
    private final Map<String, IdentifierGeneratorDefinition> idGeneratorDefinitionMap = new HashMap<String, IdentifierGeneratorDefinition>();
    private Map<String, SQLFunction> sqlFunctionMap;
    private final Set<String> defaultIdentifierGeneratorNames = new HashSet<String>();
    private final Set<String> defaultNamedQueryNames = new HashSet<String>();
    private final Set<String> defaultNamedNativeQueryNames = new HashSet<String>();
    private final Set<String> defaultSqlResultSetMappingNames = new HashSet<String>();
    private final Set<String> defaultNamedProcedureNames = new HashSet<String>();
    private Map<String, AnyMetaDef> anyMetaDefs;
    private Map<Class, org.hibernate.mapping.MappedSuperclass> mappedSuperClasses;
    private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithMapsId;
    private Map<XClass, Map<String, PropertyData>> propertiesAnnotatedWithIdAndToOne;
    private Map<String, String> mappedByResolver;
    private Map<String, String> propertyRefResolver;
    private Set<InFlightMetadataCollector.DelayedPropertyReferenceHandler> delayedPropertyReferenceHandlers;
    private Map<Table, List<UniqueConstraintHolder>> uniqueConstraintHoldersByTable;
    private Map<Table, List<JPAIndexHolder>> jpaIndexHoldersByTable;
    private Map<Identifier, Identifier> logicalToPhysicalTableNameMap = new HashMap<Identifier, Identifier>();
    private Map<Identifier, Identifier> physicalToLogicalTableNameMap = new HashMap<Identifier, Identifier>();
    private Map<Table, TableColumnNameBinding> columnNameBindingByTableMap;
    private final Map<String, AnnotatedClassType> annotatedClassTypeMap = new HashMap<String, AnnotatedClassType>();
    private final Map<String, EntityTableXrefImpl> entityTableXrefMap = new HashMap<String, EntityTableXrefImpl>();
    private ArrayList<IdGeneratorResolverSecondPass> idGeneratorResolverSecondPassList;
    private ArrayList<SetSimpleValueTypeSecondPass> setSimpleValueTypeSecondPassList;
    private ArrayList<FkSecondPass> fkSecondPassList;
    private ArrayList<CreateKeySecondPass> createKeySecondPasList;
    private ArrayList<SecondaryTableSecondPass> secondaryTableSecondPassList;
    private ArrayList<QuerySecondPass> querySecondPassList;
    private ArrayList<ImplicitColumnNamingSecondPass> implicitColumnNamingSecondPassList;
    private ArrayList<SecondPass> generalSecondPassList;
    private boolean inSecondPass = false;
    private Map<String, NaturalIdUniqueKeyBinder> naturalIdUniqueKeyBinderMap;

    public InFlightMetadataCollectorImpl(BootstrapContext bootstrapContext, MetadataBuildingOptions options) {
        this.bootstrapContext = bootstrapContext;
        this.uuid = UUID.randomUUID();
        this.options = options;
        this.identifierGeneratorFactory = options.getServiceRegistry().getService(MutableIdentifierGeneratorFactory.class);
        for (Map.Entry<String, SQLFunction> sqlFunctionEntry : bootstrapContext.getSqlFunctions().entrySet()) {
            if (this.sqlFunctionMap == null) {
                this.sqlFunctionMap = new ConcurrentHashMap<String, SQLFunction>(16, 0.75f, 1);
            }
            this.sqlFunctionMap.put(sqlFunctionEntry.getKey(), sqlFunctionEntry.getValue());
        }
        bootstrapContext.getAuxiliaryDatabaseObjectList().forEach(this.getDatabase()::addAuxiliaryDatabaseObject);
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public MetadataBuildingOptions getMetadataBuildingOptions() {
        return this.options;
    }

    @Override
    public BootstrapContext getBootstrapContext() {
        return this.bootstrapContext;
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
    public Database getDatabase() {
        if (this.database == null) {
            this.database = new Database(this.options);
        }
        return this.database;
    }

    @Override
    public NamedQueryRepository buildNamedQueryRepository(SessionFactoryImpl sessionFactory) {
        throw new UnsupportedOperationException("#buildNamedQueryRepository should not be called on InFlightMetadataCollector");
    }

    @Override
    public Map<String, SQLFunction> getSqlFunctionMap() {
        return this.sqlFunctionMap;
    }

    @Override
    public void validate() throws org.hibernate.MappingException {
    }

    @Override
    public Set<org.hibernate.mapping.MappedSuperclass> getMappedSuperclassMappingsCopy() {
        return new HashSet<org.hibernate.mapping.MappedSuperclass>(this.mappedSuperClasses.values());
    }

    @Override
    public void initSessionFactory(SessionFactoryImplementor sessionFactory) {
        throw new UnsupportedOperationException("You should not be building a SessionFactory from an in-flight metadata collector; and of course we should better segment this in the API :)");
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return this.identifierGeneratorFactory;
    }

    @Override
    public SessionFactoryBuilder getSessionFactoryBuilder() {
        throw new UnsupportedOperationException("You should not be building a SessionFactory from an in-flight metadata collector; and of course we should better segment this in the API :)");
    }

    @Override
    public SessionFactory buildSessionFactory() {
        throw new UnsupportedOperationException("You should not be building a SessionFactory from an in-flight metadata collector; and of course we should better segment this in the API :)");
    }

    @Override
    public Collection<PersistentClass> getEntityBindings() {
        return this.entityBindingMap.values();
    }

    @Override
    public Map<String, PersistentClass> getEntityBindingMap() {
        return this.entityBindingMap;
    }

    @Override
    public PersistentClass getEntityBinding(String entityName) {
        return this.entityBindingMap.get(entityName);
    }

    @Override
    public void addEntityBinding(PersistentClass persistentClass) throws DuplicateMappingException {
        String entityName = persistentClass.getEntityName();
        String jpaEntityName = persistentClass.getJpaEntityName();
        if (this.entityBindingMap.containsKey(entityName)) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.ENTITY, entityName);
        }
        PersistentClass matchingPersistentClass = this.entityBindingMap.values().stream().filter(existingPersistentClass -> existingPersistentClass.getJpaEntityName().equals(jpaEntityName)).findFirst().orElse(null);
        if (matchingPersistentClass != null) {
            throw new DuplicateMappingException(String.format("The [%s] and [%s] entities share the same JPA entity name: [%s] which is not allowed!", matchingPersistentClass.getClassName(), persistentClass.getClassName(), jpaEntityName), DuplicateMappingException.Type.ENTITY, jpaEntityName);
        }
        this.entityBindingMap.put(entityName, persistentClass);
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
    public void addCollectionBinding(org.hibernate.mapping.Collection collection) throws DuplicateMappingException {
        String collectionRole = collection.getRole();
        if (this.collectionBindingMap.containsKey(collectionRole)) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.COLLECTION, collectionRole);
        }
        this.collectionBindingMap.put(collectionRole, collection);
    }

    @Override
    public TypeDefinition getTypeDefinition(String registrationKey) {
        return this.typeDefinitionMap.get(registrationKey);
    }

    @Override
    public void addTypeDefinition(TypeDefinition typeDefinition) {
        if (typeDefinition == null) {
            throw new IllegalArgumentException("Type definition is null");
        }
        if (!StringHelper.isEmpty(typeDefinition.getName())) {
            this.addTypeDefinition(typeDefinition.getName(), typeDefinition);
        }
        if (typeDefinition.getRegistrationKeys() != null) {
            for (String registrationKey : typeDefinition.getRegistrationKeys()) {
                this.addTypeDefinition(registrationKey, typeDefinition);
            }
        }
    }

    private void addTypeDefinition(String registrationKey, TypeDefinition typeDefinition) {
        TypeDefinition previous = this.typeDefinitionMap.put(registrationKey, typeDefinition);
        if (previous != null) {
            log.debugf("Duplicate typedef name [%s] now -> %s", registrationKey, typeDefinition.getTypeImplementorClass().getName());
        }
    }

    @Override
    public ClassmateContext getClassmateContext() {
        return this.bootstrapContext.getClassmateContext();
    }

    @Override
    public void addAttributeConverter(Class<? extends AttributeConverter> converterClass) {
        this.attributeConverterManager.addConverter(new ClassBasedConverterDescriptor(converterClass, this.getBootstrapContext().getClassmateContext()));
    }

    @Override
    public void addAttributeConverter(ConverterDescriptor descriptor) {
        this.attributeConverterManager.addConverter(descriptor);
    }

    @Override
    public ConverterAutoApplyHandler getAttributeConverterAutoApplyHandler() {
        return this.attributeConverterManager;
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
    public void addFilterDefinition(FilterDefinition filterDefinition) {
        if (filterDefinition == null || filterDefinition.getFilterName() == null) {
            throw new IllegalArgumentException("Filter definition object or name is null: " + filterDefinition);
        }
        this.filterDefinitionMap.put(filterDefinition.getFilterName(), filterDefinition);
    }

    @Override
    public Collection<FetchProfile> getFetchProfiles() {
        return this.fetchProfileMap.values();
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return this.fetchProfileMap.get(name);
    }

    @Override
    public void addFetchProfile(FetchProfile profile) {
        if (profile == null || profile.getName() == null) {
            throw new IllegalArgumentException("Fetch profile object or name is null: " + profile);
        }
        FetchProfile old = this.fetchProfileMap.put(profile.getName(), profile);
        if (old != null) {
            log.warn("Duplicated fetch profile with same name [" + profile.getName() + "] found.");
        }
    }

    @Override
    public IdentifierGeneratorDefinition getIdentifierGenerator(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null is not a valid generator name");
        }
        return this.idGeneratorDefinitionMap.get(name);
    }

    @Override
    public Collection<Table> collectTableMappings() {
        ArrayList<Table> tables = new ArrayList<Table>();
        for (Namespace namespace : this.getDatabase().getNamespaces()) {
            tables.addAll(namespace.getTables());
        }
        return tables;
    }

    @Override
    public void addIdentifierGenerator(IdentifierGeneratorDefinition generator) {
        if (generator == null || generator.getName() == null) {
            throw new IllegalArgumentException("ID generator object or name is null.");
        }
        if (this.defaultIdentifierGeneratorNames.contains(generator.getName())) {
            return;
        }
        IdentifierGeneratorDefinition old = this.idGeneratorDefinitionMap.put(generator.getName(), generator);
        if (old != null && !old.equals(generator)) {
            if (this.bootstrapContext.getJpaCompliance().isGlobalGeneratorScopeEnabled()) {
                throw new IllegalArgumentException("Duplicate generator name " + old.getName() + " you will likely want to set the property " + "hibernate.jpa.compliance.global_id_generators" + " to false ");
            }
            log.duplicateGeneratorName(old.getName());
        }
    }

    @Override
    public void addDefaultIdentifierGenerator(IdentifierGeneratorDefinition generator) {
        this.addIdentifierGenerator(generator);
        this.defaultIdentifierGeneratorNames.add(generator.getName());
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
    public void addNamedEntityGraph(NamedEntityGraphDefinition definition) {
        String name = definition.getRegisteredName();
        NamedEntityGraphDefinition previous = this.namedEntityGraphMap.put(name, definition);
        if (previous != null) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.NAMED_ENTITY_GRAPH, name);
        }
    }

    @Override
    public NamedQueryDefinition getNamedQueryDefinition(String name) {
        if (name == null) {
            throw new IllegalArgumentException("null is not a valid query name");
        }
        return this.namedQueryMap.get(name);
    }

    @Override
    public Collection<NamedQueryDefinition> getNamedQueryDefinitions() {
        return this.namedQueryMap.values();
    }

    @Override
    public void addNamedQuery(NamedQueryDefinition def) {
        if (def == null) {
            throw new IllegalArgumentException("Named query definition is null");
        }
        if (def.getName() == null) {
            throw new IllegalArgumentException("Named query definition name is null: " + def.getQueryString());
        }
        if (this.defaultNamedQueryNames.contains(def.getName())) {
            return;
        }
        this.applyNamedQuery(def.getName(), def);
    }

    private void applyNamedQuery(String name, NamedQueryDefinition query) {
        this.checkQueryName(name);
        this.namedQueryMap.put(name.intern(), query);
    }

    private void checkQueryName(String name) throws DuplicateMappingException {
        if (this.namedQueryMap.containsKey(name) || this.namedNativeQueryMap.containsKey(name)) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.QUERY, name);
        }
    }

    @Override
    public void addDefaultQuery(NamedQueryDefinition queryDefinition) {
        this.applyNamedQuery(queryDefinition.getName(), queryDefinition);
        this.defaultNamedQueryNames.add(queryDefinition.getName());
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
    public void addNamedNativeQuery(NamedSQLQueryDefinition def) {
        if (def == null) {
            throw new IllegalArgumentException("Named native query definition object is null");
        }
        if (def.getName() == null) {
            throw new IllegalArgumentException("Named native query definition name is null: " + def.getQueryString());
        }
        if (this.defaultNamedNativeQueryNames.contains(def.getName())) {
            return;
        }
        this.applyNamedNativeQuery(def.getName(), def);
    }

    private void applyNamedNativeQuery(String name, NamedSQLQueryDefinition query) {
        this.checkQueryName(name);
        this.namedNativeQueryMap.put(name.intern(), query);
    }

    @Override
    public void addDefaultNamedNativeQuery(NamedSQLQueryDefinition query) {
        this.applyNamedNativeQuery(query.getName(), query);
        this.defaultNamedNativeQueryNames.add(query.getName());
    }

    @Override
    public Collection<NamedProcedureCallDefinition> getNamedProcedureCallDefinitions() {
        return this.namedProcedureCallMap.values();
    }

    @Override
    public void addNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
        if (definition == null) {
            throw new IllegalArgumentException("Named query definition is null");
        }
        String name = definition.getRegisteredName();
        if (this.defaultNamedProcedureNames.contains(name)) {
            return;
        }
        NamedProcedureCallDefinition previous = this.namedProcedureCallMap.put(name, definition);
        if (previous != null) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.PROCEDURE, name);
        }
    }

    @Override
    public void addDefaultNamedProcedureCallDefinition(NamedProcedureCallDefinition definition) {
        this.addNamedProcedureCallDefinition(definition);
        this.defaultNamedProcedureNames.add(definition.getRegisteredName());
    }

    @Override
    public Map<String, ResultSetMappingDefinition> getResultSetMappingDefinitions() {
        return this.sqlResultSetMappingMap;
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.sqlResultSetMappingMap.get(name);
    }

    @Override
    public void addResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
        if (resultSetMappingDefinition == null) {
            throw new IllegalArgumentException("Result-set mapping was null");
        }
        String name = resultSetMappingDefinition.getName();
        if (name == null) {
            throw new IllegalArgumentException("Result-set mapping name is null: " + resultSetMappingDefinition);
        }
        if (this.defaultSqlResultSetMappingNames.contains(name)) {
            return;
        }
        this.applyResultSetMapping(resultSetMappingDefinition);
    }

    public void applyResultSetMapping(ResultSetMappingDefinition resultSetMappingDefinition) {
        ResultSetMappingDefinition old = this.sqlResultSetMappingMap.put(resultSetMappingDefinition.getName(), resultSetMappingDefinition);
        if (old != null) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.RESULT_SET_MAPPING, resultSetMappingDefinition.getName());
        }
    }

    @Override
    public void addDefaultResultSetMapping(ResultSetMappingDefinition definition) {
        String name = definition.getName();
        if (!this.defaultSqlResultSetMappingNames.contains(name) && this.sqlResultSetMappingMap.containsKey(name)) {
            this.sqlResultSetMappingMap.remove(name);
        }
        this.applyResultSetMapping(definition);
        this.defaultSqlResultSetMappingNames.add(name);
    }

    @Override
    public Map<String, String> getImports() {
        return this.imports;
    }

    @Override
    public void addImport(String importName, String entityName) {
        if (importName == null || entityName == null) {
            throw new IllegalArgumentException("Import name or entity name is null");
        }
        log.tracev("Import: {0} -> {1}", importName, entityName);
        String old = this.imports.put(importName, entityName);
        if (old != null) {
            log.debug("import name [" + importName + "] overrode previous [{" + old + "}]");
        }
    }

    @Override
    public Table addTable(String schemaName, String catalogName, String name, String subselectFragment, boolean isAbstract) {
        Namespace namespace = this.getDatabase().locateNamespace(this.getDatabase().toIdentifier(catalogName), this.getDatabase().toIdentifier(schemaName));
        Identifier logicalName = name != null ? this.getDatabase().toIdentifier(name) : null;
        if (subselectFragment != null) {
            return new Table(namespace, logicalName, subselectFragment, isAbstract);
        }
        Table table = namespace.locateTable(logicalName);
        if (table != null) {
            if (!isAbstract) {
                table.setAbstract(false);
            }
            return table;
        }
        return namespace.createTable(logicalName, isAbstract);
    }

    @Override
    public Table addDenormalizedTable(String schemaName, String catalogName, String name, boolean isAbstract, String subselectFragment, Table includedTable) throws DuplicateMappingException {
        Namespace namespace = this.getDatabase().locateNamespace(this.getDatabase().toIdentifier(catalogName), this.getDatabase().toIdentifier(schemaName));
        Identifier logicalName = name != null ? this.getDatabase().toIdentifier(name) : null;
        if (subselectFragment != null) {
            return new DenormalizedTable(namespace, logicalName, subselectFragment, isAbstract, includedTable);
        }
        Table table = namespace.locateTable(logicalName);
        if (table != null) {
            throw new DuplicateMappingException(DuplicateMappingException.Type.TABLE, logicalName.toString());
        }
        table = namespace.createDenormalizedTable(logicalName, isAbstract, includedTable);
        return table;
    }

    @Override
    public Type getIdentifierType(String entityName) throws org.hibernate.MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new org.hibernate.MappingException("persistent class not known: " + entityName);
        }
        return pc.getIdentifier().getType();
    }

    @Override
    public String getIdentifierPropertyName(String entityName) throws org.hibernate.MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new org.hibernate.MappingException("persistent class not known: " + entityName);
        }
        if (!pc.hasIdentifierProperty()) {
            return null;
        }
        return pc.getIdentifierProperty().getName();
    }

    @Override
    public Type getReferencedPropertyType(String entityName, String propertyName) throws org.hibernate.MappingException {
        PersistentClass pc = this.entityBindingMap.get(entityName);
        if (pc == null) {
            throw new org.hibernate.MappingException("persistent class not known: " + entityName);
        }
        Property prop = pc.getReferencedProperty(propertyName);
        if (prop == null) {
            throw new org.hibernate.MappingException("property not known: " + entityName + '.' + propertyName);
        }
        return prop.getType();
    }

    @Override
    public void addTableNameBinding(Identifier logicalName, Table table) {
        this.logicalToPhysicalTableNameMap.put(logicalName, table.getNameIdentifier());
        this.physicalToLogicalTableNameMap.put(table.getNameIdentifier(), logicalName);
    }

    @Override
    public void addTableNameBinding(String schema, String catalog, String logicalName, String realTableName, Table denormalizedSuperTable) {
        Identifier logicalNameIdentifier = this.getDatabase().toIdentifier(logicalName);
        Identifier physicalNameIdentifier = this.getDatabase().toIdentifier(realTableName);
        this.logicalToPhysicalTableNameMap.put(logicalNameIdentifier, physicalNameIdentifier);
        this.physicalToLogicalTableNameMap.put(physicalNameIdentifier, logicalNameIdentifier);
    }

    @Override
    public String getLogicalTableName(Table ownerTable) {
        Identifier logicalName = this.physicalToLogicalTableNameMap.get(ownerTable.getNameIdentifier());
        if (logicalName == null) {
            throw new org.hibernate.MappingException("Unable to find physical table: " + ownerTable.getName());
        }
        return logicalName.render();
    }

    @Override
    public String getPhysicalTableName(Identifier logicalName) {
        Identifier physicalName = this.logicalToPhysicalTableNameMap.get(logicalName);
        return physicalName == null ? null : physicalName.render();
    }

    @Override
    public String getPhysicalTableName(String logicalName) {
        return this.getPhysicalTableName(this.getDatabase().toIdentifier(logicalName));
    }

    @Override
    public void addColumnNameBinding(Table table, String logicalName, Column column) throws DuplicateMappingException {
        this.addColumnNameBinding(table, this.getDatabase().toIdentifier(logicalName), column);
    }

    @Override
    public void addColumnNameBinding(Table table, Identifier logicalName, Column column) throws DuplicateMappingException {
        TableColumnNameBinding binding = null;
        if (this.columnNameBindingByTableMap == null) {
            this.columnNameBindingByTableMap = new HashMap<Table, TableColumnNameBinding>();
        } else {
            binding = this.columnNameBindingByTableMap.get(table);
        }
        if (binding == null) {
            binding = new TableColumnNameBinding(table.getName());
            this.columnNameBindingByTableMap.put(table, binding);
        }
        binding.addBinding(logicalName, column);
    }

    @Override
    public String getPhysicalColumnName(Table table, String logicalName) throws org.hibernate.MappingException {
        return this.getPhysicalColumnName(table, this.getDatabase().toIdentifier(logicalName));
    }

    @Override
    public String getPhysicalColumnName(Table table, Identifier logicalName) throws org.hibernate.MappingException {
        TableColumnNameBinding binding;
        if (logicalName == null) {
            throw new org.hibernate.MappingException("Logical column name cannot be null");
        }
        Table currentTable = table;
        String physicalName = null;
        while (currentTable != null && ((binding = this.columnNameBindingByTableMap.get(currentTable)) == null || (physicalName = (String)binding.logicalToPhysical.get(logicalName)) == null)) {
            if (DenormalizedTable.class.isInstance(currentTable)) {
                currentTable = ((DenormalizedTable)currentTable).getIncludedTable();
                continue;
            }
            currentTable = null;
        }
        if (physicalName == null) {
            throw new org.hibernate.MappingException("Unable to find column with logical name " + logicalName.render() + " in table " + table.getName());
        }
        return physicalName;
    }

    @Override
    public String getLogicalColumnName(Table table, String physicalName) throws org.hibernate.MappingException {
        return this.getLogicalColumnName(table, this.getDatabase().toIdentifier(physicalName));
    }

    @Override
    public String getLogicalColumnName(Table table, Identifier physicalName) throws org.hibernate.MappingException {
        TableColumnNameBinding binding;
        String physicalNameString = physicalName.render(this.getDatabase().getJdbcEnvironment().getDialect());
        Identifier logicalName = null;
        Table currentTable = table;
        while (currentTable != null && ((binding = this.columnNameBindingByTableMap.get(currentTable)) == null || (logicalName = (Identifier)binding.physicalToLogical.get(physicalNameString)) == null)) {
            if (DenormalizedTable.class.isInstance(currentTable)) {
                currentTable = ((DenormalizedTable)currentTable).getIncludedTable();
                continue;
            }
            currentTable = null;
        }
        if (logicalName == null) {
            throw new org.hibernate.MappingException("Unable to find column with physical name " + physicalNameString + " in table " + table.getName());
        }
        return logicalName.render();
    }

    @Override
    public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject auxiliaryDatabaseObject) {
        this.getDatabase().addAuxiliaryDatabaseObject(auxiliaryDatabaseObject);
    }

    @Override
    public AnnotatedClassType getClassType(XClass clazz) {
        AnnotatedClassType type = this.annotatedClassTypeMap.get(clazz.getName());
        if (type == null) {
            return this.addClassType(clazz);
        }
        return type;
    }

    @Override
    public AnnotatedClassType addClassType(XClass clazz) {
        AnnotatedClassType type = clazz.isAnnotationPresent(Entity.class) ? AnnotatedClassType.ENTITY : (clazz.isAnnotationPresent(Embeddable.class) ? AnnotatedClassType.EMBEDDABLE : (clazz.isAnnotationPresent(MappedSuperclass.class) ? AnnotatedClassType.EMBEDDABLE_SUPERCLASS : AnnotatedClassType.NONE));
        this.annotatedClassTypeMap.put(clazz.getName(), type);
        return type;
    }

    @Override
    public void addAnyMetaDef(AnyMetaDef defAnn) {
        if (this.anyMetaDefs == null) {
            this.anyMetaDefs = new HashMap<String, AnyMetaDef>();
        } else if (this.anyMetaDefs.containsKey(defAnn.name())) {
            throw new AnnotationException("Two @AnyMetaDef with the same name defined: " + defAnn.name());
        }
        this.anyMetaDefs.put(defAnn.name(), defAnn);
    }

    @Override
    public AnyMetaDef getAnyMetaDef(String name) {
        if (this.anyMetaDefs == null) {
            return null;
        }
        return this.anyMetaDefs.get(name);
    }

    @Override
    public void addMappedSuperclass(Class type, org.hibernate.mapping.MappedSuperclass mappedSuperclass) {
        if (this.mappedSuperClasses == null) {
            this.mappedSuperClasses = new HashMap<Class, org.hibernate.mapping.MappedSuperclass>();
        }
        this.mappedSuperClasses.put(type, mappedSuperclass);
    }

    @Override
    public org.hibernate.mapping.MappedSuperclass getMappedSuperclass(Class type) {
        if (this.mappedSuperClasses == null) {
            return null;
        }
        return this.mappedSuperClasses.get(type);
    }

    @Override
    public PropertyData getPropertyAnnotatedWithMapsId(XClass entityType, String propertyName) {
        if (this.propertiesAnnotatedWithMapsId == null) {
            return null;
        }
        Map<String, PropertyData> map = this.propertiesAnnotatedWithMapsId.get(entityType);
        return map == null ? null : map.get(propertyName);
    }

    @Override
    public void addPropertyAnnotatedWithMapsId(XClass entityType, PropertyData property) {
        Map<String, PropertyData> map;
        if (this.propertiesAnnotatedWithMapsId == null) {
            this.propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
        }
        if ((map = this.propertiesAnnotatedWithMapsId.get(entityType)) == null) {
            map = new HashMap<String, PropertyData>();
            this.propertiesAnnotatedWithMapsId.put(entityType, map);
        }
        map.put(((MapsId)property.getProperty().getAnnotation(MapsId.class)).value(), property);
    }

    @Override
    public void addPropertyAnnotatedWithMapsIdSpecj(XClass entityType, PropertyData property, String mapsIdValue) {
        Map<String, PropertyData> map;
        if (this.propertiesAnnotatedWithMapsId == null) {
            this.propertiesAnnotatedWithMapsId = new HashMap<XClass, Map<String, PropertyData>>();
        }
        if ((map = this.propertiesAnnotatedWithMapsId.get(entityType)) == null) {
            map = new HashMap<String, PropertyData>();
            this.propertiesAnnotatedWithMapsId.put(entityType, map);
        }
        map.put(mapsIdValue, property);
    }

    @Override
    public PropertyData getPropertyAnnotatedWithIdAndToOne(XClass entityType, String propertyName) {
        if (this.propertiesAnnotatedWithIdAndToOne == null) {
            return null;
        }
        Map<String, PropertyData> map = this.propertiesAnnotatedWithIdAndToOne.get(entityType);
        return map == null ? null : map.get(propertyName);
    }

    @Override
    public void addToOneAndIdProperty(XClass entityType, PropertyData property) {
        Map<String, PropertyData> map;
        if (this.propertiesAnnotatedWithIdAndToOne == null) {
            this.propertiesAnnotatedWithIdAndToOne = new HashMap<XClass, Map<String, PropertyData>>();
        }
        if ((map = this.propertiesAnnotatedWithIdAndToOne.get(entityType)) == null) {
            map = new HashMap<String, PropertyData>();
            this.propertiesAnnotatedWithIdAndToOne.put(entityType, map);
        }
        map.put(property.getPropertyName(), property);
    }

    @Override
    public void addMappedBy(String entityName, String propertyName, String inversePropertyName) {
        if (this.mappedByResolver == null) {
            this.mappedByResolver = new HashMap<String, String>();
        }
        this.mappedByResolver.put(entityName + "." + propertyName, inversePropertyName);
    }

    @Override
    public String getFromMappedBy(String entityName, String propertyName) {
        if (this.mappedByResolver == null) {
            return null;
        }
        return this.mappedByResolver.get(entityName + "." + propertyName);
    }

    @Override
    public void addPropertyReferencedAssociation(String entityName, String propertyName, String propertyRef) {
        if (this.propertyRefResolver == null) {
            this.propertyRefResolver = new HashMap<String, String>();
        }
        this.propertyRefResolver.put(entityName + "." + propertyName, propertyRef);
    }

    @Override
    public String getPropertyReferencedAssociation(String entityName, String propertyName) {
        if (this.propertyRefResolver == null) {
            return null;
        }
        return this.propertyRefResolver.get(entityName + "." + propertyName);
    }

    @Override
    public void addPropertyReference(String referencedClass, String propertyName) {
        this.addDelayedPropertyReferenceHandler(new DelayedPropertyReferenceHandlerAnnotationImpl(referencedClass, propertyName, false));
    }

    @Override
    public void addDelayedPropertyReferenceHandler(InFlightMetadataCollector.DelayedPropertyReferenceHandler handler) {
        if (this.delayedPropertyReferenceHandlers == null) {
            this.delayedPropertyReferenceHandlers = new HashSet<InFlightMetadataCollector.DelayedPropertyReferenceHandler>();
        }
        this.delayedPropertyReferenceHandlers.add(handler);
    }

    @Override
    public void addUniquePropertyReference(String referencedClass, String propertyName) {
        this.addDelayedPropertyReferenceHandler(new DelayedPropertyReferenceHandlerAnnotationImpl(referencedClass, propertyName, true));
    }

    @Override
    public void addUniqueConstraints(Table table, List uniqueConstraints) {
        ArrayList<UniqueConstraintHolder> constraintHolders = new ArrayList<UniqueConstraintHolder>(CollectionHelper.determineProperSizing(uniqueConstraints.size()));
        int keyNameBase = this.determineCurrentNumberOfUniqueConstraintHolders(table);
        for (String[] columns : uniqueConstraints) {
            String keyName = "key" + keyNameBase++;
            constraintHolders.add(new UniqueConstraintHolder().setName(keyName).setColumns(columns));
        }
        this.addUniqueConstraintHolders(table, constraintHolders);
    }

    private int determineCurrentNumberOfUniqueConstraintHolders(Table table) {
        List<UniqueConstraintHolder> currentHolders = this.uniqueConstraintHoldersByTable == null ? null : this.uniqueConstraintHoldersByTable.get(table);
        return currentHolders == null ? 0 : currentHolders.size();
    }

    @Override
    public void addUniqueConstraintHolders(Table table, List<UniqueConstraintHolder> uniqueConstraintHolders) {
        List<UniqueConstraintHolder> holderList = null;
        if (this.uniqueConstraintHoldersByTable == null) {
            this.uniqueConstraintHoldersByTable = new HashMap<Table, List<UniqueConstraintHolder>>();
        } else {
            holderList = this.uniqueConstraintHoldersByTable.get(table);
        }
        if (holderList == null) {
            holderList = new ArrayList<UniqueConstraintHolder>();
            this.uniqueConstraintHoldersByTable.put(table, holderList);
        }
        holderList.addAll(uniqueConstraintHolders);
    }

    @Override
    public void addJpaIndexHolders(Table table, List<JPAIndexHolder> holders) {
        List<JPAIndexHolder> holderList = null;
        if (this.jpaIndexHoldersByTable == null) {
            this.jpaIndexHoldersByTable = new HashMap<Table, List<JPAIndexHolder>>();
        } else {
            holderList = this.jpaIndexHoldersByTable.get(table);
        }
        if (holderList == null) {
            holderList = new ArrayList<JPAIndexHolder>();
            this.jpaIndexHoldersByTable.put(table, holderList);
        }
        holderList.addAll(holders);
    }

    @Override
    public InFlightMetadataCollector.EntityTableXref getEntityTableXref(String entityName) {
        return this.entityTableXrefMap.get(entityName);
    }

    @Override
    public InFlightMetadataCollector.EntityTableXref addEntityTableXref(String entityName, Identifier primaryTableLogicalName, Table primaryTable, InFlightMetadataCollector.EntityTableXref superEntityTableXref) {
        EntityTableXrefImpl entry = new EntityTableXrefImpl(primaryTableLogicalName, primaryTable, (EntityTableXrefImpl)superEntityTableXref);
        this.entityTableXrefMap.put(entityName, entry);
        return entry;
    }

    @Override
    public Map<String, Join> getJoins(String entityName) {
        EntityTableXrefImpl xrefEntry = this.entityTableXrefMap.get(entityName);
        return xrefEntry == null ? null : xrefEntry.secondaryTableJoinMap;
    }

    @Override
    public void addSecondPass(SecondPass secondPass) {
        this.addSecondPass(secondPass, false);
    }

    @Override
    public void addSecondPass(SecondPass secondPass, boolean onTopOfTheQueue) {
        if (secondPass instanceof IdGeneratorResolverSecondPass) {
            this.addIdGeneratorResolverSecondPass((IdGeneratorResolverSecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof SetSimpleValueTypeSecondPass) {
            this.addSetSimpleValueTypeSecondPass((SetSimpleValueTypeSecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof FkSecondPass) {
            this.addFkSecondPass((FkSecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof CreateKeySecondPass) {
            this.addCreateKeySecondPass((CreateKeySecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof SecondaryTableSecondPass) {
            this.addSecondaryTableSecondPass((SecondaryTableSecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof QuerySecondPass) {
            this.addQuerySecondPass((QuerySecondPass)secondPass, onTopOfTheQueue);
        } else if (secondPass instanceof ImplicitColumnNamingSecondPass) {
            this.addImplicitColumnNamingSecondPass((ImplicitColumnNamingSecondPass)secondPass);
        } else {
            if (this.generalSecondPassList == null) {
                this.generalSecondPassList = new ArrayList();
            }
            this.addSecondPass(secondPass, this.generalSecondPassList, onTopOfTheQueue);
        }
    }

    private <T extends SecondPass> void addSecondPass(T secondPass, ArrayList<T> secondPassList, boolean onTopOfTheQueue) {
        if (onTopOfTheQueue) {
            secondPassList.add(0, secondPass);
        } else {
            secondPassList.add(secondPass);
        }
    }

    private void addSetSimpleValueTypeSecondPass(SetSimpleValueTypeSecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.setSimpleValueTypeSecondPassList == null) {
            this.setSimpleValueTypeSecondPassList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.setSimpleValueTypeSecondPassList, onTopOfTheQueue);
    }

    private void addIdGeneratorResolverSecondPass(IdGeneratorResolverSecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.idGeneratorResolverSecondPassList == null) {
            this.idGeneratorResolverSecondPassList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.idGeneratorResolverSecondPassList, onTopOfTheQueue);
    }

    private void addFkSecondPass(FkSecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.fkSecondPassList == null) {
            this.fkSecondPassList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.fkSecondPassList, onTopOfTheQueue);
    }

    private void addCreateKeySecondPass(CreateKeySecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.createKeySecondPasList == null) {
            this.createKeySecondPasList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.createKeySecondPasList, onTopOfTheQueue);
    }

    private void addSecondaryTableSecondPass(SecondaryTableSecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.secondaryTableSecondPassList == null) {
            this.secondaryTableSecondPassList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.secondaryTableSecondPassList, onTopOfTheQueue);
    }

    private void addQuerySecondPass(QuerySecondPass secondPass, boolean onTopOfTheQueue) {
        if (this.querySecondPassList == null) {
            this.querySecondPassList = new ArrayList();
        }
        this.addSecondPass(secondPass, this.querySecondPassList, onTopOfTheQueue);
    }

    private void addImplicitColumnNamingSecondPass(ImplicitColumnNamingSecondPass secondPass) {
        if (this.implicitColumnNamingSecondPassList == null) {
            this.implicitColumnNamingSecondPassList = new ArrayList();
        }
        this.implicitColumnNamingSecondPassList.add(secondPass);
    }

    public void processSecondPasses(MetadataBuildingContext buildingContext) {
        this.inSecondPass = true;
        try {
            this.processSecondPasses(this.idGeneratorResolverSecondPassList);
            this.processSecondPasses(this.implicitColumnNamingSecondPassList);
            this.processSecondPasses(this.setSimpleValueTypeSecondPassList);
            this.processFkSecondPassesInOrder();
            this.processSecondPasses(this.createKeySecondPasList);
            this.processSecondPasses(this.secondaryTableSecondPassList);
            this.processSecondPasses(this.querySecondPassList);
            this.processSecondPasses(this.generalSecondPassList);
            this.processPropertyReferences();
            this.secondPassCompileForeignKeys(buildingContext);
            this.processUniqueConstraintHolders(buildingContext);
            this.processJPAIndexHolders(buildingContext);
            this.processNaturalIdUniqueKeyBinders();
            this.processCachingOverrides();
        }
        finally {
            this.inSecondPass = false;
        }
    }

    private void processSecondPasses(ArrayList<? extends SecondPass> secondPasses) {
        if (secondPasses == null) {
            return;
        }
        for (SecondPass secondPass : secondPasses) {
            secondPass.doSecondPass(this.getEntityBindingMap());
        }
        secondPasses.clear();
    }

    private void processFkSecondPassesInOrder() {
        if (this.fkSecondPassList == null || this.fkSecondPassList.isEmpty()) {
            return;
        }
        HashMap<String, Set<FkSecondPass>> isADependencyOf = new HashMap<String, Set<FkSecondPass>>();
        ArrayList<FkSecondPass> endOfQueueFkSecondPasses = new ArrayList<FkSecondPass>(this.fkSecondPassList.size());
        for (FkSecondPass sp : this.fkSecondPassList) {
            if (sp.isInPrimaryKey()) {
                String referenceEntityName = sp.getReferencedEntityName();
                PersistentClass classMapping = this.getEntityBinding(referenceEntityName);
                String dependentTable = classMapping.getTable().getQualifiedTableName().render();
                if (!isADependencyOf.containsKey(dependentTable)) {
                    isADependencyOf.put(dependentTable, new HashSet());
                }
                ((Set)isADependencyOf.get(dependentTable)).add(sp);
                continue;
            }
            endOfQueueFkSecondPasses.add(sp);
        }
        ArrayList<FkSecondPass> orderedFkSecondPasses = new ArrayList<FkSecondPass>(this.fkSecondPassList.size());
        for (String tableName : isADependencyOf.keySet()) {
            this.buildRecursiveOrderedFkSecondPasses(orderedFkSecondPasses, isADependencyOf, tableName, tableName);
        }
        for (FkSecondPass sp : orderedFkSecondPasses) {
            sp.doSecondPass(this.getEntityBindingMap());
        }
        this.processEndOfQueue(endOfQueueFkSecondPasses);
        this.fkSecondPassList.clear();
    }

    private void buildRecursiveOrderedFkSecondPasses(List<FkSecondPass> orderedFkSecondPasses, Map<String, Set<FkSecondPass>> isADependencyOf, String startTable, String currentTable) {
        Set<FkSecondPass> dependencies = isADependencyOf.get(currentTable);
        if (dependencies == null || dependencies.size() == 0) {
            return;
        }
        for (FkSecondPass sp : dependencies) {
            String dependentTable = sp.getValue().getTable().getQualifiedTableName().render();
            if (dependentTable.compareTo(startTable) == 0) {
                throw new AnnotationException("Foreign key circularity dependency involving the following tables: " + startTable + ", " + dependentTable);
            }
            this.buildRecursiveOrderedFkSecondPasses(orderedFkSecondPasses, isADependencyOf, startTable, dependentTable);
            if (orderedFkSecondPasses.contains(sp)) continue;
            orderedFkSecondPasses.add(0, sp);
        }
    }

    private void processEndOfQueue(List<FkSecondPass> endOfQueueFkSecondPasses) {
        boolean stopProcess = false;
        RuntimeException originalException = null;
        while (!stopProcess) {
            ArrayList<FkSecondPass> failingSecondPasses = new ArrayList<FkSecondPass>();
            for (FkSecondPass pass : endOfQueueFkSecondPasses) {
                try {
                    pass.doSecondPass(this.getEntityBindingMap());
                }
                catch (RecoverableException e) {
                    failingSecondPasses.add(pass);
                    if (originalException != null) continue;
                    originalException = (RuntimeException)e.getCause();
                }
            }
            stopProcess = failingSecondPasses.size() == 0 || failingSecondPasses.size() == endOfQueueFkSecondPasses.size();
            endOfQueueFkSecondPasses = failingSecondPasses;
        }
        if (endOfQueueFkSecondPasses.size() > 0) {
            throw originalException;
        }
    }

    private void secondPassCompileForeignKeys(MetadataBuildingContext buildingContext) {
        int uniqueInteger = 0;
        HashSet<ForeignKey> done = new HashSet<ForeignKey>();
        for (Table table : this.collectTableMappings()) {
            table.setUniqueInteger(uniqueInteger++);
            this.secondPassCompileForeignKeys(table, done, buildingContext);
        }
    }

    protected void secondPassCompileForeignKeys(final Table table, Set<ForeignKey> done, final MetadataBuildingContext buildingContext) throws org.hibernate.MappingException {
        table.createForeignKeys();
        Iterator<ForeignKey> itr = table.getForeignKeyIterator();
        while (itr.hasNext()) {
            final ForeignKey fk = itr.next();
            if (done.contains(fk)) continue;
            done.add(fk);
            String referencedEntityName = fk.getReferencedEntityName();
            if (referencedEntityName == null) {
                throw new org.hibernate.MappingException("An association from the table " + fk.getTable().getName() + " does not specify the referenced entity");
            }
            log.debugf("Resolving reference to class: %s", referencedEntityName);
            PersistentClass referencedClass = this.getEntityBinding(referencedEntityName);
            if (referencedClass == null) {
                throw new org.hibernate.MappingException("An association from the table " + fk.getTable().getName() + " refers to an unmapped class: " + referencedEntityName);
            }
            if (referencedClass.isJoinedSubclass()) {
                this.secondPassCompileForeignKeys(referencedClass.getSuperclass().getTable(), done, buildingContext);
            }
            fk.setReferencedTable(referencedClass.getTable());
            ImplicitForeignKeyNameSource foreignKeyNameSource = new ImplicitForeignKeyNameSource(){
                final List<Identifier> columnNames;
                List<Identifier> referencedColumnNames;
                {
                    this.columnNames = InFlightMetadataCollectorImpl.this.extractColumnNames(fk.getColumns());
                    this.referencedColumnNames = null;
                }

                @Override
                public Identifier getTableName() {
                    return table.getNameIdentifier();
                }

                @Override
                public List<Identifier> getColumnNames() {
                    return this.columnNames;
                }

                @Override
                public Identifier getReferencedTableName() {
                    return fk.getReferencedTable().getNameIdentifier();
                }

                @Override
                public List<Identifier> getReferencedColumnNames() {
                    if (this.referencedColumnNames == null) {
                        this.referencedColumnNames = InFlightMetadataCollectorImpl.this.extractColumnNames(fk.getReferencedColumns());
                    }
                    return this.referencedColumnNames;
                }

                @Override
                public Identifier getUserProvidedIdentifier() {
                    return fk.getName() != null ? Identifier.toIdentifier(fk.getName()) : null;
                }

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return buildingContext;
                }
            };
            Identifier nameIdentifier = this.getMetadataBuildingOptions().getImplicitNamingStrategy().determineForeignKeyName(foreignKeyNameSource);
            fk.setName(nameIdentifier.render(this.getDatabase().getJdbcEnvironment().getDialect()));
            fk.alignColumns();
        }
    }

    private List<Identifier> toIdentifiers(String[] names) {
        if (names == null) {
            return Collections.emptyList();
        }
        ArrayList<Identifier> columnNames = CollectionHelper.arrayList(names.length);
        for (String name : names) {
            columnNames.add(this.getDatabase().toIdentifier(name));
        }
        return columnNames;
    }

    private List<Identifier> extractColumnNames(List columns) {
        if (columns == null || columns.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Identifier> columnNames = CollectionHelper.arrayList(columns.size());
        for (Column column : columns) {
            columnNames.add(this.getDatabase().toIdentifier(column.getQuotedName()));
        }
        return columnNames;
    }

    private void processPropertyReferences() {
        if (this.delayedPropertyReferenceHandlers == null) {
            return;
        }
        log.debug("Processing association property references");
        for (InFlightMetadataCollector.DelayedPropertyReferenceHandler delayedPropertyReferenceHandler : this.delayedPropertyReferenceHandlers) {
            delayedPropertyReferenceHandler.process(this);
        }
        this.delayedPropertyReferenceHandlers.clear();
    }

    private void processUniqueConstraintHolders(MetadataBuildingContext buildingContext) {
        if (this.uniqueConstraintHoldersByTable == null) {
            return;
        }
        for (Map.Entry<Table, List<UniqueConstraintHolder>> tableListEntry : this.uniqueConstraintHoldersByTable.entrySet()) {
            Table table = tableListEntry.getKey();
            List<UniqueConstraintHolder> uniqueConstraints = tableListEntry.getValue();
            for (UniqueConstraintHolder holder : uniqueConstraints) {
                this.buildUniqueKeyFromColumnNames(table, holder.getName(), holder.getColumns(), buildingContext);
            }
        }
        this.uniqueConstraintHoldersByTable.clear();
    }

    private void buildUniqueKeyFromColumnNames(Table table, String keyName, String[] columnNames, MetadataBuildingContext buildingContext) {
        this.buildUniqueKeyFromColumnNames(table, keyName, columnNames, null, true, buildingContext);
    }

    private void buildUniqueKeyFromColumnNames(final Table table, String keyName, final String[] columnNames, String[] orderings, boolean unique, final MetadataBuildingContext buildingContext) {
        String order;
        Column column;
        int i;
        Identifier keyNameIdentifier;
        int size = columnNames.length;
        Column[] columns = new Column[size];
        HashSet<Column> unbound = new HashSet<Column>();
        HashSet<Column> unboundNoLogical = new HashSet<Column>();
        for (int index = 0; index < size; ++index) {
            String logicalColumnName = columnNames[index];
            try {
                String physicalColumnName = this.getPhysicalColumnName(table, logicalColumnName);
                columns[index] = new Column(physicalColumnName);
                unbound.add(columns[index]);
                continue;
            }
            catch (org.hibernate.MappingException e) {
                columns[index] = new Column(logicalColumnName);
                unboundNoLogical.add(columns[index]);
            }
        }
        final String originalKeyName = keyName;
        if (unique) {
            keyNameIdentifier = this.getMetadataBuildingOptions().getImplicitNamingStrategy().determineUniqueKeyName(new ImplicitUniqueKeyNameSource(){
                private List<Identifier> columnNameIdentifiers;

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return buildingContext;
                }

                @Override
                public Identifier getTableName() {
                    return table.getNameIdentifier();
                }

                @Override
                public List<Identifier> getColumnNames() {
                    if (this.columnNameIdentifiers == null) {
                        this.columnNameIdentifiers = InFlightMetadataCollectorImpl.this.toIdentifiers(columnNames);
                    }
                    return this.columnNameIdentifiers;
                }

                @Override
                public Identifier getUserProvidedIdentifier() {
                    return originalKeyName != null ? Identifier.toIdentifier(originalKeyName) : null;
                }
            });
            keyName = keyNameIdentifier.render(this.getDatabase().getJdbcEnvironment().getDialect());
            UniqueKey uk = table.getOrCreateUniqueKey(keyName);
            for (i = 0; i < columns.length; ++i) {
                column = columns[i];
                String string = order = orderings != null ? orderings[i] : null;
                if (!table.containsColumn(column)) continue;
                uk.addColumn(column, order);
                unbound.remove(column);
            }
        } else {
            keyNameIdentifier = this.getMetadataBuildingOptions().getImplicitNamingStrategy().determineIndexName(new ImplicitIndexNameSource(){
                private List<Identifier> columnNameIdentifiers;

                @Override
                public MetadataBuildingContext getBuildingContext() {
                    return buildingContext;
                }

                @Override
                public Identifier getTableName() {
                    return table.getNameIdentifier();
                }

                @Override
                public List<Identifier> getColumnNames() {
                    if (this.columnNameIdentifiers == null) {
                        this.columnNameIdentifiers = InFlightMetadataCollectorImpl.this.toIdentifiers(columnNames);
                    }
                    return this.columnNameIdentifiers;
                }

                @Override
                public Identifier getUserProvidedIdentifier() {
                    return originalKeyName != null ? Identifier.toIdentifier(originalKeyName) : null;
                }
            });
            keyName = keyNameIdentifier.render(this.getDatabase().getJdbcEnvironment().getDialect());
            Index index = table.getOrCreateIndex(keyName);
            for (i = 0; i < columns.length; ++i) {
                column = columns[i];
                String string = order = orderings != null ? orderings[i] : null;
                if (!table.containsColumn(column)) continue;
                index.addColumn(column, order);
                unbound.remove(column);
            }
        }
        if (unbound.size() > 0 || unboundNoLogical.size() > 0) {
            StringBuilder sb = new StringBuilder("Unable to create ");
            if (unique) {
                sb.append("unique key constraint (");
            } else {
                sb.append("index (");
            }
            for (String columnName : columnNames) {
                sb.append(columnName).append(", ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(") on table ").append(table.getName()).append(": database column ");
            for (Column column2 : unbound) {
                sb.append("'").append(column2.getName()).append("', ");
            }
            for (Column column3 : unboundNoLogical) {
                sb.append("'").append(column3.getName()).append("', ");
            }
            sb.setLength(sb.length() - 2);
            sb.append(" not found. Make sure that you use the correct column name which depends on the naming strategy in use (it may not be the same as the property name in the entity, especially for relational types)");
            throw new AnnotationException(sb.toString());
        }
    }

    private void processJPAIndexHolders(MetadataBuildingContext buildingContext) {
        if (this.jpaIndexHoldersByTable == null) {
            return;
        }
        for (Map.Entry<Table, List<JPAIndexHolder>> entry : this.jpaIndexHoldersByTable.entrySet()) {
            Table table = entry.getKey();
            List<JPAIndexHolder> jpaIndexHolders = entry.getValue();
            for (JPAIndexHolder holder : jpaIndexHolders) {
                this.buildUniqueKeyFromColumnNames(table, holder.getName(), holder.getColumns(), holder.getOrdering(), holder.isUnique(), buildingContext);
            }
        }
    }

    @Override
    public NaturalIdUniqueKeyBinder locateNaturalIdUniqueKeyBinder(String entityName) {
        if (this.naturalIdUniqueKeyBinderMap == null) {
            return null;
        }
        return this.naturalIdUniqueKeyBinderMap.get(entityName);
    }

    @Override
    public void registerNaturalIdUniqueKeyBinder(String entityName, NaturalIdUniqueKeyBinder ukBinder) {
        NaturalIdUniqueKeyBinder previous;
        if (this.naturalIdUniqueKeyBinderMap == null) {
            this.naturalIdUniqueKeyBinderMap = new HashMap<String, NaturalIdUniqueKeyBinder>();
        }
        if ((previous = this.naturalIdUniqueKeyBinderMap.put(entityName, ukBinder)) != null) {
            throw new AssertionFailure("Previous NaturalIdUniqueKeyBinder already registered for entity name : " + entityName);
        }
    }

    private void processNaturalIdUniqueKeyBinders() {
        if (this.naturalIdUniqueKeyBinderMap == null) {
            return;
        }
        for (NaturalIdUniqueKeyBinder naturalIdUniqueKeyBinder : this.naturalIdUniqueKeyBinderMap.values()) {
            naturalIdUniqueKeyBinder.process();
        }
        this.naturalIdUniqueKeyBinderMap.clear();
    }

    private void processCachingOverrides() {
        if (this.bootstrapContext.getCacheRegionDefinitions() == null) {
            return;
        }
        for (CacheRegionDefinition cacheRegionDefinition : this.bootstrapContext.getCacheRegionDefinitions()) {
            if (cacheRegionDefinition.getRegionType() == CacheRegionDefinition.CacheRegionType.ENTITY) {
                PersistentClass entityBinding = this.getEntityBinding(cacheRegionDefinition.getRole());
                if (entityBinding == null) {
                    throw new HibernateException("Cache override referenced an unknown entity : " + cacheRegionDefinition.getRole());
                }
                if (!RootClass.class.isInstance(entityBinding)) {
                    throw new HibernateException("Cache override referenced a non-root entity : " + cacheRegionDefinition.getRole());
                }
                entityBinding.setCached(true);
                ((RootClass)entityBinding).setCacheRegionName(cacheRegionDefinition.getRegion());
                ((RootClass)entityBinding).setCacheConcurrencyStrategy(cacheRegionDefinition.getUsage());
                ((RootClass)entityBinding).setLazyPropertiesCacheable(cacheRegionDefinition.isCacheLazy());
                continue;
            }
            if (cacheRegionDefinition.getRegionType() != CacheRegionDefinition.CacheRegionType.COLLECTION) continue;
            org.hibernate.mapping.Collection collectionBinding = this.getCollectionBinding(cacheRegionDefinition.getRole());
            if (collectionBinding == null) {
                throw new HibernateException("Cache override referenced an unknown collection role : " + cacheRegionDefinition.getRole());
            }
            collectionBinding.setCacheRegionName(cacheRegionDefinition.getRegion());
            collectionBinding.setCacheConcurrencyStrategy(cacheRegionDefinition.getUsage());
        }
    }

    @Override
    public boolean isInSecondPass() {
        return this.inSecondPass;
    }

    public MetadataImpl buildMetadataInstance(MetadataBuildingContext buildingContext) {
        this.processSecondPasses(buildingContext);
        this.processExportableProducers();
        try {
            MetadataImpl metadataImpl = new MetadataImpl(this.uuid, this.options, this.identifierGeneratorFactory, this.entityBindingMap, this.mappedSuperClasses, this.collectionBindingMap, this.typeDefinitionMap, this.filterDefinitionMap, this.fetchProfileMap, this.imports, this.idGeneratorDefinitionMap, this.namedQueryMap, this.namedNativeQueryMap, this.namedProcedureCallMap, this.sqlResultSetMappingMap, this.namedEntityGraphMap, this.sqlFunctionMap, this.getDatabase(), this.bootstrapContext);
            return metadataImpl;
        }
        finally {
            this.getBootstrapContext().release();
        }
    }

    private void processExportableProducers() {
        Dialect dialect = this.getDatabase().getJdbcEnvironment().getDialect();
        for (PersistentClass entityBinding : this.entityBindingMap.values()) {
            if (entityBinding.isInherited()) continue;
            this.handleIdentifierValueBinding(entityBinding.getIdentifier(), dialect, (RootClass)entityBinding);
        }
        for (org.hibernate.mapping.Collection collection : this.collectionBindingMap.values()) {
            if (!IdentifierCollection.class.isInstance(collection)) continue;
            this.handleIdentifierValueBinding(((IdentifierCollection)collection).getIdentifier(), dialect, null);
        }
    }

    private void handleIdentifierValueBinding(KeyValue identifierValueBinding, Dialect dialect, RootClass entityBinding) {
        try {
            IdentifierGenerator ig = identifierValueBinding.createIdentifierGenerator(this.getIdentifierGeneratorFactory(), dialect, entityBinding);
            ig.registerExportables(this.getDatabase());
        }
        catch (org.hibernate.MappingException e) {
            log.debugf("Ignoring exception thrown when trying to build IdentifierGenerator as part of Metadata building", (Object)e);
        }
    }

    private String extractName(Identifier identifier, Dialect dialect) {
        if (identifier == null) {
            return null;
        }
        return identifier.render(dialect);
    }

    private static final class EntityTableXrefImpl
    implements InFlightMetadataCollector.EntityTableXref {
        private final Identifier primaryTableLogicalName;
        private final Table primaryTable;
        private EntityTableXrefImpl superEntityTableXref;
        private Map<String, Join> secondaryTableJoinMap;

        public EntityTableXrefImpl(Identifier primaryTableLogicalName, Table primaryTable, EntityTableXrefImpl superEntityTableXref) {
            this.primaryTableLogicalName = primaryTableLogicalName;
            this.primaryTable = primaryTable;
            this.superEntityTableXref = superEntityTableXref;
        }

        @Override
        public void addSecondaryTable(LocalMetadataBuildingContext buildingContext, Identifier logicalName, Join secondaryTableJoin) {
            if (Identifier.areEqual(this.primaryTableLogicalName, logicalName)) {
                throw new MappingException(String.format(Locale.ENGLISH, "Attempt to add secondary table with same name as primary table [%s]", this.primaryTableLogicalName), buildingContext.getOrigin());
            }
            if (this.secondaryTableJoinMap == null) {
                this.secondaryTableJoinMap = new HashMap<String, Join>();
                this.secondaryTableJoinMap.put(logicalName.getCanonicalName(), secondaryTableJoin);
            } else {
                Join existing = this.secondaryTableJoinMap.put(logicalName.getCanonicalName(), secondaryTableJoin);
                if (existing != null) {
                    throw new MappingException(String.format(Locale.ENGLISH, "Added secondary table with same name [%s]", logicalName), buildingContext.getOrigin());
                }
            }
        }

        @Override
        public void addSecondaryTable(QualifiedTableName logicalQualifiedTableName, Join secondaryTableJoin) {
            Identifier logicalName = logicalQualifiedTableName.getTableName();
            if (Identifier.areEqual(Identifier.toIdentifier(new QualifiedTableName(Identifier.toIdentifier(this.primaryTable.getCatalog()), Identifier.toIdentifier(this.primaryTable.getSchema()), this.primaryTableLogicalName).render()), Identifier.toIdentifier(logicalQualifiedTableName.render()))) {
                throw new InFlightMetadataCollector.DuplicateSecondaryTableException(logicalName);
            }
            if (this.secondaryTableJoinMap == null) {
                this.secondaryTableJoinMap = new HashMap<String, Join>();
                this.secondaryTableJoinMap.put(logicalName.getCanonicalName(), secondaryTableJoin);
            } else {
                Join existing = this.secondaryTableJoinMap.put(logicalName.getCanonicalName(), secondaryTableJoin);
                if (existing != null) {
                    throw new InFlightMetadataCollector.DuplicateSecondaryTableException(logicalName);
                }
            }
        }

        @Override
        public Table getPrimaryTable() {
            return this.primaryTable;
        }

        @Override
        public Table resolveTable(Identifier tableName) {
            if (tableName == null) {
                return this.primaryTable;
            }
            if (Identifier.areEqual(this.primaryTableLogicalName, tableName)) {
                return this.primaryTable;
            }
            Join secondaryTableJoin = null;
            if (this.secondaryTableJoinMap != null) {
                secondaryTableJoin = this.secondaryTableJoinMap.get(tableName.getCanonicalName());
            }
            if (secondaryTableJoin != null) {
                return secondaryTableJoin.getTable();
            }
            if (this.superEntityTableXref != null) {
                return this.superEntityTableXref.resolveTable(tableName);
            }
            return null;
        }

        @Override
        public Join locateJoin(Identifier tableName) {
            if (tableName == null) {
                return null;
            }
            Join join = null;
            if (this.secondaryTableJoinMap != null) {
                join = this.secondaryTableJoinMap.get(tableName.getCanonicalName());
            }
            if (join != null) {
                return join;
            }
            if (this.superEntityTableXref != null) {
                return this.superEntityTableXref.locateJoin(tableName);
            }
            return null;
        }
    }

    private static class DelayedPropertyReferenceHandlerAnnotationImpl
    implements InFlightMetadataCollector.DelayedPropertyReferenceHandler {
        public final String referencedClass;
        public final String propertyName;
        public final boolean unique;

        public DelayedPropertyReferenceHandlerAnnotationImpl(String referencedClass, String propertyName, boolean unique) {
            this.referencedClass = referencedClass;
            this.propertyName = propertyName;
            this.unique = unique;
        }

        @Override
        public void process(InFlightMetadataCollector metadataCollector) {
            PersistentClass clazz = metadataCollector.getEntityBinding(this.referencedClass);
            if (clazz == null) {
                throw new org.hibernate.MappingException("property-ref to unmapped class: " + this.referencedClass);
            }
            Property prop = clazz.getReferencedProperty(this.propertyName);
            if (this.unique) {
                ((SimpleValue)prop.getValue()).setAlternateUniqueKey(true);
            }
        }
    }

    private class TableColumnNameBinding
    implements Serializable {
        private final String tableName;
        private Map<Identifier, String> logicalToPhysical = new HashMap<Identifier, String>();
        private Map<String, Identifier> physicalToLogical = new HashMap<String, Identifier>();

        private TableColumnNameBinding(String tableName) {
            this.tableName = tableName;
        }

        public void addBinding(Identifier logicalName, Column physicalColumn) {
            String physicalNameString = physicalColumn.getQuotedName(InFlightMetadataCollectorImpl.this.getDatabase().getJdbcEnvironment().getDialect());
            this.bindLogicalToPhysical(logicalName, physicalNameString);
            this.bindPhysicalToLogical(logicalName, physicalNameString);
        }

        private void bindLogicalToPhysical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
            String existingPhysicalNameMapping = this.logicalToPhysical.put(logicalName, physicalName);
            if (existingPhysicalNameMapping != null) {
                boolean areSame;
                boolean bl = areSame = logicalName.isQuoted() ? physicalName.equals(existingPhysicalNameMapping) : physicalName.equalsIgnoreCase(existingPhysicalNameMapping);
                if (!areSame) {
                    throw new DuplicateMappingException(String.format(Locale.ENGLISH, "Table [%s] contains logical column name [%s] referring to multiple physical column names: [%s], [%s]", this.tableName, logicalName, existingPhysicalNameMapping, physicalName), DuplicateMappingException.Type.COLUMN_BINDING, this.tableName + "." + logicalName);
                }
            }
        }

        private void bindPhysicalToLogical(Identifier logicalName, String physicalName) throws DuplicateMappingException {
            Identifier existingLogicalName = this.physicalToLogical.put(physicalName, logicalName);
            if (existingLogicalName != null && !existingLogicalName.equals(logicalName)) {
                throw new DuplicateMappingException(String.format(Locale.ENGLISH, "Table [%s] contains physical column name [%s] referred to by multiple logical column names: [%s], [%s]", this.tableName, physicalName, logicalName, existingLogicalName), DuplicateMappingException.Type.COLUMN_BINDING, this.tableName + "." + physicalName);
            }
        }
    }
}

