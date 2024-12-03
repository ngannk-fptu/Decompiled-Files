/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.ejb.access;

import javax.naming.NamingException;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.ejb.access.LocalSlsbInvokerInterceptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class LocalStatelessSessionProxyFactoryBean
extends LocalSlsbInvokerInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware {
    @Nullable
    private Class<?> businessInterface;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private Object proxy;

    public void setBusinessInterface(@Nullable Class<?> businessInterface) {
        this.businessInterface = businessInterface;
    }

    @Nullable
    public Class<?> getBusinessInterface() {
        return this.businessInterface;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.businessInterface == null) {
            throw new IllegalArgumentException("businessInterface is required");
        }
        this.proxy = new ProxyFactory(this.businessInterface, (Interceptor)this).getProxy(this.beanClassLoader);
    }

    @Nullable
    public Object getObject() {
        return this.proxy;
    }

    public Class<?> getObjectType() {
        return this.businessInterface;
    }

    public boolean isSingleton() {
        return true;
    }
}

