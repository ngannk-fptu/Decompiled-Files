/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.rmi;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.rmi.RmiClientInterceptor;
import org.springframework.util.Assert;

@Deprecated
public class RmiProxyFactoryBean
extends RmiClientInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware {
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, (String)"Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, (Interceptor)this).getProxy(this.getBeanClassLoader());
    }

    public Object getObject() {
        return this.serviceProxy;
    }

    public Class<?> getObjectType() {
        return this.getServiceInterface();
    }

    public boolean isSingleton() {
        return true;
    }
}

