/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.EntityTransaction
 *  javax.persistence.PersistenceException
 *  javax.persistence.RollbackException
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.DataAccessUtils
 *  org.springframework.dao.support.PersistenceExceptionTranslator
 *  org.springframework.jdbc.datasource.ConnectionHandle
 *  org.springframework.jdbc.datasource.ConnectionHolder
 *  org.springframework.jdbc.datasource.JdbcTransactionObjectSupport
 *  org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.IllegalTransactionStateException
 *  org.springframework.transaction.NestedTransactionNotSupportedException
 *  org.springframework.transaction.SavepointManager
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionSystemException
 *  org.springframework.transaction.support.AbstractPlatformTransactionManager
 *  org.springframework.transaction.support.DefaultTransactionStatus
 *  org.springframework.transaction.support.DelegatingTransactionDefinition
 *  org.springframework.transaction.support.ResourceTransactionDefinition
 *  org.springframework.transaction.support.ResourceTransactionManager
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.orm.jpa;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;
import javax.sql.DataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.jdbc.datasource.ConnectionHandle;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.DefaultJpaDialect;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.IllegalTransactionStateException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.DelegatingTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionDefinition;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class JpaTransactionManager
extends AbstractPlatformTransactionManager
implements ResourceTransactionManager,
BeanFactoryAware,
InitializingBean {
    @Nullable
    private EntityManagerFactory entityManagerFactory;
    @Nullable
    private String persistenceUnitName;
    private final Map<String, Object> jpaPropertyMap = new HashMap<String, Object>();
    @Nullable
    private DataSource dataSource;
    private JpaDialect jpaDialect = new DefaultJpaDialect();
    @Nullable
    private Consumer<EntityManager> entityManagerInitializer;

    public JpaTransactionManager() {
        this.setNestedTransactionAllowed(true);
    }

    public JpaTransactionManager(EntityManagerFactory emf) {
        this();
        this.entityManagerFactory = emf;
        this.afterPropertiesSet();
    }

    public void setEntityManagerFactory(@Nullable EntityManagerFactory emf) {
        this.entityManagerFactory = emf;
    }

    @Nullable
    public EntityManagerFactory getEntityManagerFactory() {
        return this.entityManagerFactory;
    }

    protected final EntityManagerFactory obtainEntityManagerFactory() {
        EntityManagerFactory emf = this.getEntityManagerFactory();
        Assert.state((emf != null ? 1 : 0) != 0, (String)"No EntityManagerFactory set");
        return emf;
    }

    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Nullable
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    public void setJpaProperties(@Nullable Properties jpaProperties) {
        CollectionUtils.mergePropertiesIntoMap((Properties)jpaProperties, this.jpaPropertyMap);
    }

    public void setJpaPropertyMap(@Nullable Map<String, ?> jpaProperties) {
        if (jpaProperties != null) {
            this.jpaPropertyMap.putAll(jpaProperties);
        }
    }

    public Map<String, Object> getJpaPropertyMap() {
        return this.jpaPropertyMap;
    }

    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource instanceof TransactionAwareDataSourceProxy ? ((TransactionAwareDataSourceProxy)dataSource).getTargetDataSource() : dataSource;
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    public void setJpaDialect(@Nullable JpaDialect jpaDialect) {
        this.jpaDialect = jpaDialect != null ? jpaDialect : new DefaultJpaDialect();
    }

    public JpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    public void setEntityManagerInitializer(Consumer<EntityManager> entityManagerInitializer) {
        this.entityManagerInitializer = entityManagerInitializer;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        if (this.getEntityManagerFactory() == null) {
            if (!(beanFactory instanceof ListableBeanFactory)) {
                throw new IllegalStateException("Cannot retrieve EntityManagerFactory by persistence unit name in a non-listable BeanFactory: " + beanFactory);
            }
            ListableBeanFactory lbf = (ListableBeanFactory)beanFactory;
            this.setEntityManagerFactory(EntityManagerFactoryUtils.findEntityManagerFactory(lbf, this.getPersistenceUnitName()));
        }
    }

    public void afterPropertiesSet() {
        if (this.getEntityManagerFactory() == null) {
            throw new IllegalArgumentException("'entityManagerFactory' or 'persistenceUnitName' is required");
        }
        if (this.getEntityManagerFactory() instanceof EntityManagerFactoryInfo) {
            JpaDialect jpaDialect;
            EntityManagerFactoryInfo emfInfo = (EntityManagerFactoryInfo)this.getEntityManagerFactory();
            DataSource dataSource = emfInfo.getDataSource();
            if (dataSource != null) {
                this.setDataSource(dataSource);
            }
            if ((jpaDialect = emfInfo.getJpaDialect()) != null) {
                this.setJpaDialect(jpaDialect);
            }
        }
    }

    public Object getResourceFactory() {
        return this.obtainEntityManagerFactory();
    }

    protected Object doGetTransaction() {
        JpaTransactionObject txObject = new JpaTransactionObject();
        txObject.setSavepointAllowed(this.isNestedTransactionAllowed());
        EntityManagerHolder emHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.getResource((Object)this.obtainEntityManagerFactory()));
        if (emHolder != null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Found thread-bound EntityManager [" + emHolder.getEntityManager() + "] for JPA transaction"));
            }
            txObject.setEntityManagerHolder(emHolder, false);
        }
        if (this.getDataSource() != null) {
            ConnectionHolder conHolder = (ConnectionHolder)TransactionSynchronizationManager.getResource((Object)this.getDataSource());
            txObject.setConnectionHolder(conHolder);
        }
        return txObject;
    }

    protected boolean isExistingTransaction(Object transaction) {
        return ((JpaTransactionObject)((Object)transaction)).hasTransaction();
    }

    protected void doBegin(Object transaction, TransactionDefinition definition) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)transaction);
        if (txObject.hasConnectionHolder() && !txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
            throw new IllegalTransactionStateException("Pre-bound JDBC Connection found! JpaTransactionManager does not support running within DataSourceTransactionManager if told to manage the DataSource itself. It is recommended to use a single JpaTransactionManager for all transactions on a single DataSource, no matter whether JPA or JDBC access.");
        }
        try {
            if (!txObject.hasEntityManagerHolder() || txObject.getEntityManagerHolder().isSynchronizedWithTransaction()) {
                EntityManager newEm = this.createEntityManagerForTransaction();
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Opened new EntityManager [" + newEm + "] for JPA transaction"));
                }
                txObject.setEntityManagerHolder(new EntityManagerHolder(newEm), true);
            }
            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();
            int timeoutToUse = this.determineTimeout(definition);
            Object transactionData = this.getJpaDialect().beginTransaction(em, (TransactionDefinition)new JpaTransactionDefinition(definition, timeoutToUse, txObject.isNewEntityManagerHolder()));
            txObject.setTransactionData(transactionData);
            txObject.setReadOnly(definition.isReadOnly());
            if (timeoutToUse != -1) {
                txObject.getEntityManagerHolder().setTimeoutInSeconds(timeoutToUse);
            }
            if (this.getDataSource() != null) {
                ConnectionHandle conHandle = this.getJpaDialect().getJdbcConnection(em, definition.isReadOnly());
                if (conHandle != null) {
                    ConnectionHolder conHolder = new ConnectionHolder(conHandle);
                    if (timeoutToUse != -1) {
                        conHolder.setTimeoutInSeconds(timeoutToUse);
                    }
                    if (this.logger.isDebugEnabled()) {
                        this.logger.debug((Object)("Exposing JPA transaction as JDBC [" + conHandle + "]"));
                    }
                    TransactionSynchronizationManager.bindResource((Object)this.getDataSource(), (Object)conHolder);
                    txObject.setConnectionHolder(conHolder);
                } else if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Not exposing JPA transaction [" + em + "] as JDBC transaction because JpaDialect [" + this.getJpaDialect() + "] does not support JDBC Connection retrieval"));
                }
            }
            if (txObject.isNewEntityManagerHolder()) {
                TransactionSynchronizationManager.bindResource((Object)this.obtainEntityManagerFactory(), (Object)((Object)txObject.getEntityManagerHolder()));
            }
            txObject.getEntityManagerHolder().setSynchronizedWithTransaction(true);
        }
        catch (TransactionException ex) {
            this.closeEntityManagerAfterFailedBegin(txObject);
            throw ex;
        }
        catch (Throwable ex) {
            this.closeEntityManagerAfterFailedBegin(txObject);
            throw new CannotCreateTransactionException("Could not open JPA EntityManager for transaction", ex);
        }
    }

    protected EntityManager createEntityManagerForTransaction() {
        EntityManager em;
        EntityManagerFactory emf = this.obtainEntityManagerFactory();
        Map<String, Object> properties = this.getJpaPropertyMap();
        if (emf instanceof EntityManagerFactoryInfo) {
            em = ((EntityManagerFactoryInfo)emf).createNativeEntityManager(properties);
        } else {
            EntityManager entityManager = em = !CollectionUtils.isEmpty(properties) ? emf.createEntityManager(properties) : emf.createEntityManager();
        }
        if (this.entityManagerInitializer != null) {
            this.entityManagerInitializer.accept(em);
        }
        return em;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void closeEntityManagerAfterFailedBegin(JpaTransactionObject txObject) {
        if (txObject.isNewEntityManagerHolder()) {
            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();
            try {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
            catch (Throwable ex) {
                this.logger.debug((Object)"Could not rollback EntityManager after failed transaction begin", ex);
            }
            finally {
                EntityManagerFactoryUtils.closeEntityManager(em);
            }
            txObject.setEntityManagerHolder(null, false);
        }
    }

    protected Object doSuspend(Object transaction) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)transaction);
        txObject.setEntityManagerHolder(null, false);
        EntityManagerHolder entityManagerHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.unbindResource((Object)this.obtainEntityManagerFactory()));
        txObject.setConnectionHolder(null);
        ConnectionHolder connectionHolder = null;
        if (this.getDataSource() != null && TransactionSynchronizationManager.hasResource((Object)this.getDataSource())) {
            connectionHolder = (ConnectionHolder)TransactionSynchronizationManager.unbindResource((Object)this.getDataSource());
        }
        return new SuspendedResourcesHolder(entityManagerHolder, connectionHolder);
    }

    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder)suspendedResources;
        TransactionSynchronizationManager.bindResource((Object)this.obtainEntityManagerFactory(), (Object)((Object)resourcesHolder.getEntityManagerHolder()));
        if (this.getDataSource() != null && resourcesHolder.getConnectionHolder() != null) {
            TransactionSynchronizationManager.bindResource((Object)this.getDataSource(), (Object)resourcesHolder.getConnectionHolder());
        }
    }

    protected boolean shouldCommitOnGlobalRollbackOnly() {
        return true;
    }

    protected void doCommit(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)status.getTransaction());
        if (status.isDebug()) {
            this.logger.debug((Object)("Committing JPA transaction on EntityManager [" + txObject.getEntityManagerHolder().getEntityManager() + "]"));
        }
        try {
            EntityTransaction tx = txObject.getEntityManagerHolder().getEntityManager().getTransaction();
            tx.commit();
        }
        catch (RollbackException ex) {
            DataAccessException dae;
            if (ex.getCause() instanceof RuntimeException && (dae = this.getJpaDialect().translateExceptionIfPossible((RuntimeException)ex.getCause())) != null) {
                throw dae;
            }
            throw new TransactionSystemException("Could not commit JPA transaction", (Throwable)ex);
        }
        catch (RuntimeException ex) {
            throw DataAccessUtils.translateIfNecessary((RuntimeException)ex, (PersistenceExceptionTranslator)this.getJpaDialect());
        }
    }

    protected void doRollback(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)status.getTransaction());
        if (status.isDebug()) {
            this.logger.debug((Object)("Rolling back JPA transaction on EntityManager [" + txObject.getEntityManagerHolder().getEntityManager() + "]"));
        }
        try {
            EntityTransaction tx = txObject.getEntityManagerHolder().getEntityManager().getTransaction();
            if (tx.isActive()) {
                tx.rollback();
            }
        }
        catch (PersistenceException ex) {
            throw new TransactionSystemException("Could not roll back JPA transaction", (Throwable)ex);
        }
        finally {
            if (!txObject.isNewEntityManagerHolder()) {
                txObject.getEntityManagerHolder().getEntityManager().clear();
            }
        }
    }

    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)status.getTransaction());
        if (status.isDebug()) {
            this.logger.debug((Object)("Setting JPA transaction on EntityManager [" + txObject.getEntityManagerHolder().getEntityManager() + "] rollback-only"));
        }
        txObject.setRollbackOnly();
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        JpaTransactionObject txObject = (JpaTransactionObject)((Object)transaction);
        if (txObject.isNewEntityManagerHolder()) {
            TransactionSynchronizationManager.unbindResourceIfPossible((Object)this.obtainEntityManagerFactory());
        }
        txObject.getEntityManagerHolder().clear();
        if (this.getDataSource() != null && txObject.hasConnectionHolder()) {
            TransactionSynchronizationManager.unbindResource((Object)this.getDataSource());
            ConnectionHandle conHandle = txObject.getConnectionHolder().getConnectionHandle();
            if (conHandle != null) {
                try {
                    this.getJpaDialect().releaseJdbcConnection(conHandle, txObject.getEntityManagerHolder().getEntityManager());
                }
                catch (Throwable ex) {
                    this.logger.error((Object)"Failed to release JDBC connection after transaction", ex);
                }
            }
        }
        this.getJpaDialect().cleanupTransaction(txObject.getTransactionData());
        if (txObject.isNewEntityManagerHolder()) {
            EntityManager em = txObject.getEntityManagerHolder().getEntityManager();
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Closing JPA EntityManager [" + em + "] after transaction"));
            }
            EntityManagerFactoryUtils.closeEntityManager(em);
        } else {
            this.logger.debug((Object)"Not closing pre-bound JPA EntityManager after transaction");
        }
    }

    private static final class SuspendedResourcesHolder {
        private final EntityManagerHolder entityManagerHolder;
        @Nullable
        private final ConnectionHolder connectionHolder;

        private SuspendedResourcesHolder(EntityManagerHolder emHolder, @Nullable ConnectionHolder conHolder) {
            this.entityManagerHolder = emHolder;
            this.connectionHolder = conHolder;
        }

        private EntityManagerHolder getEntityManagerHolder() {
            return this.entityManagerHolder;
        }

        @Nullable
        private ConnectionHolder getConnectionHolder() {
            return this.connectionHolder;
        }
    }

    private static class JpaTransactionDefinition
    extends DelegatingTransactionDefinition
    implements ResourceTransactionDefinition {
        private final int timeout;
        private final boolean localResource;

        public JpaTransactionDefinition(TransactionDefinition targetDefinition, int timeout, boolean localResource) {
            super(targetDefinition);
            this.timeout = timeout;
            this.localResource = localResource;
        }

        public int getTimeout() {
            return this.timeout;
        }

        public boolean isLocalResource() {
            return this.localResource;
        }
    }

    private class JpaTransactionObject
    extends JdbcTransactionObjectSupport {
        @Nullable
        private EntityManagerHolder entityManagerHolder;
        private boolean newEntityManagerHolder;
        @Nullable
        private Object transactionData;

        private JpaTransactionObject() {
        }

        public void setEntityManagerHolder(@Nullable EntityManagerHolder entityManagerHolder, boolean newEntityManagerHolder) {
            this.entityManagerHolder = entityManagerHolder;
            this.newEntityManagerHolder = newEntityManagerHolder;
        }

        public EntityManagerHolder getEntityManagerHolder() {
            Assert.state((this.entityManagerHolder != null ? 1 : 0) != 0, (String)"No EntityManagerHolder available");
            return this.entityManagerHolder;
        }

        public boolean hasEntityManagerHolder() {
            return this.entityManagerHolder != null;
        }

        public boolean isNewEntityManagerHolder() {
            return this.newEntityManagerHolder;
        }

        public boolean hasTransaction() {
            return this.entityManagerHolder != null && this.entityManagerHolder.isTransactionActive();
        }

        public void setTransactionData(@Nullable Object transactionData) {
            this.transactionData = transactionData;
            this.getEntityManagerHolder().setTransactionActive(true);
            if (transactionData instanceof SavepointManager) {
                this.getEntityManagerHolder().setSavepointManager((SavepointManager)transactionData);
            }
        }

        @Nullable
        public Object getTransactionData() {
            return this.transactionData;
        }

        public void setRollbackOnly() {
            EntityTransaction tx = this.getEntityManagerHolder().getEntityManager().getTransaction();
            if (tx.isActive()) {
                tx.setRollbackOnly();
            }
            if (this.hasConnectionHolder()) {
                this.getConnectionHolder().setRollbackOnly();
            }
        }

        public boolean isRollbackOnly() {
            EntityTransaction tx = this.getEntityManagerHolder().getEntityManager().getTransaction();
            return tx.getRollbackOnly();
        }

        public void flush() {
            try {
                this.getEntityManagerHolder().getEntityManager().flush();
            }
            catch (RuntimeException ex) {
                throw DataAccessUtils.translateIfNecessary((RuntimeException)ex, (PersistenceExceptionTranslator)JpaTransactionManager.this.getJpaDialect());
            }
        }

        public Object createSavepoint() throws TransactionException {
            if (this.getEntityManagerHolder().isRollbackOnly()) {
                throw new CannotCreateTransactionException("Cannot create savepoint for transaction which is already marked as rollback-only");
            }
            return this.getSavepointManager().createSavepoint();
        }

        public void rollbackToSavepoint(Object savepoint) throws TransactionException {
            this.getSavepointManager().rollbackToSavepoint(savepoint);
            this.getEntityManagerHolder().resetRollbackOnly();
        }

        public void releaseSavepoint(Object savepoint) throws TransactionException {
            this.getSavepointManager().releaseSavepoint(savepoint);
        }

        private SavepointManager getSavepointManager() {
            if (!this.isSavepointAllowed()) {
                throw new NestedTransactionNotSupportedException("Transaction manager does not allow nested transactions");
            }
            SavepointManager savepointManager = this.getEntityManagerHolder().getSavepointManager();
            if (savepointManager == null) {
                throw new NestedTransactionNotSupportedException("JpaDialect does not support savepoints - check your JPA provider's capabilities");
            }
            return savepointManager;
        }
    }
}

