/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.EntityManager
 *  javax.persistence.EntityManagerFactory
 *  javax.persistence.PersistenceException
 *  javax.persistence.Query
 *  javax.persistence.SynchronizationType
 *  javax.persistence.spi.PersistenceProvider
 *  javax.persistence.spi.PersistenceUnitInfo
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.core.task.AsyncTaskExecutor
 *  org.springframework.dao.DataAccessException
 *  org.springframework.dao.support.PersistenceExceptionTranslator
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.orm.jpa;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.SynchronizationType;
import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.support.PersistenceExceptionTranslator;
import org.springframework.lang.Nullable;
import org.springframework.orm.jpa.EntityManagerFactoryInfo;
import org.springframework.orm.jpa.EntityManagerFactoryUtils;
import org.springframework.orm.jpa.ExtendedEntityManagerCreator;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

public abstract class AbstractEntityManagerFactoryBean
implements FactoryBean<EntityManagerFactory>,
BeanClassLoaderAware,
BeanFactoryAware,
BeanNameAware,
InitializingBean,
DisposableBean,
EntityManagerFactoryInfo,
PersistenceExceptionTranslator,
Serializable {
    protected final Log logger = LogFactory.getLog(this.getClass());
    @Nullable
    private PersistenceProvider persistenceProvider;
    @Nullable
    private String persistenceUnitName;
    private final Map<String, Object> jpaPropertyMap = new HashMap<String, Object>();
    @Nullable
    private Class<? extends EntityManagerFactory> entityManagerFactoryInterface;
    @Nullable
    private Class<? extends EntityManager> entityManagerInterface;
    @Nullable
    private JpaDialect jpaDialect;
    @Nullable
    private JpaVendorAdapter jpaVendorAdapter;
    @Nullable
    private Consumer<EntityManager> entityManagerInitializer;
    @Nullable
    private AsyncTaskExecutor bootstrapExecutor;
    private ClassLoader beanClassLoader = this.getClass().getClassLoader();
    @Nullable
    private BeanFactory beanFactory;
    @Nullable
    private String beanName;
    @Nullable
    private EntityManagerFactory nativeEntityManagerFactory;
    @Nullable
    private Future<EntityManagerFactory> nativeEntityManagerFactoryFuture;
    @Nullable
    private EntityManagerFactory entityManagerFactory;

    public void setPersistenceProviderClass(Class<? extends PersistenceProvider> persistenceProviderClass) {
        this.persistenceProvider = (PersistenceProvider)BeanUtils.instantiateClass(persistenceProviderClass);
    }

    public void setPersistenceProvider(@Nullable PersistenceProvider persistenceProvider) {
        this.persistenceProvider = persistenceProvider;
    }

    @Override
    @Nullable
    public PersistenceProvider getPersistenceProvider() {
        return this.persistenceProvider;
    }

    public void setPersistenceUnitName(@Nullable String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    @Nullable
    public String getPersistenceUnitName() {
        return this.persistenceUnitName;
    }

    public void setJpaProperties(Properties jpaProperties) {
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

    public void setEntityManagerFactoryInterface(Class<? extends EntityManagerFactory> emfInterface) {
        this.entityManagerFactoryInterface = emfInterface;
    }

    public void setEntityManagerInterface(@Nullable Class<? extends EntityManager> emInterface) {
        this.entityManagerInterface = emInterface;
    }

    @Override
    @Nullable
    public Class<? extends EntityManager> getEntityManagerInterface() {
        return this.entityManagerInterface;
    }

    public void setJpaDialect(@Nullable JpaDialect jpaDialect) {
        this.jpaDialect = jpaDialect;
    }

    @Override
    @Nullable
    public JpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

    public void setJpaVendorAdapter(@Nullable JpaVendorAdapter jpaVendorAdapter) {
        this.jpaVendorAdapter = jpaVendorAdapter;
    }

    @Nullable
    public JpaVendorAdapter getJpaVendorAdapter() {
        return this.jpaVendorAdapter;
    }

    public void setEntityManagerInitializer(Consumer<EntityManager> entityManagerInitializer) {
        this.entityManagerInitializer = entityManagerInitializer;
    }

    public void setBootstrapExecutor(@Nullable AsyncTaskExecutor bootstrapExecutor) {
        this.bootstrapExecutor = bootstrapExecutor;
    }

    @Nullable
    public AsyncTaskExecutor getBootstrapExecutor() {
        return this.bootstrapExecutor;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    public void afterPropertiesSet() throws PersistenceException {
        AsyncTaskExecutor bootstrapExecutor;
        JpaVendorAdapter jpaVendorAdapter = this.getJpaVendorAdapter();
        if (jpaVendorAdapter != null) {
            PersistenceUnitInfo pui;
            Map<String, ?> vendorPropertyMap;
            if (this.persistenceProvider == null) {
                this.persistenceProvider = jpaVendorAdapter.getPersistenceProvider();
            }
            Map<String, ?> map = vendorPropertyMap = (pui = this.getPersistenceUnitInfo()) != null ? jpaVendorAdapter.getJpaPropertyMap(pui) : jpaVendorAdapter.getJpaPropertyMap();
            if (!CollectionUtils.isEmpty(vendorPropertyMap)) {
                vendorPropertyMap.forEach((key, value) -> {
                    if (!this.jpaPropertyMap.containsKey(key)) {
                        this.jpaPropertyMap.put((String)key, value);
                    }
                });
            }
            if (this.entityManagerFactoryInterface == null) {
                this.entityManagerFactoryInterface = jpaVendorAdapter.getEntityManagerFactoryInterface();
                if (!ClassUtils.isVisible(this.entityManagerFactoryInterface, (ClassLoader)this.beanClassLoader)) {
                    this.entityManagerFactoryInterface = EntityManagerFactory.class;
                }
            }
            if (this.entityManagerInterface == null) {
                this.entityManagerInterface = jpaVendorAdapter.getEntityManagerInterface();
                if (!ClassUtils.isVisible(this.entityManagerInterface, (ClassLoader)this.beanClassLoader)) {
                    this.entityManagerInterface = EntityManager.class;
                }
            }
            if (this.jpaDialect == null) {
                this.jpaDialect = jpaVendorAdapter.getJpaDialect();
            }
        }
        if ((bootstrapExecutor = this.getBootstrapExecutor()) != null) {
            this.nativeEntityManagerFactoryFuture = bootstrapExecutor.submit(this::buildNativeEntityManagerFactory);
        } else {
            this.nativeEntityManagerFactory = this.buildNativeEntityManagerFactory();
        }
        this.entityManagerFactory = this.createEntityManagerFactoryProxy(this.nativeEntityManagerFactory);
    }

    private EntityManagerFactory buildNativeEntityManagerFactory() {
        EntityManagerFactory emf;
        try {
            emf = this.createNativeEntityManagerFactory();
        }
        catch (PersistenceException ex) {
            String causeString;
            String message;
            Throwable cause;
            if (((Object)((Object)ex)).getClass() == PersistenceException.class && (cause = ex.getCause()) != null && !(message = ex.getMessage()).endsWith(causeString = cause.toString())) {
                ex = new PersistenceException(message + "; nested exception is " + causeString, cause);
            }
            if (this.logger.isErrorEnabled()) {
                this.logger.error((Object)("Failed to initialize JPA EntityManagerFactory: " + ex.getMessage()));
            }
            throw ex;
        }
        JpaVendorAdapter jpaVendorAdapter = this.getJpaVendorAdapter();
        if (jpaVendorAdapter != null) {
            jpaVendorAdapter.postProcessEntityManagerFactory(emf);
        }
        if (this.logger.isInfoEnabled()) {
            this.logger.info((Object)("Initialized JPA EntityManagerFactory for persistence unit '" + this.getPersistenceUnitName() + "'"));
        }
        return emf;
    }

    protected EntityManagerFactory createEntityManagerFactoryProxy(@Nullable EntityManagerFactory emf) {
        LinkedHashSet<Class<Object>> ifcs = new LinkedHashSet<Class<Object>>();
        Class<? extends EntityManagerFactory> entityManagerFactoryInterface = this.entityManagerFactoryInterface;
        if (entityManagerFactoryInterface != null) {
            ifcs.add(entityManagerFactoryInterface);
        } else if (emf != null) {
            ifcs.addAll(ClassUtils.getAllInterfacesForClassAsSet(emf.getClass(), (ClassLoader)this.beanClassLoader));
        } else {
            ifcs.add(EntityManagerFactory.class);
        }
        ifcs.add(EntityManagerFactoryInfo.class);
        try {
            return (EntityManagerFactory)Proxy.newProxyInstance(this.beanClassLoader, ClassUtils.toClassArray(ifcs), (InvocationHandler)new ManagedEntityManagerFactoryInvocationHandler(this));
        }
        catch (IllegalArgumentException ex) {
            if (entityManagerFactoryInterface != null) {
                throw new IllegalStateException("EntityManagerFactory interface [" + entityManagerFactoryInterface + "] seems to conflict with Spring's EntityManagerFactoryInfo mixin - consider resetting the 'entityManagerFactoryInterface' property to plain [javax.persistence.EntityManagerFactory]", ex);
            }
            throw new IllegalStateException("Conflicting EntityManagerFactory interfaces - consider specifying the 'jpaVendorAdapter' or 'entityManagerFactoryInterface' property to select a specific EntityManagerFactory interface to proceed with", ex);
        }
    }

    Object invokeProxyMethod(Method method, @Nullable Object[] args) throws Throwable {
        Object retVal;
        if (method.getDeclaringClass().isAssignableFrom(EntityManagerFactoryInfo.class)) {
            return method.invoke((Object)this, args);
        }
        if (method.getName().equals("createEntityManager") && args != null && args.length > 0 && args[0] == SynchronizationType.SYNCHRONIZED) {
            EntityManager rawEntityManager = args.length > 1 ? this.getNativeEntityManagerFactory().createEntityManager((Map)args[1]) : this.getNativeEntityManagerFactory().createEntityManager();
            this.postProcessEntityManager(rawEntityManager);
            return ExtendedEntityManagerCreator.createApplicationManagedEntityManager(rawEntityManager, this, true);
        }
        if (args != null) {
            for (int i = 0; i < args.length; ++i) {
                Object arg = args[i];
                if (!(arg instanceof Query) || !Proxy.isProxyClass(arg.getClass())) continue;
                try {
                    args[i] = ((Query)arg).unwrap(null);
                    continue;
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
            }
        }
        if ((retVal = method.invoke((Object)this.getNativeEntityManagerFactory(), args)) instanceof EntityManager) {
            EntityManager rawEntityManager = (EntityManager)retVal;
            this.postProcessEntityManager(rawEntityManager);
            retVal = ExtendedEntityManagerCreator.createApplicationManagedEntityManager(rawEntityManager, this, false);
        }
        return retVal;
    }

    protected abstract EntityManagerFactory createNativeEntityManagerFactory() throws PersistenceException;

    @Nullable
    public DataAccessException translateExceptionIfPossible(RuntimeException ex) {
        JpaDialect jpaDialect = this.getJpaDialect();
        return jpaDialect != null ? jpaDialect.translateExceptionIfPossible(ex) : EntityManagerFactoryUtils.convertJpaAccessExceptionIfPossible(ex);
    }

    @Override
    public EntityManagerFactory getNativeEntityManagerFactory() {
        if (this.nativeEntityManagerFactory != null) {
            return this.nativeEntityManagerFactory;
        }
        Assert.state((this.nativeEntityManagerFactoryFuture != null ? 1 : 0) != 0, (String)"No native EntityManagerFactory available");
        try {
            return this.nativeEntityManagerFactoryFuture.get();
        }
        catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted during initialization of native EntityManagerFactory", ex);
        }
        catch (ExecutionException ex) {
            Throwable cause = ex.getCause();
            if (cause instanceof PersistenceException) {
                throw (PersistenceException)cause;
            }
            throw new IllegalStateException("Failed to asynchronously initialize native EntityManagerFactory: " + ex.getMessage(), cause);
        }
    }

    @Override
    public EntityManager createNativeEntityManager(@Nullable Map<?, ?> properties) {
        EntityManager rawEntityManager = !CollectionUtils.isEmpty(properties) ? this.getNativeEntityManagerFactory().createEntityManager(properties) : this.getNativeEntityManagerFactory().createEntityManager();
        this.postProcessEntityManager(rawEntityManager);
        return rawEntityManager;
    }

    protected void postProcessEntityManager(EntityManager rawEntityManager) {
        Consumer<EntityManager> customizer;
        JpaVendorAdapter jpaVendorAdapter = this.getJpaVendorAdapter();
        if (jpaVendorAdapter != null) {
            jpaVendorAdapter.postProcessEntityManager(rawEntityManager);
        }
        if ((customizer = this.entityManagerInitializer) != null) {
            customizer.accept(rawEntityManager);
        }
    }

    @Override
    @Nullable
    public PersistenceUnitInfo getPersistenceUnitInfo() {
        return null;
    }

    @Override
    @Nullable
    public DataSource getDataSource() {
        return null;
    }

    @Nullable
    public EntityManagerFactory getObject() {
        return this.entityManagerFactory;
    }

    public Class<? extends EntityManagerFactory> getObjectType() {
        return this.entityManagerFactory != null ? this.entityManagerFactory.getClass() : EntityManagerFactory.class;
    }

    public boolean isSingleton() {
        return true;
    }

    public void destroy() {
        if (this.entityManagerFactory != null) {
            if (this.logger.isInfoEnabled()) {
                this.logger.info((Object)("Closing JPA EntityManagerFactory for persistence unit '" + this.getPersistenceUnitName() + "'"));
            }
            this.entityManagerFactory.close();
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("An EntityManagerFactoryBean itself is not deserializable - just a SerializedEntityManagerFactoryBeanReference is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.beanFactory != null && this.beanName != null) {
            return new SerializedEntityManagerFactoryBeanReference(this.beanFactory, this.beanName);
        }
        throw new NotSerializableException("EntityManagerFactoryBean does not run within a BeanFactory");
    }

    private static class ManagedEntityManagerFactoryInvocationHandler
    implements InvocationHandler,
    Serializable {
        private final AbstractEntityManagerFactoryBean entityManagerFactoryBean;

        public ManagedEntityManagerFactoryInvocationHandler(AbstractEntityManagerFactoryBean emfb) {
            this.entityManagerFactoryBean = emfb;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "unwrap": {
                    Class targetClass = (Class)args[0];
                    if (targetClass == null) {
                        return this.entityManagerFactoryBean.getNativeEntityManagerFactory();
                    }
                    if (!targetClass.isInstance(proxy)) break;
                    return proxy;
                }
            }
            try {
                return this.entityManagerFactoryBean.invokeProxyMethod(method, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }

    private static class SerializedEntityManagerFactoryBeanReference
    implements Serializable {
        private final BeanFactory beanFactory;
        private final String lookupName;

        public SerializedEntityManagerFactoryBeanReference(BeanFactory beanFactory, String beanName) {
            this.beanFactory = beanFactory;
            this.lookupName = "&" + beanName;
        }

        private Object readResolve() {
            return this.beanFactory.getBean(this.lookupName, AbstractEntityManagerFactoryBean.class);
        }
    }
}

