/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.FactoryBeanNotInitializedException
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ClassUtils
 */
package org.springframework.aop.framework;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public abstract class AbstractSingletonProxyFactoryBean
extends ProxyConfig
implements FactoryBean<Object>,
BeanClassLoaderAware,
InitializingBean {
    @Nullable
    private Object target;
    @Nullable
    private Class<?>[] proxyInterfaces;
    @Nullable
    private Object[] preInterceptors;
    @Nullable
    private Object[] postInterceptors;
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
    @Nullable
    private transient ClassLoader proxyClassLoader;
    @Nullable
    private Object proxy;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setProxyInterfaces(Class<?>[] proxyInterfaces) {
        this.proxyInterfaces = proxyInterfaces;
    }

    public void setPreInterceptors(Object[] preInterceptors) {
        this.preInterceptors = preInterceptors;
    }

    public void setPostInterceptors(Object[] postInterceptors) {
        this.postInterceptors = postInterceptors;
    }

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    public void setProxyClassLoader(ClassLoader classLoader) {
        this.proxyClassLoader = classLoader;
    }

    public void setBeanClassLoader(ClassLoader classLoader) {
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = classLoader;
        }
    }

    public void afterPropertiesSet() {
        Class<?> targetClass;
        if (this.target == null) {
            throw new IllegalArgumentException("Property 'target' is required");
        }
        if (this.target instanceof String) {
            throw new IllegalArgumentException("'target' needs to be a bean reference, not a bean name as value");
        }
        if (this.proxyClassLoader == null) {
            this.proxyClassLoader = ClassUtils.getDefaultClassLoader();
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        if (this.preInterceptors != null) {
            for (Object interceptor : this.preInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(this.createMainInterceptor()));
        if (this.postInterceptors != null) {
            for (Object interceptor : this.postInterceptors) {
                proxyFactory.addAdvisor(this.advisorAdapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.copyFrom(this);
        TargetSource targetSource = this.createTargetSource(this.target);
        proxyFactory.setTargetSource(targetSource);
        if (this.proxyInterfaces != null) {
            proxyFactory.setInterfaces(this.proxyInterfaces);
        } else if (!this.isProxyTargetClass() && (targetClass = targetSource.getTargetClass()) != null) {
            proxyFactory.setInterfaces(ClassUtils.getAllInterfacesForClass(targetClass, (ClassLoader)this.proxyClassLoader));
        }
        this.postProcessProxyFactory(proxyFactory);
        this.proxy = proxyFactory.getProxy(this.proxyClassLoader);
    }

    protected TargetSource createTargetSource(Object target) {
        if (target instanceof TargetSource) {
            return (TargetSource)target;
        }
        return new SingletonTargetSource(target);
    }

    protected void postProcessProxyFactory(ProxyFactory proxyFactory) {
    }

    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }

    @Nullable
    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        if (this.proxyInterfaces != null && this.proxyInterfaces.length == 1) {
            return this.proxyInterfaces[0];
        }
        if (this.target instanceof TargetSource) {
            return ((TargetSource)this.target).getTargetClass();
        }
        if (this.target != null) {
            return this.target.getClass();
        }
        return null;
    }

    public final boolean isSingleton() {
        return true;
    }

    protected abstract Object createMainInterceptor();
}

