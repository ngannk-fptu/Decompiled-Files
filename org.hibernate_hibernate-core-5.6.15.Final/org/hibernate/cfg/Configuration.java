/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.SharedCacheMode
 */
package org.hibernate.cfg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.persistence.AttributeConverter;
import javax.persistence.SharedCacheMode;
import org.hibernate.EmptyInterceptor;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.SessionFactoryBuilder;
import org.hibernate.boot.jaxb.spi.Binding;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyJpaCompliantImpl;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
import org.hibernate.boot.model.relational.AuxiliaryDatabaseObject;
import org.hibernate.boot.registry.BootstrapServiceRegistry;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.XmlMappingBinderAccess;
import org.hibernate.cfg.AttributeConverterDefinition;
import org.hibernate.cfg.annotations.NamedEntityGraphDefinition;
import org.hibernate.cfg.annotations.NamedProcedureCallDefinition;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.xml.XmlDocument;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tuple.entity.EntityTuplizerFactory;
import org.hibernate.type.BasicType;
import org.hibernate.type.CompositeCustomType;
import org.hibernate.type.CustomType;
import org.hibernate.type.SerializationException;
import org.hibernate.usertype.CompositeUserType;
import org.hibernate.usertype.UserType;
import org.w3c.dom.Document;

public class Configuration {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(Configuration.class);
    public static final String ARTEFACT_PROCESSING_ORDER = "hibernate.mapping.precedence";
    private final BootstrapServiceRegistry bootstrapServiceRegistry;
    private final MetadataSources metadataSources;
    private ImplicitNamingStrategy implicitNamingStrategy;
    private PhysicalNamingStrategy physicalNamingStrategy;
    private List<BasicType> basicTypes = new ArrayList<BasicType>();
    private List<TypeContributor> typeContributorRegistrations = new ArrayList<TypeContributor>();
    private Map<String, NamedQueryDefinition> namedQueries;
    private Map<String, NamedSQLQueryDefinition> namedSqlQueries;
    private Map<String, NamedProcedureCallDefinition> namedProcedureCallMap;
    private Map<String, ResultSetMappingDefinition> sqlResultSetMappings;
    private Map<String, NamedEntityGraphDefinition> namedEntityGraphMap;
    private Map<String, SQLFunction> sqlFunctions;
    private List<AuxiliaryDatabaseObject> auxiliaryDatabaseObjectList;
    private HashMap<Class, AttributeConverterDefinition> attributeConverterDefinitionsByClass;
    private StandardServiceRegistryBuilder standardServiceRegistryBuilder;
    private EntityNotFoundDelegate entityNotFoundDelegate;
    private EntityTuplizerFactory entityTuplizerFactory;
    private Interceptor interceptor;
    private SessionFactoryObserver sessionFactoryObserver;
    private CurrentTenantIdentifierResolver currentTenantIdentifierResolver;
    private Properties properties;
    private SharedCacheMode sharedCacheMode;

    public Configuration() {
        this(new BootstrapServiceRegistryBuilder().build());
    }

    public Configuration(BootstrapServiceRegistry serviceRegistry) {
        this.bootstrapServiceRegistry = serviceRegistry;
        this.metadataSources = new MetadataSources(serviceRegistry);
        this.reset();
    }

    public Configuration(MetadataSources metadataSources) {
        this.bootstrapServiceRegistry = Configuration.getBootstrapRegistry(metadataSources.getServiceRegistry());
        this.metadataSources = metadataSources;
        this.reset();
    }

    private static BootstrapServiceRegistry getBootstrapRegistry(ServiceRegistry serviceRegistry) {
        if (BootstrapServiceRegistry.class.isInstance(serviceRegistry)) {
            return (BootstrapServiceRegistry)serviceRegistry;
        }
        if (StandardServiceRegistry.class.isInstance(serviceRegistry)) {
            StandardServiceRegistry ssr = (StandardServiceRegistry)serviceRegistry;
            return (BootstrapServiceRegistry)ssr.getParentServiceRegistry();
        }
        throw new HibernateException("No ServiceRegistry was passed to Configuration#buildSessionFactory and could not determine how to locate BootstrapServiceRegistry from Configuration instantiation");
    }

    protected void reset() {
        this.implicitNamingStrategy = ImplicitNamingStrategyJpaCompliantImpl.INSTANCE;
        this.physicalNamingStrategy = PhysicalNamingStrategyStandardImpl.INSTANCE;
        this.namedQueries = new HashMap<String, NamedQueryDefinition>();
        this.namedSqlQueries = new HashMap<String, NamedSQLQueryDefinition>();
        this.sqlResultSetMappings = new HashMap<String, ResultSetMappingDefinition>();
        this.namedEntityGraphMap = new HashMap<String, NamedEntityGraphDefinition>();
        this.namedProcedureCallMap = new HashMap<String, NamedProcedureCallDefinition>();
        this.standardServiceRegistryBuilder = new StandardServiceRegistryBuilder(this.bootstrapServiceRegistry);
        this.entityTuplizerFactory = new EntityTuplizerFactory();
        this.interceptor = EmptyInterceptor.INSTANCE;
        this.properties = new Properties();
        this.properties.putAll((Map<?, ?>)this.standardServiceRegistryBuilder.getSettings());
    }

    public Properties getProperties() {
        return this.properties;
    }

    public Configuration setProperties(Properties properties) {
        this.properties = properties;
        return this;
    }

    public String getProperty(String propertyName) {
        Object o = this.properties.get(propertyName);
        return o instanceof String ? (String)o : null;
    }

    public Configuration setProperty(String propertyName, String value) {
        this.properties.setProperty(propertyName, value);
        return this;
    }

    public Configuration addProperties(Properties properties) {
        this.properties.putAll((Map<?, ?>)properties);
        return this;
    }

    public void setImplicitNamingStrategy(ImplicitNamingStrategy implicitNamingStrategy) {
        this.implicitNamingStrategy = implicitNamingStrategy;
    }

    public void setPhysicalNamingStrategy(PhysicalNamingStrategy physicalNamingStrategy) {
        this.physicalNamingStrategy = physicalNamingStrategy;
    }

    public Configuration configure() throws HibernateException {
        return this.configure("hibernate.cfg.xml");
    }

    public Configuration configure(String resource) throws HibernateException {
        this.standardServiceRegistryBuilder.configure(resource);
        this.properties.putAll((Map<?, ?>)this.standardServiceRegistryBuilder.getSettings());
        return this;
    }

    public StandardServiceRegistryBuilder getStandardServiceRegistryBuilder() {
        return this.standardServiceRegistryBuilder;
    }

    public Configuration configure(URL url) throws HibernateException {
        this.standardServiceRegistryBuilder.configure(url);
        this.properties.putAll((Map<?, ?>)this.standardServiceRegistryBuilder.getSettings());
        return this;
    }

    public Configuration configure(File configFile) throws HibernateException {
        this.standardServiceRegistryBuilder.configure(configFile);
        this.properties.putAll((Map<?, ?>)this.standardServiceRegistryBuilder.getSettings());
        return this;
    }

    @Deprecated
    public Configuration configure(Document document) throws HibernateException {
        return this;
    }

    public Configuration registerTypeContributor(TypeContributor typeContributor) {
        this.typeContributorRegistrations.add(typeContributor);
        return this;
    }

    public Configuration registerTypeOverride(BasicType type) {
        this.basicTypes.add(type);
        return this;
    }

    public Configuration registerTypeOverride(UserType type, String[] keys) {
        this.basicTypes.add(new CustomType(type, keys));
        return this;
    }

    public Configuration registerTypeOverride(CompositeUserType type, String[] keys) {
        this.basicTypes.add(new CompositeCustomType(type, keys));
        return this;
    }

    public Configuration addFile(String xmlFile) throws MappingException {
        this.metadataSources.addFile(xmlFile);
        return this;
    }

    public Configuration addFile(File xmlFile) throws MappingException {
        this.metadataSources.addFile(xmlFile);
        return this;
    }

    public XmlMappingBinderAccess getXmlMappingBinderAccess() {
        return this.metadataSources.getXmlMappingBinderAccess();
    }

    @Deprecated
    public void add(XmlDocument metadataXml) {
    }

    public Configuration addXmlMapping(Binding<?> binding) {
        this.metadataSources.addXmlBinding(binding);
        return this;
    }

    public Configuration addCacheableFile(File xmlFile) throws MappingException {
        this.metadataSources.addCacheableFile(xmlFile);
        return this;
    }

    public Configuration addCacheableFileStrictly(File xmlFile) throws SerializationException, FileNotFoundException {
        this.metadataSources.addCacheableFileStrictly(xmlFile);
        return this;
    }

    public Configuration addCacheableFile(String xmlFile) throws MappingException {
        this.metadataSources.addCacheableFile(xmlFile);
        return this;
    }

    @Deprecated
    public Configuration addXML(String xml) throws MappingException {
        return this;
    }

    public Configuration addURL(URL url) throws MappingException {
        this.metadataSources.addURL(url);
        return this;
    }

    @Deprecated
    public Configuration addDocument(Document doc) throws MappingException {
        this.metadataSources.addDocument(doc);
        return this;
    }

    public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
        this.metadataSources.addInputStream(xmlInputStream);
        return this;
    }

    @Deprecated
    public Configuration addResource(String resourceName, ClassLoader classLoader) throws MappingException {
        return this.addResource(resourceName);
    }

    public Configuration addResource(String resourceName) throws MappingException {
        this.metadataSources.addResource(resourceName);
        return this;
    }

    public Configuration addClass(Class persistentClass) throws MappingException {
        this.metadataSources.addClass(persistentClass);
        return this;
    }

    public Configuration addAnnotatedClass(Class annotatedClass) {
        this.metadataSources.addAnnotatedClass(annotatedClass);
        return this;
    }

    public Configuration addPackage(String packageName) throws MappingException {
        this.metadataSources.addPackage(packageName);
        return this;
    }

    public Configuration addJar(File jar) throws MappingException {
        this.metadataSources.addJar(jar);
        return this;
    }

    public Configuration addDirectory(File dir) throws MappingException {
        this.metadataSources.addDirectory(dir);
        return this;
    }

    public Interceptor getInterceptor() {
        return this.interceptor;
    }

    public Configuration setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public EntityTuplizerFactory getEntityTuplizerFactory() {
        return this.entityTuplizerFactory;
    }

    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.entityNotFoundDelegate;
    }

    public void setEntityNotFoundDelegate(EntityNotFoundDelegate entityNotFoundDelegate) {
        this.entityNotFoundDelegate = entityNotFoundDelegate;
    }

    public SessionFactoryObserver getSessionFactoryObserver() {
        return this.sessionFactoryObserver;
    }

    public void setSessionFactoryObserver(SessionFactoryObserver sessionFactoryObserver) {
        this.sessionFactoryObserver = sessionFactoryObserver;
    }

    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return this.currentTenantIdentifierResolver;
    }

    public void setCurrentTenantIdentifierResolver(CurrentTenantIdentifierResolver currentTenantIdentifierResolver) {
        this.currentTenantIdentifierResolver = currentTenantIdentifierResolver;
    }

    public SessionFactory buildSessionFactory(ServiceRegistry serviceRegistry) throws HibernateException {
        log.debug("Building session factory using provided StandardServiceRegistry");
        MetadataBuilder metadataBuilder = this.metadataSources.getMetadataBuilder((StandardServiceRegistry)serviceRegistry);
        if (this.implicitNamingStrategy != null) {
            metadataBuilder.applyImplicitNamingStrategy(this.implicitNamingStrategy);
        }
        if (this.physicalNamingStrategy != null) {
            metadataBuilder.applyPhysicalNamingStrategy(this.physicalNamingStrategy);
        }
        if (this.sharedCacheMode != null) {
            metadataBuilder.applySharedCacheMode(this.sharedCacheMode);
        }
        if (!this.typeContributorRegistrations.isEmpty()) {
            for (TypeContributor typeContributor : this.typeContributorRegistrations) {
                metadataBuilder.applyTypes(typeContributor);
            }
        }
        if (!this.basicTypes.isEmpty()) {
            for (BasicType basicType : this.basicTypes) {
                metadataBuilder.applyBasicType(basicType);
            }
        }
        if (this.sqlFunctions != null) {
            for (Map.Entry entry : this.sqlFunctions.entrySet()) {
                metadataBuilder.applySqlFunction((String)entry.getKey(), (SQLFunction)entry.getValue());
            }
        }
        if (this.auxiliaryDatabaseObjectList != null) {
            for (AuxiliaryDatabaseObject auxiliaryDatabaseObject : this.auxiliaryDatabaseObjectList) {
                metadataBuilder.applyAuxiliaryDatabaseObject(auxiliaryDatabaseObject);
            }
        }
        if (this.attributeConverterDefinitionsByClass != null) {
            for (AttributeConverterDefinition attributeConverterDefinition : this.attributeConverterDefinitionsByClass.values()) {
                metadataBuilder.applyAttributeConverter(attributeConverterDefinition);
            }
        }
        Metadata metadata = metadataBuilder.build();
        SessionFactoryBuilder sessionFactoryBuilder = metadata.getSessionFactoryBuilder();
        if (this.interceptor != null && this.interceptor != EmptyInterceptor.INSTANCE) {
            sessionFactoryBuilder.applyInterceptor(this.interceptor);
        }
        if (this.getSessionFactoryObserver() != null) {
            sessionFactoryBuilder.addSessionFactoryObservers(this.getSessionFactoryObserver());
        }
        if (this.getEntityNotFoundDelegate() != null) {
            sessionFactoryBuilder.applyEntityNotFoundDelegate(this.getEntityNotFoundDelegate());
        }
        if (this.getEntityTuplizerFactory() != null) {
            sessionFactoryBuilder.applyEntityTuplizerFactory(this.getEntityTuplizerFactory());
        }
        if (this.getCurrentTenantIdentifierResolver() != null) {
            sessionFactoryBuilder.applyCurrentTenantIdentifierResolver(this.getCurrentTenantIdentifierResolver());
        }
        return sessionFactoryBuilder.build();
    }

    public SessionFactory buildSessionFactory() throws HibernateException {
        log.debug("Building session factory using internal StandardServiceRegistryBuilder");
        this.standardServiceRegistryBuilder.applySettings(this.properties);
        StandardServiceRegistry serviceRegistry = this.standardServiceRegistryBuilder.build();
        try {
            return this.buildSessionFactory(serviceRegistry);
        }
        catch (Throwable t) {
            serviceRegistry.close();
            throw t;
        }
    }

    public Map<String, SQLFunction> getSqlFunctions() {
        return this.sqlFunctions;
    }

    public void addSqlFunction(String functionName, SQLFunction function) {
        if (this.sqlFunctions == null) {
            this.sqlFunctions = new HashMap<String, SQLFunction>();
        }
        this.sqlFunctions.put(functionName, function);
    }

    public void addAuxiliaryDatabaseObject(AuxiliaryDatabaseObject object) {
        if (this.auxiliaryDatabaseObjectList == null) {
            this.auxiliaryDatabaseObjectList = new ArrayList<AuxiliaryDatabaseObject>();
        }
        this.auxiliaryDatabaseObjectList.add(object);
    }

    public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass, boolean autoApply) {
        this.addAttributeConverter(AttributeConverterDefinition.from(attributeConverterClass, autoApply));
    }

    public void addAttributeConverter(Class<? extends AttributeConverter> attributeConverterClass) {
        this.addAttributeConverter(AttributeConverterDefinition.from(attributeConverterClass));
    }

    public void addAttributeConverter(AttributeConverter attributeConverter) {
        this.addAttributeConverter(AttributeConverterDefinition.from(attributeConverter));
    }

    public void addAttributeConverter(AttributeConverter attributeConverter, boolean autoApply) {
        this.addAttributeConverter(AttributeConverterDefinition.from(attributeConverter, autoApply));
    }

    public void addAttributeConverter(AttributeConverterDefinition definition) {
        if (this.attributeConverterDefinitionsByClass == null) {
            this.attributeConverterDefinitionsByClass = new HashMap();
        }
        this.attributeConverterDefinitionsByClass.put(definition.getAttributeConverter().getClass(), definition);
    }

    public void setSharedCacheMode(SharedCacheMode sharedCacheMode) {
        this.sharedCacheMode = sharedCacheMode;
    }

    public Map getNamedSQLQueries() {
        return this.namedSqlQueries;
    }

    public Map getSqlResultSetMappings() {
        return this.sqlResultSetMappings;
    }

    public Collection<NamedEntityGraphDefinition> getNamedEntityGraphs() {
        return this.namedEntityGraphMap == null ? Collections.emptyList() : this.namedEntityGraphMap.values();
    }

    public Map<String, NamedQueryDefinition> getNamedQueries() {
        return this.namedQueries;
    }

    public Map<String, NamedProcedureCallDefinition> getNamedProcedureCallMap() {
        return this.namedProcedureCallMap;
    }

    @Deprecated
    public void buildMappings() {
    }

    public Configuration mergeProperties(Properties properties) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            if (this.properties.containsKey(entry.getKey())) continue;
            this.properties.setProperty((String)entry.getKey(), (String)entry.getValue());
        }
        return this;
    }
}

