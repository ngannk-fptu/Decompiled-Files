/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.EntityTransaction
 *  javax.persistence.TransactionRequiredException
 *  javax.persistence.spi.PersistenceUnitInfo
 *  javax.persistence.spi.PersistenceUnitTransactionType
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.Ordered
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.PersistenceExceptionTranslator
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.ResourceHolder
 *  org.springframework.transaction.support.ResourceHolderSynchronization
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.orm.jpa;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashSet;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.TransactionRequiredException;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.persistence.spi.PersistenceUnitTransactionType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Ordered;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerHolder;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.transaction.support.ResourceHolder;
import org.springframework.transaction.support.ResourceHolderSynchronization;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

public abstract class ExtendedEntityManagerCreator {
    private static final Map<Class<?>, Class<?>[]> cachedEntityManagerInterfaces = new ConcurrentReferenceHashMap(4);

    public static EntityManager createApplicationManagedEntityManager(EntityManager rawEntityManager, EntityManagerFactoryInfo emfInfo) {
        return ExtendedEntityManagerCreator.createProxy(rawEntityManager, emfInfo, false, false);
    }

    public static EntityManager createApplicationManagedEntityManager(EntityManager rawEntityManager, EntityManagerFactoryInfo emfInfo, boolean synchronizedWithTransaction) {
        return ExtendedEntityManagerCreator.createProxy(rawEntityManager, emfInfo, false, synchronizedWithTransaction);
    }

    public static EntityManager createContainerManagedEntityManager(EntityManager rawEntityManager, EntityManagerFactoryInfo emfInfo) {
        return ExtendedEntityManagerCreator.createProxy(rawEntityManager, emfInfo, true, true);
    }

    public static EntityManager createContainerManagedEntityManager(EntityManagerFactory emf) {
        return ExtendedEntityManagerCreator.createContainerManagedEntityManager(emf, null, true);
    }

    public static EntityManager createContainerManagedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties) {
        return ExtendedEntityManagerCreator.createContainerManagedEntityManager(emf, properties, true);
    }

    public static EntityManager createContainerManagedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties, boolean synchronizedWithTransaction) {
        Assert.notNull((Object)emf, (String)"EntityManagerFactory must not be null");
        if (emf instanceof EntityManagerFactoryInfo) {
            EntityManagerFactoryInfo emfInfo = (EntityManagerFactoryInfo)emf;
            EntityManager rawEntityManager = emfInfo.createNativeEntityManager(properties);
            return ExtendedEntityManagerCreator.createProxy(rawEntityManager, emfInfo, true, synchronizedWithTransaction);
        }
        EntityManager rawEntityManager = !CollectionUtils.isEmpty(properties) ? emf.createEntityManager(properties) : emf.createEntityManager();
        return ExtendedEntityManagerCreator.createProxy(rawEntityManager, null, null, null, null, true, synchronizedWithTransaction);
    }

    private static EntityManager createProxy(EntityManager rawEntityManager, EntityManagerFactoryInfo emfInfo, boolean containerManaged, boolean synchronizedWithTransaction) {
        Assert.notNull((Object)emfInfo, (String)"EntityManagerFactoryInfo must not be null");
        JpaDialect jpaDialect = emfInfo.getJpaDialect();
        PersistenceUnitInfo pui = emfInfo.getPersistenceUnitInfo();
        Boolean jta = pui != null ? Boolean.valueOf(pui.getTransactionType() == PersistenceUnitTransactionType.JTA) : null;
        return ExtendedEntityManagerCreator.createProxy(rawEntityManager, emfInfo.getEntityManagerInterface(), emfInfo.getBeanClassLoader(), jpaDialect, jta, containerManaged, synchronizedWithTransaction);
    }

    private static EntityManager createProxy(EntityManager rawEm, @Nullable Class<? extends EntityManager> emIfc, @Nullable ClassLoader cl, @Nullable PersistenceExceptionTranslator exceptionTranslator, @Nullable Boolean jta, boolean containerManaged, boolean synchronizedWithTransaction) {
        Assert.notNull((Object)rawEm, (String)"EntityManager must not be null");
        Class[] interfaces = emIfc != null ? cachedEntityManagerInterfaces.computeIfAbsent(emIfc, key -> {
            if (EntityManagerProxy.class.equals(key)) {
                return new Class[]{key};
            }
            return new Class[]{key, EntityManagerProxy.class};
        }) : cachedEntityManagerInterfaces.computeIfAbsent(rawEm.getClass(), key -> {
            LinkedHashSet<Class<EntityManagerProxy>> ifcs = new LinkedHashSet<Class<EntityManagerProxy>>(ClassUtils.getAllInterfacesForClassAsSet((Class)key, (ClassLoader)cl));
            ifcs.add(EntityManagerProxy.class);
            return ClassUtils.toClassArray(ifcs);
        });
        return (EntityManager)Proxy.newProxyInstance(cl != null ? cl : ExtendedEntityManagerCreator.class.getClassLoader(), interfaces, (InvocationHandler)new ExtendedEntityManagerInvocationHandler(rawEm, exceptionTranslator, jta, containerManaged, synchronizedWithTransaction));
    }

    private static class ExtendedEntityManagerSynchronization
    extends ResourceHolderSynchronization<EntityManagerHolder, EntityManager>
    implements Ordered {
        private final EntityManager entityManager;
        @Nullable
        private final PersistenceExceptionTranslator exceptionTranslator;
        public volatile boolean closeOnCompletion;

        public ExtendedEntityManagerSynchronization(EntityManager em, @Nullable PersistenceExceptionTranslator exceptionTranslator) {
            super((ResourceHolder)new EntityManagerHolder(em), (Object)em);
            this.entityManager = em;
            this.exceptionTranslator = exceptionTranslator;
        }

        public int getOrder() {
            return 899;
        }

        protected void flushResource(EntityManagerHolder resourceHolder) {
            try {
                this.entityManager.flush();
            }
            catch (RuntimeException ex) {
                throw this.convertException(ex);
            }
        }

        protected boolean shouldReleaseBeforeCompletion() {
            return false;
        }

        public void afterCommit() {
            super.afterCommit();
            try {
                this.entityManager.getTransaction().commit();
            }
            catch (RuntimeException ex) {
                throw this.convertException(ex);
            }
        }

        public void afterCompletion(int status) {
            block6: {
                try {
                    super.afterCompletion(status);
                    if (status == 0) break block6;
                    try {
                        this.entityManager.getTransaction().rollback();
                    }
                    catch (RuntimeException ex) {
                        throw this.convertException(ex);
                    }
                }
                finally {
                    if (this.closeOnCompletion) {
                        EntityManagerFactoryUtils.closeEntityManager(this.entityManager);
                    }
                }
            }
        }

        private RuntimeException convertException(RuntimeException ex) {
            DataAccessException dae = this.exceptionTranslator != null ? this.exceptionTranslator.translateExceptionIfPossible(ex) : EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
            return dae != null ? dae : ex;
        }
    }

    private static final class ExtendedEntityManagerInvocationHandler
    implements InvocationHandler,
    Serializable {
        private static final Log logger = LogFactory.getLog(ExtendedEntityManagerInvocationHandler.class);
        private final EntityManager target;
        @Nullable
        private final PersistenceExceptionTranslator exceptionTranslator;
        private final boolean jta;
        private final boolean containerManaged;
        private final boolean synchronizedWithTransaction;

        private ExtendedEntityManagerInvocationHandler(EntityManager target, @Nullable PersistenceExceptionTranslator exceptionTranslator, @Nullable Boolean jta, boolean containerManaged, boolean synchronizedWithTransaction) {
            this.target = target;
            this.exceptionTranslator = exceptionTranslator;
            this.jta = jta != null ? jta.booleanValue() : this.isJtaEntityManager();
            this.containerManaged = containerManaged;
            this.synchronizedWithTransaction = synchronizedWithTransaction;
        }

        private boolean isJtaEntityManager() {
            try {
                this.target.getTransaction();
                return false;
            }
            catch (IllegalStateException ex) {
                logger.debug((Object)"Cannot access EntityTransaction handle - assuming we're in a JTA environment");
                return true;
            }
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return this.hashCode();
                }
                case "getTargetEntityManager": {
                    return this.target;
                }
                case "unwrap": {
                    Class targetClass = (Class)args[0];
                    if (targetClass == null) {
                        return this.target;
                    }
                    if (!targetClass.isInstance(proxy)) break;
                    return proxy;
                }
                case "isOpen": {
                    if (!this.containerManaged) break;
                    return true;
                }
                case "close": {
                    if (this.containerManaged) {
                        throw new IllegalStateException("Invalid usage: Cannot close a container-managed EntityManager");
                    }
                    ExtendedEntityManagerSynchronization synch = (ExtendedEntityManagerSynchronization)((Object)TransactionSynchronizationManager.getResource((Object)this.target));
                    if (synch == null) break;
                    synch.closeOnCompletion = true;
                    return null;
                }
                case "getTransaction": {
                    if (!this.synchronizedWithTransaction) break;
                    throw new IllegalStateException("Cannot obtain local EntityTransaction from a transaction-synchronized EntityManager");
                }
                case "joinTransaction": {
                    this.doJoinTransaction(true);
                    return null;
                }
                case "isJoinedToTransaction": {
                    if (this.jta) break;
                    return TransactionSynchronizationManager.hasResource((Object)this.target);
                }
            }
            if (this.synchronizedWithTransaction && method.getDeclaringClass().isInterface()) {
                this.doJoinTransaction(false);
            }
            try {
                return method.invoke((Object)this.target, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private void doJoinTransaction(boolean enforce) {
            if (this.jta) {
                try {
                    this.target.joinTransaction();
                    logger.debug((Object)"Joined JTA transaction");
                }
                catch (TransactionRequiredException ex) {
                    if (!enforce) {
                        logger.debug((Object)("No JTA transaction to join: " + (Object)((Object)ex)));
                    }
                    throw ex;
                }
            } else if (TransactionSynchronizationManager.isSynchronizationActive()) {
                if (!TransactionSynchronizationManager.hasResource((Object)this.target) && !this.target.getTransaction().isActive()) {
                    this.enlistInCurrentTransaction();
                }
                logger.debug((Object)"Joined local transaction");
            } else if (!enforce) {
                logger.debug((Object)"No local transaction to join");
            } else {
                throw new TransactionRequiredException("No local transaction to join");
            }
        }

        private void enlistInCurrentTransaction() {
            EntityTransaction et = this.target.getTransaction();
            et.begin();
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Starting resource-local transaction on application-managed EntityManager [" + this.target + "]"));
            }
            ExtendedEntityManagerSynchronization extendedEntityManagerSynchronization = new ExtendedEntityManagerSynchronization(this.target, this.exceptionTranslator);
            TransactionSynchronizationManager.bindResource((Object)this.target, (Object)((Object)extendedEntityManagerSynchronization));
            TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)extendedEntityManagerSynchronization);
        }
    }
}

