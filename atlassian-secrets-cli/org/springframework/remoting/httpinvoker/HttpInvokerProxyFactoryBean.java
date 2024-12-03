/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.httpinvoker;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.util.Assert;

public class HttpInvokerProxyFactoryBean
extends HttpInvokerClientInterceptor
implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(this.getBeanClassLoader());
    }

    @Override
    @Nullable
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

