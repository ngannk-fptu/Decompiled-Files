/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.rmi.RmiClientInterceptor;
import org.springframework.util.Assert;

public class RmiProxyFactoryBean
extends RmiClientInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware {
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(this.getBeanClassLoader());
    }

    @Override
    public Object getObject() {
        return this.serviceProxy;
    }

    @Override
    public Class<?> getObjectType() {
        return this.getServiceInterface();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

