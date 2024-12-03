/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.caucho;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.caucho.HessianClientInterceptor;

public class HessianProxyFactoryBean
extends HessianClientInterceptor
implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        this.serviceProxy = new ProxyFactory(this.getServiceInterface(), this).getProxy(this.getBeanClassLoader());
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

