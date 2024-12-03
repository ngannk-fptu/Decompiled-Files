/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.rmi;

import javax.naming.NamingException;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.rmi.JndiRmiClientInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class JndiRmiProxyFactoryBean
extends JndiRmiClientInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware {
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private Object serviceProxy;

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, "Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, this).getProxy(this.beanClassLoader);
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

