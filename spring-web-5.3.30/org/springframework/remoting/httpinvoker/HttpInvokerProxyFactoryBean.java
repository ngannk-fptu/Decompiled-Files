/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.httpinvoker;

import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.httpinvoker.HttpInvokerClientInterceptor;
import org.springframework.util.Assert;

@Deprecated
public class HttpInvokerProxyFactoryBean
extends HttpInvokerClientInterceptor
implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class ifc = this.getServiceInterface();
        Assert.notNull((Object)ifc, (String)"Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, (Interceptor)this).getProxy(this.getBeanClassLoader());
    }

    @Nullable
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

