/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.Interceptor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.remoting.rmi;

import javax.naming.NamingException;
import org.aopalliance.intercept.Interceptor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.remoting.rmi.JndiRmiClientInterceptor;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

@Deprecated
public class JndiRmiProxyFactoryBean
extends JndiRmiClientInterceptor
implements FactoryBean<Object>,
BeanClassLoaderAware {
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();
    private Object serviceProxy;

    public void setBeanClassLoader(ClassLoader classLoader) {
        this.beanClassLoader = classLoader;
    }

    @Override
    public void afterPropertiesSet() throws NamingException {
        super.afterPropertiesSet();
        Class<?> ifc = this.getServiceInterface();
        Assert.notNull(ifc, (String)"Property 'serviceInterface' is required");
        this.serviceProxy = new ProxyFactory(ifc, (Interceptor)this).getProxy(this.beanClassLoader);
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

