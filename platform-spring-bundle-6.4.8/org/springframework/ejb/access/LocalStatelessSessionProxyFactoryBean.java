/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ejb.access;

import javax.naming.NamingException;
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

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        if (this.businessInterface == null) {
            throw new IllegalArgumentException("businessInterface is required");
        }
        this.proxy = new ProxyFactory(this.businessInterface, this).getProxy(this.beanClassLoader);
    }

    @Override
    @Nullable
    public Object getObject() {
        return this.proxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.businessInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

