/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityGraph
 */
package org.hibernate.engine.spi;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityGraph;
import org.hibernate.CustomEntityDirtinessStrategy;
import org.hibernate.EntityNameResolver;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.model.relational.SqlStringGenerationContext;
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
import org.hibernate.engine.spi.Mapping;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.engine.spi.SessionBuilderImplementor;
import org.hibernate.event.spi.EventEngine;
import org.hibernate.exception.spi.SQLExceptionConverter;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.EntityNotFoundDelegate;
import org.hibernate.query.spi.NamedQueryRepository;
import org.hibernate.query.spi.QueryParameterBindingTypeResolver;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeResolver;

public interface SessionFactoryImplementor
extends Mapping,
SessionFactory,
QueryParameterBindingTypeResolver {
    public String getUuid();

    public String getName();

    @Override
    public SessionBuilderImplementor withOptions();

    public Session openTemporarySession() throws HibernateException;

    @Override
    public CacheImplementor getCache();

    @Override
    public StatisticsImplementor getStatistics();

    public ServiceRegistryImplementor getServiceRegistry();

    public EventEngine getEventEngine();

    @Deprecated
    public Interceptor getInterceptor();

    @Deprecated
    public QueryPlanCache getQueryPlanCache();

    @Deprecated
    public NamedQueryRepository getNamedQueryRepository();

    public FetchProfile getFetchProfile(String var1);

    @Deprecated
    public TypeResolver getTypeResolver();

    public IdentifierGenerator getIdentifierGenerator(String var1);

    public EntityNotFoundDelegate getEntityNotFoundDelegate();

    public SQLFunctionRegistry getSqlFunctionRegistry();

    public void addObserver(SessionFactoryObserver var1);

    public CustomEntityDirtinessStrategy getCustomEntityDirtinessStrategy();

    public CurrentTenantIdentifierResolver getCurrentTenantIdentifierResolver();

    @Deprecated
    default public Iterable<EntityNameResolver> iterateEntityNameResolvers() {
        return this.getMetamodel().getEntityNameResolvers();
    }

    public FastSessionServices getFastSessionServices();

    public DeserializationResolver getDeserializationResolver();

    @Deprecated
    default public Type[] getReturnTypes(String queryString) {
        throw new UnsupportedOperationException("Concept of query return org.hibernate.type.Types is no longer supported");
    }

    @Deprecated
    default public String[] getReturnAliases(String queryString) {
        throw new UnsupportedOperationException("Access to of query return aliases via Sessionfactory is no longer supported");
    }

    @Deprecated
    default public StatisticsImplementor getStatisticsImplementor() {
        return this.getStatistics();
    }

    @Deprecated
    default public NamedQueryDefinition getNamedQuery(String queryName) {
        return this.getNamedQueryRepository().getNamedQueryDefinition(queryName);
    }

    @Deprecated
    default public void registerNamedQueryDefinition(String name, NamedQueryDefinition definition) {
        this.getNamedQueryRepository().registerNamedQueryDefinition(name, definition);
    }

    @Deprecated
    default public NamedSQLQueryDefinition getNamedSQLQuery(String queryName) {
        return this.getNamedQueryRepository().getNamedSQLQueryDefinition(queryName);
    }

    @Deprecated
    default public void registerNamedSQLQueryDefinition(String name, NamedSQLQueryDefinition definition) {
        this.getNamedQueryRepository().registerNamedSQLQueryDefinition(name, definition);
    }

    @Deprecated
    default public ResultSetMappingDefinition getResultSetMapping(String name) {
        return this.getNamedQueryRepository().getResultSetMappingDefinition(name);
    }

    public JdbcServices getJdbcServices();

    @Deprecated
    default public Dialect getDialect() {
        return this.getJdbcServices().getDialect();
    }

    public SqlStringGenerationContext getSqlStringGenerationContext();

    @Deprecated
    default public SQLExceptionConverter getSQLExceptionConverter() {
        return this.getJdbcServices().getSqlExceptionHelper().getSqlExceptionConverter();
    }

    @Deprecated
    default public SqlExceptionHelper getSQLExceptionHelper() {
        return this.getJdbcServices().getSqlExceptionHelper();
    }

    @Deprecated
    public Settings getSettings();

    @Override
    public MetamodelImplementor getMetamodel();

    @Override
    default public <T> List<EntityGraph<? super T>> findEntityGraphsByType(Class<T> entityClass) {
        return this.findEntityGraphsByJavaType(entityClass);
    }

    public <T> List<RootGraphImplementor<? super T>> findEntityGraphsByJavaType(Class<T> var1);

    public RootGraphImplementor<?> findEntityGraphByName(String var1);

    @Deprecated
    default public EntityPersister getEntityPersister(String entityName) throws MappingException {
        return this.getMetamodel().entityPersister(entityName);
    }

    @Deprecated
    default public Map<String, EntityPersister> getEntityPersisters() {
        return this.getMetamodel().entityPersisters();
    }

    @Deprecated
    default public CollectionPersister getCollectionPersister(String role) throws MappingException {
        return this.getMetamodel().collectionPersister(role);
    }

    @Deprecated
    default public Map<String, CollectionPersister> getCollectionPersisters() {
        return this.getMetamodel().collectionPersisters();
    }

    @Deprecated
    default public Set<String> getCollectionRolesByEntityParticipant(String entityName) {
        return this.getMetamodel().getCollectionRolesByEntityParticipant(entityName);
    }

    @Deprecated
    default public EntityPersister locateEntityPersister(Class byClass) {
        return this.getMetamodel().locateEntityPersister(byClass);
    }

    @Deprecated
    default public EntityPersister locateEntityPersister(String byName) {
        return this.getMetamodel().locateEntityPersister(byName);
    }

    @Deprecated
    default public String[] getImplementors(String entityName) {
        return this.getMetamodel().getImplementors(entityName);
    }

    @Deprecated
    default public String getImportedClassName(String name) {
        return this.getMetamodel().getImportedClassName(name);
    }

    public static interface DeserializationResolver<T extends SessionFactoryImplementor>
    extends Serializable {
        public T resolve();
    }
}

