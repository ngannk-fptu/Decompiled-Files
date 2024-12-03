/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 *  javax.persistence.EntityManager
 *  javax.persistence.PersistenceUnitUtil
 *  javax.persistence.Query
 *  javax.persistence.SynchronizationType
 *  javax.persistence.criteria.CriteriaBuilder
 */
package org.hibernate.engine.spi;

import java.sql.Connection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceUnitUtil;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.StatelessSession;
import org.hibernate.StatelessSessionBuilder;
import org.hibernate.TypeHelper;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cfg.Settings;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.function.SQLFunctionRegistry;
import org.hibernate.engine.ResultSetMappingDefinition;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.query.spi.QueryPlanCache;
import org.hibernate.engine.spi.FilterDefinition;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.spi.EventEngine;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metadata.CollectionMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

public class SessionFactoryDelegatingImpl
implements SessionFactoryImplementor,
SessionFactory {
    private final SessionFactoryImplementor delegate;

    public SessionFactoryDelegatingImpl(SessionFactoryImplementor delegate) {
        this.delegate = delegate;
    }

    protected SessionFactoryImplementor delegate() {
        return this.delegate;
    }

    @Override
    public SessionFactoryOptions getSessionFactoryOptions() {
        return this.delegate.getSessionFactoryOptions();
    }

    @Override
    public SessionBuilderImplementor withOptions() {
        return this.delegate.withOptions();
    }

    @Override
    public Session openSession() throws HibernateException {
        return this.delegate.openSession();
    }

    @Override
    public Session getCurrentSession() throws HibernateException {
        return this.delegate.getCurrentSession();
    }

    @Override
    public StatelessSessionBuilder withStatelessOptions() {
        return this.delegate.withStatelessOptions();
    }

    @Override
    public StatelessSession openStatelessSession() {
        return this.delegate.openStatelessSession();
    }

    @Override
    public StatelessSession openStatelessSession(Connection connection) {
        return this.delegate.openStatelessSession(connection);
    }

    @Override
    public ClassMetadata getClassMetadata(Class entityClass) {
        return this.delegate.getClassMetadata(entityClass);
    }

    @Override
    public ClassMetadata getClassMetadata(String entityName) {
        return this.delegate.getClassMetadata(entityName);
    }

    @Override
    public CollectionMetadata getCollectionMetadata(String roleName) {
        return this.delegate.getCollectionMetadata(roleName);
    }

    @Override
    public Map<String, ClassMetadata> getAllClassMetadata() {
        return this.delegate.getAllClassMetadata();
    }

    @Override
    public Map getAllCollectionMetadata() {
        return this.delegate.getAllCollectionMetadata();
    }

    @Override
    public StatisticsImplementor getStatistics() {
        return this.delegate.getStatistics();
    }

    @Override
    public EventEngine getEventEngine() {
        return this.delegate.getEventEngine();
    }

    @Override
    public void close() throws HibernateException {
        this.delegate.close();
    }

    @Override
    public boolean isClosed() {
        return this.delegate.isClosed();
    }

    @Override
    public CacheImplementor getCache() {
        return this.delegate.getCache();
    }

    public PersistenceUnitUtil getPersistenceUnitUtil() {
        return this.delegate.getPersistenceUnitUtil();
    }

    public void addNamedQuery(String name, Query query) {
        this.delegate.addNamedQuery(name, query);
    }

    public <T> T unwrap(Class<T> cls) {
        return (T)this.delegate.unwrap(cls);
    }

    public <T> void addNamedEntityGraph(String graphName, EntityGraph<T> entityGraph) {
        this.delegate.addNamedEntityGraph(graphName, entityGraph);
    }

    @Override
    public Set getDefinedFilterNames() {
        return this.delegate.getDefinedFilterNames();
    }

    @Override
    public FilterDefinition getFilterDefinition(String filterName) throws HibernateException {
        return this.delegate.getFilterDefinition(filterName);
    }

    @Override
    public boolean containsFetchProfileDefinition(String name) {
        return this.delegate.containsFetchProfileDefinition(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.delegate.getTypeHelper();
    }

    @Override
    @Deprecated
    public TypeResolver getTypeResolver() {
        return this.delegate.getTypeResolver();
    }

    public Map<String, Object> getProperties() {
        return this.delegate.getProperties();
    }

    @Override
    public EntityPersister getEntityPersister(String entityName) throws MappingException {
        return this.delegate.getEntityPersister(entityName);
    }

    @Override
    public Map<String, EntityPersister> getEntityPersisters() {
        return this.delegate.getEntityPersisters();
    }

    @Override
    public CollectionPersister getCollectionPersister(String role) throws MappingException {
        return this.delegate.getCollectionPersister(role);
    }

    @Override
    public Map<String, CollectionPersister> getCollectionPersisters() {
        return this.delegate.getCollectionPersisters();
    }

    @Override
    public JdbcServices getJdbcServices() {
        return this.delegate.getJdbcServices();
    }

    @Override
    public SqlStringGenerationContext getSqlStringGenerationContext() {
        return this.delegate.getSqlStringGenerationContext();
    }

    @Override
    public Dialect getDialect() {
        return this.delegate.getDialect();
    }

    @Override
    public Interceptor getInterceptor() {
        return this.delegate.getInterceptor();
    }

    @Override
    public QueryPlanCache getQueryPlanCache() {
        return this.delegate.getQueryPlanCache();
    }

    @Override
    public Type[] getReturnTypes(String queryString) throws HibernateException {
        return this.delegate.getReturnTypes(queryString);
    }

    @Override
    public String[] getReturnAliases(String queryString) throws HibernateException {
        return this.delegate.getReturnAliases(queryString);
    }

    @Override
    public String[] getImplementors(String className) throws MappingException {
        return this.delegate.getImplementors(className);
    }

    @Override
    public String getImportedClassName(String name) {
        return this.delegate.getImportedClassName(name);
    }

    public RootGraphImplementor findEntityGraphByName(String name) {
        return this.delegate.findEntityGraphByName(name);
    }

    @Override
    public StatisticsImplementor getStatisticsImplementor() {
        return this.delegate.getStatistics();
    }

    @Override
    public NamedQueryDefinition getNamedQuery(String queryName) {
        return this.delegate.getNamedQuery(queryName);
    }

    @Override
    public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
        this.delegate.registerNamedQueryDefinition(name, definition);
    }

    @Override
    public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
        return this.delegate.getNamedSQLQuery(queryName);
    }

    @Override
    public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
        this.delegate.registerNamedSQLQueryDefinition(name, definition);
    }

    @Override
    public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.delegate.getResultSetMapping(name);
    }

    @Override
    public IdentifierGenerator getIdentifierGenerator(String rootEntityName) {
        return this.delegate.getIdentifierGenerator(rootEntityName);
    }

    @Override
    public SQLExceptionConverter getSQLExceptionConverter() {
        return this.delegate.getSQLExceptionConverter();
    }

    @Override
    public SqlExceptionHelper getSQLExceptionHelper() {
        return this.delegate.getSQLExceptionHelper();
    }

    @Override
    public Settings getSettings() {
        return this.delegate.getSettings();
    }

    @Override
    public Session openTemporarySession() throws HibernateException {
        return this.delegate.openTemporarySession();
    }

    @Override
    public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return this.delegate.getCollectionRolesByEntityParticipant(entityName);
    }

    @Override
    public EntityNotFoundDelegate getEntityNotFoundDelegate() {
        return this.delegate.getEntityNotFoundDelegate();
    }

    @Override
    public SQLFunctionRegistry getSqlFunctionRegistry() {
        return this.delegate.getSqlFunctionRegistry();
    }

    @Override
    public FetchProfile getFetchProfile(String name) {
        return this.delegate.getFetchProfile(name);
    }

    @Override
    public ServiceRegistryImplementor getServiceRegistry() {
        return this.delegate.getServiceRegistry();
    }

    @Override
    public void addObserver(SessionFactoryObserver observer) {
        this.delegate.addObserver(observer);
    }

    @Override
    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy() {
        return this.delegate.getCustomEntityDirtinessStrategy();
    }

    @Override
    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver() {
        return this.delegate.getCurrentTenantIdentifierResolver();
    }

    @Override
    public NamedQueryRepository getNamedQueryRepository() {
        return this.delegate.getNamedQueryRepository();
    }

    @Override
    public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
        return this.delegate.iterateEntityNameResolvers();
    }

    @Override
    public FastSessionServices getFastSessionServices() {
        return this.delegate.getFastSessionServices();
    }

    @Override
    public EntityPersister locateEntityPersister(Class byClass) {
        return this.delegate.locateEntityPersister(byClass);
    }

    @Override
    public EntityPersister locateEntityPersister(String byName) {
        return this.delegate.locateEntityPersister(byName);
    }

    @Override
    public SessionFactoryImplementor.DeserializationResolver getDeserializationResolver() {
        return this.delegate.getDeserializationResolver();
    }

    @Override
    public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
        return this.delegate.getIdentifierGeneratorFactory();
    }

    @Override
    public Type getIdentifierType(String className) throws MappingException {
        return this.delegate.getIdentifierType(className);
    }

    @Override
    public String getIdentifierPropertyName(String className) throws MappingException {
        return this.delegate.getIdentifierPropertyName(className);
    }

    @Override
    public Type getReferencedPropertyType(String className, String propertyName) throws MappingException {
        return this.delegate.getReferencedPropertyType(className, propertyName);
    }

    @Override
    public String getUuid() {
        return this.delegate.getUuid();
    }

    @Override
    public String getName() {
        return this.delegate.getName();
    }

    @Override
    public Reference getReference() throws NamingException {
        return this.delegate.getReference();
    }

    @Override
    public <T> List<RootGraphImplementor<? super T>> findEntityGraphsByJavaType(Class<T> entityClass) {
        return this.delegate.findEntityGraphsByJavaType(entityClass);
    }

    public EntityManager createEntityManager() {
        return this.delegate.createEntityManager();
    }

    public EntityManager createEntityManager(Map map) {
        return this.delegate.createEntityManager(map);
    }

    public EntityManager createEntityManager(SynchronizationType synchronizationType) {
        return this.delegate.createEntityManager(synchronizationType);
    }

    public EntityManager createEntityManager(SynchronizationType synchronizationType, Map map) {
        return this.delegate.createEntityManager(synchronizationType, map);
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return this.delegate.getCriteriaBuilder();
    }

    @Override
    public MetamodelImplementor getMetamodel() {
        return this.delegate.getMetamodel();
    }

    public boolean isOpen() {
        return this.delegate.isOpen();
    }

    @Override
    public Type resolveParameterBindType(Object bindValue) {
        return this.delegate.resolveParameterBindType(bindValue);
    }

    @Override
    public Type resolveParameterBindType(Class clazz) {
        return this.delegate.resolveParameterBindType(clazz);
    }
}

