/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 */
package org.hibernate.internal;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.transaction.SystemException;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.ScrollMode;
import org.hibernate.SessionException;
import org.hibernate.StatelessSession;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.transaction.internal.jta.JtaStatusHelper;
import org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform;
import org.hibernate.id.IdentifierGeneratorHelper;
import org.hibernate.internal.AbstractSharedSessionContract;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.tuple.entity.EntityMetamodel;

public class StatelessSessionImpl
extends AbstractSharedSessionContract
implements StatelessSession {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(StatelessSessionImpl.class);
    private static LoadQueryInfluencers NO_INFLUENCERS = new LoadQueryInfluencers(null){

        @Override
        public String getInternalFetchProfile() {
            return null;
        }

        @Override
        public void setInternalFetchProfile(String internalFetchProfile) {
        }
    };
    private final PersistenceContext temporaryPersistenceContext = new StatefulPersistenceContext(this);
    private final boolean connectionProvided;

    public StatelessSessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
        super(factory, options);
        this.connectionProvided = options.getConnection() != null;
    }

    @Override
    public boolean shouldAutoJoinTransaction() {
        return true;
    }

    @Override
    public Serializable insert(Object entity) {
        this.checkOpen();
        return this.insert(null, entity);
    }

    @Override
    public Serializable insert(String entityName, Object entity) {
        boolean substitute;
        this.checkOpen();
        EntityPersister persister = this.getEntityPersister(entityName, entity);
        Serializable id = persister.getIdentifierGenerator().generate(this, entity);
        Object[] state = persister.getPropertyValues(entity);
        if (persister.isVersioned() && (substitute = Versioning.seedVersion(state, persister.getVersionProperty(), persister.getVersionType(), this))) {
            persister.setPropertyValues(entity, state);
        }
        if (id == IdentifierGeneratorHelper.POST_INSERT_INDICATOR) {
            id = persister.insert(state, entity, this);
        } else {
            persister.insert(id, state, entity, this);
        }
        persister.setIdentifier(entity, id, this);
        return id;
    }

    @Override
    public void delete(Object entity) {
        this.checkOpen();
        this.delete(null, entity);
    }

    @Override
    public void delete(String entityName, Object entity) {
        this.checkOpen();
        EntityPersister persister = this.getEntityPersister(entityName, entity);
        Serializable id = persister.getIdentifier(entity, this);
        Object version = persister.getVersion(entity);
        persister.delete(id, version, entity, this);
    }

    @Override
    public void update(Object entity) {
        this.checkOpen();
        this.update(null, entity);
    }

    @Override
    public void update(String entityName, Object entity) {
        Object oldVersion;
        this.checkOpen();
        EntityPersister persister = this.getEntityPersister(entityName, entity);
        Serializable id = persister.getIdentifier(entity, this);
        Object[] state = persister.getPropertyValues(entity);
        if (persister.isVersioned()) {
            oldVersion = persister.getVersion(entity);
            Object newVersion = Versioning.increment(oldVersion, persister.getVersionType(), this);
            Versioning.setVersion(state, newVersion, persister);
            persister.setPropertyValues(entity, state);
        } else {
            oldVersion = null;
        }
        persister.update(id, state, null, false, null, oldVersion, entity, null, this);
    }

    @Override
    public Object get(Class entityClass, Serializable id) {
        return this.get(entityClass.getName(), id);
    }

    @Override
    public Object get(Class entityClass, Serializable id, LockMode lockMode) {
        return this.get(entityClass.getName(), id, lockMode);
    }

    @Override
    public Object get(String entityName, Serializable id) {
        return this.get(entityName, id, LockMode.NONE);
    }

    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode) {
        this.checkOpen();
        Object result = this.getFactory().getMetamodel().entityPersister(entityName).load(id, null, this.getNullSafeLockMode(lockMode), (SharedSessionContractImplementor)this);
        if (this.temporaryPersistenceContext.isLoadFinished()) {
            this.temporaryPersistenceContext.clear();
        }
        return result;
    }

    @Override
    public void refresh(Object entity) {
        this.refresh(this.bestGuessEntityName(entity), entity, LockMode.NONE);
    }

    @Override
    public void refresh(String entityName, Object entity) {
        this.refresh(entityName, entity, LockMode.NONE);
    }

    @Override
    public void refresh(Object entity, LockMode lockMode) {
        this.refresh(this.bestGuessEntityName(entity), entity, lockMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void refresh(String entityName, Object entity, LockMode lockMode) {
        EntityDataAccess cacheAccess;
        EntityPersister persister = this.getEntityPersister(entityName, entity);
        Serializable id = persister.getIdentifier(entity, this);
        if (LOG.isTraceEnabled()) {
            LOG.tracev("Refreshing transient {0}", MessageHelper.infoString(persister, id, this.getFactory()));
        }
        if (persister.canWriteToCache() && (cacheAccess = persister.getCacheAccessStrategy()) != null) {
            Object ck = cacheAccess.generateCacheKey(id, persister, this.getFactory(), this.getTenantIdentifier());
            cacheAccess.evict(ck);
        }
        String previousFetchProfile = this.getLoadQueryInfluencers().getInternalFetchProfile();
        Object result = null;
        try {
            this.getLoadQueryInfluencers().setInternalFetchProfile("refresh");
            result = persister.load(id, entity, this.getNullSafeLockMode(lockMode), (SharedSessionContractImplementor)this);
        }
        finally {
            this.getLoadQueryInfluencers().setInternalFetchProfile(previousFetchProfile);
        }
        UnresolvableObjectException.throwIfNull(result, id, persister.getEntityName());
        if (this.temporaryPersistenceContext.isLoadFinished()) {
            this.temporaryPersistenceContext.clear();
        }
    }

    @Override
    public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
        if (this.getPersistenceContextInternal().isLoadFinished()) {
            throw new SessionException("proxies cannot be fetched by a stateless session");
        }
        return this.get(entityName, id);
    }

    @Override
    public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
        throw new SessionException("collections cannot be fetched by a stateless session");
    }

    @Override
    public Object instantiate(String entityName, Serializable id) throws HibernateException {
        return this.instantiate(this.getFactory().getMetamodel().entityPersister(entityName), id);
    }

    @Override
    public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
        this.checkOpen();
        return persister.instantiate(id, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) throws HibernateException {
        this.checkOpen();
        EntityPersister persister = this.getFactory().getMetamodel().entityPersister(entityName);
        EntityKey entityKey = this.generateEntityKey(id, persister);
        PersistenceContext persistenceContext = this.getPersistenceContext();
        Object loaded = persistenceContext.getEntity(entityKey);
        if (loaded != null) {
            return loaded;
        }
        if (!eager) {
            EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
            BytecodeEnhancementMetadata bytecodeEnhancementMetadata = entityMetamodel.getBytecodeEnhancementMetadata();
            if (bytecodeEnhancementMetadata.isEnhancedForLazyLoading()) {
                if (persister.getEntityMetamodel().getTuplizer().getProxyFactory() != null) {
                    Object proxy = persistenceContext.getProxy(entityKey);
                    if (proxy != null) {
                        if (LOG.isTraceEnabled()) {
                            LOG.trace("Entity proxy found in session cache");
                        }
                        if (LOG.isDebugEnabled() && ((HibernateProxy)proxy).getHibernateLazyInitializer().isUnwrap()) {
                            LOG.debug("Ignoring NO_PROXY to honor laziness");
                        }
                        return persistenceContext.narrowProxy(proxy, persister, entityKey, null);
                    }
                    if (entityMetamodel.hasSubclasses()) {
                        LOG.debugf("Creating a HibernateProxy for to-one association with subclasses to honor laziness", new Object[0]);
                        return this.createProxy(entityKey);
                    }
                    return bytecodeEnhancementMetadata.createEnhancedProxy(entityKey, false, this);
                }
                if (!entityMetamodel.hasSubclasses()) {
                    return bytecodeEnhancementMetadata.createEnhancedProxy(entityKey, false, this);
                }
            } else if (persister.hasProxy()) {
                Object existingProxy = persistenceContext.getProxy(entityKey);
                if (existingProxy != null) {
                    return persistenceContext.narrowProxy(existingProxy, persister, entityKey, null);
                }
                return this.createProxy(entityKey);
            }
        }
        persistenceContext.beforeLoad();
        try {
            Object object = this.get(entityName, id);
            return object;
        }
        finally {
            persistenceContext.afterLoad();
        }
    }

    private Object createProxy(EntityKey entityKey) {
        Object proxy = entityKey.getPersister().createProxy(entityKey.getIdentifier(), this);
        this.getPersistenceContext().addProxy(entityKey, proxy);
        return proxy;
    }

    @Override
    public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List listFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        return this.getFactory().getSessionFactoryOptions().isAutoCloseSessionEnabled();
    }

    @Override
    public boolean shouldAutoClose() {
        return this.isAutoCloseSessionEnabled() && !this.isClosed();
    }

    private boolean isFlushModeNever() {
        return false;
    }

    private void managedClose() {
        if (this.isClosed()) {
            throw new SessionException("Session was already closed!");
        }
        this.close();
    }

    private void managedFlush() {
        this.checkOpen();
        this.getJdbcCoordinator().executeBatch();
    }

    @Override
    public String bestGuessEntityName(Object object) {
        if (object instanceof HibernateProxy) {
            object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
        }
        return this.guessEntityName(object);
    }

    @Override
    public Connection connection() {
        this.checkOpen();
        return this.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpen();
        queryParameters.validateParameters();
        HQLQueryPlan plan = this.getQueryPlan(query, false);
        boolean success = false;
        int result = 0;
        try {
            result = plan.performExecuteUpdate(queryParameters, this);
            success = true;
        }
        finally {
            this.afterOperation(success);
        }
        this.temporaryPersistenceContext.clear();
        return result;
    }

    @Override
    public CacheMode getCacheMode() {
        return CacheMode.IGNORE;
    }

    @Override
    public void setCacheMode(CacheMode cm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setFlushMode(FlushMode fm) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setHibernateFlushMode(FlushMode flushMode) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getDontFlushFromFind() {
        return 0;
    }

    @Override
    public Serializable getContextEntityIdentifier(Object object) {
        this.checkOpen();
        return null;
    }

    public EntityMode getEntityMode() {
        return EntityMode.POJO;
    }

    @Override
    public String guessEntityName(Object entity) throws HibernateException {
        this.checkOpen();
        return entity.getClass().getName();
    }

    @Override
    public EntityPersister getEntityPersister(String entityName, Object object) throws HibernateException {
        this.checkOpen();
        if (entityName == null) {
            return this.getFactory().getMetamodel().entityPersister(this.guessEntityName(object));
        }
        return this.getFactory().getMetamodel().entityPersister(entityName).getSubclassEntityPersister(object, this.getFactory());
    }

    @Override
    public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
        this.checkOpen();
        PersistenceContext persistenceContext = this.getPersistenceContext();
        Object result = persistenceContext.getEntity(key);
        if (result != null) {
            return result;
        }
        Object newObject = this.getInterceptor().getEntity(key.getEntityName(), key.getIdentifier());
        if (newObject != null) {
            persistenceContext.addEntity(key, newObject);
            return newObject;
        }
        return null;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        return this.temporaryPersistenceContext;
    }

    @Override
    public void setAutoClear(boolean enabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Object load(String entityName, Serializable identifier) {
        return null;
    }

    @Override
    public boolean isEventSource() {
        return false;
    }

    public boolean isDefaultReadOnly() {
        return false;
    }

    public void setDefaultReadOnly(boolean readOnly) throws HibernateException {
        if (readOnly) {
            throw new UnsupportedOperationException();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List list(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpen();
        queryParameters.validateParameters();
        HQLQueryPlan plan = this.getQueryPlan(query, false);
        boolean success = false;
        List results = Collections.EMPTY_LIST;
        try {
            results = plan.performList(queryParameters, this);
            success = true;
        }
        finally {
            this.afterOperation(success);
        }
        this.temporaryPersistenceContext.clear();
        return results;
    }

    public void afterOperation(boolean success) {
        if (!this.isTransactionInProgress()) {
            this.getJdbcCoordinator().afterTransaction();
        }
    }

    @Override
    public Criteria createCriteria(Class persistentClass, String alias) {
        this.checkOpen();
        return new CriteriaImpl(persistentClass.getName(), alias, this);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias) {
        this.checkOpen();
        return new CriteriaImpl(entityName, alias, this);
    }

    @Override
    public Criteria createCriteria(Class persistentClass) {
        this.checkOpen();
        return new CriteriaImpl(persistentClass.getName(), this);
    }

    @Override
    public Criteria createCriteria(String entityName) {
        this.checkOpen();
        return new CriteriaImpl(entityName, this);
    }

    @Override
    public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
        CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
        this.checkOpen();
        String entityName = criteriaImpl.getEntityOrClassName();
        CriteriaLoader loader = new CriteriaLoader(this.getOuterJoinLoadable(entityName), this.getFactory(), criteriaImpl, entityName, this.getLoadQueryInfluencers());
        return loader.scroll(this, scrollMode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List list(Criteria criteria) throws HibernateException {
        CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
        this.checkOpen();
        String[] implementors = this.getFactory().getMetamodel().getImplementors(criteriaImpl.getEntityOrClassName());
        int size = implementors.length;
        CriteriaLoader[] loaders = new CriteriaLoader[size];
        for (int i = 0; i < size; ++i) {
            loaders[i] = new CriteriaLoader(this.getOuterJoinLoadable(implementors[i]), this.getFactory(), criteriaImpl, implementors[i], this.getLoadQueryInfluencers());
        }
        List results = Collections.EMPTY_LIST;
        boolean success = false;
        try {
            for (int i = 0; i < size; ++i) {
                List currentResults = loaders[i].list(this);
                currentResults.addAll(results);
                results = currentResults;
            }
            success = true;
        }
        finally {
            this.afterOperation(success);
        }
        this.temporaryPersistenceContext.clear();
        return results;
    }

    private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
        EntityPersister persister = this.getFactory().getMetamodel().entityPersister(entityName);
        if (!(persister instanceof OuterJoinLoadable)) {
            throw new MappingException("class persister is not OuterJoinLoadable: " + entityName);
        }
        return (OuterJoinLoadable)persister;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        List results;
        this.checkOpen();
        CustomLoader loader = new CustomLoader(customQuery, this.getFactory());
        boolean success = false;
        try {
            results = loader.list(this, queryParameters);
            success = true;
        }
        finally {
            this.afterOperation(success);
        }
        this.temporaryPersistenceContext.clear();
        return results;
    }

    @Override
    public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) throws HibernateException {
        this.checkOpen();
        CustomLoader loader = new CustomLoader(customQuery, this.getFactory());
        return loader.scroll(queryParameters, this);
    }

    @Override
    public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpen();
        HQLQueryPlan plan = this.getQueryPlan(query, false);
        return plan.performScroll(queryParameters, this);
    }

    @Override
    public void afterScrollOperation() {
        this.temporaryPersistenceContext.clear();
    }

    @Override
    public void flush() {
    }

    @Override
    public LoadQueryInfluencers getLoadQueryInfluencers() {
        return NO_INFLUENCERS;
    }

    @Override
    public PersistenceContext getPersistenceContextInternal() {
        return this.temporaryPersistenceContext;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int executeNativeUpdate(NativeSQLQuerySpecification nativeSQLQuerySpecification, QueryParameters queryParameters) throws HibernateException {
        this.checkOpen();
        queryParameters.validateParameters();
        NativeSQLQueryPlan plan = this.getNativeQueryPlan(nativeSQLQuerySpecification);
        boolean success = false;
        int result = 0;
        try {
            result = plan.performExecuteUpdate(queryParameters, this);
            success = true;
        }
        finally {
            this.afterOperation(success);
        }
        this.temporaryPersistenceContext.clear();
        return result;
    }

    @Override
    public void afterTransactionBegin() {
    }

    @Override
    public void beforeTransactionCompletion() {
        this.flushBeforeTransactionCompletion();
    }

    @Override
    public void afterTransactionCompletion(boolean successful, boolean delayed) {
        if (this.shouldAutoClose() && !this.isClosed()) {
            this.managedClose();
        }
    }

    @Override
    public boolean isTransactionInProgress() {
        return this.connectionProvided || super.isTransactionInProgress();
    }

    @Override
    public void flushBeforeTransactionCompletion() {
        boolean flush = false;
        try {
            flush = !this.isClosed() && !this.isFlushModeNever() && !JtaStatusHelper.isRollback(this.getJtaPlatform().getCurrentStatus());
        }
        catch (SystemException se) {
            throw new HibernateException("could not determine transaction status in beforeCompletion()", se);
        }
        if (flush) {
            this.managedFlush();
        }
    }

    private JtaPlatform getJtaPlatform() {
        return this.getFactory().getServiceRegistry().getService(JtaPlatform.class);
    }

    private LockMode getNullSafeLockMode(LockMode lockMode) {
        return lockMode == null ? LockMode.NONE : lockMode;
    }
}

