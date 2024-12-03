/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.BindingProvider
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.remoting.jaxws;

import javax.xml.ws.BindingProvider;
import org.aopalliance.aop.Advice;
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
        Assert.notNull(ifc, (String)"Property 'serviceInterface' is required");
        ProxyFactory pf = new ProxyFactory();
        pf.addInterface(ifc);
        pf.addInterface(BindingProvider.class);
        pf.addAdvice((Advice)this);
        this.serviceProxy = pf.getProxy(this.getBeanClassLoader());
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

