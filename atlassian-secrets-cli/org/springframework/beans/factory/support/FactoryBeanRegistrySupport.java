/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.beans.factory.support.NullBean;
import org.springframework.lang.Nullable;

public abstract class FactoryBeanRegistrySupport
extends DefaultSingletonBeanRegistry {
    private final Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<String, Object>(16);

    @Nullable
    protected Class<?> getTypeForFactoryBean(FactoryBean<?> factoryBean) {
        try {
            if (System.getSecurityManager() != null) {
                return AccessController.doPrivileged(factoryBean::getObjectType, this.getAccessControlContext());
            }
            return factoryBean.getObjectType();
        }
        catch (Throwable ex) {
            this.logger.info("FactoryBean threw exception from getObjectType, despite the contract saying that it should return null if the type of its object cannot be determined yet", ex);
            return null;
        }
    }

    @Nullable
    protected Object getCachedObjectForFactoryBean(String beanName) {
        return this.factoryBeanObjectCache.get(beanName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName, boolean shouldPostProcess) {
        if (factory.isSingleton() && this.containsSingleton(beanName)) {
            Object object = this.getSingletonMutex();
            synchronized (object) {
                Object object2 = this.factoryBeanObjectCache.get(beanName);
                if (object2 == null) {
                    object2 = this.doGetObjectFromFactoryBean(factory, beanName);
                    Object alreadyThere = this.factoryBeanObjectCache.get(beanName);
                    if (alreadyThere != null) {
                        object2 = alreadyThere;
                    } else {
                        if (shouldPostProcess) {
                            if (this.isSingletonCurrentlyInCreation(beanName)) {
                                return object2;
                            }
                            this.beforeSingletonCreation(beanName);
                            try {
                                object2 = this.postProcessObjectFromFactoryBean(object2, beanName);
                            }
                            catch (Throwable ex) {
                                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's singleton object failed", ex);
                            }
                            finally {
                                this.afterSingletonCreation(beanName);
                            }
                        }
                        if (this.containsSingleton(beanName)) {
                            this.factoryBeanObjectCache.put(beanName, object2);
                        }
                    }
                }
                return object2;
            }
        }
        Object object = this.doGetObjectFromFactoryBean(factory, beanName);
        if (shouldPostProcess) {
            try {
                object = this.postProcessObjectFromFactoryBean(object, beanName);
            }
            catch (Throwable ex) {
                throw new BeanCreationException(beanName, "Post-processing of FactoryBean's object failed", ex);
            }
        }
        return object;
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) throws BeanCreationException {
        Object object;
        block8: {
            try {
                if (System.getSecurityManager() != null) {
                    AccessControlContext acc = this.getAccessControlContext();
                    try {
                        object = AccessController.doPrivileged(factory::getObject, acc);
                        break block8;
                    }
                    catch (PrivilegedActionException pae) {
                        throw pae.getException();
                    }
                }
                object = factory.getObject();
            }
            catch (FactoryBeanNotInitializedException ex) {
                throw new BeanCurrentlyInCreationException(beanName, ex.toString());
            }
            catch (Throwable ex) {
                throw new BeanCreationException(beanName, "FactoryBean threw exception on object creation", ex);
            }
        }
        if (object == null) {
            if (this.isSingletonCurrentlyInCreation(beanName)) {
                throw new BeanCurrentlyInCreationException(beanName, "FactoryBean which is currently in creation returned null from getObject");
            }
            object = new NullBean();
        }
        return object;
    }

    protected Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeanCreationException(beanName, "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        return (FactoryBean)beanInstance;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void removeSingleton(String beanName) {
        Object object = this.getSingletonMutex();
        synchronized (object) {
            super.removeSingleton(beanName);
            this.factoryBeanObjectCache.remove(beanName);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void clearSingletonCache() {
        Object object = this.getSingletonMutex();
        synchronized (object) {
            super.clearSingletonCache();
            this.factoryBeanObjectCache.clear();
        }
    }

    protected AccessControlContext getAccessControlContext() {
        return AccessController.getContext();
    }
}

