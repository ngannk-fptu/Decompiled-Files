/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.access;

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
        this.mbeanProxy = new ProxyFactory(this.proxyInterface, this).getProxy(this.beanClassLoader);
    }

    @Override
    @Nullable
    public Object getObject() {
        return this.mbeanProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.proxyInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

