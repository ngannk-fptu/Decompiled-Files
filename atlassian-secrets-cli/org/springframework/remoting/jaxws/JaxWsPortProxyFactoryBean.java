/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.BindingProvider
 */
package org.springframework.remoting.jaxws;

import javax.xml.ws.BindingProvider;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.remoting.jaxws.JaxWsPortClientInterceptor;
import org.springframework.util.Assert;

public class JaxWsPortProxyFactoryBean
extends JaxWsPortClientInterceptor
implements FactoryBean<Object> {
    @Nullable
    private Object serviceProxy;

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        ProxyFactory pf = new ProxyFactory();
        pf.addInterface(ifc);
        pf.addInterface(BindingProvider.class);
        pf.addAdvice(this);
        this.serviceProxy = pf.getProxy(this.getBeanClassLoader());
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

