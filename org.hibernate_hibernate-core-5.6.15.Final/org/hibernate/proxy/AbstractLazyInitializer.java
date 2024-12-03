/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.proxy;

import java.io.Serializable;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LazyInitializationException;
import org.hibernate.SessionException;
import org.hibernate.TransientObjectException;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.SessionFactoryRegistry;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.LazyInitializer;

public abstract class AbstractLazyInitializer
implements LazyInitializer {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(AbstractLazyInitializer.class);
    private String entityName;
    private Serializable id;
    private Object target;
    private boolean initialized;
    private boolean readOnly;
    private boolean unwrap;
    private transient SharedSessionContractImplementor session;
    private Boolean readOnlyBeforeAttachedToSession;
    private String sessionFactoryUuid;
    private boolean allowLoadOutsideTransaction;

    @Deprecated
    protected AbstractLazyInitializer() {
    }

    protected AbstractLazyInitializer(String entityName, Serializable id, SharedSessionContractImplementor session) {
        this.entityName = entityName;
        this.id = id;
        if (session == null) {
            this.unsetSession();
        } else {
            this.setSession(session);
        }
    }

    @Override
    public final String getEntityName() {
        return this.entityName;
    }

    @Override
    public final Serializable getInternalIdentifier() {
        return this.id;
    }

    @Override
    public final Serializable getIdentifier() {
        if (this.isUninitialized() && this.isInitializeProxyWhenAccessingIdentifier()) {
            this.initialize();
        }
        return this.id;
    }

    private boolean isInitializeProxyWhenAccessingIdentifier() {
        return this.getSession() != null && this.getSession().getFactory().getSessionFactoryOptions().getJpaCompliance().isJpaProxyComplianceEnabled();
    }

    @Override
    public final void setIdentifier(Serializable id) {
        this.id = id;
    }

    @Override
    public final boolean isUninitialized() {
        return !this.initialized;
    }

    @Override
    public final SharedSessionContractImplementor getSession() {
        return this.session;
    }

    @Override
    public final void setSession(SharedSessionContractImplementor s) throws HibernateException {
        if (s != this.session) {
            if (s == null) {
                this.unsetSession();
            } else {
                if (this.isConnectedToSession()) {
                    LOG.attemptToAssociateProxyWithTwoOpenSessions(this.entityName, this.id);
                    throw new HibernateException("illegally attempted to associate proxy [" + this.entityName + "#" + this.id + "] with two open Sessions");
                }
                this.session = s;
                if (this.readOnlyBeforeAttachedToSession == null) {
                    EntityPersister persister = s.getFactory().getEntityPersister(this.entityName);
                    this.setReadOnly(s.getPersistenceContext().isDefaultReadOnly() || !persister.isMutable());
                } else {
                    this.setReadOnly(this.readOnlyBeforeAttachedToSession);
                    this.readOnlyBeforeAttachedToSession = null;
                }
            }
        }
    }

    private static EntityKey generateEntityKeyOrNull(Serializable id, SharedSessionContractImplementor s, String entityName) {
        if (id == null || s == null || entityName == null) {
            return null;
        }
        return s.generateEntityKey(id, s.getFactory().getEntityPersister(entityName));
    }

    @Override
    public final void unsetSession() {
        this.prepareForPossibleLoadingOutsideTransaction();
        this.session = null;
        this.readOnly = false;
        this.readOnlyBeforeAttachedToSession = null;
    }

    @Override
    public final void initialize() throws HibernateException {
        if (!this.initialized) {
            try {
                if (this.allowLoadOutsideTransaction) {
                    this.permissiveInitialization();
                }
                if (this.session == null) {
                    throw new LazyInitializationException("could not initialize proxy [" + this.entityName + "#" + this.id + "] - no Session");
                }
                if (!this.session.isOpenOrWaitingForAutoClose()) {
                    throw new LazyInitializationException("could not initialize proxy [" + this.entityName + "#" + this.id + "] - the owning Session was closed");
                }
                if (!this.session.isConnected()) {
                    throw new LazyInitializationException("could not initialize proxy [" + this.entityName + "#" + this.id + "] - the owning Session is disconnected");
                }
                this.target = this.session.immediateLoad(this.entityName, this.id);
                this.initialized = true;
                this.checkTargetState(this.session);
            }
            finally {
                if (this.session != null && !this.session.isTransactionInProgress()) {
                    this.session.getJdbcCoordinator().afterTransaction();
                }
            }
        } else {
            this.checkTargetState(this.session);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void permissiveInitialization() {
        block15: {
            if (this.session == null) {
                if (this.sessionFactoryUuid == null) {
                    throw new LazyInitializationException("could not initialize proxy [" + this.entityName + "#" + this.id + "] - no Session");
                }
                try {
                    SessionFactoryImplementor sf = (SessionFactoryImplementor)SessionFactoryRegistry.INSTANCE.getSessionFactory(this.sessionFactoryUuid);
                    SharedSessionContractImplementor session = (SharedSessionContractImplementor)((Object)sf.openSession());
                    session.getPersistenceContext().setDefaultReadOnly(true);
                    session.setFlushMode(FlushMode.MANUAL);
                    boolean isJTA = session.getTransactionCoordinator().getTransactionCoordinatorBuilder().isJta();
                    if (!isJTA) {
                        session.beginTransaction();
                    }
                    try {
                        this.target = session.immediateLoad(this.entityName, this.id);
                        this.initialized = true;
                        this.checkTargetState(session);
                        break block15;
                    }
                    finally {
                        try {
                            if (!isJTA) {
                                session.getTransaction().commit();
                            }
                            session.close();
                        }
                        catch (Exception e) {
                            LOG.warn("Unable to close temporary session used to load lazy proxy associated to no session");
                        }
                    }
                }
                catch (Exception e) {
                    LOG.error("Initialization failure [" + this.entityName + "#" + this.id + "]", e);
                    throw new LazyInitializationException(e.getMessage());
                }
            }
            if (this.session.isOpenOrWaitingForAutoClose() && this.session.isConnected()) {
                this.target = this.session.immediateLoad(this.entityName, this.id);
                this.initialized = true;
                this.checkTargetState(this.session);
            } else {
                throw new LazyInitializationException("could not initialize proxy [" + this.entityName + "#" + this.id + "] - Session was closed or disced");
            }
        }
    }

    public final void initializeWithoutLoadIfPossible() {
        if (!this.initialized && this.session != null && this.session.isOpenOrWaitingForAutoClose()) {
            EntityKey key = this.session.generateEntityKey(this.getInternalIdentifier(), this.session.getFactory().getMetamodel().entityPersister(this.getEntityName()));
            Object entity = this.session.getPersistenceContextInternal().getEntity(key);
            if (entity != null) {
                this.setImplementation(entity);
            }
        }
    }

    protected void prepareForPossibleLoadingOutsideTransaction() {
        if (this.session != null) {
            this.allowLoadOutsideTransaction = this.session.getFactory().getSessionFactoryOptions().isInitializeLazyStateOutsideTransactionsEnabled();
            if (this.allowLoadOutsideTransaction && this.sessionFactoryUuid == null) {
                this.sessionFactoryUuid = this.session.getFactory().getUuid();
            }
        }
    }

    private void checkTargetState(SharedSessionContractImplementor session) {
        if (!this.unwrap && this.target == null) {
            session.getFactory().getEntityNotFoundDelegate().handleEntityNotFound(this.entityName, this.id);
        }
    }

    protected final boolean isConnectedToSession() {
        return this.getProxyOrNull() != null;
    }

    private Object getProxyOrNull() {
        EntityKey entityKey = AbstractLazyInitializer.generateEntityKeyOrNull(this.getInternalIdentifier(), this.session, this.getEntityName());
        if (entityKey != null && this.session != null && this.session.isOpenOrWaitingForAutoClose()) {
            return this.session.getPersistenceContextInternal().getProxy(entityKey);
        }
        return null;
    }

    @Override
    public final Object getImplementation() {
        this.initialize();
        return this.target;
    }

    @Override
    public final void setImplementation(Object target) {
        this.target = target;
        this.initialized = true;
    }

    @Override
    public final Object getImplementation(SharedSessionContractImplementor s) throws HibernateException {
        EntityKey entityKey = AbstractLazyInitializer.generateEntityKeyOrNull(this.getInternalIdentifier(), s, this.getEntityName());
        return entityKey == null ? null : s.getPersistenceContext().getEntity(entityKey);
    }

    protected final Object getTarget() {
        return this.target;
    }

    @Override
    public final boolean isReadOnlySettingAvailable() {
        return this.session != null && !this.session.isClosed();
    }

    private void errorIfReadOnlySettingNotAvailable() {
        if (this.session == null) {
            throw new TransientObjectException("Proxy [" + this.entityName + "#" + this.id + "] is detached (i.e, session is null). The read-only/modifiable setting is only accessible when the proxy is associated with an open session.");
        }
        if (!this.session.isOpenOrWaitingForAutoClose()) {
            throw new SessionException("Session is closed. The read-only/modifiable setting is only accessible when the proxy [" + this.entityName + "#" + this.id + "] is associated with an open session.");
        }
    }

    @Override
    public final boolean isReadOnly() {
        this.errorIfReadOnlySettingNotAvailable();
        return this.readOnly;
    }

    @Override
    public final void setReadOnly(boolean readOnly) {
        this.errorIfReadOnlySettingNotAvailable();
        if (this.readOnly != readOnly) {
            EntityPersister persister = this.session.getFactory().getEntityPersister(this.entityName);
            if (!persister.isMutable() && !readOnly) {
                throw new IllegalStateException("cannot make proxies [" + this.entityName + "#" + this.id + "] for immutable entities modifiable");
            }
            this.readOnly = readOnly;
            if (this.initialized) {
                EntityKey key = AbstractLazyInitializer.generateEntityKeyOrNull(this.getInternalIdentifier(), this.session, this.getEntityName());
                PersistenceContext persistenceContext = this.session.getPersistenceContext();
                if (key != null && persistenceContext.containsEntity(key)) {
                    persistenceContext.setReadOnly(this.target, readOnly);
                }
            }
        }
    }

    public final Boolean isReadOnlyBeforeAttachedToSession() {
        if (this.isReadOnlySettingAvailable()) {
            throw new IllegalStateException("Cannot call isReadOnlyBeforeAttachedToSession when isReadOnlySettingAvailable == true [" + this.entityName + "#" + this.id + "]");
        }
        return this.readOnlyBeforeAttachedToSession;
    }

    protected boolean isAllowLoadOutsideTransaction() {
        return this.allowLoadOutsideTransaction;
    }

    protected String getSessionFactoryUuid() {
        return this.sessionFactoryUuid;
    }

    final void afterDeserialization(Boolean readOnlyBeforeAttachedToSession, String sessionFactoryUuid, boolean allowLoadOutsideTransaction) {
        if (this.isReadOnlySettingAvailable()) {
            throw new IllegalStateException("Cannot call afterDeserialization when isReadOnlySettingAvailable == true [" + this.entityName + "#" + this.id + "]");
        }
        this.readOnlyBeforeAttachedToSession = readOnlyBeforeAttachedToSession;
        this.sessionFactoryUuid = sessionFactoryUuid;
        this.allowLoadOutsideTransaction = allowLoadOutsideTransaction;
    }

    @Override
    public boolean isUnwrap() {
        return this.unwrap;
    }

    @Override
    public void setUnwrap(boolean unwrap) {
        this.unwrap = unwrap;
    }
}

