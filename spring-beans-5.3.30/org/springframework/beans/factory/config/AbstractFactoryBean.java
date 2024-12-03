/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.beans.factory.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public abstract class AbstractFactoryBean<T>
implements FactoryBean<T>,
BeanClassLoaderAware,
BeanFactoryAware,
InitializingBean,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private boolean singleton = true;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private BeanFactory beanFactory;
    private boolean initialized = false;
    @Nullable
    private T singletonInstance;
    @Nullable
    private T earlySingletonInstance;

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }

    @Override
    public boolean isSingleton() {
        return this.singleton;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void setBeanFactory(@Nullable BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Nullable
    protected BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    protected TypeConverter getBeanTypeConverter() {
        BeanFactory beanFactory = this.getBeanFactory();
        if (beanFactory instanceof ConfigurableBeanFactory) {
            return ((ConfigurableBeanFactory)beanFactory).getTypeConverter();
        }
        return new SimpleTypeConverter();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.isSingleton()) {
            this.initialized = true;
            this.singletonInstance = this.createInstance();
            this.earlySingletonInstance = null;
        }
    }

    @Override
    public final T getObject() throws Exception {
        if (this.isSingleton()) {
            return this.initialized ? this.singletonInstance : this.getEarlySingletonInstance();
        }
        return this.createInstance();
    }

    private T getEarlySingletonInstance() throws Exception {
        Class<?>[] ifcs = this.getEarlySingletonInterfaces();
        if (ifcs == null) {
            throw new FactoryBeanNotInitializedException(this.getClass().getName() + " does not support circular references");
        }
        if (this.earlySingletonInstance == null) {
            this.earlySingletonInstance = Proxy.newProxyInstance(this.beanClassLoader, ifcs, (InvocationHandler)new EarlySingletonInvocationHandler());
        }
        return this.earlySingletonInstance;
    }

    @Nullable
    private T getSingletonInstance() throws IllegalStateException {
        Assert.state((boolean)this.initialized, (String)"Singleton instance not initialized yet");
        return this.singletonInstance;
    }

    @Override
    public void destroy() throws Exception {
        if (this.isSingleton()) {
            this.destroyInstance(this.singletonInstance);
        }
    }

    @Override
    @Nullable
    public abstract Class<?> getObjectType();

    protected abstract T createInstance() throws Exception;

    @Nullable
    protected Class<?>[] getEarlySingletonInterfaces() {
        Class[] classArray;
        Class<?> type = this.getObjectType();
        if (type != null && type.isInterface()) {
            Class[] classArray2 = new Class[1];
            classArray = classArray2;
            classArray2[0] = type;
        } else {
            classArray = null;
        }
        return classArray;
    }

    protected void destroyInstance(@Nullable T instance) throws Exception {
    }

    private class EarlySingletonInvocationHandler
    implements InvocationHandler {
        private EarlySingletonInvocationHandler() {
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (ReflectionUtils.isEqualsMethod((Method)method)) {
                return proxy == args[0];
            }
            if (ReflectionUtils.isHashCodeMethod((Method)method)) {
                return System.identityHashCode(proxy);
            }
            if (!AbstractFactoryBean.this.initialized && ReflectionUtils.isToStringMethod((Method)method)) {
                return "Early singleton proxy for interfaces " + ObjectUtils.nullSafeToString((Object[])AbstractFactoryBean.this.getEarlySingletonInterfaces());
            }
            try {
                return method.invoke(AbstractFactoryBean.this.getSingletonInstance(), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

