/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.jmx.access;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.MBeanServerNotFoundException;
import org.springframework.jmx.access.MBeanClientInterceptor;
import org.springframework.jmx.access.MBeanInfoRetrievalException;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class MBeanProxyFactoryBean
extends MBeanClientInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware,
InitializingBean {
    @Nullable
    private Class<?> proxyInterface;
    @Nullable
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    @Nullable
    private Object mbeanProxy;

    public void setProxyInterface(Class<?> proxyInterface) {
        this.proxyInterface = proxyInterface;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws MBeanServerNotFoundException, MBeanInfoRetrievalException {
        super.afterPropertiesSet();
        if (this.proxyInterface == null) {
            this.proxyInterface = this.getManagementInterface();
            if (this.proxyInterface == null) {
                throw new IllegalArgumentException("Property 'proxyInterface' or 'managementInterface' is required");
            }
        } else if (this.getManagementInterface() == null) {
            this.setManagementInterface(this.proxyInterface);
        }
        this.mbeanProxy = new ProxyFactory(this.proxyInterface, (Interceptor)this).getProxy(this.beanClassLoader);
    }

    @Nullable
    public Object getObject() {
        return this.mbeanProxy;
    }

    public Class<?> getObjectType() {
        return this.proxyInterface;
    }

    public boolean isSingleton() {
        return true;
    }
}

