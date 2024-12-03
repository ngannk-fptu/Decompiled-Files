/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 *  org.hibernate.ConnectionReleaseMode
 *  org.hibernate.FlushMode
 *  org.hibernate.HibernateException
 *  org.hibernate.Interceptor
 *  org.hibernate.Session
 *  org.hibernate.SessionFactory
 *  org.hibernate.Transaction
 *  org.hibernate.TransactionException
 *  org.hibernate.engine.spi.SessionImplementor
 *  org.hibernate.resource.transaction.spi.TransactionStatus
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.jdbc.datasource.ConnectionHolder
 *  org.springframework.jdbc.datasource.DataSourceUtils
 *  org.springframework.jdbc.datasource.JdbcTransactionObjectSupport
 *  org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.IllegalTransactionStateException
 *  org.springframework.transaction.InvalidIsolationLevelException
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionSystemException
 *  org.springframework.transaction.support.AbstractPlatformTransactionManager
 *  org.springframework.transaction.support.DefaultTransactionStatus
 *  org.springframework.transaction.support.ResourceTransactionManager
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 */
package org.springframework.orm.hibernate5;

import java.sql.Connection;
import java.util.function.Consumer;
import javax.persistence.PersistenceException;
import javax.sql.DataSource;
import org.hibernate.ConnectionReleaseMode;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.TransactionException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.lang.Nullable;
import org.springframework.orm.hibernate5.SessionFactoryUtils;
import org.springframework.orm.hibernate5.SessionHolder;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.InvalidIsolationLevelException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public class HibernateTransactionManager
extends AbstractPlatformTransactionManager
implements ResourceTransactionManager,
BeanFactoryAware,
InitializingBean {
    @Nullable
    private SessionFactory sessionFactory;
    @Nullable
    private DataSource dataSource;
    private boolean autodetectDataSource = true;
    private boolean prepareConnection = true;
    private boolean allowResultAccessAfterCompletion = false;
    private boolean hibernateManagedSession = false;
    @Nullable
    private Consumer<Session> sessionInitializer;
    @Nullable
    private Object entityInterceptor;
    @Nullable
    private BeanFactory beanFactory;

    public HibernateTransactionManager() {
    }

    public HibernateTransactionManager(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.afterPropertiesSet();
    }

    public void setSessionFactory(@Nullable SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Nullable
    public SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }

    protected final SessionFactory obtainSessionFactory() {
        SessionFactory sessionFactory = this.getSessionFactory();
        Assert.state((sessionFactory != null ? 1 : 0) != 0, (String)"No SessionFactory set");
        return sessionFactory;
    }

    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource instanceof TransactionAwareDataSourceProxy ? ((TransactionAwareDataSourceProxy)dataSource).getTargetDataSource() : dataSource;
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setAutodetectDataSource(boolean autodetectDataSource) {
        this.autodetectDataSource = autodetectDataSource;
    }

    public void setPrepareConnection(boolean prepareConnection) {
        this.prepareConnection = prepareConnection;
    }

    @Deprecated
    public void setAllowResultAccessAfterCompletion(boolean allowResultAccessAfterCompletion) {
        this.allowResultAccessAfterCompletion = allowResultAccessAfterCompletion;
    }

    public void setHibernateManagedSession(boolean hibernateManagedSession) {
        this.hibernateManagedSession = hibernateManagedSession;
    }

    public void setSessionInitializer(Consumer<Session> sessionInitializer) {
        this.sessionInitializer = sessionInitializer;
    }

    public void setEntityInterceptorBeanName(String entityInterceptorBeanName) {
        this.entityInterceptor = entityInterceptorBeanName;
    }

    public void setEntityInterceptor(@Nullable Interceptor entityInterceptor) {
        this.entityInterceptor = entityInterceptor;
    }

    @Nullable
    public Interceptor getEntityInterceptor() throws IllegalStateException, BeansException {
        if (this.entityInterceptor instanceof Interceptor) {
            return (Interceptor)this.entityInterceptor;
        }
        if (this.entityInterceptor instanceof String) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("Cannot get entity interceptor via bean name if no bean factory set");
            }
            String beanName = (String)this.entityInterceptor;
            return (Interceptor)this.beanFactory.getBean(beanName, Interceptor.class);
        }
        return null;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void afterPropertiesSet() {
        DataSource sfds;
        if (this.getSessionFactory() == null) {
            throw new IllegalArgumentException("Property 'sessionFactory' is required");
        }
        if (this.entityInterceptor instanceof String && this.beanFactory == null) {
            throw new IllegalArgumentException("Property 'beanFactory' is required for 'entityInterceptorBeanName'");
        }
        if (this.autodetectDataSource && this.getDataSource() == null && (sfds = SessionFactoryUtils.getDataSource(this.getSessionFactory())) != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using DataSource [" + sfds + "] of Hibernate SessionFactory for HibernateTransactionManager"));
            }
            this.setDataSource(sfds);
        }
    }

    public Object getResourceFactory() {
        return this.obtainSessionFactory();
    }

    protected Object doGetTransaction() {
        HibernateTransactionObject txObject = new HibernateTransactionObject();
        txObject.setSavepointAllowed(this.isNestedTransactionAllowed());
        SessionFactory sessionFactory = this.obtainSessionFactory();
        SessionHolder sessionHolder = (SessionHolder)((Object)TransactionSynchronizationManager.getResource((Object)sessionFactory));
        if (sessionHolder != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Found thread-bound Session [" + sessionHolder.getSession() + "] for Hibernate transaction"));
            }
            txObject.setSessionHolder(sessionHolder);
        } else if (this.hibernateManagedSession) {
            try {
                Session session = sessionFactory.getCurrentSession();
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Found Hibernate-managed Session [" + session + "] for Spring-managed transaction"));
                }
                txObject.setExistingSession(session);
            }
            catch (HibernateException ex) {
                throw new DataAccessResourceFailureException("Could not obtain Hibernate-managed Session for Spring-managed transaction", (Throwable)ex);
            }
        }
        if (this.getDataSource() != null) {
            ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource((Object)this.getDataSource());
            txObject.setConnectionHolder(conHolder);
        }
        return txObject;
    }

    protected boolean isExistingTransaction(Object transaction) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)transaction);
        return txObject.hasSpringManagedTransaction() || this.hibernateManagedSession && txObject.hasHibernateManagedTransaction();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)transaction);
        if (txObject.hasConnectionHolder() && !txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            throw new IllegalTransactionStateException("Pre-bound JDBC Connection found! HibernateTransactionManager does not support running within DataSourceTransactionManager if told to manage the DataSource itself. It is recommended to use a single HibernateTransactionManager for all transactions on a single DataSource, no matter whether Hibernate or JDBC access.");
        }
        SessionImplementor session = null;
        try {
            Transaction hibTx;
            int timeout;
            FlushMode flushMode;
            boolean isolationLevelNeeded;
            if (!txObject.hasSessionHolder() || txObject.getSessionHolder().isSynchronizedWithTransaction()) {
                Session newSession;
                Interceptor entityInterceptor = this.getEntityInterceptor();
                Session session2 = newSession = entityInterceptor != null ? this.obtainSessionFactory().withOptions().interceptor(entityInterceptor).openSession() : this.obtainSessionFactory().openSession();
                if (this.sessionInitializer != null) {
                    this.sessionInitializer.accept(newSession);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Opened new Session [" + newSession + "] for Hibernate transaction"));
                }
                txObject.setSession(newSession);
            }
            session = (SessionImplementor)txObject.getSessionHolder().getSession().unwrap(SessionImplementor.class);
            boolean holdabilityNeeded = this.allowResultAccessAfterCompletion && !txObject.isNewSession();
            boolean bl = isolationLevelNeeded = definition.getIsolationLevel() != -1;
            if (holdabilityNeeded || isolationLevelNeeded || definition.isReadOnly()) {
                if (this.prepareConnection && ConnectionReleaseMode.ON_CLOSE.equals((Object)session.getJdbcCoordinator().getLogicalConnection().getConnectionHandlingMode().getReleaseMode())) {
                    int currentHoldability;
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Preparing JDBC Connection of Hibernate Session [" + session + "]"));
                    }
                    Connection con = session.connection();
                    Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction((Connection)con, (TransactionDefinition)definition);
                    txObject.setPreviousIsolationLevel(previousIsolationLevel);
                    txObject.setReadOnly(definition.isReadOnly());
                    if (this.allowResultAccessAfterCompletion && !txObject.isNewSession() && (currentHoldability = con.getHoldability()) != 1) {
                        txObject.setPreviousHoldability(currentHoldability);
                        con.setHoldability(1);
                    }
                    txObject.connectionPrepared();
                } else {
                    if (isolationLevelNeeded) {
                        throw new InvalidIsolationLevelException("HibernateTransactionManager is not allowed to support custom isolation levels: make sure that its 'prepareConnection' flag is on (the default) and that the Hibernate connection release mode is set to ON_CLOSE.");
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Not preparing JDBC Connection of Hibernate Session [" + session + "]"));
                    }
                }
            }
            if (definition.isReadOnly() && txObject.isNewSession()) {
                session.setHibernateFlushMode(FlushMode.MANUAL);
                session.setDefaultReadOnly(true);
            }
            if (!definition.isReadOnly() && !txObject.isNewSession() && FlushMode.MANUAL.equals((Object)(flushMode = session.getHibernateFlushMode()))) {
                session.setHibernateFlushMode(FlushMode.AUTO);
                txObject.getSessionHolder().setPreviousFlushMode(flushMode);
            }
            if ((timeout = this.determineTimeout(definition)) != -1) {
                hibTx = session.getTransaction();
                hibTx.setTimeout(timeout);
                hibTx.begin();
            } else {
                hibTx = session.beginTransaction();
            }
            txObject.getSessionHolder().setTransaction(hibTx);
            if (this.getDataSource() != null) {
                ConnectionHolder conHolder = new ConnectionHolder(() -> ((SessionImplementor)session).connection());
                if (timeout != -1) {
                    conHolder.setTimeoutInSeconds(timeout);
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Exposing Hibernate transaction as JDBC [" + conHolder.getConnectionHandle() + "]"));
                }
                TransactionSynchronizationManager.bindResource((Object)this.getDataSource(), (Object)conHolder);
                txObject.setConnectionHolder(conHolder);
            }
            if (txObject.isNewSessionHolder()) {
                TransactionSynchronizationManager.bindResource((Object)this.obtainSessionFactory(), (Object)((Object)txObject.getSessionHolder()));
            }
            txObject.getSessionHolder().setSynchronizedWithTransaction(true);
        }
        catch (Throwable ex) {
            if (txObject.isNewSession()) {
                block26: {
                    try {
                        if (session == null || session.getTransaction().getStatus() != TransactionStatus.ACTIVE) break block26;
                        session.getTransaction().rollback();
                    }
                    catch (Throwable ex2) {
                        try {
                            this.logger.debug((Object)"Could not rollback Session after failed transaction begin", ex);
                        }
                        catch (Throwable throwable) {
                            SessionFactoryUtils.closeSession(session);
                            txObject.setSessionHolder(null);
                            throw throwable;
                        }
                        SessionFactoryUtils.closeSession((Session)session);
                        txObject.setSessionHolder(null);
                    }
                }
                SessionFactoryUtils.closeSession(session);
                txObject.setSessionHolder(null);
            }
            throw new CannotCreateTransactionException("Could not open Hibernate Session for transaction", ex);
        }
    }

    protected Object doSuspend(Object transaction) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)transaction);
        txObject.setSessionHolder(null);
        SessionHolder sessionHolder = (SessionHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)this.obtainSessionFactory()));
        txObject.setConnectionHolder(null);
        ConnectionHolder connectionHolder = null;
        if (this.getDataSource() != null) {
            connectionHolder = (ConnectionHolder)TransactionSynchronizationManager.unbindResource((Object)this.getDataSource());
        }
        return new SuspendedResourcesHolder(sessionHolder, connectionHolder);
    }

    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        SessionFactory sessionFactory = this.obtainSessionFactory();
        SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder)suspendedResources;
        if (TransactionSynchronizationManager.hasResource((Object)sessionFactory)) {
            TransactionSynchronizationManager.unbindResource((Object)sessionFactory);
        }
        TransactionSynchronizationManager.bindResource((Object)sessionFactory, (Object)((Object)resourcesHolder.getSessionHolder()));
        if (this.getDataSource() != null && resourcesHolder.getConnectionHolder() != null) {
            TransactionSynchronizationManager.bindResource((Object)this.getDataSource(), (Object)resourcesHolder.getConnectionHolder());
        }
    }

    protected void doCommit(DefaultTransactionStatus status) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)status.getTransaction());
        Transaction hibTx = txObject.getSessionHolder().getTransaction();
        Assert.state((hibTx != null ? 1 : 0) != 0, (String)"No Hibernate transaction");
        if (status.isDebug()) {
            this.logger.debug((Object)("Committing Hibernate transaction on Session [" + txObject.getSessionHolder().getSession() + "]"));
        }
        try {
            hibTx.commit();
        }
        catch (TransactionException ex) {
            throw new TransactionSystemException("Could not commit Hibernate transaction", (Throwable)ex);
        }
        catch (HibernateException ex) {
            throw this.convertHibernateAccessException(ex);
        }
        catch (PersistenceException ex) {
            if (ex.getCause() instanceof HibernateException) {
                throw this.convertHibernateAccessException((HibernateException)ex.getCause());
            }
            throw ex;
        }
    }

    protected void doRollback(DefaultTransactionStatus status) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)status.getTransaction());
        Transaction hibTx = txObject.getSessionHolder().getTransaction();
        Assert.state((hibTx != null ? 1 : 0) != 0, (String)"No Hibernate transaction");
        if (status.isDebug()) {
            this.logger.debug((Object)("Rolling back Hibernate transaction on Session [" + txObject.getSessionHolder().getSession() + "]"));
        }
        try {
            hibTx.rollback();
        }
        catch (TransactionException ex) {
            throw new TransactionSystemException("Could not roll back Hibernate transaction", (Throwable)ex);
        }
        catch (HibernateException ex) {
            throw this.convertHibernateAccessException(ex);
        }
        catch (PersistenceException ex) {
            if (ex.getCause() instanceof HibernateException) {
                throw this.convertHibernateAccessException((HibernateException)ex.getCause());
            }
            throw ex;
        }
        finally {
            if (!txObject.isNewSession() && !this.hibernateManagedSession) {
                txObject.getSessionHolder().getSession().clear();
            }
        }
    }

    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)status.getTransaction());
        if (status.isDebug()) {
            this.logger.debug((Object)("Setting Hibernate transaction on Session [" + txObject.getSessionHolder().getSession() + "] rollback-only"));
        }
        txObject.setRollbackOnly();
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        HibernateTransactionObject txObject = (HibernateTransactionObject)((Object)transaction);
        if (txObject.isNewSessionHolder()) {
            TransactionSynchronizationManager.unbindResource((Object)this.obtainSessionFactory());
        }
        if (this.getDataSource() != null) {
            TransactionSynchronizationManager.unbindResource((Object)this.getDataSource());
        }
        SessionImplementor session = (SessionImplementor)txObject.getSessionHolder().getSession().unwrap(SessionImplementor.class);
        if (txObject.needsConnectionReset() && session.getJdbcCoordinator().getLogicalConnection().isPhysicallyConnected()) {
            try {
                Connection con = session.connection();
                Integer previousHoldability = txObject.getPreviousHoldability();
                if (previousHoldability != null) {
                    con.setHoldability(previousHoldability);
                }
                DataSourceUtils.resetConnectionAfterTransaction((Connection)con, (Integer)txObject.getPreviousIsolationLevel(), (boolean)txObject.isReadOnly());
            }
            catch (HibernateException ex) {
                this.logger.debug((Object)"Could not access JDBC Connection of Hibernate Session", (Throwable)ex);
            }
            catch (Throwable ex) {
                this.logger.debug((Object)"Could not reset JDBC Connection after transaction", ex);
            }
        }
        if (txObject.isNewSession()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Closing Hibernate Session [" + session + "] after transaction"));
            }
            SessionFactoryUtils.closeSession((Session)session);
        } else {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Not closing pre-bound Hibernate Session [" + session + "] after transaction"));
            }
            if (txObject.getSessionHolder().getPreviousFlushMode() != null) {
                session.setHibernateFlushMode(txObject.getSessionHolder().getPreviousFlushMode());
            }
            if (!this.allowResultAccessAfterCompletion && !this.hibernateManagedSession) {
                this.disconnectOnCompletion((Session)session);
            }
        }
        txObject.getSessionHolder().clear();
    }

    protected void disconnectOnCompletion(Session session) {
        session.disconnect();
    }

    protected DataAccessException convertHibernateAccessException(HibernateException ex) {
        return SessionFactoryUtils.convertHibernateAccessException(ex);
    }

    private static final class SuspendedResourcesHolder {
        private final SessionHolder sessionHolder;
        @Nullable
        private final ConnectionHolder connectionHolder;

        private SuspendedResourcesHolder(SessionHolder sessionHolder, @Nullable ConnectionHolder conHolder) {
            this.sessionHolder = sessionHolder;
            this.connectionHolder = conHolder;
        }

        private SessionHolder getSessionHolder() {
            return this.sessionHolder;
        }

        @Nullable
        private ConnectionHolder getConnectionHolder() {
            return this.connectionHolder;
        }
    }

    private class HibernateTransactionObject
    extends JdbcTransactionObjectSupport {
        @Nullable
        private SessionHolder sessionHolder;
        private boolean newSessionHolder;
        private boolean newSession;
        private boolean needsConnectionReset;
        @Nullable
        private Integer previousHoldability;

        private HibernateTransactionObject() {
        }

        public void setSession(Session session) {
            this.sessionHolder = new SessionHolder(session);
            this.newSessionHolder = true;
            this.newSession = true;
        }

        public void setExistingSession(Session session) {
            this.sessionHolder = new SessionHolder(session);
            this.newSessionHolder = true;
            this.newSession = false;
        }

        public void setSessionHolder(@Nullable SessionHolder sessionHolder) {
            this.sessionHolder = sessionHolder;
            this.newSessionHolder = false;
            this.newSession = false;
        }

        public SessionHolder getSessionHolder() {
            Assert.state((this.sessionHolder != null ? 1 : 0) != 0, (String)"No SessionHolder available");
            return this.sessionHolder;
        }

        public boolean hasSessionHolder() {
            return this.sessionHolder != null;
        }

        public boolean isNewSessionHolder() {
            return this.newSessionHolder;
        }

        public boolean isNewSession() {
            return this.newSession;
        }

        public void connectionPrepared() {
            this.needsConnectionReset = true;
        }

        public boolean needsConnectionReset() {
            return this.needsConnectionReset;
        }

        public void setPreviousHoldability(@Nullable Integer previousHoldability) {
            this.previousHoldability = previousHoldability;
        }

        @Nullable
        public Integer getPreviousHoldability() {
            return this.previousHoldability;
        }

        public boolean hasSpringManagedTransaction() {
            return this.sessionHolder != null && this.sessionHolder.getTransaction() != null;
        }

        public boolean hasHibernateManagedTransaction() {
            return this.sessionHolder != null && this.sessionHolder.getSession().getTransaction().getStatus() == TransactionStatus.ACTIVE;
        }

        public void setRollbackOnly() {
            this.getSessionHolder().setRollbackOnly();
            if (this.hasConnectionHolder()) {
                this.getConnectionHolder().setRollbackOnly();
            }
        }

        public boolean isRollbackOnly() {
            return this.getSessionHolder().isRollbackOnly() || this.hasConnectionHolder() && this.getConnectionHolder().isRollbackOnly();
        }

        public void flush() {
            try {
                this.getSessionHolder().getSession().flush();
            }
            catch (HibernateException ex) {
                throw HibernateTransactionManager.this.convertHibernateAccessException(ex);
            }
            catch (PersistenceException ex) {
                if (ex.getCause() instanceof HibernateException) {
                    throw HibernateTransactionManager.this.convertHibernateAccessException((HibernateException)ex.getCause());
                }
                throw ex;
            }
        }
    }
}

