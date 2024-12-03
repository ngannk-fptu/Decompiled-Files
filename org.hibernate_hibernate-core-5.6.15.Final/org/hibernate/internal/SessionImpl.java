/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.CacheRetrieveMode
 *  javax.persistence.CacheStoreMode
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityNotFoundException
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 *  javax.persistence.PersistenceException
 *  javax.persistence.StoredProcedureQuery
 *  javax.persistence.TransactionRequiredException
 *  javax.persistence.criteria.CriteriaBuilder
 */
package org.hibernate.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import javax.persistence.CacheRetrieveMode;
import javax.persistence.CacheStoreMode;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;
import javax.persistence.criteria.CriteriaBuilder;
import org.hibernate.CacheMode;
import org.hibernate.Criteria;
import org.hibernate.FetchNotFoundException;
import org.hibernate.Filter;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.IdentifierLoadAccess;
import org.hibernate.JDBCException;
import org.hibernate.LobHelper;
import org.hibernate.LockMode;
import org.hibernate.LockOptions;
import org.hibernate.MappingException;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.NaturalIdLoadAccess;
import org.hibernate.ObjectDeletedException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.QueryException;
import org.hibernate.ReplicationMode;
import org.hibernate.ScrollMode;
import org.hibernate.Session;
import org.hibernate.SessionEventListener;
import org.hibernate.SessionException;
import org.hibernate.SharedSessionBuilder;
import org.hibernate.SimpleNaturalIdLoadAccess;
import org.hibernate.Transaction;
import org.hibernate.TransientObjectException;
import org.hibernate.TypeHelper;
import org.hibernate.TypeMismatchException;
import org.hibernate.UnknownProfileException;
import org.hibernate.UnresolvableObjectException;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.spi.BytecodeEnhancementMetadata;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.criterion.NaturalIdentifier;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.jdbc.LobCreator;
import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.query.spi.FilterQueryPlan;
import org.hibernate.engine.query.spi.HQLQueryPlan;
import org.hibernate.engine.query.spi.NativeSQLQueryPlan;
import org.hibernate.engine.query.spi.sql.NativeSQLQuerySpecification;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.EffectiveEntityGraph;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.NamedQueryDefinition;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.QueryParameters;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.engine.spi.TypedValue;
import org.hibernate.engine.transaction.spi.TransactionImplementor;
import org.hibernate.engine.transaction.spi.TransactionObserver;
import org.hibernate.event.spi.AutoFlushEvent;
import org.hibernate.event.spi.AutoFlushEventListener;
import org.hibernate.event.spi.ClearEvent;
import org.hibernate.event.spi.ClearEventListener;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.DirtyCheckEvent;
import org.hibernate.event.spi.DirtyCheckEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EvictEvent;
import org.hibernate.event.spi.EvictEventListener;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.event.spi.FlushEventListener;
import org.hibernate.event.spi.InitializeCollectionEvent;
import org.hibernate.event.spi.InitializeCollectionEventListener;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.event.spi.LockEvent;
import org.hibernate.event.spi.LockEventListener;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.RefreshEvent;
import org.hibernate.event.spi.RefreshEventListener;
import org.hibernate.event.spi.ReplicateEvent;
import org.hibernate.event.spi.ReplicateEventListener;
import org.hibernate.event.spi.ResolveNaturalIdEvent;
import org.hibernate.event.spi.ResolveNaturalIdEventListener;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.event.spi.SaveOrUpdateEventListener;
import org.hibernate.graph.GraphSemantic;
import org.hibernate.graph.RootGraph;
import org.hibernate.graph.internal.RootGraphImpl;
import org.hibernate.graph.spi.RootGraphImplementor;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.internal.AbstractSessionImpl;
import org.hibernate.internal.CriteriaImpl;
import org.hibernate.internal.EntityManagerMessageLogger;
import org.hibernate.internal.ExceptionMapperStandardImpl;
import org.hibernate.internal.FilterImpl;
import org.hibernate.internal.HEMLogging;
import org.hibernate.internal.SessionCreationOptions;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.internal.SharedSessionCreationOptions;
import org.hibernate.internal.log.DeprecationLogger;
import org.hibernate.internal.util.NullnessHelper;
import org.hibernate.jpa.HibernateEntityManagerFactory;
import org.hibernate.jpa.internal.util.CacheModeHelper;
import org.hibernate.jpa.internal.util.ConfigurationHelper;
import org.hibernate.jpa.internal.util.FlushModeTypeHelper;
import org.hibernate.jpa.internal.util.LockModeTypeHelper;
import org.hibernate.jpa.internal.util.LockOptionsHelper;
import org.hibernate.jpa.spi.HibernateEntityManagerImplementor;
import org.hibernate.loader.criteria.CriteriaLoader;
import org.hibernate.loader.custom.CustomLoader;
import org.hibernate.loader.custom.CustomQuery;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.MultiLoadOptions;
import org.hibernate.persister.entity.OuterJoinLoadable;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureCallMemento;
import org.hibernate.procedure.UnknownSqlResultSetMappingException;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.query.ImmutableEntityUpdateQueryHandlingMode;
import org.hibernate.query.Query;
import org.hibernate.query.internal.CollectionFilterImpl;
import org.hibernate.query.spi.ScrollableResultsImplementor;
import org.hibernate.resource.transaction.TransactionRequiredForJoinException;
import org.hibernate.resource.transaction.backend.jta.internal.JtaTransactionCoordinatorImpl;
import org.hibernate.resource.transaction.spi.TransactionCoordinator;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.hibernate.stat.SessionStatistics;
import org.hibernate.stat.internal.SessionStatisticsImpl;
import org.hibernate.stat.spi.StatisticsImplementor;

public class SessionImpl
extends AbstractSessionImpl
implements EventSource,
SessionImplementor,
HibernateEntityManagerImplementor {
    private static final EntityManagerMessageLogger log = HEMLogging.messageLogger(SessionImpl.class);
    private Map<String, Object> properties;
    private transient ActionQueue actionQueue;
    private transient StatefulPersistenceContext persistenceContext = this.createPersistenceContext();
    private transient LoadQueryInfluencers loadQueryInfluencers;
    private LockOptions lockOptions;
    private boolean autoClear;
    private boolean autoClose;
    private boolean queryParametersValidationEnabled;
    private transient int dontFlushFromFind;
    private transient LoadEvent loadEvent;
    private transient TransactionObserver transactionObserver;
    private transient boolean isEnforcingFetchGraph;
    private transient LobHelperImpl lobHelper;

    public SessionImpl(SessionFactoryImpl factory, SessionCreationOptions options) {
        super(factory, options);
        this.actionQueue = this.createActionQueue();
        this.autoClear = options.shouldAutoClear();
        this.autoClose = options.shouldAutoClose();
        this.queryParametersValidationEnabled = options.isQueryParametersValidationEnabled();
        if (options instanceof SharedSessionCreationOptions) {
            SharedSessionCreationOptions sharedOptions = (SharedSessionCreationOptions)options;
            ActionQueue.TransactionCompletionProcesses transactionCompletionProcesses = sharedOptions.getTransactionCompletionProcesses();
            if (sharedOptions.isTransactionCoordinatorShared() && transactionCompletionProcesses != null) {
                this.actionQueue.setTransactionCompletionProcesses(transactionCompletionProcesses, true);
            }
        }
        this.loadQueryInfluencers = new LoadQueryInfluencers(factory);
        StatisticsImplementor statistics = factory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.openSession();
        }
        if (this.properties != null) {
            LockOptionsHelper.applyPropertiesToLockOptions(this.properties, this::getLockOptionsForWrite);
        }
        this.getSession().setCacheMode(this.fastSessionServices.initialSessionCacheMode);
        this.getTransactionCoordinator().pulse();
        if (this.getHibernateFlushMode() == null) {
            FlushMode initialMode;
            if (this.properties == null) {
                initialMode = this.fastSessionServices.initialSessionFlushMode;
            } else {
                Object setting = NullnessHelper.coalesceSuppliedValues(() -> this.getSessionProperty("org.hibernate.flushMode"), () -> {
                    Object oldSetting = this.getSessionProperty("org.hibernate.flushMode");
                    return oldSetting;
                });
                initialMode = ConfigurationHelper.getFlushMode(this.getSessionProperty("org.hibernate.flushMode"), FlushMode.AUTO);
            }
            this.getSession().setHibernateFlushMode(initialMode);
        }
        if (log.isTraceEnabled()) {
            log.tracef("Opened Session [%s] at timestamp: %s", this.getSessionIdentifier(), this.getTimestamp());
        }
    }

    protected StatefulPersistenceContext createPersistenceContext() {
        return new StatefulPersistenceContext(this);
    }

    protected ActionQueue createActionQueue() {
        return new ActionQueue(this);
    }

    private LockOptions getLockOptionsForRead() {
        return this.lockOptions == null ? this.fastSessionServices.defaultLockOptions : this.lockOptions;
    }

    private LockOptions getLockOptionsForWrite() {
        if (this.lockOptions == null) {
            this.lockOptions = new LockOptions();
        }
        return this.lockOptions;
    }

    @Override
    protected void applyQuerySettingsAndHints(Query query) {
        Object jakartaLockTimeout;
        Object jpaLockTimeout;
        Object jakartaQueryTimeout;
        Object queryTimeout;
        LockOptions lockOptionsForRead = this.getLockOptionsForRead();
        if (lockOptionsForRead.getLockMode() != LockMode.NONE) {
            query.setLockMode(this.getLockMode((Object)lockOptionsForRead.getLockMode()));
        }
        if ((queryTimeout = this.getSessionProperty("javax.persistence.query.timeout")) != null) {
            query.setHint("javax.persistence.query.timeout", queryTimeout);
        }
        if ((jakartaQueryTimeout = this.getSessionProperty("jakarta.persistence.query.timeout")) != null) {
            query.setHint("jakarta.persistence.query.timeout", jakartaQueryTimeout);
        }
        Object lockTimeout = (jpaLockTimeout = this.getSessionProperty("javax.persistence.lock.timeout")) == null ? this.getSessionProperty("jakarta.persistence.lock.timeout") : (Integer.valueOf(-1).equals(jpaLockTimeout) ? ((jakartaLockTimeout = this.getSessionProperty("jakarta.persistence.lock.timeout")) == null ? jpaLockTimeout : jakartaLockTimeout) : jpaLockTimeout);
        if (lockTimeout != null) {
            query.setHint("javax.persistence.lock.timeout", lockTimeout);
        }
    }

    private Object getSessionProperty(String name) {
        if (this.properties == null) {
            return this.fastSessionServices.defaultSessionProperties.get(name);
        }
        return this.properties.get(name);
    }

    @Override
    public SharedSessionBuilder sessionWithOptions() {
        return new SharedSessionBuilderImpl(this);
    }

    @Override
    public void clear() {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        try {
            this.internalClear();
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    private void internalClear() {
        this.persistenceContext.clear();
        this.actionQueue.clear();
        this.fastSessionServices.eventListenerGroup_CLEAR.fireLazyEventOnEachListener(this::createClearEvent, ClearEventListener::onClear);
    }

    private ClearEvent createClearEvent() {
        return new ClearEvent(this);
    }

    @Override
    public void close() throws HibernateException {
        if (this.isClosed()) {
            if (this.getFactory().getSessionFactoryOptions().getJpaCompliance().isJpaClosedComplianceEnabled()) {
                throw new IllegalStateException("Illegal call to #close() on already closed Session/EntityManager");
            }
            log.trace("Already closed");
            return;
        }
        this.closeWithoutOpenChecks();
    }

    public void closeWithoutOpenChecks() throws HibernateException {
        SessionFactoryImplementor sessionFactory;
        if (log.isTraceEnabled()) {
            log.tracef("Closing session [%s]", this.getSessionIdentifier());
        }
        if ((sessionFactory = this.getSessionFactory()).getSessionFactoryOptions().isJpaBootstrap()) {
            this.checkSessionFactoryOpen();
            this.checkOpenOrWaitingForAutoClose();
            if (this.fastSessionServices.discardOnClose || !this.isTransactionInProgress(false)) {
                super.close();
            } else {
                this.prepareForAutoClose();
            }
        } else {
            super.close();
        }
        StatisticsImplementor statistics = sessionFactory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.closeSession();
        }
    }

    private boolean isTransactionInProgress(boolean isMarkedRollbackConsideredActive) {
        if (this.waitingForAutoClose) {
            return this.getSessionFactory().isOpen() && this.getTransactionCoordinator().isTransactionActive(isMarkedRollbackConsideredActive);
        }
        return !this.isClosed() && this.getTransactionCoordinator().isTransactionActive(isMarkedRollbackConsideredActive);
    }

    @Override
    protected boolean shouldCloseJdbcCoordinatorOnClose(boolean isTransactionCoordinatorShared) {
        if (!isTransactionCoordinatorShared) {
            return super.shouldCloseJdbcCoordinatorOnClose(isTransactionCoordinatorShared);
        }
        ActionQueue actionQueue = this.getActionQueue();
        if (actionQueue.hasBeforeTransactionActions() || actionQueue.hasAfterTransactionActions()) {
            log.warn("On close, shared Session had before/after transaction actions that have not yet been processed");
        }
        return false;
    }

    @Override
    public boolean isAutoCloseSessionEnabled() {
        return this.autoClose;
    }

    @Override
    public boolean isQueryParametersValidationEnabled() {
        return this.queryParametersValidationEnabled;
    }

    @Override
    public boolean isOpen() {
        this.checkSessionFactoryOpen();
        this.checkTransactionSynchStatus();
        try {
            return !this.isClosed();
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
    }

    protected void checkSessionFactoryOpen() {
        if (!this.getFactory().isOpen()) {
            log.debug("Forcing Session/EntityManager closed as SessionFactory/EntityManagerFactory has been closed");
            this.setClosed();
        }
    }

    private void managedFlush() {
        if (this.isClosed() && !this.waitingForAutoClose) {
            log.trace("Skipping auto-flush due to session closed");
            return;
        }
        log.trace("Automatically flushing session");
        this.doFlush();
    }

    @Override
    public boolean shouldAutoClose() {
        if (this.waitingForAutoClose) {
            return true;
        }
        if (this.isClosed()) {
            return false;
        }
        return this.isAutoCloseSessionEnabled();
    }

    private void managedClose() {
        log.trace("Automatically closing session");
        this.closeWithoutOpenChecks();
    }

    @Override
    public Connection connection() throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        return this.getJdbcCoordinator().getLogicalConnection().getPhysicalConnection();
    }

    @Override
    public Connection disconnect() throws HibernateException {
        this.checkOpen();
        log.debug("Disconnecting session");
        return this.getJdbcCoordinator().getLogicalConnection().manualDisconnect();
    }

    @Override
    public void reconnect(Connection conn) throws HibernateException {
        this.checkOpen();
        log.debug("Reconnecting session");
        this.checkTransactionSynchStatus();
        this.getJdbcCoordinator().getLogicalConnection().manualReconnect(conn);
    }

    @Override
    public void setAutoClear(boolean enabled) {
        this.checkOpenOrWaitingForAutoClose();
        this.autoClear = enabled;
    }

    public void afterOperation(boolean success) {
        if (!this.isTransactionInProgress()) {
            this.getJdbcCoordinator().afterTransaction();
        }
    }

    @Override
    public void addEventListeners(SessionEventListener ... listeners) {
        this.getEventListenerManager().addListener(listeners);
    }

    @Override
    protected void cleanupOnClose() {
        this.persistenceContext.clear();
    }

    @Override
    public LockMode getCurrentLockMode(Object object) throws HibernateException {
        this.checkOpen();
        this.checkTransactionSynchStatus();
        if (object == null) {
            throw new NullPointerException("null object passed to getCurrentLockMode()");
        }
        if (object instanceof HibernateProxy && (object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation(this)) == null) {
            return LockMode.NONE;
        }
        EntityEntry e = this.persistenceContext.getEntry(object);
        if (e == null) {
            throw new TransientObjectException("Given object not associated with the session");
        }
        if (e.getStatus() != Status.MANAGED) {
            throw new ObjectDeletedException("The given object was deleted", e.getId(), e.getPersister().getEntityName());
        }
        return e.getLockMode();
    }

    @Override
    public Object getEntityUsingInterceptor(EntityKey key) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        Object result = this.persistenceContext.getEntity(key);
        if (result == null) {
            Object newObject = this.getInterceptor().getEntity(key.getEntityName(), key.getIdentifier());
            if (newObject != null) {
                this.lock(newObject, LockMode.NONE);
            }
            return newObject;
        }
        return result;
    }

    protected void checkNoUnresolvedActionsBeforeOperation() {
        if (this.persistenceContext.getCascadeLevel() == 0 && this.actionQueue.hasUnresolvedEntityInsertActions()) {
            throw new IllegalStateException("There are delayed insert actions before operation as cascade level 0.");
        }
    }

    protected void checkNoUnresolvedActionsAfterOperation() {
        if (this.persistenceContext.getCascadeLevel() == 0) {
            this.actionQueue.checkNoUnresolvedActionsAfterOperation();
        }
        this.delayedAfterCompletion();
    }

    @Override
    protected void delayedAfterCompletion() {
        if (this.getTransactionCoordinator() instanceof JtaTransactionCoordinatorImpl) {
            ((JtaTransactionCoordinatorImpl)this.getTransactionCoordinator()).getSynchronizationCallbackCoordinator().processAnyDelayedAfterCompletion();
        }
    }

    @Override
    public void saveOrUpdate(Object object) throws HibernateException {
        this.saveOrUpdate(null, object);
    }

    @Override
    public void saveOrUpdate(String entityName, Object obj) throws HibernateException {
        this.fireSaveOrUpdate(new SaveOrUpdateEvent(entityName, obj, this));
    }

    private void fireSaveOrUpdate(SaveOrUpdateEvent event) {
        this.checkOpen();
        this.checkTransactionSynchStatus();
        this.checkNoUnresolvedActionsBeforeOperation();
        this.fastSessionServices.eventListenerGroup_SAVE_UPDATE.fireEventOnEachListener(event, SaveOrUpdateEventListener::onSaveOrUpdate);
        this.checkNoUnresolvedActionsAfterOperation();
    }

    @Override
    public Serializable save(Object obj) throws HibernateException {
        return this.save(null, obj);
    }

    @Override
    public Serializable save(String entityName, Object object) throws HibernateException {
        return this.fireSave(new SaveOrUpdateEvent(entityName, object, this));
    }

    private Serializable fireSave(SaveOrUpdateEvent event) {
        this.checkOpen();
        this.checkTransactionSynchStatus();
        this.checkNoUnresolvedActionsBeforeOperation();
        this.fastSessionServices.eventListenerGroup_SAVE.fireEventOnEachListener(event, SaveOrUpdateEventListener::onSaveOrUpdate);
        this.checkNoUnresolvedActionsAfterOperation();
        return event.getResultId();
    }

    @Override
    public void update(Object obj) throws HibernateException {
        this.update(null, obj);
    }

    @Override
    public void update(String entityName, Object object) throws HibernateException {
        this.fireUpdate(new SaveOrUpdateEvent(entityName, object, this));
    }

    private void fireUpdate(SaveOrUpdateEvent event) {
        this.checkOpen();
        this.checkTransactionSynchStatus();
        this.checkNoUnresolvedActionsBeforeOperation();
        this.fastSessionServices.eventListenerGroup_UPDATE.fireEventOnEachListener(event, SaveOrUpdateEventListener::onSaveOrUpdate);
        this.checkNoUnresolvedActionsAfterOperation();
    }

    @Override
    public void lock(String entityName, Object object, LockMode lockMode) throws HibernateException {
        this.fireLock(new LockEvent(entityName, object, lockMode, (EventSource)this));
    }

    @Override
    public Session.LockRequest buildLockRequest(LockOptions lockOptions) {
        return new LockRequestImpl(lockOptions);
    }

    @Override
    public void lock(Object object, LockMode lockMode) throws HibernateException {
        this.fireLock(new LockEvent(object, lockMode, (EventSource)this));
    }

    private void fireLock(String entityName, Object object, LockOptions options) {
        this.fireLock(new LockEvent(entityName, object, options, (EventSource)this));
    }

    private void fireLock(Object object, LockOptions options) {
        this.fireLock(new LockEvent(object, options, (EventSource)this));
    }

    private void fireLock(LockEvent event) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.fastSessionServices.eventListenerGroup_LOCK.fireEventOnEachListener(event, LockEventListener::onLock);
        this.delayedAfterCompletion();
    }

    @Override
    public void persist(String entityName, Object object) throws HibernateException {
        this.checkOpen();
        this.firePersist(new PersistEvent(entityName, object, this));
    }

    @Override
    public void persist(Object object) throws HibernateException {
        this.checkOpen();
        this.firePersist(new PersistEvent(null, object, this));
    }

    @Override
    public void persist(String entityName, Object object, Map copiedAlready) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.firePersist(copiedAlready, new PersistEvent(entityName, object, this));
    }

    private void firePersist(PersistEvent event) {
        try {
            this.checkTransactionSynchStatus();
            this.checkNoUnresolvedActionsBeforeOperation();
            this.fastSessionServices.eventListenerGroup_PERSIST.fireEventOnEachListener(event, PersistEventListener::onPersist);
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage()));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            try {
                this.checkNoUnresolvedActionsAfterOperation();
            }
            catch (RuntimeException e) {
                throw this.getExceptionConverter().convert(e);
            }
        }
    }

    private void firePersist(Map copiedAlready, PersistEvent event) {
        this.pulseTransactionCoordinator();
        try {
            this.fastSessionServices.eventListenerGroup_PERSIST.fireEventOnEachListener(event, copiedAlready, PersistEventListener::onPersist);
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage()));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    @Override
    public void persistOnFlush(String entityName, Object object, Map copiedAlready) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        PersistEvent event = new PersistEvent(entityName, object, this);
        this.fastSessionServices.eventListenerGroup_PERSIST_ONFLUSH.fireEventOnEachListener(event, copiedAlready, PersistEventListener::onPersist);
        this.delayedAfterCompletion();
    }

    @Override
    public Object merge(String entityName, Object object) throws HibernateException {
        this.checkOpen();
        return this.fireMerge(new MergeEvent(entityName, object, this));
    }

    @Override
    public Object merge(Object object) throws HibernateException {
        this.checkOpen();
        return this.fireMerge(new MergeEvent(null, object, this));
    }

    @Override
    public void merge(String entityName, Object object, Map copiedAlready) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.fireMerge(copiedAlready, new MergeEvent(entityName, object, this));
    }

    private Object fireMerge(MergeEvent event) {
        try {
            this.checkTransactionSynchStatus();
            this.checkNoUnresolvedActionsBeforeOperation();
            this.fastSessionServices.eventListenerGroup_MERGE.fireEventOnEachListener(event, MergeEventListener::onMerge);
            this.checkNoUnresolvedActionsAfterOperation();
        }
        catch (ObjectDeletedException sse) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException((Throwable)((Object)sse)));
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        return event.getResult();
    }

    private void fireMerge(Map copiedAlready, MergeEvent event) {
        try {
            this.pulseTransactionCoordinator();
            this.fastSessionServices.eventListenerGroup_MERGE.fireEventOnEachListener(event, copiedAlready, MergeEventListener::onMerge);
        }
        catch (ObjectDeletedException sse) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException((Throwable)((Object)sse)));
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    @Override
    public void delete(Object object) throws HibernateException {
        this.checkOpen();
        this.fireDelete(new DeleteEvent(object, this));
    }

    @Override
    public void delete(String entityName, Object object) throws HibernateException {
        this.checkOpen();
        this.fireDelete(new DeleteEvent(entityName, object, this));
    }

    @Override
    public void delete(String entityName, Object object, boolean isCascadeDeleteEnabled, Set transientEntities) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        boolean removingOrphanBeforeUpates = this.persistenceContext.isRemovingOrphanBeforeUpates();
        boolean traceEnabled = log.isTraceEnabled();
        if (traceEnabled && removingOrphanBeforeUpates) {
            this.logRemoveOrphanBeforeUpdates("before continuing", entityName, object);
        }
        this.fireDelete(new DeleteEvent(entityName, object, isCascadeDeleteEnabled, removingOrphanBeforeUpates, this), transientEntities);
        if (traceEnabled && removingOrphanBeforeUpates) {
            this.logRemoveOrphanBeforeUpdates("after continuing", entityName, object);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeOrphanBeforeUpdates(String entityName, Object child) {
        boolean traceEnabled = log.isTraceEnabled();
        if (traceEnabled) {
            this.logRemoveOrphanBeforeUpdates("begin", entityName, child);
        }
        this.persistenceContext.beginRemoveOrphanBeforeUpdates();
        try {
            this.checkOpenOrWaitingForAutoClose();
            this.fireDelete(new DeleteEvent(entityName, child, false, true, this));
        }
        finally {
            this.persistenceContext.endRemoveOrphanBeforeUpdates();
            if (traceEnabled) {
                this.logRemoveOrphanBeforeUpdates("end", entityName, child);
            }
        }
    }

    private void logRemoveOrphanBeforeUpdates(String timing, String entityName, Object entity) {
        if (log.isTraceEnabled()) {
            EntityEntry entityEntry = this.persistenceContext.getEntry(entity);
            log.tracef("%s remove orphan before updates: [%s]", timing, entityEntry == null ? entityName : MessageHelper.infoString(entityName, entityEntry.getId()));
        }
    }

    private void fireDelete(DeleteEvent event) {
        try {
            this.pulseTransactionCoordinator();
            this.fastSessionServices.eventListenerGroup_DELETE.fireEventOnEachListener(event, DeleteEventListener::onDelete);
        }
        catch (ObjectDeletedException sse) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException((Throwable)((Object)sse)));
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    private void fireDelete(DeleteEvent event, Set transientEntities) {
        try {
            this.pulseTransactionCoordinator();
            this.fastSessionServices.eventListenerGroup_DELETE.fireEventOnEachListener(event, transientEntities, DeleteEventListener::onDelete);
        }
        catch (ObjectDeletedException sse) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException((Throwable)((Object)sse)));
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    @Override
    public void load(Object object, Serializable id) throws HibernateException {
        LoadEvent event = this.loadEvent;
        this.loadEvent = null;
        if (event == null) {
            event = new LoadEvent(id, object, this, this.getReadOnlyFromLoadQueryInfluencers());
        } else {
            event.setEntityClassName(null);
            event.setEntityId(id);
            event.setInstanceToLoad(object);
            event.setLockMode(LoadEvent.DEFAULT_LOCK_MODE);
            event.setLockScope(LoadEvent.DEFAULT_LOCK_OPTIONS.getScope());
            event.setLockTimeout(LoadEvent.DEFAULT_LOCK_OPTIONS.getTimeOut());
        }
        this.fireLoad(event, LoadEventListener.RELOAD);
        if (this.loadEvent == null) {
            event.setEntityClassName(null);
            event.setEntityId(null);
            event.setInstanceToLoad(null);
            event.setResult(null);
            this.loadEvent = event;
        }
    }

    @Override
    public <T> T load(Class<T> entityClass, Serializable id) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).getReference(id);
    }

    @Override
    public Object load(String entityName, Serializable id) throws HibernateException {
        return this.byId(entityName).getReference(id);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).load(id);
    }

    @Override
    public Object get(String entityName, Serializable id) throws HibernateException {
        return this.byId(entityName).load(id);
    }

    @Override
    public Object immediateLoad(String entityName, Serializable id) throws HibernateException {
        if (log.isDebugEnabled()) {
            EntityPersister persister = this.getFactory().getMetamodel().entityPersister(entityName);
            log.debugf("Initializing proxy: %s", MessageHelper.infoString(persister, id, (SessionFactoryImplementor)this.getFactory()));
        }
        LoadEvent event = this.loadEvent;
        this.loadEvent = null;
        event = this.recycleEventInstance(event, id, entityName);
        try {
            this.fireLoadNoChecks(event, LoadEventListener.IMMEDIATE_LOAD);
        }
        catch (FetchNotFoundException e) {
            this.getSessionFactory().getEntityNotFoundDelegate().handleEntityNotFound(e.getEntityName(), (Serializable)e.getIdentifier());
        }
        Object result = event.getResult();
        if (this.loadEvent == null) {
            event.setEntityClassName(null);
            event.setEntityId(null);
            event.setInstanceToLoad(null);
            event.setResult(null);
            this.loadEvent = event;
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Object internalLoad(String entityName, Serializable id, boolean eager, boolean nullable) {
        EffectiveEntityGraph effectiveEntityGraph = this.getLoadQueryInfluencers().getEffectiveEntityGraph();
        GraphSemantic semantic = effectiveEntityGraph.getSemantic();
        RootGraphImplementor<?> graph = effectiveEntityGraph.getGraph();
        boolean clearedEffectiveGraph = false;
        if (semantic != null && !graph.appliesTo(entityName)) {
            log.debug("Clearing effective entity graph for subsequent-select");
            clearedEffectiveGraph = true;
            effectiveEntityGraph.clear();
        }
        try {
            LoadEventListener.LoadType type = nullable ? LoadEventListener.INTERNAL_LOAD_NULLABLE : (eager ? LoadEventListener.INTERNAL_LOAD_EAGER : LoadEventListener.INTERNAL_LOAD_LAZY);
            LoadEvent event = this.loadEvent;
            this.loadEvent = null;
            event = this.recycleEventInstance(event, id, entityName);
            this.fireLoadNoChecks(event, type);
            Object result = event.getResult();
            if (!nullable) {
                UnresolvableObjectException.throwIfNull(result, id, entityName);
            }
            if (this.loadEvent == null) {
                event.setEntityClassName(null);
                event.setEntityId(null);
                event.setInstanceToLoad(null);
                event.setResult(null);
                this.loadEvent = event;
            }
            Object object = result;
            return object;
        }
        finally {
            if (clearedEffectiveGraph) {
                effectiveEntityGraph.applyGraph(graph, semantic);
            }
        }
    }

    private LoadEvent recycleEventInstance(LoadEvent event, Serializable id, String entityName) {
        if (event == null) {
            return new LoadEvent(id, entityName, true, (EventSource)this, this.getReadOnlyFromLoadQueryInfluencers());
        }
        event.setEntityClassName(entityName);
        event.setEntityId(id);
        event.setInstanceToLoad(null);
        event.setLockMode(LoadEvent.DEFAULT_LOCK_MODE);
        event.setLockScope(LoadEvent.DEFAULT_LOCK_OPTIONS.getScope());
        event.setLockTimeout(LoadEvent.DEFAULT_LOCK_OPTIONS.getTimeOut());
        return event;
    }

    @Override
    public <T> T load(Class<T> entityClass, Serializable id, LockMode lockMode) throws HibernateException {
        return ((IdentifierLoadAccessImpl)((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).with(new LockOptions(lockMode))).getReference(id);
    }

    @Override
    public <T> T load(Class<T> entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
        return ((IdentifierLoadAccessImpl)((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).with(lockOptions)).getReference(id);
    }

    @Override
    public Object load(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId(entityName).with(new LockOptions(lockMode))).getReference(id);
    }

    @Override
    public Object load(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId(entityName).with(lockOptions)).getReference(id);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id, LockMode lockMode) throws HibernateException {
        return ((IdentifierLoadAccessImpl)((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).with(new LockOptions(lockMode))).load(id);
    }

    @Override
    public <T> T get(Class<T> entityClass, Serializable id, LockOptions lockOptions) throws HibernateException {
        return ((IdentifierLoadAccessImpl)((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).with(lockOptions)).load(id);
    }

    @Override
    public Object get(String entityName, Serializable id, LockMode lockMode) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId(entityName).with(new LockOptions(lockMode))).load(id);
    }

    @Override
    public Object get(String entityName, Serializable id, LockOptions lockOptions) throws HibernateException {
        return ((IdentifierLoadAccessImpl)this.byId(entityName).with(lockOptions)).load(id);
    }

    @Override
    public IdentifierLoadAccessImpl byId(String entityName) {
        return new IdentifierLoadAccessImpl(entityName);
    }

    public <T> IdentifierLoadAccessImpl<T> byId(Class<T> entityClass) {
        return new IdentifierLoadAccessImpl(entityClass);
    }

    @Override
    public <T> MultiIdentifierLoadAccess<T> byMultipleIds(Class<T> entityClass) {
        return new MultiIdentifierLoadAccessImpl(this.locateEntityPersister(entityClass));
    }

    @Override
    public MultiIdentifierLoadAccess byMultipleIds(String entityName) {
        return new MultiIdentifierLoadAccessImpl(this.locateEntityPersister(entityName));
    }

    @Override
    public NaturalIdLoadAccess byNaturalId(String entityName) {
        return new NaturalIdLoadAccessImpl(entityName);
    }

    @Override
    public <T> NaturalIdLoadAccess<T> byNaturalId(Class<T> entityClass) {
        return new NaturalIdLoadAccessImpl(entityClass);
    }

    @Override
    public SimpleNaturalIdLoadAccess bySimpleNaturalId(String entityName) {
        return new SimpleNaturalIdLoadAccessImpl(entityName);
    }

    @Override
    public <T> SimpleNaturalIdLoadAccess<T> bySimpleNaturalId(Class<T> entityClass) {
        return new SimpleNaturalIdLoadAccessImpl(entityClass);
    }

    private void fireLoad(LoadEvent event, LoadEventListener.LoadType loadType) {
        this.checkOpenOrWaitingForAutoClose();
        this.fireLoadNoChecks(event, loadType);
        this.delayedAfterCompletion();
    }

    private void fireLoadNoChecks(LoadEvent event, LoadEventListener.LoadType loadType) {
        this.pulseTransactionCoordinator();
        this.fastSessionServices.eventListenerGroup_LOAD.fireEventOnEachListener(event, loadType, LoadEventListener::onLoad);
    }

    private void fireResolveNaturalId(ResolveNaturalIdEvent event) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        this.fastSessionServices.eventListenerGroup_RESOLVE_NATURAL_ID.fireEventOnEachListener(event, ResolveNaturalIdEventListener::onResolveNaturalId);
        this.delayedAfterCompletion();
    }

    @Override
    public void refresh(Object object) throws HibernateException {
        this.checkOpen();
        this.fireRefresh(new RefreshEvent(null, object, (EventSource)this));
    }

    @Override
    public void refresh(String entityName, Object object) throws HibernateException {
        this.checkOpen();
        this.fireRefresh(new RefreshEvent(entityName, object, (EventSource)this));
    }

    @Override
    public void refresh(Object object, LockMode lockMode) throws HibernateException {
        this.checkOpen();
        this.fireRefresh(new RefreshEvent(object, lockMode, (EventSource)this));
    }

    @Override
    public void refresh(Object object, LockOptions lockOptions) throws HibernateException {
        this.checkOpen();
        this.refresh(null, object, lockOptions);
    }

    @Override
    public void refresh(String entityName, Object object, LockOptions lockOptions) throws HibernateException {
        this.checkOpen();
        this.fireRefresh(new RefreshEvent(entityName, object, lockOptions, this));
    }

    @Override
    public void refresh(String entityName, Object object, Map refreshedAlready) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.fireRefresh(refreshedAlready, new RefreshEvent(entityName, object, (EventSource)this));
    }

    private void fireRefresh(RefreshEvent event) {
        try {
            if (!this.getSessionFactory().getSessionFactoryOptions().isAllowRefreshDetachedEntity() && (event.getEntityName() != null ? !this.contains(event.getEntityName(), event.getObject()) : !this.contains(event.getObject()))) {
                throw new IllegalArgumentException("Entity not managed");
            }
            this.pulseTransactionCoordinator();
            this.fastSessionServices.eventListenerGroup_REFRESH.fireEventOnEachListener(event, RefreshEventListener::onRefresh);
        }
        catch (RuntimeException e) {
            if (!this.getSessionFactory().getSessionFactoryOptions().isJpaBootstrap() && e instanceof HibernateException) {
                throw e;
            }
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    private void fireRefresh(Map refreshedAlready, RefreshEvent event) {
        try {
            this.pulseTransactionCoordinator();
            this.fastSessionServices.eventListenerGroup_REFRESH.fireEventOnEachListener(event, refreshedAlready, RefreshEventListener::onRefresh);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
        finally {
            this.delayedAfterCompletion();
        }
    }

    @Override
    public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {
        this.fireReplicate(new ReplicateEvent(obj, replicationMode, this));
    }

    @Override
    public void replicate(String entityName, Object obj, ReplicationMode replicationMode) throws HibernateException {
        this.fireReplicate(new ReplicateEvent(entityName, obj, replicationMode, this));
    }

    private void fireReplicate(ReplicateEvent event) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.fastSessionServices.eventListenerGroup_REPLICATE.fireEventOnEachListener(event, ReplicateEventListener::onReplicate);
        this.delayedAfterCompletion();
    }

    @Override
    public void evict(Object object) throws HibernateException {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        EvictEvent event = new EvictEvent(object, this);
        this.fastSessionServices.eventListenerGroup_EVICT.fireEventOnEachListener(event, EvictEventListener::onEvict);
        this.delayedAfterCompletion();
    }

    protected boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {
        this.checkOpen();
        if (!this.isTransactionInProgress()) {
            return false;
        }
        AutoFlushEvent event = new AutoFlushEvent(querySpaces, this);
        this.fastSessionServices.eventListenerGroup_AUTO_FLUSH.fireEventOnEachListener(event, AutoFlushEventListener::onAutoFlush);
        return event.isFlushRequired();
    }

    @Override
    public boolean isDirty() throws HibernateException {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        log.debug("Checking session dirtiness");
        if (this.actionQueue.areInsertionsOrDeletionsQueued()) {
            log.debug("Session dirty (scheduled updates and insertions)");
            return true;
        }
        DirtyCheckEvent event = new DirtyCheckEvent(this);
        this.fastSessionServices.eventListenerGroup_DIRTY_CHECK.fireEventOnEachListener(event, DirtyCheckEventListener::onDirtyCheck);
        this.delayedAfterCompletion();
        return event.isDirty();
    }

    @Override
    public void flush() throws HibernateException {
        this.checkOpen();
        this.doFlush();
    }

    private void doFlush() {
        this.pulseTransactionCoordinator();
        this.checkTransactionNeededForUpdateOperation();
        try {
            if (this.persistenceContext.getCascadeLevel() > 0) {
                throw new HibernateException("Flush during cascade is dangerous");
            }
            FlushEvent event = new FlushEvent(this);
            this.fastSessionServices.eventListenerGroup_FLUSH.fireEventOnEachListener(event, FlushEventListener::onFlush);
            this.delayedAfterCompletion();
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public void setFlushMode(FlushModeType flushModeType) {
        this.checkOpen();
        this.setHibernateFlushMode(FlushModeTypeHelper.getFlushMode(flushModeType));
    }

    @Override
    public void forceFlush(EntityEntry entityEntry) throws HibernateException {
        if (log.isDebugEnabled()) {
            log.debugf("Flushing to force deletion of re-saved object: %s", MessageHelper.infoString(entityEntry.getPersister(), entityEntry.getId(), (SessionFactoryImplementor)this.getFactory()));
        }
        if (this.persistenceContext.getCascadeLevel() > 0) {
            throw new ObjectDeletedException("deleted object would be re-saved by cascade (remove deleted object from associations)", entityEntry.getId(), entityEntry.getPersister().getEntityName());
        }
        this.checkOpenOrWaitingForAutoClose();
        this.doFlush();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List list(String query, QueryParameters queryParameters) throws HibernateException {
        List results;
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        queryParameters.validateParameters();
        HQLQueryPlan plan = queryParameters.getQueryPlan();
        if (plan == null) {
            plan = this.getQueryPlan(query, false);
        }
        this.autoFlushIfRequired(plan.getQuerySpaces());
        boolean success = false;
        ++this.dontFlushFromFind;
        try {
            results = plan.performList(queryParameters, this);
            success = true;
        }
        finally {
            --this.dontFlushFromFind;
            this.afterOperation(success);
            this.delayedAfterCompletion();
        }
        return results;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int executeUpdate(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        queryParameters.validateParameters();
        HQLQueryPlan plan = this.getQueryPlan(query, false);
        this.autoFlushIfRequired(plan.getQuerySpaces());
        this.verifyImmutableEntityUpdate(plan);
        boolean success = false;
        int result = 0;
        try {
            result = plan.performExecuteUpdate(queryParameters, this);
            success = true;
        }
        finally {
            this.afterOperation(success);
            this.delayedAfterCompletion();
        }
        return result;
    }

    protected void verifyImmutableEntityUpdate(HQLQueryPlan plan) {
        if (plan.isUpdate()) {
            ArrayList<String> primaryFromClauseTables = new ArrayList<String>();
            for (QueryTranslator queryTranslator : plan.getTranslators()) {
                primaryFromClauseTables.addAll(queryTranslator.getPrimaryFromClauseTables());
            }
            block5: for (EntityPersister entityPersister : this.getSessionFactory().getMetamodel().entityPersisters().values()) {
                if (entityPersister.isMutable()) continue;
                ArrayList<Serializable> entityQuerySpaces = new ArrayList<Serializable>(Arrays.asList(entityPersister.getQuerySpaces()));
                boolean matching = false;
                for (Serializable entityQuerySpace : entityQuerySpaces) {
                    if (!primaryFromClauseTables.contains(entityQuerySpace)) continue;
                    matching = true;
                    break;
                }
                if (!matching) continue;
                ImmutableEntityUpdateQueryHandlingMode immutableEntityUpdateQueryHandlingMode = this.getSessionFactory().getSessionFactoryOptions().getImmutableEntityUpdateQueryHandlingMode();
                String querySpaces = Arrays.toString(entityQuerySpaces.toArray());
                switch (immutableEntityUpdateQueryHandlingMode) {
                    case WARNING: {
                        log.immutableEntityUpdateQuery(plan.getSourceQuery(), querySpaces);
                        continue block5;
                    }
                    case EXCEPTION: {
                        throw new HibernateException("The query: [" + plan.getSourceQuery() + "] attempts to update an immutable entity: " + querySpaces);
                    }
                }
                throw new UnsupportedOperationException("The " + (Object)((Object)immutableEntityUpdateQueryHandlingMode) + " is not supported!");
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int executeNativeUpdate(NativeSQLQuerySpecification nativeQuerySpecification, QueryParameters queryParameters) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        queryParameters.validateParameters();
        NativeSQLQueryPlan plan = this.getNativeQueryPlan(nativeQuerySpecification);
        this.autoFlushIfRequired(plan.getCustomQuery().getQuerySpaces());
        boolean success = false;
        int result = 0;
        try {
            result = plan.performExecuteUpdate(queryParameters, this);
            success = true;
        }
        finally {
            this.afterOperation(success);
            this.delayedAfterCompletion();
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        queryParameters.validateParameters();
        HQLQueryPlan plan = queryParameters.getQueryPlan();
        if (plan == null || !plan.isShallow()) {
            plan = this.getQueryPlan(query, true);
        }
        this.autoFlushIfRequired(plan.getQuerySpaces());
        ++this.dontFlushFromFind;
        try {
            Iterator iterator = plan.performIterate(queryParameters, this);
            return iterator;
        }
        finally {
            this.delayedAfterCompletion();
            --this.dontFlushFromFind;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScrollableResultsImplementor scroll(String query, QueryParameters queryParameters) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        HQLQueryPlan plan = queryParameters.getQueryPlan();
        if (plan == null) {
            plan = this.getQueryPlan(query, false);
        }
        this.autoFlushIfRequired(plan.getQuerySpaces());
        ++this.dontFlushFromFind;
        try {
            ScrollableResultsImplementor scrollableResultsImplementor = plan.performScroll(queryParameters, this);
            return scrollableResultsImplementor;
        }
        finally {
            this.delayedAfterCompletion();
            --this.dontFlushFromFind;
        }
    }

    @Override
    public Query createFilter(Object collection, String queryString) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        CollectionFilterImpl filter = new CollectionFilterImpl(queryString, collection, this, this.getFilterQueryPlan(collection, queryString, null, false).getParameterMetadata());
        filter.setComment(queryString);
        this.delayedAfterCompletion();
        return filter;
    }

    @Override
    public Object instantiate(String entityName, Serializable id) throws HibernateException {
        return this.instantiate(this.getFactory().getMetamodel().entityPersister(entityName), id);
    }

    @Override
    public Object instantiate(EntityPersister persister, Serializable id) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        Object result = this.getInterceptor().instantiate(persister.getEntityName(), persister.getEntityMetamodel().getEntityMode(), id);
        if (result == null) {
            result = persister.instantiate(id, this);
        }
        this.delayedAfterCompletion();
        return result;
    }

    @Override
    public EntityPersister getEntityPersister(String entityName, Object object) {
        this.checkOpenOrWaitingForAutoClose();
        if (entityName == null) {
            return this.getFactory().getMetamodel().entityPersister(this.guessEntityName(object));
        }
        try {
            return this.getFactory().getMetamodel().entityPersister(entityName).getSubclassEntityPersister(object, (SessionFactoryImplementor)this.getFactory());
        }
        catch (HibernateException e) {
            try {
                return this.getEntityPersister(null, object);
            }
            catch (HibernateException e2) {
                throw e;
            }
        }
    }

    @Override
    public Serializable getIdentifier(Object object) throws HibernateException {
        this.checkOpen();
        this.checkTransactionSynchStatus();
        if (object instanceof HibernateProxy) {
            LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
            if (li.getSession() != this) {
                throw new TransientObjectException("The proxy was not associated with this session");
            }
            return li.getInternalIdentifier();
        }
        EntityEntry entry = this.persistenceContext.getEntry(object);
        if (entry == null) {
            throw new TransientObjectException("The instance was not associated with this session");
        }
        return entry.getId();
    }

    @Override
    public Serializable getContextEntityIdentifier(Object object) {
        this.checkOpenOrWaitingForAutoClose();
        if (object instanceof HibernateProxy) {
            return this.getProxyIdentifier(object);
        }
        EntityEntry entry = this.persistenceContext.getEntry(object);
        return entry != null ? entry.getId() : null;
    }

    private Serializable getProxyIdentifier(Object proxy) {
        return ((HibernateProxy)proxy).getHibernateLazyInitializer().getInternalIdentifier();
    }

    private FilterQueryPlan getFilterQueryPlan(Object collection, String filter, QueryParameters parameters, boolean shallow) throws HibernateException {
        if (collection == null) {
            throw new NullPointerException("null collection passed to filter");
        }
        CollectionEntry entry = this.persistenceContext.getCollectionEntryOrNull(collection);
        CollectionPersister roleBeforeFlush = entry == null ? null : entry.getLoadedPersister();
        FilterQueryPlan plan = null;
        Map<String, Filter> enabledFilters = this.getLoadQueryInfluencers().getEnabledFilters();
        HibernateEntityManagerFactory factory = this.getFactory();
        if (roleBeforeFlush == null) {
            CollectionPersister roleAfterFlush;
            this.flush();
            entry = this.persistenceContext.getCollectionEntryOrNull(collection);
            CollectionPersister collectionPersister = roleAfterFlush = entry == null ? null : entry.getLoadedPersister();
            if (roleAfterFlush == null) {
                throw new QueryException("The collection was unreferenced");
            }
            plan = factory.getQueryPlanCache().getFilterQueryPlan(filter, roleAfterFlush.getRole(), shallow, enabledFilters);
        } else {
            plan = factory.getQueryPlanCache().getFilterQueryPlan(filter, roleBeforeFlush.getRole(), shallow, enabledFilters);
            if (this.autoFlushIfRequired(plan.getQuerySpaces())) {
                CollectionPersister roleAfterFlush;
                entry = this.persistenceContext.getCollectionEntryOrNull(collection);
                CollectionPersister collectionPersister = roleAfterFlush = entry == null ? null : entry.getLoadedPersister();
                if (roleBeforeFlush != roleAfterFlush) {
                    if (roleAfterFlush == null) {
                        throw new QueryException("The collection was dereferenced");
                    }
                    plan = factory.getQueryPlanCache().getFilterQueryPlan(filter, roleAfterFlush.getRole(), shallow, enabledFilters);
                }
            }
        }
        if (parameters != null) {
            parameters.getNamedParameters().put("{collection_key}", new TypedValue(entry.getLoadedPersister().getKeyType(), entry.getLoadedKey()));
        }
        return plan;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List listFilter(Object collection, String filter, QueryParameters queryParameters) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        FilterQueryPlan plan = this.getFilterQueryPlan(collection, filter, queryParameters, false);
        List results = Collections.EMPTY_LIST;
        boolean success = false;
        ++this.dontFlushFromFind;
        try {
            results = plan.performList(queryParameters, this);
            success = true;
        }
        finally {
            --this.dontFlushFromFind;
            this.afterOperation(success);
            this.delayedAfterCompletion();
        }
        return results;
    }

    @Override
    public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        FilterQueryPlan plan = this.getFilterQueryPlan(collection, filter, queryParameters, true);
        Iterator itr = plan.performIterate(queryParameters, this);
        this.delayedAfterCompletion();
        return itr;
    }

    @Override
    public Criteria createCriteria(Class persistentClass, String alias) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
        this.checkOpen();
        this.checkTransactionSynchStatus();
        return new CriteriaImpl(persistentClass.getName(), alias, this);
    }

    @Override
    public Criteria createCriteria(String entityName, String alias) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
        this.checkOpen();
        this.checkTransactionSynchStatus();
        return new CriteriaImpl(entityName, alias, this);
    }

    @Override
    public Criteria createCriteria(Class persistentClass) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
        this.checkOpen();
        this.checkTransactionSynchStatus();
        return new CriteriaImpl(persistentClass.getName(), this);
    }

    @Override
    public Criteria createCriteria(String entityName) {
        DeprecationLogger.DEPRECATION_LOGGER.deprecatedLegacyCriteria();
        this.checkOpen();
        this.checkTransactionSynchStatus();
        return new CriteriaImpl(entityName, this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScrollableResultsImplementor scroll(Criteria criteria, ScrollMode scrollMode) {
        CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        String entityName = criteriaImpl.getEntityOrClassName();
        CriteriaLoader loader = new CriteriaLoader(this.getOuterJoinLoadable(entityName), (SessionFactoryImplementor)this.getFactory(), criteriaImpl, entityName, this.getLoadQueryInfluencers());
        this.autoFlushIfRequired(loader.getQuerySpaces());
        ++this.dontFlushFromFind;
        try {
            ScrollableResultsImplementor scrollableResultsImplementor = loader.scroll(this, scrollMode);
            return scrollableResultsImplementor;
        }
        finally {
            this.delayedAfterCompletion();
            --this.dontFlushFromFind;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List list(Criteria criteria) throws HibernateException {
        CriteriaImpl criteriaImpl = (CriteriaImpl)criteria;
        if (criteriaImpl.getMaxResults() != null && criteriaImpl.getMaxResults() == 0) {
            return Collections.EMPTY_LIST;
        }
        NaturalIdLoadAccess naturalIdLoadAccess = this.tryNaturalIdLoadAccess(criteriaImpl);
        if (naturalIdLoadAccess != null) {
            return Arrays.asList(naturalIdLoadAccess.load());
        }
        this.checkOpenOrWaitingForAutoClose();
        String[] implementors = this.getFactory().getMetamodel().getImplementors(criteriaImpl.getEntityOrClassName());
        int size = implementors.length;
        CriteriaLoader[] loaders = new CriteriaLoader[size];
        HashSet spaces = new HashSet();
        for (int i = 0; i < size; ++i) {
            loaders[i] = new CriteriaLoader(this.getOuterJoinLoadable(implementors[i]), (SessionFactoryImplementor)this.getFactory(), criteriaImpl, implementors[i], this.getLoadQueryInfluencers());
            spaces.addAll(loaders[i].getQuerySpaces());
        }
        this.autoFlushIfRequired(spaces);
        List results = Collections.EMPTY_LIST;
        ++this.dontFlushFromFind;
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
            --this.dontFlushFromFind;
            this.afterOperation(success);
            this.delayedAfterCompletion();
        }
        return results;
    }

    private NaturalIdLoadAccess tryNaturalIdLoadAccess(CriteriaImpl criteria) {
        if (!criteria.isLookupByNaturalKey()) {
            return null;
        }
        String entityName = criteria.getEntityOrClassName();
        EntityPersister entityPersister = this.getFactory().getMetamodel().entityPersister(entityName);
        if (!entityPersister.hasNaturalIdentifier()) {
            return null;
        }
        CriteriaImpl.CriterionEntry criterionEntry = criteria.iterateExpressionEntries().next();
        NaturalIdentifier naturalIdentifier = (NaturalIdentifier)criterionEntry.getCriterion();
        Map<String, Object> naturalIdValues = naturalIdentifier.getNaturalIdValues();
        int[] naturalIdentifierProperties = entityPersister.getNaturalIdentifierProperties();
        if (naturalIdentifierProperties.length != naturalIdValues.size()) {
            return null;
        }
        String[] propertyNames = entityPersister.getPropertyNames();
        NaturalIdLoadAccess naturalIdLoader = this.byNaturalId(entityName);
        for (int naturalIdentifierProperty : naturalIdentifierProperties) {
            String naturalIdProperty = propertyNames[naturalIdentifierProperty];
            Object naturalIdValue = naturalIdValues.get(naturalIdProperty);
            if (naturalIdValue == null) {
                return null;
            }
            naturalIdLoader.using(naturalIdProperty, naturalIdValue);
        }
        log.warn("Session.byNaturalId(" + entityName + ") should be used for naturalId queries instead of Restrictions.naturalId() from a Criteria");
        return naturalIdLoader;
    }

    private OuterJoinLoadable getOuterJoinLoadable(String entityName) throws MappingException {
        EntityPersister persister = this.getFactory().getMetamodel().entityPersister(entityName);
        if (!(persister instanceof OuterJoinLoadable)) {
            throw new MappingException("class persister is not OuterJoinLoadable: " + entityName);
        }
        return (OuterJoinLoadable)persister;
    }

    public boolean contains(Object object) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        if (object == null) {
            return false;
        }
        try {
            if (object instanceof HibernateProxy) {
                LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
                if (li.isUninitialized()) {
                    return li.getSession() == this;
                }
                object = li.getImplementation();
            }
            EntityEntry entry = this.persistenceContext.getEntry(object);
            this.delayedAfterCompletion();
            if (entry == null) {
                if (!HibernateProxy.class.isInstance(object) && this.persistenceContext.getEntry(object) == null) {
                    try {
                        String entityName = this.getEntityNameResolver().resolveEntityName(object);
                        if (entityName == null) {
                            throw new IllegalArgumentException("Could not resolve entity-name [" + object + "]");
                        }
                        this.getSessionFactory().getMetamodel().entityPersister(entityName);
                    }
                    catch (HibernateException e) {
                        throw new IllegalArgumentException("Not an entity [" + object.getClass() + "]", (Throwable)((Object)e));
                    }
                }
                return false;
            }
            return entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
        }
        catch (MappingException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    @Override
    public boolean contains(String entityName, Object object) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        if (object == null) {
            return false;
        }
        try {
            if (!HibernateProxy.class.isInstance(object) && this.persistenceContext.getEntry(object) == null) {
                try {
                    this.getSessionFactory().getMetamodel().entityPersister(entityName);
                }
                catch (HibernateException e) {
                    throw new IllegalArgumentException("Not an entity [" + entityName + "] : " + object);
                }
            }
            if (object instanceof HibernateProxy) {
                LazyInitializer li = ((HibernateProxy)object).getHibernateLazyInitializer();
                if (li.isUninitialized()) {
                    return li.getSession() == this;
                }
                object = li.getImplementation();
            }
            EntityEntry entry = this.persistenceContext.getEntry(object);
            this.delayedAfterCompletion();
            return entry != null && entry.getStatus() != Status.DELETED && entry.getStatus() != Status.GONE;
        }
        catch (MappingException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName) {
        this.checkOpen();
        return super.createStoredProcedureCall(procedureName);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, String ... resultSetMappings) {
        this.checkOpen();
        return super.createStoredProcedureCall(procedureName, resultSetMappings);
    }

    @Override
    public ProcedureCall createStoredProcedureCall(String procedureName, Class ... resultClasses) {
        this.checkOpen();
        return super.createStoredProcedureCall(procedureName, resultClasses);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public ScrollableResultsImplementor scrollCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
        this.checkOpenOrWaitingForAutoClose();
        if (log.isTraceEnabled()) {
            log.tracev("Scroll SQL query: {0}", customQuery.getSQL());
        }
        CustomLoader loader = this.getFactory().getQueryPlanCache().getNativeQueryInterpreter().createCustomLoader(customQuery, (SessionFactoryImplementor)this.getFactory());
        this.autoFlushIfRequired(loader.getQuerySpaces());
        ++this.dontFlushFromFind;
        try {
            ScrollableResultsImplementor scrollableResultsImplementor = loader.scroll(queryParameters, this);
            return scrollableResultsImplementor;
        }
        finally {
            this.delayedAfterCompletion();
            --this.dontFlushFromFind;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public List listCustomQuery(CustomQuery customQuery, QueryParameters queryParameters) {
        this.checkOpenOrWaitingForAutoClose();
        if (log.isTraceEnabled()) {
            log.tracev("SQL query: {0}", customQuery.getSQL());
        }
        CustomLoader loader = this.getFactory().getQueryPlanCache().getNativeQueryInterpreter().createCustomLoader(customQuery, (SessionFactoryImplementor)this.getFactory());
        this.autoFlushIfRequired(loader.getQuerySpaces());
        ++this.dontFlushFromFind;
        boolean success = false;
        try {
            List results = loader.list(this, queryParameters);
            success = true;
            List list = results;
            return list;
        }
        finally {
            --this.dontFlushFromFind;
            this.delayedAfterCompletion();
            this.afterOperation(success);
        }
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.getFactory();
    }

    @Override
    public void initializeCollection(PersistentCollection collection, boolean writing) {
        this.checkOpenOrWaitingForAutoClose();
        this.pulseTransactionCoordinator();
        InitializeCollectionEvent event = new InitializeCollectionEvent(collection, this);
        this.fastSessionServices.eventListenerGroup_INIT_COLLECTION.fireEventOnEachListener(event, InitializeCollectionEventListener::onInitializeCollection);
        this.delayedAfterCompletion();
    }

    @Override
    public String bestGuessEntityName(Object object) {
        EntityEntry entry;
        if (object instanceof HibernateProxy) {
            LazyInitializer initializer = ((HibernateProxy)object).getHibernateLazyInitializer();
            if (initializer.isUninitialized()) {
                return initializer.getEntityName();
            }
            object = initializer.getImplementation();
        }
        if ((entry = this.persistenceContext.getEntry(object)) == null) {
            return this.guessEntityName(object);
        }
        return entry.getPersister().getEntityName();
    }

    @Override
    public String getEntityName(Object object) {
        EntityEntry entry;
        this.checkOpen();
        if (object instanceof HibernateProxy) {
            if (!this.persistenceContext.containsProxy(object)) {
                throw new TransientObjectException("proxy was not associated with the session");
            }
            object = ((HibernateProxy)object).getHibernateLazyInitializer().getImplementation();
        }
        if ((entry = this.persistenceContext.getEntry(object)) == null) {
            this.throwTransientObjectException(object);
        }
        return entry.getPersister().getEntityName();
    }

    private void throwTransientObjectException(Object object) throws HibernateException {
        throw new TransientObjectException("object references an unsaved transient instance - save the transient instance before flushing: " + this.guessEntityName(object));
    }

    @Override
    public <T> T getReference(T object) {
        this.checkOpen();
        if (object instanceof HibernateProxy) {
            LazyInitializer initializer = ((HibernateProxy)object).getHibernateLazyInitializer();
            return this.getReference(initializer.getPersistentClass(), initializer.getIdentifier());
        }
        EntityPersister persister = this.getEntityPersister(null, object);
        return this.getReference(persister.getMappedClass(), persister.getIdentifier(object, this));
    }

    @Override
    public String guessEntityName(Object object) throws HibernateException {
        this.checkOpenOrWaitingForAutoClose();
        return this.getEntityNameResolver().resolveEntityName(object);
    }

    @Override
    public void cancelQuery() throws HibernateException {
        this.checkOpen();
        this.getJdbcCoordinator().cancelLastQuery();
    }

    @Override
    public int getDontFlushFromFind() {
        return this.dontFlushFromFind;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder(500).append("SessionImpl(").append(System.identityHashCode(this));
        if (!this.isClosed()) {
            if (log.isTraceEnabled()) {
                buf.append(this.persistenceContext).append(";").append(this.actionQueue);
            } else {
                buf.append("<open>");
            }
        } else {
            buf.append("<closed>");
        }
        return buf.append(')').toString();
    }

    @Override
    public ActionQueue getActionQueue() {
        this.checkOpenOrWaitingForAutoClose();
        return this.actionQueue;
    }

    @Override
    public PersistenceContext getPersistenceContext() {
        this.checkOpenOrWaitingForAutoClose();
        return this.persistenceContext;
    }

    @Override
    public PersistenceContext getPersistenceContextInternal() {
        return this.persistenceContext;
    }

    @Override
    public SessionStatistics getStatistics() {
        this.pulseTransactionCoordinator();
        return new SessionStatisticsImpl(this);
    }

    @Override
    public boolean isEventSource() {
        this.pulseTransactionCoordinator();
        return true;
    }

    @Override
    public boolean isDefaultReadOnly() {
        return this.persistenceContext.isDefaultReadOnly();
    }

    @Override
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.persistenceContext.setDefaultReadOnly(defaultReadOnly);
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        this.checkOpen();
        return this.persistenceContext.isReadOnly(entityOrProxy);
    }

    @Override
    public void setReadOnly(Object entity, boolean readOnly) {
        this.checkOpen();
        this.persistenceContext.setReadOnly(entity, readOnly);
    }

    @Override
    public void afterScrollOperation() {
    }

    @Override
    public LoadQueryInfluencers getLoadQueryInfluencers() {
        return this.loadQueryInfluencers;
    }

    @Override
    public Filter getEnabledFilter(String filterName) {
        this.pulseTransactionCoordinator();
        return this.loadQueryInfluencers.getEnabledFilter(filterName);
    }

    @Override
    public Filter enableFilter(String filterName) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        return this.loadQueryInfluencers.enableFilter(filterName);
    }

    @Override
    public void disableFilter(String filterName) {
        this.checkOpen();
        this.pulseTransactionCoordinator();
        this.loadQueryInfluencers.disableFilter(filterName);
    }

    @Override
    public boolean isFetchProfileEnabled(String name) throws UnknownProfileException {
        return this.loadQueryInfluencers.isFetchProfileEnabled(name);
    }

    @Override
    public void enableFetchProfile(String name) throws UnknownProfileException {
        this.loadQueryInfluencers.enableFetchProfile(name);
    }

    @Override
    public void disableFetchProfile(String name) throws UnknownProfileException {
        this.loadQueryInfluencers.disableFetchProfile(name);
    }

    @Override
    public TypeHelper getTypeHelper() {
        return this.getSessionFactory().getTypeHelper();
    }

    @Override
    public LobHelper getLobHelper() {
        if (this.lobHelper == null) {
            this.lobHelper = new LobHelperImpl(this);
        }
        return this.lobHelper;
    }

    private Transaction getTransactionIfAccessible() {
        return this.fastSessionServices.isJtaTransactionAccessible ? this.accessTransaction() : null;
    }

    @Override
    public void beforeTransactionCompletion() {
        log.trace("SessionImpl#beforeTransactionCompletion()");
        this.flushBeforeTransactionCompletion();
        this.actionQueue.beforeTransactionCompletion();
        try {
            this.getInterceptor().beforeTransactionCompletion(this.getTransactionIfAccessible());
        }
        catch (Throwable t) {
            log.exceptionInBeforeTransactionCompletionInterceptor(t);
        }
        super.beforeTransactionCompletion();
    }

    @Override
    public void afterTransactionCompletion(boolean successful, boolean delayed) {
        if (log.isTraceEnabled()) {
            log.tracef("SessionImpl#afterTransactionCompletion(successful=%s, delayed=%s)", successful, delayed);
        }
        if (!(this.isClosed() && !this.waitingForAutoClose || !this.autoClear && successful)) {
            this.internalClear();
        }
        this.persistenceContext.afterTransactionCompletion();
        this.actionQueue.afterTransactionCompletion(successful);
        this.getEventListenerManager().transactionCompletion(successful);
        StatisticsImplementor statistics = this.getFactory().getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.endTransaction(successful);
        }
        try {
            this.getInterceptor().afterTransactionCompletion(this.getTransactionIfAccessible());
        }
        catch (Throwable t) {
            log.exceptionInAfterTransactionCompletionInterceptor(t);
        }
        if (!delayed && this.shouldAutoClose() && (!this.isClosed() || this.waitingForAutoClose)) {
            this.managedClose();
        }
        super.afterTransactionCompletion(successful, delayed);
    }

    @Override
    protected void addSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
        this.transactionObserver = new TransactionObserver(){

            @Override
            public void afterBegin() {
            }

            @Override
            public void beforeCompletion() {
                if (SessionImpl.this.isOpen() && SessionImpl.this.getHibernateFlushMode() != FlushMode.MANUAL) {
                    SessionImpl.this.managedFlush();
                }
                SessionImpl.this.actionQueue.beforeTransactionCompletion();
                try {
                    SessionImpl.this.getInterceptor().beforeTransactionCompletion(SessionImpl.this.getTransactionIfAccessible());
                }
                catch (Throwable t) {
                    log.exceptionInBeforeTransactionCompletionInterceptor(t);
                }
            }

            @Override
            public void afterCompletion(boolean successful, boolean delayed) {
                SessionImpl.this.afterTransactionCompletion(successful, delayed);
                if (!SessionImpl.this.isClosed() && SessionImpl.this.autoClose) {
                    SessionImpl.this.managedClose();
                }
            }
        };
        transactionCoordinator.addObserver(this.transactionObserver);
    }

    @Override
    protected void removeSharedSessionTransactionObserver(TransactionCoordinator transactionCoordinator) {
        super.removeSharedSessionTransactionObserver(transactionCoordinator);
        transactionCoordinator.removeObserver(this.transactionObserver);
    }

    private EntityPersister locateEntityPersister(Class entityClass) {
        return this.getFactory().getMetamodel().locateEntityPersister(entityClass);
    }

    private EntityPersister locateEntityPersister(String entityName) {
        return this.getFactory().getMetamodel().locateEntityPersister(entityName);
    }

    @Override
    public void startTransactionBoundary() {
        this.checkOpenOrWaitingForAutoClose();
        super.startTransactionBoundary();
    }

    @Override
    public void afterTransactionBegin() {
        this.checkOpenOrWaitingForAutoClose();
        this.getInterceptor().afterTransactionBegin(this.getTransactionIfAccessible());
    }

    @Override
    public void flushBeforeTransactionCompletion() {
        boolean doFlush = this.isTransactionFlushable() && this.getHibernateFlushMode() != FlushMode.MANUAL;
        try {
            if (doFlush) {
                this.managedFlush();
            }
        }
        catch (RuntimeException re) {
            throw ExceptionMapperStandardImpl.INSTANCE.mapManagedFlushFailure("error during managed flush", re, this);
        }
    }

    private boolean isTransactionFlushable() {
        if (this.getCurrentTransaction() == null) {
            return true;
        }
        TransactionStatus status = this.getCurrentTransaction().getStatus();
        return status == TransactionStatus.ACTIVE || status == TransactionStatus.COMMITTING;
    }

    @Override
    public boolean isFlushBeforeCompletionEnabled() {
        return this.getHibernateFlushMode() != FlushMode.MANUAL;
    }

    @Override
    public SessionImplementor getSession() {
        return this;
    }

    @Override
    public LockOptions getLockRequest(LockModeType lockModeType, Map<String, Object> properties) {
        LockOptions lockOptions = new LockOptions();
        if (this.lockOptions != null) {
            LockOptions.copy(this.lockOptions, lockOptions);
        }
        lockOptions.setLockMode(LockModeTypeHelper.getLockMode(lockModeType));
        if (properties != null) {
            LockOptionsHelper.applyPropertiesToLockOptions(properties, () -> lockOptions);
        }
        return lockOptions;
    }

    public void remove(Object entity) {
        this.checkOpen();
        try {
            this.delete(entity);
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public <T> T find(Class<T> entityClass, Object primaryKey) {
        return this.find(entityClass, primaryKey, null, null);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) {
        return this.find(entityClass, primaryKey, null, properties);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockModeType) {
        return this.find(entityClass, primaryKey, lockModeType, null);
    }

    public <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockModeType, Map<String, Object> properties) {
        this.checkOpen();
        LockOptions lockOptions = null;
        try {
            this.getLoadQueryInfluencers().getEffectiveEntityGraph().applyConfiguredGraph(properties);
            Boolean readOnly = properties == null ? null : (Boolean)properties.get("org.hibernate.readOnly");
            this.getLoadQueryInfluencers().setReadOnly(readOnly);
            IdentifierLoadAccess loadAccess = this.byId((Class)entityClass);
            loadAccess.with(this.determineAppropriateLocalCacheMode(properties));
            if (lockModeType != null) {
                if (!LockModeType.NONE.equals((Object)lockModeType)) {
                    this.checkTransactionNeededForUpdateOperation();
                }
                lockOptions = this.buildLockOptions(lockModeType, properties);
                loadAccess.with(lockOptions);
            }
            if (this.getLoadQueryInfluencers().getEffectiveEntityGraph().getSemantic() == GraphSemantic.FETCH) {
                this.setEnforcingFetchGraph(true);
            }
            Object t = loadAccess.load((Serializable)primaryKey);
            return t;
        }
        catch (EntityNotFoundException ignored) {
            if (log.isDebugEnabled()) {
                String entityName = entityClass != null ? entityClass.getName() : null;
                String identifierValue = primaryKey != null ? primaryKey.toString() : null;
                log.ignoringEntityNotFound(entityName, identifierValue);
            }
            T t = null;
            return t;
        }
        catch (ObjectDeletedException e) {
            T t = null;
            return t;
        }
        catch (ObjectNotFoundException e) {
            throw new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e));
        }
        catch (ClassCastException | MappingException | TypeMismatchException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(((Throwable)e).getMessage(), (Throwable)e));
        }
        catch (JDBCException e) {
            if (this.accessTransaction().isActive() && this.accessTransaction().getRollbackOnly()) {
                if (log.isDebugEnabled()) {
                    log.debug("JDBCException was thrown for a transaction marked for rollback; this is probably due to an operation failing fast due to the transaction marked for rollback.", (Throwable)((Object)e));
                }
                T t = null;
                return t;
            }
            throw this.getExceptionConverter().convert(e, lockOptions);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e, lockOptions);
        }
        finally {
            this.getLoadQueryInfluencers().getEffectiveEntityGraph().clear();
            this.getLoadQueryInfluencers().setReadOnly(null);
            this.setEnforcingFetchGraph(false);
        }
    }

    protected CacheMode determineAppropriateLocalCacheMode(Map<String, Object> localProperties) {
        CacheRetrieveMode retrieveMode = null;
        CacheStoreMode storeMode = null;
        if (localProperties != null) {
            retrieveMode = SessionImpl.determineCacheRetrieveMode(localProperties);
            storeMode = SessionImpl.determineCacheStoreMode(localProperties);
        }
        if (retrieveMode == null) {
            retrieveMode = this.fastSessionServices.getCacheRetrieveMode(this.properties);
        }
        if (storeMode == null) {
            storeMode = this.fastSessionServices.getCacheStoreMode(this.properties);
        }
        return CacheModeHelper.interpretCacheMode(storeMode, retrieveMode);
    }

    private static CacheRetrieveMode determineCacheRetrieveMode(Map<String, Object> settings) {
        CacheRetrieveMode cacheRetrieveMode = (CacheRetrieveMode)settings.get("javax.persistence.cache.retrieveMode");
        if (cacheRetrieveMode == null) {
            return (CacheRetrieveMode)settings.get("jakarta.persistence.cache.retrieveMode");
        }
        return cacheRetrieveMode;
    }

    private static CacheStoreMode determineCacheStoreMode(Map<String, Object> settings) {
        CacheStoreMode cacheStoreMode = (CacheStoreMode)settings.get("javax.persistence.cache.storeMode");
        if (cacheStoreMode == null) {
            return (CacheStoreMode)settings.get("jakarta.persistence.cache.storeMode");
        }
        return cacheStoreMode;
    }

    private void checkTransactionNeededForUpdateOperation() {
        this.checkTransactionNeededForUpdateOperation("no transaction is in progress");
    }

    public <T> T getReference(Class<T> entityClass, Object primaryKey) {
        this.checkOpen();
        try {
            return ((IdentifierLoadAccessImpl)this.byId((Class)entityClass)).getReference((Serializable)primaryKey);
        }
        catch (ClassCastException | MappingException | TypeMismatchException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(((Throwable)e).getMessage(), (Throwable)e));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public void lock(Object entity, LockModeType lockModeType) {
        this.lock(entity, lockModeType, null);
    }

    public void lock(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
        this.checkOpen();
        this.checkTransactionNeededForUpdateOperation();
        if (!this.contains(entity)) {
            throw new IllegalArgumentException("entity not in the persistence context");
        }
        LockOptions lockOptions = this.buildLockOptions(lockModeType, properties);
        try {
            this.buildLockRequest(lockOptions).lock(entity);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e, lockOptions);
        }
    }

    public void refresh(Object entity, Map<String, Object> properties) {
        this.refresh(entity, null, properties);
    }

    public void refresh(Object entity, LockModeType lockModeType) {
        this.refresh(entity, lockModeType, null);
    }

    public void refresh(Object entity, LockModeType lockModeType, Map<String, Object> properties) {
        this.checkOpen();
        CacheMode previousCacheMode = this.getCacheMode();
        CacheMode refreshCacheMode = this.determineAppropriateLocalCacheMode(properties);
        LockOptions lockOptions = null;
        try {
            this.setCacheMode(refreshCacheMode);
            if (!this.contains(entity)) {
                throw this.getExceptionConverter().convert(new IllegalArgumentException("Entity not managed"));
            }
            if (lockModeType != null) {
                if (!LockModeType.NONE.equals((Object)lockModeType)) {
                    this.checkTransactionNeededForUpdateOperation();
                }
                lockOptions = this.buildLockOptions(lockModeType, properties);
                this.refresh(entity, lockOptions);
            } else {
                this.refresh(entity);
            }
        }
        catch (MappingException e) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException(e.getMessage(), (Throwable)((Object)e)));
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e, lockOptions);
        }
        finally {
            this.setCacheMode(previousCacheMode);
        }
    }

    public void detach(Object entity) {
        this.checkOpen();
        try {
            this.evict(entity);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public LockModeType getLockMode(Object entity) {
        this.checkOpen();
        if (!this.isTransactionInProgress()) {
            throw new TransactionRequiredException("Call to EntityManager#getLockMode should occur within transaction according to spec");
        }
        if (!this.contains(entity)) {
            throw this.getExceptionConverter().convert(new IllegalArgumentException("entity not in the persistence context"));
        }
        return LockModeTypeHelper.getLockModeType(this.getCurrentLockMode(entity));
    }

    public void setProperty(String propertyName, Object value) {
        this.checkOpen();
        if (!(value instanceof Serializable)) {
            log.warnf("Property '" + propertyName + "' is not serializable, value won't be set.", new Object[0]);
            return;
        }
        if (propertyName == null) {
            log.warnf("Property having key null is illegal; value won't be set.", new Object[0]);
            return;
        }
        if (this.properties == null) {
            this.properties = this.computeCurrentSessionProperties();
        }
        this.properties.put(propertyName, value);
        if ("org.hibernate.flushMode".equals(propertyName) || "org.hibernate.flushMode".equals(propertyName)) {
            this.setHibernateFlushMode(ConfigurationHelper.getFlushMode(value, FlushMode.AUTO));
        } else if ("javax.persistence.lock.scope".equals(propertyName) || "javax.persistence.lock.timeout".equals(propertyName) || "jakarta.persistence.lock.scope".equals(propertyName) || "jakarta.persistence.lock.timeout".equals(propertyName)) {
            LockOptionsHelper.applyPropertiesToLockOptions(this.properties, this::getLockOptionsForWrite);
        } else if ("javax.persistence.cache.retrieveMode".equals(propertyName) || "javax.persistence.cache.storeMode".equals(propertyName) || "jakarta.persistence.cache.retrieveMode".equals(propertyName) || "jakarta.persistence.cache.storeMode".equals(propertyName)) {
            this.getSession().setCacheMode(CacheModeHelper.interpretCacheMode(SessionImpl.determineCacheStoreMode(this.properties), SessionImpl.determineCacheRetrieveMode(this.properties)));
        }
    }

    private Map<String, Object> computeCurrentSessionProperties() {
        HashMap<String, Object> map = new HashMap<String, Object>(this.fastSessionServices.defaultSessionProperties);
        map.put("org.hibernate.flushMode", this.getHibernateFlushMode().name());
        return map;
    }

    public Map<String, Object> getProperties() {
        if (this.properties == null) {
            this.properties = this.computeCurrentSessionProperties();
        }
        return Collections.unmodifiableMap(this.properties);
    }

    @Override
    protected void initQueryFromNamedDefinition(Query query, NamedQueryDefinition namedQueryDefinition) {
        super.initQueryFromNamedDefinition(query, namedQueryDefinition);
        if (namedQueryDefinition.getLockOptions() != null && namedQueryDefinition.getLockOptions().getLockMode() != null) {
            query.setLockMode(LockModeTypeHelper.getLockModeType(namedQueryDefinition.getLockOptions().getLockMode()));
        }
    }

    public StoredProcedureQuery createNamedStoredProcedureQuery(String name) {
        this.checkOpen();
        try {
            ProcedureCallMemento memento = this.getFactory().getNamedQueryRepository().getNamedProcedureCallMemento(name);
            if (memento == null) {
                throw new IllegalArgumentException("No @NamedStoredProcedureQuery was found with that name : " + name);
            }
            return memento.makeProcedureCall(this);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName) {
        try {
            return this.createStoredProcedureCall(procedureName);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, Class ... resultClasses) {
        try {
            return this.createStoredProcedureCall(procedureName, resultClasses);
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public StoredProcedureQuery createStoredProcedureQuery(String procedureName, String ... resultSetMappings) {
        this.checkOpen();
        try {
            try {
                return this.createStoredProcedureCall(procedureName, resultSetMappings);
            }
            catch (UnknownSqlResultSetMappingException unknownResultSetMapping) {
                throw new IllegalArgumentException(unknownResultSetMapping.getMessage(), (Throwable)((Object)unknownResultSetMapping));
            }
        }
        catch (RuntimeException e) {
            throw this.getExceptionConverter().convert(e);
        }
    }

    public void joinTransaction() {
        this.checkOpen();
        this.joinTransaction(true);
    }

    private void joinTransaction(boolean explicitRequest) {
        if (!this.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta()) {
            if (explicitRequest) {
                log.callingJoinTransactionOnNonJtaEntityManager();
            }
            return;
        }
        try {
            this.getTransactionCoordinator().explicitJoin();
        }
        catch (TransactionRequiredForJoinException e) {
            throw new TransactionRequiredException(e.getMessage());
        }
        catch (HibernateException he) {
            throw this.getExceptionConverter().convert(he);
        }
    }

    public boolean isJoinedToTransaction() {
        this.checkOpen();
        return this.getTransactionCoordinator().isJoined();
    }

    public <T> T unwrap(Class<T> clazz) {
        this.checkOpen();
        if (Session.class.isAssignableFrom(clazz)) {
            return (T)this;
        }
        if (SessionImplementor.class.isAssignableFrom(clazz)) {
            return (T)this;
        }
        if (SharedSessionContractImplementor.class.isAssignableFrom(clazz)) {
            return (T)this;
        }
        if (EntityManager.class.isAssignableFrom(clazz)) {
            return (T)this;
        }
        throw new PersistenceException("Hibernate cannot unwrap " + clazz);
    }

    public Object getDelegate() {
        this.checkOpen();
        return this;
    }

    public SessionFactoryImplementor getEntityManagerFactory() {
        this.checkOpen();
        return this.getFactory();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        this.checkOpen();
        return this.getFactory().getCriteriaBuilder();
    }

    public MetamodelImplementor getMetamodel() {
        this.checkOpen();
        return this.getFactory().getMetamodel();
    }

    @Override
    public <T> RootGraphImplementor<T> createEntityGraph(Class<T> rootType) {
        this.checkOpen();
        return new RootGraphImpl(null, this.getMetamodel().entity(rootType), this.getEntityManagerFactory());
    }

    @Override
    public RootGraphImplementor<?> createEntityGraph(String graphName) {
        this.checkOpen();
        RootGraphImplementor<?> named = this.getEntityManagerFactory().findEntityGraphByName(graphName);
        if (named != null) {
            return named.makeRootGraph(graphName, true);
        }
        return named;
    }

    @Override
    public RootGraphImplementor<?> getEntityGraph(String graphName) {
        this.checkOpen();
        RootGraphImplementor<?> named = this.getEntityManagerFactory().findEntityGraphByName(graphName);
        if (named == null) {
            throw new IllegalArgumentException("Could not locate EntityGraph with given name : " + graphName);
        }
        return named;
    }

    public List getEntityGraphs(Class entityClass) {
        this.checkOpen();
        return this.getEntityManagerFactory().findEntityGraphsByType(entityClass);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        if (log.isTraceEnabled()) {
            log.tracef("Serializing Session [%s]", this.getSessionIdentifier());
        }
        oos.defaultWriteObject();
        this.persistenceContext.serialize(oos);
        this.actionQueue.serialize(oos);
        oos.writeObject(this.loadQueryInfluencers);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException, SQLException {
        if (log.isTraceEnabled()) {
            log.tracef("Deserializing Session [%s]", this.getSessionIdentifier());
        }
        ois.defaultReadObject();
        this.persistenceContext = StatefulPersistenceContext.deserialize(ois, this);
        this.actionQueue = ActionQueue.deserialize(ois, this);
        this.loadQueryInfluencers = (LoadQueryInfluencers)ois.readObject();
        for (String filterName : this.loadQueryInfluencers.getEnabledFilterNames()) {
            ((FilterImpl)this.loadQueryInfluencers.getEnabledFilter(filterName)).afterDeserialize((SessionFactoryImplementor)this.getFactory());
        }
    }

    private Boolean getReadOnlyFromLoadQueryInfluencers() {
        Boolean readOnly = null;
        if (this.loadQueryInfluencers != null) {
            readOnly = this.loadQueryInfluencers.getReadOnly();
        }
        return readOnly;
    }

    @Override
    public boolean isEnforcingFetchGraph() {
        return this.isEnforcingFetchGraph;
    }

    @Override
    public void setEnforcingFetchGraph(boolean isEnforcingFetchGraph) {
        this.isEnforcingFetchGraph = isEnforcingFetchGraph;
    }

    private class SimpleNaturalIdLoadAccessImpl<T>
    extends BaseNaturalIdLoadAccessImpl<T>
    implements SimpleNaturalIdLoadAccess<T> {
        private final String naturalIdAttributeName;

        private SimpleNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
            super(entityPersister);
            if (entityPersister.getNaturalIdentifierProperties().length != 1) {
                throw new HibernateException(String.format("Entity [%s] did not define a simple natural id", entityPersister.getEntityName()));
            }
            int naturalIdAttributePosition = entityPersister.getNaturalIdentifierProperties()[0];
            this.naturalIdAttributeName = entityPersister.getPropertyNames()[naturalIdAttributePosition];
        }

        private SimpleNaturalIdLoadAccessImpl(String entityName) {
            this(sessionImpl.locateEntityPersister(entityName));
        }

        private SimpleNaturalIdLoadAccessImpl(Class entityClass) {
            this(sessionImpl.locateEntityPersister(entityClass));
        }

        @Override
        public final SimpleNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
            return (SimpleNaturalIdLoadAccessImpl)super.with(lockOptions);
        }

        private Map<String, Object> getNaturalIdParameters(Object naturalIdValue) {
            return Collections.singletonMap(this.naturalIdAttributeName, naturalIdValue);
        }

        @Override
        public SimpleNaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
            super.synchronizationEnabled(synchronizationEnabled);
            return this;
        }

        @Override
        public T getReference(Object naturalIdValue) {
            Serializable entityId = this.resolveNaturalId(this.getNaturalIdParameters(naturalIdValue));
            if (entityId == null) {
                return null;
            }
            return this.getIdentifierLoadAccess().getReference(entityId);
        }

        @Override
        public T load(Object naturalIdValue) {
            Serializable entityId = this.resolveNaturalId(this.getNaturalIdParameters(naturalIdValue));
            if (entityId == null) {
                return null;
            }
            try {
                return this.getIdentifierLoadAccess().load(entityId);
            }
            catch (EntityNotFoundException | ObjectNotFoundException object) {
                return null;
            }
        }

        @Override
        public Optional<T> loadOptional(Serializable naturalIdValue) {
            return Optional.ofNullable(this.load(naturalIdValue));
        }
    }

    private class NaturalIdLoadAccessImpl<T>
    extends BaseNaturalIdLoadAccessImpl<T>
    implements NaturalIdLoadAccess<T> {
        private final Map<String, Object> naturalIdParameters;

        private NaturalIdLoadAccessImpl(EntityPersister entityPersister) {
            super(entityPersister);
            this.naturalIdParameters = new LinkedHashMap<String, Object>();
        }

        private NaturalIdLoadAccessImpl(String entityName) {
            this(sessionImpl.locateEntityPersister(entityName));
        }

        private NaturalIdLoadAccessImpl(Class entityClass) {
            this(sessionImpl.locateEntityPersister(entityClass));
        }

        @Override
        public NaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
            return (NaturalIdLoadAccessImpl)super.with(lockOptions);
        }

        @Override
        public NaturalIdLoadAccess<T> using(String attributeName, Object value) {
            this.naturalIdParameters.put(attributeName, value);
            return this;
        }

        @Override
        public NaturalIdLoadAccessImpl<T> setSynchronizationEnabled(boolean synchronizationEnabled) {
            super.synchronizationEnabled(synchronizationEnabled);
            return this;
        }

        @Override
        public final T getReference() {
            Serializable entityId = this.resolveNaturalId(this.naturalIdParameters);
            if (entityId == null) {
                return null;
            }
            return this.getIdentifierLoadAccess().getReference(entityId);
        }

        @Override
        public final T load() {
            Serializable entityId = this.resolveNaturalId(this.naturalIdParameters);
            if (entityId == null) {
                return null;
            }
            try {
                return this.getIdentifierLoadAccess().load(entityId);
            }
            catch (EntityNotFoundException | ObjectNotFoundException object) {
                return null;
            }
        }

        @Override
        public Optional<T> loadOptional() {
            return Optional.ofNullable(this.load());
        }
    }

    private abstract class BaseNaturalIdLoadAccessImpl<T> {
        private final EntityPersister entityPersister;
        private LockOptions lockOptions;
        private boolean synchronizationEnabled = true;

        private BaseNaturalIdLoadAccessImpl(EntityPersister entityPersister) {
            this.entityPersister = entityPersister;
            if (!entityPersister.hasNaturalIdentifier()) {
                throw new HibernateException(String.format("Entity [%s] did not define a natural id", entityPersister.getEntityName()));
            }
        }

        public BaseNaturalIdLoadAccessImpl<T> with(LockOptions lockOptions) {
            this.lockOptions = lockOptions;
            return this;
        }

        protected void synchronizationEnabled(boolean synchronizationEnabled) {
            this.synchronizationEnabled = synchronizationEnabled;
        }

        protected final Serializable resolveNaturalId(Map<String, Object> naturalIdParameters) {
            this.performAnyNeededCrossReferenceSynchronizations();
            ResolveNaturalIdEvent event = new ResolveNaturalIdEvent(naturalIdParameters, this.entityPersister, SessionImpl.this);
            SessionImpl.this.fireResolveNaturalId(event);
            if (event.getEntityId() == PersistenceContext.NaturalIdHelper.INVALID_NATURAL_ID_REFERENCE) {
                return null;
            }
            return event.getEntityId();
        }

        protected void performAnyNeededCrossReferenceSynchronizations() {
            if (!this.synchronizationEnabled) {
                return;
            }
            if (this.entityPersister.getEntityMetamodel().hasImmutableNaturalId()) {
                return;
            }
            if (!SessionImpl.this.isTransactionInProgress()) {
                return;
            }
            PersistenceContext persistenceContext = SessionImpl.this.getPersistenceContextInternal();
            boolean debugEnabled = log.isDebugEnabled();
            for (Serializable pk : persistenceContext.getNaturalIdHelper().getCachedPkResolutions(this.entityPersister)) {
                EntityKey entityKey = SessionImpl.this.generateEntityKey(pk, this.entityPersister);
                Object entity = persistenceContext.getEntity(entityKey);
                EntityEntry entry = persistenceContext.getEntry(entity);
                if (entry == null) {
                    if (!debugEnabled) continue;
                    log.debug("Cached natural-id/pk resolution linked to null EntityEntry in persistence context : " + MessageHelper.infoString(this.entityPersister, pk, (SessionFactoryImplementor)SessionImpl.this.getFactory()));
                    continue;
                }
                if (!entry.requiresDirtyCheck(entity) || entry.getStatus() != Status.MANAGED) continue;
                persistenceContext.getNaturalIdHelper().handleSynchronization(this.entityPersister, pk, entity);
            }
        }

        protected final IdentifierLoadAccess getIdentifierLoadAccess() {
            IdentifierLoadAccessImpl identifierLoadAccess = new IdentifierLoadAccessImpl(this.entityPersister);
            if (this.lockOptions != null) {
                identifierLoadAccess.with(this.lockOptions);
            }
            return identifierLoadAccess;
        }

        protected EntityPersister entityPersister() {
            return this.entityPersister;
        }
    }

    private class MultiIdentifierLoadAccessImpl<T>
    implements MultiIdentifierLoadAccess<T>,
    MultiLoadOptions {
        private final EntityPersister entityPersister;
        private LockOptions lockOptions;
        private CacheMode cacheMode;
        private RootGraphImplementor<T> rootGraph;
        private GraphSemantic graphSemantic;
        private Integer batchSize;
        private boolean sessionCheckingEnabled;
        private boolean returnOfDeletedEntitiesEnabled;
        private boolean orderedReturnEnabled = true;

        public MultiIdentifierLoadAccessImpl(EntityPersister entityPersister) {
            this.entityPersister = entityPersister;
        }

        @Override
        public LockOptions getLockOptions() {
            return this.lockOptions;
        }

        @Override
        public final MultiIdentifierLoadAccess<T> with(LockOptions lockOptions) {
            this.lockOptions = lockOptions;
            return this;
        }

        @Override
        public MultiIdentifierLoadAccess<T> with(CacheMode cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        @Override
        public MultiIdentifierLoadAccess<T> with(RootGraph<T> graph, GraphSemantic semantic) {
            this.rootGraph = (RootGraphImplementor)graph;
            this.graphSemantic = semantic;
            return this;
        }

        @Override
        public Integer getBatchSize() {
            return this.batchSize;
        }

        @Override
        public MultiIdentifierLoadAccess<T> withBatchSize(int batchSize) {
            this.batchSize = batchSize < 1 ? null : Integer.valueOf(batchSize);
            return this;
        }

        @Override
        public boolean isSessionCheckingEnabled() {
            return this.sessionCheckingEnabled;
        }

        @Override
        public boolean isSecondLevelCacheCheckingEnabled() {
            return this.cacheMode == CacheMode.NORMAL || this.cacheMode == CacheMode.GET;
        }

        @Override
        public MultiIdentifierLoadAccess<T> enableSessionCheck(boolean enabled) {
            this.sessionCheckingEnabled = enabled;
            return this;
        }

        @Override
        public boolean isReturnOfDeletedEntitiesEnabled() {
            return this.returnOfDeletedEntitiesEnabled;
        }

        @Override
        public MultiIdentifierLoadAccess<T> enableReturnOfDeletedEntities(boolean enabled) {
            this.returnOfDeletedEntitiesEnabled = enabled;
            return this;
        }

        @Override
        public boolean isOrderReturnEnabled() {
            return this.orderedReturnEnabled;
        }

        @Override
        public MultiIdentifierLoadAccess<T> enableOrderedReturn(boolean enabled) {
            this.orderedReturnEnabled = enabled;
            return this;
        }

        @Override
        public <K extends Serializable> List<T> multiLoad(K ... ids) {
            return this.perform(() -> this.entityPersister.multiLoad((Serializable[])ids, SessionImpl.this, this));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public List<T> perform(Supplier<List<T>> executor) {
            CacheMode sessionCacheMode = SessionImpl.this.getCacheMode();
            boolean cacheModeChanged = false;
            if (this.cacheMode != null && this.cacheMode != sessionCacheMode) {
                SessionImpl.this.setCacheMode(this.cacheMode);
                cacheModeChanged = true;
            }
            try {
                if (this.graphSemantic != null) {
                    if (this.rootGraph == null) {
                        throw new IllegalArgumentException("Graph semantic specified, but no RootGraph was supplied");
                    }
                    SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().applyGraph(this.rootGraph, this.graphSemantic);
                }
                try {
                    List<T> list = executor.get();
                    if (this.graphSemantic != null) {
                        SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().clear();
                    }
                    return list;
                }
                catch (Throwable throwable) {
                    if (this.graphSemantic != null) {
                        SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().clear();
                    }
                    throw throwable;
                }
            }
            finally {
                if (cacheModeChanged) {
                    SessionImpl.this.setCacheMode(sessionCacheMode);
                }
            }
        }

        @Override
        public <K extends Serializable> List<T> multiLoad(List<K> ids) {
            return this.perform(() -> this.entityPersister.multiLoad(ids.toArray(new Serializable[ids.size()]), SessionImpl.this, this));
        }
    }

    private class IdentifierLoadAccessImpl<T>
    implements IdentifierLoadAccess<T> {
        private final EntityPersister entityPersister;
        private LockOptions lockOptions;
        private CacheMode cacheMode;
        private RootGraphImplementor<T> rootGraph;
        private GraphSemantic graphSemantic;

        private IdentifierLoadAccessImpl(EntityPersister entityPersister) {
            this.entityPersister = entityPersister;
        }

        private IdentifierLoadAccessImpl(String entityName) {
            this(sessionImpl.locateEntityPersister(entityName));
        }

        private IdentifierLoadAccessImpl(Class<T> entityClass) {
            this(sessionImpl.locateEntityPersister(entityClass));
        }

        @Override
        public final IdentifierLoadAccessImpl<T> with(LockOptions lockOptions) {
            this.lockOptions = lockOptions;
            return this;
        }

        @Override
        public IdentifierLoadAccess<T> with(CacheMode cacheMode) {
            this.cacheMode = cacheMode;
            return this;
        }

        @Override
        public IdentifierLoadAccess<T> with(RootGraph<T> graph, GraphSemantic semantic) {
            this.rootGraph = (RootGraphImplementor)graph;
            this.graphSemantic = semantic;
            return this;
        }

        @Override
        public final T getReference(Serializable id) {
            return (T)this.perform(() -> this.doGetReference(id));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected T perform(Supplier<T> executor) {
            CacheMode sessionCacheMode = SessionImpl.this.getCacheMode();
            boolean cacheModeChanged = false;
            if (this.cacheMode != null && this.cacheMode != sessionCacheMode) {
                SessionImpl.this.setCacheMode(this.cacheMode);
                cacheModeChanged = true;
            }
            try {
                T t;
                block11: {
                    if (this.graphSemantic != null) {
                        if (this.rootGraph == null) {
                            throw new IllegalArgumentException("Graph semantic specified, but no RootGraph was supplied");
                        }
                        SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().applyGraph(this.rootGraph, this.graphSemantic);
                    }
                    try {
                        t = executor.get();
                        if (this.graphSemantic == null) break block11;
                        SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().clear();
                    }
                    catch (Throwable throwable) {
                        if (this.graphSemantic != null) {
                            SessionImpl.this.loadQueryInfluencers.getEffectiveEntityGraph().clear();
                        }
                        throw throwable;
                    }
                }
                return t;
            }
            finally {
                if (cacheModeChanged) {
                    SessionImpl.this.setCacheMode(sessionCacheMode);
                }
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected T doGetReference(Serializable id) {
            if (this.lockOptions != null) {
                LoadEvent event = new LoadEvent(id, this.entityPersister.getEntityName(), this.lockOptions, (EventSource)SessionImpl.this, SessionImpl.this.getReadOnlyFromLoadQueryInfluencers());
                SessionImpl.this.fireLoad(event, LoadEventListener.LOAD);
                return (T)event.getResult();
            }
            LoadEvent event = new LoadEvent(id, this.entityPersister.getEntityName(), false, (EventSource)SessionImpl.this, SessionImpl.this.getReadOnlyFromLoadQueryInfluencers());
            boolean success = false;
            try {
                SessionImpl.this.fireLoad(event, LoadEventListener.LOAD);
                if (event.getResult() == null) {
                    SessionImpl.this.getFactory().getEntityNotFoundDelegate().handleEntityNotFound(this.entityPersister.getEntityName(), id);
                }
                success = true;
                Object object = event.getResult();
                return (T)object;
            }
            finally {
                SessionImpl.this.afterOperation(success);
            }
        }

        @Override
        public final T load(Serializable id) {
            return (T)this.perform(() -> this.doLoad(id));
        }

        @Override
        public Optional<T> loadOptional(Serializable id) {
            return Optional.ofNullable(this.perform(() -> this.doLoad(id)));
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected final T doLoad(Serializable id) {
            if (this.lockOptions != null) {
                LoadEvent event = new LoadEvent(id, this.entityPersister.getEntityName(), this.lockOptions, (EventSource)SessionImpl.this, SessionImpl.this.getReadOnlyFromLoadQueryInfluencers());
                SessionImpl.this.fireLoad(event, LoadEventListener.GET);
                Object result = event.getResult();
                this.initializeIfNecessary(result);
                return (T)result;
            }
            LoadEvent event = new LoadEvent(id, this.entityPersister.getEntityName(), false, (EventSource)SessionImpl.this, SessionImpl.this.getReadOnlyFromLoadQueryInfluencers());
            boolean success = false;
            try {
                SessionImpl.this.fireLoad(event, LoadEventListener.GET);
                success = true;
            }
            catch (ObjectNotFoundException objectNotFoundException) {
            }
            finally {
                SessionImpl.this.afterOperation(success);
            }
            Object result = event.getResult();
            this.initializeIfNecessary(result);
            return (T)result;
        }

        private void initializeIfNecessary(Object result) {
            if (result == null) {
                return;
            }
            if (result instanceof HibernateProxy) {
                HibernateProxy hibernateProxy = (HibernateProxy)result;
                LazyInitializer initializer = hibernateProxy.getHibernateLazyInitializer();
                if (initializer.isUninitialized()) {
                    initializer.initialize();
                }
                return;
            }
            BytecodeEnhancementMetadata enhancementMetadata = this.entityPersister.getEntityMetamodel().getBytecodeEnhancementMetadata();
            if (!enhancementMetadata.isEnhancedForLazyLoading()) {
                return;
            }
            BytecodeLazyAttributeInterceptor interceptor = enhancementMetadata.extractLazyInterceptor(result);
            if (interceptor instanceof EnhancementAsProxyLazinessInterceptor) {
                ((EnhancementAsProxyLazinessInterceptor)interceptor).forceInitialize(result, null);
            }
        }
    }

    private class LockRequestImpl
    implements Session.LockRequest {
        private final LockOptions lockOptions = new LockOptions();

        private LockRequestImpl(LockOptions lo) {
            LockOptions.copy(lo, this.lockOptions);
        }

        @Override
        public LockMode getLockMode() {
            return this.lockOptions.getLockMode();
        }

        @Override
        public Session.LockRequest setLockMode(LockMode lockMode) {
            this.lockOptions.setLockMode(lockMode);
            return this;
        }

        @Override
        public int getTimeOut() {
            return this.lockOptions.getTimeOut();
        }

        @Override
        public Session.LockRequest setTimeOut(int timeout) {
            this.lockOptions.setTimeOut(timeout);
            return this;
        }

        @Override
        public boolean getScope() {
            return this.lockOptions.getScope();
        }

        @Override
        public Session.LockRequest setScope(boolean scope) {
            this.lockOptions.setScope(scope);
            return this;
        }

        @Override
        public void lock(String entityName, Object object) throws HibernateException {
            SessionImpl.this.fireLock(entityName, object, this.lockOptions);
        }

        @Override
        public void lock(Object object) throws HibernateException {
            SessionImpl.this.fireLock(object, this.lockOptions);
        }
    }

    private static class SharedSessionBuilderImpl<T extends SharedSessionBuilder>
    extends SessionFactoryImpl.SessionBuilderImpl<T>
    implements SharedSessionBuilder<T>,
    SharedSessionCreationOptions {
        private final SessionImpl session;
        private boolean shareTransactionContext;

        private SharedSessionBuilderImpl(SessionImpl session) {
            super((SessionFactoryImpl)session.getFactory());
            this.session = session;
            super.tenantIdentifier(session.getTenantIdentifier());
        }

        @Override
        public T tenantIdentifier(String tenantIdentifier) {
            throw new SessionException("Cannot redefine tenant identifier on child session");
        }

        @Override
        public T interceptor() {
            return (T)((SharedSessionBuilder)this.interceptor(this.session.getInterceptor()));
        }

        @Override
        public T connection() {
            this.shareTransactionContext = true;
            return (T)this;
        }

        @Override
        public T connectionReleaseMode() {
            return (T)((SharedSessionBuilder)this.connectionReleaseMode(this.session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode().getReleaseMode()));
        }

        @Override
        public T connectionHandlingMode() {
            return (T)((SharedSessionBuilder)this.connectionHandlingMode(this.session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode()));
        }

        @Override
        public T autoJoinTransactions() {
            return (T)((SharedSessionBuilder)this.autoJoinTransactions(this.session.isAutoCloseSessionEnabled()));
        }

        @Override
        public T flushMode() {
            return (T)((SharedSessionBuilder)this.flushMode(this.session.getHibernateFlushMode()));
        }

        @Override
        public T autoClose() {
            return (T)((SharedSessionBuilder)this.autoClose(this.session.autoClose));
        }

        @Override
        public boolean isTransactionCoordinatorShared() {
            return this.shareTransactionContext;
        }

        @Override
        public TransactionCoordinator getTransactionCoordinator() {
            return this.shareTransactionContext ? this.session.getTransactionCoordinator() : null;
        }

        @Override
        public JdbcCoordinator getJdbcCoordinator() {
            return this.shareTransactionContext ? this.session.getJdbcCoordinator() : null;
        }

        @Override
        public TransactionImplementor getTransaction() {
            return this.shareTransactionContext ? this.session.getCurrentTransaction() : null;
        }

        @Override
        public ActionQueue.TransactionCompletionProcesses getTransactionCompletionProcesses() {
            return this.shareTransactionContext ? this.session.getActionQueue().getTransactionCompletionProcesses() : null;
        }

        @Override
        public boolean isQueryParametersValidationEnabled() {
            return this.session.isQueryParametersValidationEnabled();
        }
    }

    private static class LobHelperImpl
    implements LobHelper {
        private final SessionImpl session;

        private LobHelperImpl(SessionImpl session) {
            this.session = session;
        }

        @Override
        public Blob createBlob(byte[] bytes) {
            return this.lobCreator().createBlob(bytes);
        }

        private LobCreator lobCreator() {
            return NonContextualLobCreator.INSTANCE;
        }

        @Override
        public Blob createBlob(InputStream stream, long length) {
            return this.lobCreator().createBlob(stream, length);
        }

        @Override
        public Clob createClob(String string) {
            return this.lobCreator().createClob(string);
        }

        @Override
        public Clob createClob(Reader reader, long length) {
            return this.lobCreator().createClob(reader, length);
        }

        @Override
        public NClob createNClob(String string) {
            return this.lobCreator().createNClob(string);
        }

        @Override
        public NClob createNClob(Reader reader, long length) {
            return this.lobCreator().createNClob(reader, length);
        }
    }
}

