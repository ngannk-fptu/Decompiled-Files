/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.ParameterMode
 *  javax.persistence.Query
 *  javax.persistence.StoredProcedureQuery
 *  javax.persistence.TransactionRequiredException
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.orm.jpa;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.ParameterMode;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.persistence.TransactionRequiredException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.EntityManagerProxy;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ConcurrentReferenceHashMap;

public abstract class SharedEntityManagerCreator {
    private static final Class<?>[] NO_ENTITY_MANAGER_INTERFACES = new Class[0];
    private static final Map<Class<?>, Class<?>[]> cachedQueryInterfaces = new ConcurrentReferenceHashMap(4);
    private static final Set<String> transactionRequiringMethods = new HashSet<String>(6);
    private static final Set<String> queryTerminatingMethods = new HashSet<String>(9);

    public static EntityManager createSharedEntityManager(EntityManagerFactory emf) {
        return SharedEntityManagerCreator.createSharedEntityManager(emf, null, true);
    }

    public static EntityManager createSharedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties) {
        return SharedEntityManagerCreator.createSharedEntityManager(emf, properties, true);
    }

    public static EntityManager createSharedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties, boolean synchronizedWithTransaction) {
        Class<?>[] classArray;
        Class<EntityManager> emIfc;
        Class clazz = emIfc = emf instanceof EntityManagerFactoryInfo ? ((EntityManagerFactoryInfo)emf).getEntityManagerInterface() : EntityManager.class;
        if (emIfc == null) {
            classArray = NO_ENTITY_MANAGER_INTERFACES;
        } else {
            Class[] classArray2 = new Class[1];
            classArray = classArray2;
            classArray2[0] = emIfc;
        }
        return SharedEntityManagerCreator.createSharedEntityManager(emf, properties, synchronizedWithTransaction, classArray);
    }

    public static EntityManager createSharedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties, Class<?> ... entityManagerInterfaces) {
        return SharedEntityManagerCreator.createSharedEntityManager(emf, properties, true, entityManagerInterfaces);
    }

    public static EntityManager createSharedEntityManager(EntityManagerFactory emf, @Nullable Map<?, ?> properties, boolean synchronizedWithTransaction, Class<?> ... entityManagerInterfaces) {
        ClassLoader cl = null;
        if (emf instanceof EntityManagerFactoryInfo) {
            cl = ((EntityManagerFactoryInfo)emf).getBeanClassLoader();
        }
        Class[] ifcs = new Class[entityManagerInterfaces.length + 1];
        System.arraycopy(entityManagerInterfaces, 0, ifcs, 0, entityManagerInterfaces.length);
        ifcs[entityManagerInterfaces.length] = EntityManagerProxy.class;
        return (EntityManager)Proxy.newProxyInstance(cl != null ? cl : SharedEntityManagerCreator.class.getClassLoader(), ifcs, (InvocationHandler)new SharedEntityManagerInvocationHandler(emf, properties, synchronizedWithTransaction));
    }

    static {
        transactionRequiringMethods.add("joinTransaction");
        transactionRequiringMethods.add("flush");
        transactionRequiringMethods.add("persist");
        transactionRequiringMethods.add("merge");
        transactionRequiringMethods.add("remove");
        transactionRequiringMethods.add("refresh");
        queryTerminatingMethods.add("execute");
        queryTerminatingMethods.add("executeUpdate");
        queryTerminatingMethods.add("getSingleResult");
        queryTerminatingMethods.add("getResultStream");
        queryTerminatingMethods.add("getResultList");
        queryTerminatingMethods.add("list");
        queryTerminatingMethods.add("stream");
        queryTerminatingMethods.add("uniqueResult");
        queryTerminatingMethods.add("uniqueResultOptional");
    }

    private static class DeferredQueryInvocationHandler
    implements InvocationHandler {
        private final Query target;
        @Nullable
        private EntityManager entityManager;
        @Nullable
        private Map<Object, Object> outputParameters;

        public DeferredQueryInvocationHandler(Query target, EntityManager entityManager) {
            this.target = target;
            this.entityManager = entityManager;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return this.hashCode();
                }
                case "unwrap": {
                    Class targetClass = (Class)args[0];
                    if (targetClass == null) {
                        return this.target;
                    }
                    if (!targetClass.isInstance(proxy)) break;
                    return proxy;
                }
                case "getOutputParameterValue": {
                    if (this.entityManager != null) break;
                    Object key = args[0];
                    if (this.outputParameters == null || !this.outputParameters.containsKey(key)) {
                        throw new IllegalArgumentException("OUT/INOUT parameter not available: " + key);
                    }
                    Object value = this.outputParameters.get(key);
                    if (value instanceof IllegalArgumentException) {
                        throw (IllegalArgumentException)value;
                    }
                    return value;
                }
            }
            try {
                Object retVal = method.invoke((Object)this.target, args);
                if (method.getName().equals("registerStoredProcedureParameter") && args.length == 3 && (args[2] == ParameterMode.OUT || args[2] == ParameterMode.INOUT)) {
                    if (this.outputParameters == null) {
                        this.outputParameters = new LinkedHashMap<Object, Object>();
                    }
                    this.outputParameters.put(args[0], null);
                }
                Object object = retVal == this.target ? proxy : retVal;
                return object;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            finally {
                if (queryTerminatingMethods.contains(method.getName())) {
                    if (this.outputParameters != null && this.target instanceof StoredProcedureQuery) {
                        StoredProcedureQuery storedProc = (StoredProcedureQuery)this.target;
                        for (Map.Entry<Object, Object> entry : this.outputParameters.entrySet()) {
                            try {
                                Object key = entry.getKey();
                                if (key instanceof Integer) {
                                    entry.setValue(storedProc.getOutputParameterValue(((Integer)key).intValue()));
                                    continue;
                                }
                                entry.setValue(storedProc.getOutputParameterValue(key.toString()));
                            }
                            catch (RuntimeException ex) {
                                entry.setValue(ex);
                            }
                        }
                    }
                    EntityManagerFactoryUtils.closeEntityManager(this.entityManager);
                    this.entityManager = null;
                }
            }
        }
    }

    private static class SharedEntityManagerInvocationHandler
    implements InvocationHandler,
    Serializable {
        private final Log logger = LogFactory.getLog(this.getClass());
        private final EntityManagerFactory targetFactory;
        @Nullable
        private final Map<?, ?> properties;
        private final boolean synchronizedWithTransaction;
        @Nullable
        private volatile transient ClassLoader proxyClassLoader;

        public SharedEntityManagerInvocationHandler(EntityManagerFactory target, @Nullable Map<?, ?> properties, boolean synchronizedWithTransaction) {
            this.targetFactory = target;
            this.properties = properties;
            this.synchronizedWithTransaction = synchronizedWithTransaction;
            this.initProxyClassLoader();
        }

        private void initProxyClassLoader() {
            this.proxyClassLoader = this.targetFactory instanceof EntityManagerFactoryInfo ? ((EntityManagerFactoryInfo)this.targetFactory).getBeanClassLoader() : this.targetFactory.getClass().getClassLoader();
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
                case "toString": {
                    return "Shared EntityManager proxy for target factory [" + this.targetFactory + "]";
                }
                case "getEntityManagerFactory": {
                    return this.targetFactory;
                }
                case "getCriteriaBuilder": 
                case "getMetamodel": {
                    try {
                        return EntityManagerFactory.class.getMethod(method.getName(), new Class[0]).invoke((Object)this.targetFactory, new Object[0]);
                    }
                    catch (InvocationTargetException ex) {
                        throw ex.getTargetException();
                    }
                }
                case "unwrap": {
                    Class targetClass = (Class)args[0];
                    if (targetClass == null || !targetClass.isInstance(proxy)) break;
                    return proxy;
                }
                case "isOpen": {
                    return true;
                }
                case "close": {
                    return null;
                }
                case "getTransaction": {
                    throw new IllegalStateException("Not allowed to create transaction on shared EntityManager - use Spring transactions or EJB CMT instead");
                }
            }
            EntityManager target = EntityManagerFactoryUtils.doGetTransactionalEntityManager(this.targetFactory, this.properties, this.synchronizedWithTransaction);
            switch (method.getName()) {
                case "getTargetEntityManager": {
                    if (target == null) {
                        throw new IllegalStateException("No transactional EntityManager available");
                    }
                    return target;
                }
                case "unwrap": {
                    Class targetClass = (Class)args[0];
                    if (targetClass == null) {
                        return target != null ? target : proxy;
                    }
                    if (target != null) break;
                    throw new IllegalStateException("No transactional EntityManager available");
                }
            }
            if (transactionRequiringMethods.contains(method.getName()) && (target == null || !TransactionSynchronizationManager.isActualTransactionActive() && !target.getTransaction().isActive())) {
                throw new TransactionRequiredException("No EntityManager with actual transaction available for current thread - cannot reliably process '" + method.getName() + "' call");
            }
            boolean isNewEm = false;
            if (target == null) {
                this.logger.debug((Object)"Creating new EntityManager for shared EntityManager invocation");
                target = !CollectionUtils.isEmpty(this.properties) ? this.targetFactory.createEntityManager(this.properties) : this.targetFactory.createEntityManager();
                isNewEm = true;
            }
            try {
                Object result = method.invoke((Object)target, args);
                if (result instanceof Query) {
                    Query query = (Query)result;
                    if (isNewEm) {
                        Class[] ifcs = cachedQueryInterfaces.computeIfAbsent(query.getClass(), key -> ClassUtils.getAllInterfacesForClass((Class)key, (ClassLoader)this.proxyClassLoader));
                        result = Proxy.newProxyInstance(this.proxyClassLoader, ifcs, (InvocationHandler)new DeferredQueryInvocationHandler(query, target));
                        isNewEm = false;
                    } else {
                        EntityManagerFactoryUtils.applyTransactionTimeout(query, this.targetFactory);
                    }
                }
                Object object = result;
                return object;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            finally {
                if (isNewEm) {
                    EntityManagerFactoryUtils.closeEntityManager(target);
                }
            }
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            this.initProxyClassLoader();
        }
    }
}

