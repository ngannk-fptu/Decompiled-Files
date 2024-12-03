/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityExistsException
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.EntityNotFoundException
 *  javax.persistence.LockTimeoutException
 *  javax.persistence.NoResultException
 *  javax.persistence.NonUniqueResultException
 *  javax.persistence.OptimisticLockException
 *  javax.persistence.PersistenceException
 *  javax.persistence.PessimisticLockException
 *  javax.persistence.Query
 *  javax.persistence.QueryTimeoutException
 *  javax.persistence.SynchronizationType
 *  javax.persistence.TransactionRequiredException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 *  org.springframework.beans.factory.NoSuchBeanDefinitionException
 *  org.springframework.core.Ordered
 *  org.springframework.dao.CannotAcquireLockException
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.DataAccessResourceFailureException
 *  org.springframework.dao.DataIntegrityViolationException
 *  org.springframework.dao.EmptyResultDataAccessException
 *  org.springframework.dao.IncorrectResultSizeDataAccessException
 *  org.springframework.dao.InvalidDataAccessApiUsageException
 *  org.springframework.dao.PessimisticLockingFailureException
 *  org.springframework.dao.QueryTimeoutException
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.ResourceHolder
 *  org.springframework.transaction.support.ResourceHolderSynchronization
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.orm.jpa;

import java.util.Map;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityNotFoundException;
import javax.persistence.LockTimeoutException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.TransactionRequiredException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.Ordered;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public abstract class EntityManagerFactoryUtils {
    public static final int ENTITY_MANAGER_SYNCHRONIZATION_ORDER = 900;
    private static final Log logger = LogFactory.getLog(EntityManagerFactoryUtils.class);

    public static EntityManagerFactory findEntityManagerFactory(ListableBeanFactory beanFactory, @Nullable String unitName) throws NoSuchBeanDefinitionException {
        Assert.notNull((Object)beanFactory, (String)"ListableBeanFactory must not be null");
        if (StringUtils.hasLength((String)unitName)) {
            String[] candidateNames;
            for (String candidateName : candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors((ListableBeanFactory)beanFactory, EntityManagerFactory.class)) {
                EntityManagerFactory emf = (EntityManagerFactory)beanFactory.getBean(candidateName);
                if (!(emf instanceof EntityManagerFactoryInfo) || !unitName.equals(((EntityManagerFactoryInfo)emf).getPersistenceUnitName())) continue;
                return emf;
            }
            return (EntityManagerFactory)beanFactory.getBean(unitName, EntityManagerFactory.class);
        }
        return (EntityManagerFactory)beanFactory.getBean(EntityManagerFactory.class);
    }

    @Nullable
    public static EntityManager getTransactionalEntityManager(EntityManagerFactory emf) throws DataAccessResourceFailureException {
        return EntityManagerFactoryUtils.getTransactionalEntityManager(emf, null);
    }

    @Nullable
    public static EntityManager getTransactionalEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties) throws DataAccessResourceFailureException {
        try {
            return EntityManagerFactoryUtils.doGetTransactionalEntityManager(emf, properties, true);
        }
        catch (PersistenceException ex) {
            throw new DataAccessResourceFailureException("Could not obtain JPA EntityManager", (Throwable)ex);
        }
    }

    @Nullable
    public static EntityManager doGetTransactionalEntityManager(EntityManagerFactory emf, Map<?, ?> properties) throws PersistenceException {
        return EntityManagerFactoryUtils.doGetTransactionalEntityManager(emf, properties, true);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Nullable
    public static EntityManager doGetTransactionalEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties, boolean synchronizedWithTransaction) throws PersistenceException {
        Assert.notNull((Object)emf, (String)"No EntityManagerFactory specified");
        EntityManagerHolder emHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.getResource((Object)emf));
        if (emHolder != null) {
            if (synchronizedWithTransaction) {
                if (!emHolder.isSynchronizedWithTransaction()) {
                    if (TransactionSynchronizationManager.isActualTransactionActive()) {
                        try {
                            emHolder.getEntityManager().joinTransaction();
                        }
                        catch (TransactionRequiredException ex) {
                            logger.debug((Object)"Could not join transaction because none was actually active", (Throwable)ex);
                        }
                    }
                    if (TransactionSynchronizationManager.isSynchronizationActive()) {
                        Object transactionData = EntityManagerFactoryUtils.prepareTransaction(emHolder.getEntityManager(), emf);
                        TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionalEntityManagerSynchronization(emHolder, emf, transactionData, false));
                        emHolder.setSynchronizedWithTransaction(true);
                    }
                }
                emHolder.requested();
                return emHolder.getEntityManager();
            }
            if (!emHolder.isTransactionActive() || emHolder.isOpen()) return emHolder.getEntityManager();
            if (!TransactionSynchronizationManager.isSynchronizationActive()) {
                return null;
            }
            TransactionSynchronizationManager.unbindResource((Object)emf);
        } else if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return null;
        }
        logger.debug((Object)"Opening JPA EntityManager");
        EntityManager em = null;
        if (!synchronizedWithTransaction) {
            try {
                em = emf.createEntityManager(SynchronizationType.UNSYNCHRONIZED, properties);
            }
            catch (AbstractMethodError abstractMethodError) {
                // empty catch block
            }
        }
        if (em == null) {
            em = !CollectionUtils.isEmpty(properties) ? emf.createEntityManager(properties) : emf.createEntityManager();
        }
        try {
            emHolder = new EntityManagerHolder(em);
            if (synchronizedWithTransaction) {
                Object transactionData = EntityManagerFactoryUtils.prepareTransaction(em, emf);
                TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionalEntityManagerSynchronization(emHolder, emf, transactionData, true));
                emHolder.setSynchronizedWithTransaction(true);
            } else {
                TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new TransactionScopedEntityManagerSynchronization(emHolder, emf));
            }
            TransactionSynchronizationManager.bindResource((Object)emf, (Object)((Object)emHolder));
            return em;
        }
        catch (RuntimeException ex) {
            EntityManagerFactoryUtils.closeEntityManager(em);
            throw ex;
        }
    }

    @Nullable
    private static Object prepareTransaction(EntityManager em, EntityManagerFactory emf) {
        EntityManagerFactoryInfo emfInfo;
        JpaDialect jpaDialect;
        if (emf instanceof EntityManagerFactoryInfo && (jpaDialect = (emfInfo = (EntityManagerFactoryInfo)emf).getJpaDialect()) != null) {
            return jpaDialect.prepareTransaction(em, TransactionSynchronizationManager.isCurrentTransactionReadOnly(), TransactionSynchronizationManager.getCurrentTransactionName());
        }
        return null;
    }

    private static void cleanupTransaction(@Nullable Object transactionData, EntityManagerFactory emf) {
        EntityManagerFactoryInfo emfInfo;
        JpaDialect jpaDialect;
        if (emf instanceof EntityManagerFactoryInfo && (jpaDialect = (emfInfo = (EntityManagerFactoryInfo)emf).getJpaDialect()) != null) {
            jpaDialect.cleanupTransaction(transactionData);
        }
    }

    public static void applyTransactionTimeout(Query query, EntityManagerFactory emf) {
        EntityManagerHolder emHolder = (EntityManagerHolder)((Object)TransactionSynchronizationManager.getResource((Object)emf));
        if (emHolder != null && emHolder.hasTimeout()) {
            int timeoutValue = (int)emHolder.getTimeToLiveInMillis();
            try {
                query.setHint("javax.persistence.query.timeout", (Object)timeoutValue);
            }
            catch (IllegalArgumentException illegalArgumentException) {
                // empty catch block
            }
        }
    }

    @Nullable
    public static DataAccessException convertJpaAccessExceptionIfPossible(RuntimeException ex) {
        if (ex instanceof IllegalStateException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof IllegalArgumentException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof EntityNotFoundException) {
            return new JpaObjectRetrievalFailureException((EntityNotFoundException)ex);
        }
        if (ex instanceof NoResultException) {
            return new EmptyResultDataAccessException(ex.getMessage(), 1, (Throwable)ex);
        }
        if (ex instanceof NonUniqueResultException) {
            return new IncorrectResultSizeDataAccessException(ex.getMessage(), 1, (Throwable)ex);
        }
        if (ex instanceof javax.persistence.QueryTimeoutException) {
            return new QueryTimeoutException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof LockTimeoutException) {
            return new CannotAcquireLockException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof PessimisticLockException) {
            return new PessimisticLockingFailureException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof OptimisticLockException) {
            return new JpaOptimisticLockingFailureException((OptimisticLockException)ex);
        }
        if (ex instanceof EntityExistsException) {
            return new DataIntegrityViolationException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof TransactionRequiredException) {
            return new InvalidDataAccessApiUsageException(ex.getMessage(), (Throwable)ex);
        }
        if (ex instanceof PersistenceException) {
            return new JpaSystemException(ex);
        }
        return null;
    }

    public static void closeEntityManager(@Nullable EntityManager em) {
        if (em != null) {
            try {
                if (em.isOpen()) {
                    em.close();
                }
            }
            catch (Throwable ex) {
                logger.error((Object)"Failed to release JPA EntityManager", ex);
            }
        }
    }

    private static class TransactionScopedEntityManagerSynchronization
    extends ResourceHolderSynchronization<EntityManagerHolder, EntityManagerFactory>
    implements Ordered {
        public TransactionScopedEntityManagerSynchronization(EntityManagerHolder emHolder, EntityManagerFactory emf) {
            super((ResourceHolder)emHolder, (Object)emf);
        }

        public int getOrder() {
            return 901;
        }

        protected void releaseResource(EntityManagerHolder resourceHolder, EntityManagerFactory resourceKey) {
            EntityManagerFactoryUtils.closeEntityManager(resourceHolder.getEntityManager());
        }
    }

    private static class TransactionalEntityManagerSynchronization
    extends ResourceHolderSynchronization<EntityManagerHolder, EntityManagerFactory>
    implements Ordered {
        @Nullable
        private final Object transactionData;
        @Nullable
        private final JpaDialect jpaDialect;
        private final boolean newEntityManager;

        public TransactionalEntityManagerSynchronization(EntityManagerHolder emHolder, EntityManagerFactory emf, @Nullable Object txData, boolean newEm) {
            super((ResourceHolder)emHolder, (Object)emf);
            this.transactionData = txData;
            this.jpaDialect = emf instanceof EntityManagerFactoryInfo ? ((EntityManagerFactoryInfo)emf).getJpaDialect() : null;
            this.newEntityManager = newEm;
        }

        public int getOrder() {
            return 900;
        }

        protected void flushResource(EntityManagerHolder resourceHolder) {
            EntityManager target;
            EntityManager em = resourceHolder.getEntityManager();
            if (em instanceof EntityManagerProxy && TransactionSynchronizationManager.hasResource((Object)(target = ((EntityManagerProxy)em).getTargetEntityManager()))) {
                return;
            }
            try {
                em.flush();
            }
            catch (RuntimeException ex) {
                DataAccessException dae = this.jpaDialect != null ? this.jpaDialect.translateExceptionIfPossible(ex) : EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
                throw dae != null ? dae : ex;
            }
        }

        protected boolean shouldUnbindAtCompletion() {
            return this.newEntityManager;
        }

        protected void releaseResource(EntityManagerHolder resourceHolder, EntityManagerFactory resourceKey) {
            EntityManagerFactoryUtils.closeEntityManager(resourceHolder.getEntityManager());
        }

        protected void cleanupResource(EntityManagerHolder resourceHolder, EntityManagerFactory resourceKey, boolean committed) {
            if (!committed) {
                resourceHolder.getEntityManager().clear();
            }
            EntityManagerFactoryUtils.cleanupTransaction(this.transactionData, resourceKey);
        }
    }
}

