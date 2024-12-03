/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceContextType
 *  javax.persistence.PersistenceException
 *  javax.persistence.PersistenceUnitUtil
 *  javax.persistence.Query
 *  javax.persistence.SynchronizationType
 *  javax.persistence.criteria.CriteriaBuilder
 *  org.jboss.logging.Logger
 */
package org.hibernate.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.function.Supplier;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContextType;
import javax.persistence.PersistenceException;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.ConnectionAcquisitionMode;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EmptyInterceptor;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionBuilder;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.cfgxml.spi.CfgXmlAccessService;
import org.hibernate.boot.cfgxml.spi.LoadedConfig;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.model.relational.internal.SqlStringGenerationContextImpl;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cfg.Settings;
import org.hibernate.context.internal.JTASessionContext;
import org.hibernate.context.internal.ManagedSessionContext;
import org.hibernate.context.internal.ThreadLocalSessionContext;
import org.hibernate.context.spi.CurrentSessionContext;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.jdbc.connections.spi.JdbcConnectionAccess;
import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.engine.jdbc.env.internal.JdbcEnvironmentInitiator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jndi.spi.JndiService;
import org.hibernate.engine.profile.Association;
import org.hibernate.engine.profile.Fetch;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.query.spi.ReturnMetadata;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedQueryDefinitionBuilder;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinitionBuilder;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionOwner;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventEngine;
import org.hibernate.event.spi.EventType;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.integrator.spi.IntegratorService;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.internal.SessionFactoryObserverChain;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.internal.SessionImpl;
import org.hibernate.internal.SessionOwnerBehavior;
import org.hibernate.internal.StatelessSessionImpl;
import org.hibernate.internal.TypeLocatorImpl;
import org.hibernate.internal.util.config.ConfigurationException;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.jpa.internal.AfterCompletionActionLegacyJpaImpl;
import org.hibernate.jpa.internal.ExceptionMapperLegacyJpaImpl;
import org.hibernate.jpa.internal.ManagedFlushCheckerLegacyJpaImpl;
import org.hibernate.jpa.internal.PersistenceUnitUtilImpl;
import org.hibernate.mapping.FetchProfile;
import org.hibernate.mapping.RootClass;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.internal.JpaMetaModelPopulationSetting;
import org.hibernate.metamodel.internal.MetamodelImpl;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Loadable;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.proxy.HibernateProxyHelper;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.resource.jdbc.spi.PhysicalConnectionHandlingMode;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.AfterCompletionAction;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ExceptionMapper;
import org.hibernate.resource.transaction.backend.jta.internal.synchronization.ManagedFlushChecker;
import org.hibernate.resource.transaction.spi.TransactionCoordinatorBuilder;
import org.hibernate.secure.spi.GrantedPermission;
import org.hibernate.secure.spi.JaccPermissionDeclarations;
import org.hibernate.secure.spi.JaccService;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.hibernate.service.spi.SessionFactoryServiceRegistryFactory;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.tool.schema.spi.DelayedDropAction;
import org.hibernate.tool.schema.spi.SchemaManagementToolCoordinator;
import org.hibernate.type.SerializableType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;
import org.jboss.logging.Logger;

public class SessionFactoryImpl
implements SessionFactoryImplementor {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SessionFactoryImpl.class);
    private final String name;
    private final String uuid;
    private volatile transient Status status = Status.OPEN;
    private final transient SessionFactoryObserverChain observer = new SessionFactoryObserverChain();
    private final transient SessionFactoryOptions sessionFactoryOptions;
    private final transient Settings settings;
    private final transient Map<String, Object> properties;
    private final transient SessionFactoryServiceRegistry serviceRegistry;
    private final transient EventEngine eventEngine;
    private final transient JdbcServices jdbcServices;
    private final transient SqlStringGenerationContext sqlStringGenerationContext;
    private final transient SQLFunctionRegistry sqlFunctionRegistry;
    private final transient MetamodelImplementor metamodel;
    private final transient CriteriaBuilderImpl criteriaBuilder;
    private final PersistenceUnitUtil jpaPersistenceUnitUtil;
    private final transient CacheImplementor cacheAccess;
    private final transient NamedQueryRepository namedQueryRepository;
    private final transient QueryPlanCache queryPlanCache;
    private final transient CurrentSessionContext currentSessionContext;
    private volatile DelayedDropAction delayedDropAction;
    private final transient Map<String, IdentifierGenerator> identifierGenerators;
    private final transient Map<String, FilterDefinition> filters;
    private final transient Map<String, FetchProfile> fetchProfiles;
    private final transient TypeHelper typeHelper;
    private final transient FastSessionServices fastSessionServices;
    private final transient SessionBuilder defaultSessionOpenOptions;
    private final transient SessionBuilder temporarySessionOpenOptions;
    private final transient StatelessSessionBuilder defaultStatelessOptions;
    private transient SynchronizationType synchronizationType;
    private transient PersistenceContextType persistenceContextType;
    private transient StatisticsImplementor statistics;

    public SessionFactoryImpl(MetadataImplementor metadata, SessionFactoryOptions options, QueryPlanCache.QueryPlanCreator queryPlanCacheFunction) {
        LOG.debug("Building session factory");
        this.sessionFactoryOptions = options;
        this.settings = new Settings(options, metadata);
        this.serviceRegistry = options.getServiceRegistry().getService(SessionFactoryServiceRegistryFactory.class).buildServiceRegistry(this, options);
        this.eventEngine = new EventEngine(metadata, this);
        metadata.initSessionFactory(this);
        CfgXmlAccessService cfgXmlAccessService = this.serviceRegistry.getService(CfgXmlAccessService.class);
        String sfName = this.settings.getSessionFactoryName();
        if (cfgXmlAccessService.getAggregatedConfig() != null) {
            if (sfName == null) {
                sfName = cfgXmlAccessService.getAggregatedConfig().getSessionFactoryName();
            }
            this.applyCfgXmlValues(cfgXmlAccessService.getAggregatedConfig(), this.serviceRegistry);
        }
        this.name = sfName;
        this.uuid = options.getUuid();
        this.jdbcServices = this.serviceRegistry.getService(JdbcServices.class);
        ConfigurationService configurationService = this.serviceRegistry.getService(ConfigurationService.class);
        this.properties = new HashMap<String, Object>();
        this.properties.putAll(configurationService.getSettings());
        if (!this.properties.containsKey("javax.persistence.validation.factory") && !this.properties.containsKey("jakarta.persistence.validation.factory") && this.getSessionFactoryOptions().getValidatorFactoryReference() != null) {
            this.properties.put("javax.persistence.validation.factory", this.getSessionFactoryOptions().getValidatorFactoryReference());
            this.properties.put("jakarta.persistence.validation.factory", this.getSessionFactoryOptions().getValidatorFactoryReference());
        }
        this.maskOutSensitiveInformation(this.properties);
        this.logIfEmptyCompositesEnabled(this.properties);
        this.sqlStringGenerationContext = SqlStringGenerationContextImpl.fromExplicit(this.jdbcServices.getJdbcEnvironment(), metadata.getDatabase(), options.getDefaultCatalog(), options.getDefaultSchema());
        this.sqlFunctionRegistry = new SQLFunctionRegistry(this.jdbcServices.getJdbcEnvironment().getDialect(), options.getCustomSqlFunctionMap());
        this.cacheAccess = this.serviceRegistry.getService(CacheImplementor.class);
        this.criteriaBuilder = new CriteriaBuilderImpl(this);
        this.jpaPersistenceUnitUtil = new PersistenceUnitUtilImpl(this);
        for (SessionFactoryObserver sessionFactoryObserver : options.getSessionFactoryObservers()) {
            this.observer.addObserver(sessionFactoryObserver);
        }
        this.typeHelper = new TypeLocatorImpl(metadata.getTypeConfiguration().getTypeResolver());
        this.filters = new HashMap<String, FilterDefinition>();
        this.filters.putAll(metadata.getFilterDefinitions());
        LOG.debugf("Session factory constructed with filter configurations : %s", this.filters);
        LOG.debugf("Instantiating session factory with properties: %s", this.properties);
        this.queryPlanCache = new QueryPlanCache(this, queryPlanCacheFunction);
        class IntegratorObserver
        implements SessionFactoryObserver {
            private ArrayList<Integrator> integrators = new ArrayList();

            IntegratorObserver() {
            }

            @Override
            public void sessionFactoryCreated(SessionFactory factory) {
            }

            @Override
            public void sessionFactoryClosed(SessionFactory factory) {
                for (Integrator integrator : this.integrators) {
                    integrator.disintegrate(SessionFactoryImpl.this, SessionFactoryImpl.this.serviceRegistry);
                }
                this.integrators.clear();
            }
        }
        IntegratorObserver integratorObserver = new IntegratorObserver();
        this.observer.addObserver(integratorObserver);
        try {
            Map<String, HibernateException> errors;
            for (Integrator integrator : this.serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
                integrator.integrate(metadata, this, this.serviceRegistry);
                integratorObserver.integrators.add(integrator);
            }
            this.identifierGenerators = new HashMap<String, IdentifierGenerator>();
            metadata.getEntityBindings().stream().filter(model -> !model.isInherited()).forEach(model -> {
                IdentifierGenerator generator = model.getIdentifier().createIdentifierGenerator(metadata.getIdentifierGeneratorFactory(), this.jdbcServices.getJdbcEnvironment().getDialect(), (RootClass)model);
                generator.initialize(this.sqlStringGenerationContext);
                this.identifierGenerators.put(model.getEntityName(), generator);
            });
            metadata.validate();
            LOG.debug("Instantiated session factory");
            this.metamodel = metadata.getTypeConfiguration().scope(this);
            ((MetamodelImpl)this.metamodel).initialize(metadata, JpaMetaModelPopulationSetting.determineJpaMetaModelPopulationSetting(this.properties));
            this.namedQueryRepository = metadata.buildNamedQueryRepository(this);
            this.settings.getMultiTableBulkIdStrategy().prepare(this.jdbcServices, this.buildLocalConnectionAccess(), metadata, this.sessionFactoryOptions, this.sqlStringGenerationContext);
            SchemaManagementToolCoordinator.process(metadata, this.serviceRegistry, this.properties, action -> {
                this.delayedDropAction = action;
            });
            this.currentSessionContext = this.buildCurrentSessionContext();
            this.fetchProfiles = new HashMap<String, FetchProfile>();
            for (org.hibernate.mapping.FetchProfile mappingProfile : metadata.getFetchProfiles()) {
                FetchProfile fetchProfile = new FetchProfile(mappingProfile.getName());
                for (FetchProfile.Fetch fetch : mappingProfile.getFetches()) {
                    EntityPersister owner;
                    String entityName = this.metamodel.getImportedClassName(fetch.getEntity());
                    EntityPersister entityPersister = owner = entityName == null ? null : this.metamodel.entityPersister(entityName);
                    if (owner == null) {
                        throw new HibernateException("Unable to resolve entity reference [" + fetch.getEntity() + "] in fetch profile [" + fetchProfile.getName() + "]");
                    }
                    Type associationType = owner.getPropertyType(fetch.getAssociation());
                    if (associationType == null || !associationType.isAssociationType()) {
                        throw new HibernateException("Fetch profile [" + fetchProfile.getName() + "] specified an invalid association");
                    }
                    Fetch.Style fetchStyle = Fetch.Style.parse(fetch.getStyle());
                    fetchProfile.addFetch(new Association(owner, fetch.getAssociation()), fetchStyle);
                    ((Loadable)owner).registerAffectingFetchProfile(fetchProfile.getName());
                }
                this.fetchProfiles.put(fetchProfile.getName(), fetchProfile);
            }
            this.defaultSessionOpenOptions = this.createDefaultSessionOpenOptionsIfPossible();
            this.temporarySessionOpenOptions = this.defaultSessionOpenOptions == null ? null : this.buildTemporarySessionOpenOptions();
            this.defaultStatelessOptions = this.defaultSessionOpenOptions == null ? null : this.withStatelessOptions();
            this.fastSessionServices = new FastSessionServices(this);
            if (this.settings.isNamedQueryStartupCheckingEnabled() && !(errors = this.checkNamedQueries()).isEmpty()) {
                StringBuilder failingQueries = new StringBuilder("Errors in named queries: ");
                String separator = System.lineSeparator();
                for (Map.Entry entry : errors.entrySet()) {
                    LOG.namedQueryError((String)entry.getKey(), (HibernateException)((Object)entry.getValue()));
                    failingQueries.append(separator).append((String)entry.getKey()).append(" failed because of: ").append(entry.getValue());
                }
                throw new HibernateException(failingQueries.toString());
            }
            this.observer.sessionFactoryCreated(this);
            SessionFactoryRegistry.INSTANCE.addSessionFactory(this.getUuid(), this.name, this.settings.isSessionFactoryNameAlsoJndiName(), this, this.serviceRegistry.getService(JndiService.class));
            metadata.getMetadataBuildingOptions().getReflectionManager().reset();
        }
        catch (Exception e) {
            for (Integrator integrator : this.serviceRegistry.getService(IntegratorService.class).getIntegrators()) {
                integrator.disintegrate(this, this.serviceRegistry);
                integratorObserver.integrators.remove(integrator);
            }
            this.close();
            throw e;
        }
    }

    private SessionBuilder createDefaultSessionOpenOptionsIfPossible() {
        CurrentTenantIdentifierResolver currentTenantIdentifierResolver = this.getCurrentTenantIdentifierResolver();
        if (currentTenantIdentifierResolver == null) {
            return this.withOptions();
        }
        return null;
    }

    private SessionBuilder buildTemporarySessionOpenOptions() {
        return this.withOptions().autoClose(false).flushMode(FlushMode.MANUAL).connectionHandlingMode(PhysicalConnectionHandlingMode.DELAYED_ACQUISITION_AND_RELEASE_AFTER_STATEMENT);
    }

    private void applyCfgXmlValues(LoadedConfig aggregatedConfig, SessionFactoryServiceRegistry serviceRegistry) {
        JaccPermissionDeclarations permissions;
        JaccService jaccService = serviceRegistry.getService(JaccService.class);
        if (jaccService.getContextId() != null && (permissions = aggregatedConfig.getJaccPermissions(jaccService.getContextId())) != null) {
            for (GrantedPermission grantedPermission : permissions.getPermissionDeclarations()) {
                jaccService.addPermission(grantedPermission);
            }
        }
        if (aggregatedConfig.getEventListenerMap() != null) {
            ClassLoaderService cls = serviceRegistry.getService(ClassLoaderService.class);
            EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
            for (Map.Entry<EventType, Set<String>> entry : aggregatedConfig.getEventListenerMap().entrySet()) {
                EventListenerGroup group = eventListenerRegistry.getEventListenerGroup(entry.getKey());
                for (String listenerClassName : entry.getValue()) {
                    try {
                        group.appendListener(cls.classForName(listenerClassName).newInstance());
                    }
                    catch (Exception e) {
                        throw new ConfigurationException("Unable to instantiate event listener class : " + listenerClassName, e);
                    }
                }
            }
        }
    }

    private JdbcConnectionAccess buildLocalConnectionAccess() {
        if (this.settings.getMultiTenancyStrategy().requiresMultiTenantConnectionProvider()) {
            MultiTenantConnectionProvider mTenantConnectionProvider = this.serviceRegistry.getService(MultiTenantConnectionProvider.class);
            return new JdbcEnvironmentInitiator.MultiTenantConnectionProviderJdbcConnectionAccess(mTenantConnectionProvider);
        }
        ConnectionProvider connectionProvider = this.serviceRegistry.getService(ConnectionProvider.class);
        return new JdbcEnvironmentInitiator.ConnectionProviderJdbcConnectionAccess(connectionProvider);
    }

    @Override
    public Session openSession() throws HibernateException {
        if (this.defaultSessionOpenOptions != null) {
            return this.defaultSessionOpenOptions.openSession();
        }
        return this.withOptions().openSession();
    }

    @Override
    public Session openTemporarySession() throws HibernateException {
        if (this.temporarySessionOpenOptions != null) {
            return this.temporarySessionOpenOptions.openSession();
        }
        return this.buildTemporarySessionOpenOptions().openSession();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        if (this.currentSessionContext == null) {
            throw new HibernateException("No CurrentSessionContext configured!");
        }
        return this.currentSessionContext.currentSession();
    }

    @Override
    public SessionBuilderImplementor withOptions() {
        return new SessionBuilderImpl(this);
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return new StatelessSessionBuilderImpl(this);
    }

    @Override
    public StatelessSession openStatelessSession() {
        if (this.defaultStatelessOptions != null) {
            return this.defaultStatelessOptions.openStatelessSession();
        }
        return this.withStatelessOptions().openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return this.withStatelessOptions().connection(connection).openStatelessSession();
    }

    @Override
    public void addObserver(SessionFactoryObserver observer) {
        this.observer.addObserver(observer);
    }

    public Map<String, Object> getProperties() {
        this.validateNotClosed();
        return this.properties;
    }

    protected void validateNotClosed() {
        if (this.status == Status.CLOSED) {
            throw new IllegalStateException("EntityManagerFactory is closed");
        }
    }

    @Override
    public String getUuid() {
        return this.uuid;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public EventEngine getEventEngine() {
        return this.eventEngine;
    }

    @Override
    public JdbcServices getJdbcServices() {
        return this.jdbcServices;
    }

    @Override
    public SqlStringGenerationContext getSqlStringGenerationContext() {
        return this.sqlStringGenerationContext;
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return null;
    }

    @Override
    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.metamodel.getTypeConfiguration().getTypeResolver();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        return this.queryPlanCache;
    }

    private Map<String, HibernateException> checkNamedQueries() throws HibernateException {
        return this.namedQueryRepository.checkNamedQueries(this.queryPlanCache);
    }

    @Override
    public SessionFactoryImplementor.DeserializationResolver getDeserializationResolver() {
        return new SessionFactoryImplementor.DeserializationResolver(){

            public SessionFactoryImplementor resolve() {
                return (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.findSessionFactory(SessionFactoryImpl.this.uuid, SessionFactoryImpl.this.name);
            }
        };
    }

    @Override
    public Settings getSettings() {
        return this.settings;
    }

    @Override
    public <T> List<RootGraphImplementor<? super T>> findEntityGraphsByJavaType(Class<T> entityClass) {
        return this.getMetamodel().findEntityGraphsByJavaType(entityClass);
    }

    public Session createEntityManager() {
        this.validateNotClosed();
        return this.buildEntityManager(SynchronizationType.SYNCHRONIZED, null);
    }

    private <K, V> Session buildEntityManager(SynchronizationType synchronizationType, Map<K, V> map) {
        assert (this.status != Status.CLOSED);
        SessionBuilderImplementor builder = this.withOptions();
        if (synchronizationType == SynchronizationType.SYNCHRONIZED) {
            builder.autoJoinTransactions(true);
        } else {
            builder.autoJoinTransactions(false);
        }
        Session session = builder.openSession();
        if (map != null) {
            for (Map.Entry<K, V> o : map.entrySet()) {
                K key = o.getKey();
                if (!(key instanceof String)) continue;
                String sKey = (String)key;
                session.setProperty(sKey, o.getValue());
            }
        }
        return session;
    }

    public Session createEntityManager(Map map) {
        this.validateNotClosed();
        return this.buildEntityManager(SynchronizationType.SYNCHRONIZED, map);
    }

    public Session createEntityManager(SynchronizationType synchronizationType) {
        this.validateNotClosed();
        this.errorIfResourceLocalDueToExplicitSynchronizationType();
        return this.buildEntityManager(synchronizationType, null);
    }

    private void errorIfResourceLocalDueToExplicitSynchronizationType() {
        if (!this.getServiceRegistry().getService(TransactionCoordinatorBuilder.class).isJta()) {
            throw new IllegalStateException("Illegal attempt to specify a SynchronizationType when building an EntityManager from a EntityManagerFactory defined as RESOURCE_LOCAL (as opposed to JTA)");
        }
    }

    public Session createEntityManager(SynchronizationType synchronizationType, Map map) {
        this.validateNotClosed();
        this.errorIfResourceLocalDueToExplicitSynchronizationType();
        return this.buildEntityManager(synchronizationType, map);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        this.validateNotClosed();
        return this.criteriaBuilder;
    }

    @Override
    public MetamodelImplementor getMetamodel() {
        this.validateNotClosed();
        return this.metamodel;
    }

    public boolean isOpen() {
        return this.status != Status.CLOSED;
    }

    public RootGraphImplementor findEntityGraphByName(String name) {
        return this.getMetamodel().findEntityGraphByName(name);
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.sessionFactoryOptions;
    }

    @Override
    public Interceptor getInterceptor() {
        return this.sessionFactoryOptions.getInterceptor();
    }

    @Override
    public Reference getReference() {
        LOG.debug("Returning a Reference to the SessionFactory");
        return new Reference(SessionFactoryImpl.class.getName(), new StringRefAddr("uuid", this.getUuid()), SessionFactoryRegistry.ObjectFactoryImpl.class.getName(), null);
    }

    @Override
    public NamedQueryRepository getNamedQueryRepository() {
        return this.namedQueryRepository;
    }

    @Override
    public Type getIdentifierType(String className) throws MappingException {
        return this.getMetamodel().entityPersister(className).getIdentifierType();
    }

    @Override
    public String getIdentifierPropertyName(String className) throws MappingException {
        return this.getMetamodel().entityPersister(className).getIdentifierPropertyName();
    }

    @Override
    public Type[] getReturnTypes(String queryString) throws HibernateException {
        ReturnMetadata metadata = this.queryPlanCache.getHQLQueryPlan(queryString, false, Collections.EMPTY_MAP).getReturnMetadata();
        return metadata == null ? null : metadata.getReturnTypes();
    }

    @Override
    public String[] getReturnAliases(String queryString) throws HibernateException {
        ReturnMetadata metadata = this.queryPlanCache.getHQLQueryPlan(queryString, false, Collections.EMPTY_MAP).getReturnMetadata();
        return metadata == null ? null : metadata.getReturnAliases();
    }

    @Override
    public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
        return this.getClassMetadata(persistentClass.getName());
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
        return (CollectionMetadata)((Object)this.getMetamodel().collectionPersister(roleName));
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) throws HibernateException {
        return (ClassMetadata)((Object)this.getMetamodel().entityPersister(entityName));
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() throws HibernateException {
        throw new UnsupportedOperationException("org.hibernate.SessionFactory.getAllClassMetadata is no longer supported");
    }

    @Override
    public Map getAllCollectionMetadata() throws HibernateException {
        throw new UnsupportedOperationException("org.hibernate.SessionFactory.getAllCollectionMetadata is no longer supported");
    }

    @Override
    public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
        return this.getMetamodel().entityPersister(className).getPropertyType(propertyName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() throws HibernateException {
        SessionFactoryImpl sessionFactoryImpl = this;
        synchronized (sessionFactoryImpl) {
            if (this.status != Status.OPEN) {
                if (this.getSessionFactoryOptions().getJpaCompliance().isJpaClosedComplianceEnabled()) {
                    throw new IllegalStateException("EntityManagerFactory is already closed");
                }
                LOG.trace("Already closed");
                return;
            }
            this.status = Status.CLOSING;
        }
        try {
            LOG.closing();
            this.observer.sessionFactoryClosing(this);
            this.settings.getMultiTableBulkIdStrategy().release(this.serviceRegistry.getService(JdbcServices.class), this.buildLocalConnectionAccess());
            if (this.cacheAccess != null) {
                this.cacheAccess.close();
            }
            if (this.metamodel != null) {
                this.metamodel.close();
            }
            if (this.queryPlanCache != null) {
                this.queryPlanCache.cleanup();
            }
            if (this.delayedDropAction != null) {
                this.delayedDropAction.perform(this.serviceRegistry);
            }
            SessionFactoryRegistry.INSTANCE.removeSessionFactory(this.getUuid(), this.name, this.settings.isSessionFactoryNameAlsoJndiName(), this.serviceRegistry.getService(JndiService.class));
        }
        finally {
            this.status = Status.CLOSED;
        }
        this.observer.sessionFactoryClosed(this);
        this.serviceRegistry.destroy();
    }

    @Override
    public CacheImplementor getCache() {
        this.validateNotClosed();
        return this.cacheAccess;
    }

    public PersistenceUnitUtil getPersistenceUnitUtil() {
        this.validateNotClosed();
        return this.jpaPersistenceUnitUtil;
    }

    public void addNamedQuery(String name, Query query) {
        this.validateNotClosed();
        try {
            ProcedureCall unwrapped = (ProcedureCall)query.unwrap(ProcedureCall.class);
            if (unwrapped != null) {
                this.addNamedStoredProcedureQuery(name, unwrapped);
                return;
            }
        }
        catch (PersistenceException unwrapped) {
            // empty catch block
        }
        try {
            org.hibernate.query.Query hibernateQuery = (org.hibernate.query.Query)query.unwrap(org.hibernate.query.Query.class);
            if (hibernateQuery != null) {
                if (NativeQuery.class.isInstance(hibernateQuery)) {
                    this.getNamedQueryRepository().registerNamedSQLQueryDefinition(name, this.extractSqlQueryDefinition((NativeQuery)hibernateQuery, name));
                } else {
                    this.getNamedQueryRepository().registerNamedQueryDefinition(name, this.extractHqlQueryDefinition(hibernateQuery, name));
                }
                return;
            }
        }
        catch (PersistenceException persistenceException) {
            // empty catch block
        }
        throw new PersistenceException(String.format("Unsure how to how to properly unwrap given Query [%s] as basis for named query", query));
    }

    private void addNamedStoredProcedureQuery(String name, ProcedureCall procedureCall) {
        this.getNamedQueryRepository().registerNamedProcedureCallMemento(name, procedureCall.extractMemento(procedureCall.getHints()));
    }

    private NamedSQLQueryDefinition extractSqlQueryDefinition(NativeQuery nativeSqlQuery, String name) {
        NamedSQLQueryDefinitionBuilder builder = new NamedSQLQueryDefinitionBuilder(name);
        this.fillInNamedQueryBuilder(builder, nativeSqlQuery);
        builder.setCallable(nativeSqlQuery.isCallable()).setQuerySpaces(nativeSqlQuery.getSynchronizedQuerySpaces()).setQueryReturns(nativeSqlQuery.getQueryReturns());
        return builder.createNamedQueryDefinition();
    }

    private NamedQueryDefinition extractHqlQueryDefinition(org.hibernate.query.Query hqlQuery, String name) {
        NamedQueryDefinitionBuilder builder = new NamedQueryDefinitionBuilder(name);
        this.fillInNamedQueryBuilder(builder, hqlQuery);
        builder.setLockOptions(hqlQuery.getLockOptions().makeCopy());
        return builder.createNamedQueryDefinition();
    }

    private void fillInNamedQueryBuilder(NamedQueryDefinitionBuilder builder, org.hibernate.query.Query query) {
        builder.setQuery(query.getQueryString()).setComment(query.getComment()).setCacheable(query.isCacheable()).setCacheRegion(query.getCacheRegion()).setCacheMode(query.getCacheMode()).setReadOnly(query.isReadOnly()).setFlushMode(query.getHibernateFlushMode());
        if (query.getQueryOptions().getFirstRow() != null) {
            builder.setFirstResult(query.getQueryOptions().getFirstRow());
        }
        if (query.getQueryOptions().getMaxRows() != null) {
            builder.setMaxResults(query.getQueryOptions().getMaxRows());
        }
        if (query.getQueryOptions().getTimeout() != null) {
            builder.setTimeout(query.getQueryOptions().getTimeout());
        }
        if (query.getQueryOptions().getFetchSize() != null) {
            builder.setFetchSize(query.getQueryOptions().getFetchSize());
        }
    }

    public <T> T unwrap(Class<T> type) {
        if (type.isAssignableFrom(SessionFactory.class)) {
            return type.cast(this);
        }
        if (type.isAssignableFrom(SessionFactoryImplementor.class)) {
            return type.cast(this);
        }
        if (type.isAssignableFrom(SessionFactoryImpl.class)) {
            return type.cast(this);
        }
        if (type.isAssignableFrom(EntityManagerFactory.class)) {
            return type.cast(this);
        }
        throw new PersistenceException("Hibernate cannot unwrap EntityManagerFactory as '" + type.getName() + "'");
    }

    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.getMetamodel().addNamedEntityGraph(graphName, (RootGraphImplementor)entityGraph);
    }

    @Override
    public boolean isClosed() {
        return this.status == Status.CLOSED;
    }

    @Override
    public StatisticsImplementor getStatistics() {
        if (this.statistics == null) {
            this.statistics = this.serviceRegistry.getService(StatisticsImplementor.class);
        }
        return this.statistics;
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        FilterDefinition def = this.filters.get(filterName);
        if (def == null) {
            throw new HibernateException("No such filter configured [" + filterName + "]");
        }
        return def;
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return this.fetchProfiles.containsKey(name);
    }

    @Override
    public Set getDefinedFilterNames() {
        return this.filters.keySet();
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
        return this.identifierGenerators.get(rootEntityName);
    }

    private boolean canAccessTransactionManager() {
        try {
            return this.serviceRegistry.getService(JtaPlatform.class).retrieveTransactionManager() != null;
        }
        catch (Exception e) {
            return false;
        }
    }

    private CurrentSessionContext buildCurrentSessionContext() {
        String impl = (String)this.properties.get("hibernate.current_session_context_class");
        if (impl == null) {
            if (this.canAccessTransactionManager()) {
                impl = "jta";
            } else {
                return null;
            }
        }
        if ("jta".equals(impl)) {
            return new JTASessionContext(this);
        }
        if ("thread".equals(impl)) {
            return new ThreadLocalSessionContext(this);
        }
        if ("managed".equals(impl)) {
            return new ManagedSessionContext(this);
        }
        try {
            Class implClass = this.serviceRegistry.getService(ClassLoaderService.class).classForName(impl);
            return (CurrentSessionContext)implClass.getConstructor(SessionFactoryImplementor.class).newInstance(this);
        }
        catch (Throwable t) {
            LOG.unableToConstructCurrentSessionContext(impl, t);
            return null;
        }
    }

    @Override
    public ServiceRegistryImplementor getServiceRegistry() {
        return this.serviceRegistry;
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.sessionFactoryOptions.getEntityNotFoundDelegate();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return this.sqlFunctionRegistry;
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return this.fetchProfiles.get(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.typeHelper;
    }

    @Override
    public Type resolveParameterBindType(Object bindValue) {
        if (bindValue == null) {
            return null;
        }
        return this.resolveParameterBindType(HibernateProxyHelper.getClassWithoutInitializingProxy(bindValue));
    }

    @Override
    public Type resolveParameterBindType(Class clazz) {
        boolean serializable;
        String typename = clazz.getName();
        Type type = this.getTypeResolver().heuristicType(typename);
        boolean bl = serializable = type != null && type instanceof SerializableType;
        if (type == null || serializable) {
            try {
                this.getMetamodel().entityPersister(clazz.getName());
            }
            catch (MappingException me) {
                if (serializable) {
                    return type;
                }
                throw new HibernateException("Could not determine a type for class: " + typename);
            }
            return this.getTypeHelper().entity(clazz);
        }
        return type;
    }

    @Deprecated
    public static Interceptor configuredInterceptor(Interceptor interceptor, SessionFactoryOptions options) {
        return SessionFactoryImpl.configuredInterceptor(interceptor, false, options);
    }

    public static Interceptor configuredInterceptor(Interceptor interceptor, boolean explicitNoInterceptor, SessionFactoryOptions options) {
        if (interceptor != null && interceptor != EmptyInterceptor.INSTANCE) {
            return interceptor;
        }
        Interceptor optionsInterceptor = options.getInterceptor();
        if (optionsInterceptor != null && optionsInterceptor != EmptyInterceptor.INSTANCE) {
            return optionsInterceptor;
        }
        if (explicitNoInterceptor) {
            return null;
        }
        Class<? extends Interceptor> statelessInterceptorImplementor = options.getStatelessInterceptorImplementor();
        Supplier<? extends Interceptor> statelessInterceptorImplementorSupplier = options.getStatelessInterceptorImplementorSupplier();
        if (statelessInterceptorImplementor != null && statelessInterceptorImplementorSupplier != null) {
            throw new HibernateException("A session scoped interceptor class or supplier are allowed, but not both!");
        }
        if (statelessInterceptorImplementor != null) {
            try {
                return statelessInterceptorImplementor.newInstance();
            }
            catch (IllegalAccessException | InstantiationException e) {
                throw new HibernateException("Could not supply session-scoped SessionFactory Interceptor", e);
            }
        }
        if (statelessInterceptorImplementorSupplier != null) {
            return statelessInterceptorImplementorSupplier.get();
        }
        return null;
    }

    @Override
    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return this.getSessionFactoryOptions().getCustomEntityDirtinessStrategy();
    }

    @Override
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return this.getSessionFactoryOptions().getCurrentTenantIdentifierResolver();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        LOG.debugf("Serializing: %s", this.getUuid());
        out.defaultWriteObject();
        LOG.trace("Serialized");
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        LOG.trace("Deserializing");
        in.defaultReadObject();
        LOG.debugf("Deserialized: %s", this.getUuid());
    }

    private Object readResolve() throws InvalidObjectException {
        LOG.trace("Resolving serialized SessionFactory");
        return SessionFactoryImpl.locateSessionFactoryOnDeserialization(this.getUuid(), this.name);
    }

    private static SessionFactory locateSessionFactoryOnDeserialization(String uuid, String name) throws InvalidObjectException {
        SessionFactory namedResult;
        SessionFactory uuidResult = SessionFactoryRegistry.INSTANCE.getSessionFactory(uuid);
        if (uuidResult != null) {
            LOG.debugf("Resolved SessionFactory by UUID [%s]", uuid);
            return uuidResult;
        }
        if (name != null && (namedResult = SessionFactoryRegistry.INSTANCE.getNamedSessionFactory(name)) != null) {
            LOG.debugf("Resolved SessionFactory by name [%s]", name);
            return namedResult;
        }
        throw new InvalidObjectException("Could not find a SessionFactory [uuid=" + uuid + ",name=" + name + "]");
    }

    void serialize(ObjectOutputStream oos) throws IOException {
        oos.writeUTF(this.getUuid());
        oos.writeBoolean(this.name != null);
        if (this.name != null) {
            oos.writeUTF(this.name);
        }
    }

    static SessionFactoryImpl deserialize(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        LOG.trace("Deserializing SessionFactory from Session");
        String uuid = ois.readUTF();
        boolean isNamed = ois.readBoolean();
        String name = isNamed ? ois.readUTF() : null;
        return (SessionFactoryImpl)SessionFactoryImpl.locateSessionFactoryOnDeserialization(uuid, name);
    }

    private void maskOutSensitiveInformation(Map<String, Object> props) {
        this.maskOutIfSet(props, "javax.persistence.jdbc.user");
        this.maskOutIfSet(props, "javax.persistence.jdbc.password");
        this.maskOutIfSet(props, "jakarta.persistence.jdbc.user");
        this.maskOutIfSet(props, "jakarta.persistence.jdbc.password");
        this.maskOutIfSet(props, "hibernate.connection.username");
        this.maskOutIfSet(props, "hibernate.connection.password");
    }

    private void maskOutIfSet(Map<String, Object> props, String setting) {
        if (props.containsKey(setting)) {
            props.put(setting, "****");
        }
    }

    private void logIfEmptyCompositesEnabled(Map<String, Object> props) {
        boolean isEmptyCompositesEnabled = ConfigurationHelper.getBoolean("hibernate.create_empty_composites.enabled", props, false);
        if (isEmptyCompositesEnabled) {
            LOG.emptyCompositesEnabled();
        }
    }

    @Override
    public FastSessionServices getFastSessionServices() {
        return this.fastSessionServices;
    }

    private static enum Status {
        OPEN,
        CLOSING,
        CLOSED;

    }

    public static class StatelessSessionBuilderImpl
    implements StatelessSessionBuilder,
    SessionCreationOptions {
        private final SessionFactoryImpl sessionFactory;
        private Connection connection;
        private String tenantIdentifier;
        private boolean queryParametersValidationEnabled;

        public StatelessSessionBuilderImpl(SessionFactoryImpl sessionFactory) {
            this.sessionFactory = sessionFactory;
            CurrentTenantIdentifierResolver tenantIdentifierResolver = sessionFactory.getCurrentTenantIdentifierResolver();
            if (tenantIdentifierResolver != null) {
                this.tenantIdentifier = tenantIdentifierResolver.resolveCurrentTenantIdentifier();
            }
            this.queryParametersValidationEnabled = sessionFactory.getSessionFactoryOptions().isQueryParametersValidationEnabled();
        }

        @Override
        public StatelessSession openStatelessSession() {
            return new StatelessSessionImpl(this.sessionFactory, this);
        }

        public StatelessSessionBuilder connection(Connection connection) {
            this.connection = connection;
            return this;
        }

        public StatelessSessionBuilder tenantIdentifier(String tenantIdentifier) {
            this.tenantIdentifier = tenantIdentifier;
            return this;
        }

        @Override
        public boolean shouldAutoJoinTransactions() {
            return true;
        }

        @Override
        public FlushMode getInitialSessionFlushMode() {
            return FlushMode.ALWAYS;
        }

        @Override
        public boolean shouldAutoClose() {
            return false;
        }

        @Override
        public boolean shouldAutoClear() {
            return false;
        }

        @Override
        public Connection getConnection() {
            return this.connection;
        }

        @Override
        public Interceptor getInterceptor() {
            return SessionFactoryImpl.configuredInterceptor(EmptyInterceptor.INSTANCE, false, this.sessionFactory.getSessionFactoryOptions());
        }

        @Override
        public StatementInspector getStatementInspector() {
            return null;
        }

        @Override
        public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
            return this.sessionFactory.getSessionFactoryOptions().getPhysicalConnectionHandlingMode();
        }

        @Override
        public String getTenantIdentifier() {
            return this.tenantIdentifier;
        }

        @Override
        public TimeZone getJdbcTimeZone() {
            return this.sessionFactory.getSessionFactoryOptions().getJdbcTimeZone();
        }

        @Override
        public List<SessionEventListener> getCustomSessionEventListener() {
            return null;
        }

        @Override
        public SessionOwner getSessionOwner() {
            return null;
        }

        @Override
        public ExceptionMapper getExceptionMapper() {
            return null;
        }

        @Override
        public AfterCompletionAction getAfterCompletionAction() {
            return null;
        }

        @Override
        public ManagedFlushChecker getManagedFlushChecker() {
            return null;
        }

        @Override
        public boolean isQueryParametersValidationEnabled() {
            return this.queryParametersValidationEnabled;
        }

        public StatelessSessionBuilder setQueryParameterValidation(boolean enabled) {
            this.queryParametersValidationEnabled = enabled;
            return this;
        }
    }

    public static class SessionBuilderImpl<T extends SessionBuilder>
    implements SessionBuilderImplementor<T>,
    SessionCreationOptions {
        private static final Logger log = CoreLogging.logger(SessionBuilderImpl.class);
        private final SessionFactoryImpl sessionFactory;
        private Interceptor interceptor;
        private StatementInspector statementInspector;
        private Connection connection;
        private PhysicalConnectionHandlingMode connectionHandlingMode;
        private boolean autoJoinTransactions = true;
        private FlushMode flushMode;
        private boolean autoClose;
        private boolean autoClear;
        private String tenantIdentifier;
        private TimeZone jdbcTimeZone;
        private boolean queryParametersValidationEnabled;
        private boolean explicitNoInterceptor;
        private List<SessionEventListener> listeners;
        private SessionOwnerBehavior sessionOwnerBehavior = SessionOwnerBehavior.LEGACY_NATIVE;

        public SessionBuilderImpl(SessionFactoryImpl sessionFactory) {
            this.sessionFactory = sessionFactory;
            SessionFactoryOptions sessionFactoryOptions = sessionFactory.getSessionFactoryOptions();
            this.statementInspector = sessionFactoryOptions.getStatementInspector();
            this.connectionHandlingMode = sessionFactoryOptions.getPhysicalConnectionHandlingMode();
            this.autoClose = sessionFactoryOptions.isAutoCloseSessionEnabled();
            CurrentTenantIdentifierResolver currentTenantIdentifierResolver = sessionFactory.getCurrentTenantIdentifierResolver();
            if (currentTenantIdentifierResolver != null) {
                this.tenantIdentifier = currentTenantIdentifierResolver.resolveCurrentTenantIdentifier();
            }
            this.jdbcTimeZone = sessionFactoryOptions.getJdbcTimeZone();
            this.queryParametersValidationEnabled = sessionFactoryOptions.isQueryParametersValidationEnabled();
        }

        @Override
        public SessionOwner getSessionOwner() {
            return null;
        }

        @Override
        public ExceptionMapper getExceptionMapper() {
            return this.sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA ? ExceptionMapperLegacyJpaImpl.INSTANCE : null;
        }

        @Override
        public AfterCompletionAction getAfterCompletionAction() {
            return this.sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA ? AfterCompletionActionLegacyJpaImpl.INSTANCE : null;
        }

        @Override
        public ManagedFlushChecker getManagedFlushChecker() {
            return this.sessionOwnerBehavior == SessionOwnerBehavior.LEGACY_JPA ? ManagedFlushCheckerLegacyJpaImpl.INSTANCE : null;
        }

        @Override
        public boolean isQueryParametersValidationEnabled() {
            return this.queryParametersValidationEnabled;
        }

        @Override
        public boolean shouldAutoJoinTransactions() {
            return this.autoJoinTransactions;
        }

        @Override
        public FlushMode getInitialSessionFlushMode() {
            return this.flushMode;
        }

        @Override
        public boolean shouldAutoClose() {
            return this.autoClose;
        }

        @Override
        public boolean shouldAutoClear() {
            return this.autoClear;
        }

        @Override
        public Connection getConnection() {
            return this.connection;
        }

        @Override
        public Interceptor getInterceptor() {
            return SessionFactoryImpl.configuredInterceptor(this.interceptor, this.explicitNoInterceptor, this.sessionFactory.getSessionFactoryOptions());
        }

        @Override
        public StatementInspector getStatementInspector() {
            return this.statementInspector;
        }

        @Override
        public PhysicalConnectionHandlingMode getPhysicalConnectionHandlingMode() {
            return this.connectionHandlingMode;
        }

        @Override
        public String getTenantIdentifier() {
            return this.tenantIdentifier;
        }

        @Override
        public TimeZone getJdbcTimeZone() {
            return this.jdbcTimeZone;
        }

        @Override
        public List<SessionEventListener> getCustomSessionEventListener() {
            return this.listeners;
        }

        @Override
        public Session openSession() {
            log.tracef("Opening Hibernate Session.  tenant=%s", (Object)this.tenantIdentifier);
            return new SessionImpl(this.sessionFactory, this);
        }

        @Override
        public T owner(SessionOwner sessionOwner) {
            throw new UnsupportedOperationException("SessionOwner was long deprecated and this method should no longer be invoked");
        }

        @Override
        public T interceptor(Interceptor interceptor) {
            this.interceptor = interceptor;
            this.explicitNoInterceptor = false;
            return (T)this;
        }

        @Override
        public T noInterceptor() {
            this.interceptor = EmptyInterceptor.INSTANCE;
            this.explicitNoInterceptor = true;
            return (T)this;
        }

        @Override
        public T statementInspector(StatementInspector statementInspector) {
            this.statementInspector = statementInspector;
            return (T)this;
        }

        @Override
        public T connection(Connection connection) {
            this.connection = connection;
            return (T)this;
        }

        @Override
        public T connectionReleaseMode(ConnectionReleaseMode connectionReleaseMode) {
            PhysicalConnectionHandlingMode handlingMode = PhysicalConnectionHandlingMode.interpret(ConnectionAcquisitionMode.AS_NEEDED, connectionReleaseMode);
            this.connectionHandlingMode(handlingMode);
            return (T)this;
        }

        @Override
        public T connectionHandlingMode(PhysicalConnectionHandlingMode connectionHandlingMode) {
            this.connectionHandlingMode = connectionHandlingMode;
            return (T)this;
        }

        @Override
        public T autoJoinTransactions(boolean autoJoinTransactions) {
            this.autoJoinTransactions = autoJoinTransactions;
            return (T)this;
        }

        @Override
        public T autoClose(boolean autoClose) {
            this.autoClose = autoClose;
            return (T)this;
        }

        @Override
        public T autoClear(boolean autoClear) {
            this.autoClear = autoClear;
            return (T)this;
        }

        @Override
        public T flushMode(FlushMode flushMode) {
            this.flushMode = flushMode;
            return (T)this;
        }

        @Override
        public T tenantIdentifier(String tenantIdentifier) {
            this.tenantIdentifier = tenantIdentifier;
            return (T)this;
        }

        @Override
        public T eventListeners(SessionEventListener ... listeners) {
            if (this.listeners == null) {
                this.listeners = this.sessionFactory.getSessionFactoryOptions().getBaselineSessionEventsListenerBuilder().buildBaselineList();
            }
            Collections.addAll(this.listeners, listeners);
            return (T)this;
        }

        @Override
        public T clearEventListeners() {
            if (this.listeners == null) {
                this.listeners = new ArrayList<SessionEventListener>(3);
            } else {
                this.listeners.clear();
            }
            return (T)this;
        }

        @Override
        public T jdbcTimeZone(TimeZone timeZone) {
            this.jdbcTimeZone = timeZone;
            return (T)this;
        }

        @Override
        public T setQueryParameterValidation(boolean enabled) {
            this.queryParametersValidationEnabled = enabled;
            return (T)this;
        }
    }
}

