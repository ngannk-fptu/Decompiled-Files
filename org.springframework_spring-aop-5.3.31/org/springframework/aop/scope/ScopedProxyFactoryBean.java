/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.FactoryBeanNotInitializedException
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.aop.scope;

import java.lang.reflect.Modifier;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.scope.DefaultScopedObject;
import org.springframework.aop.support.DelegatingIntroductionInterceptor;
import org.springframework.aop.target.SimpleBeanTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public class ScopedProxyFactoryBean
extends ProxyConfig
implements FactoryBean<Object>,
BeanFactoryAware,
AopInfrastructureBean {
    private final SimpleBeanTargetSource scopedTargetSource = new SimpleBeanTargetSource();
    @Nullable
    private String targetBeanName;
    @Nullable
    private Object proxy;

    public ScopedProxyFactoryBean() {
        this.setProxyTargetClass(true);
    }

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
        this.scopedTargetSource.setTargetBeanName(targetBeanName);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Not running in a ConfigurableBeanFactory: " + beanFactory);
        }
        ConfigurableBeanFactory cbf = (ConfigurableBeanFactory)beanFactory;
        this.scopedTargetSource.setBeanFactory(beanFactory);
        ProxyFactory pf = new ProxyFactory();
        pf.copyFrom(this);
        pf.setTargetSource(this.scopedTargetSource);
        Assert.notNull((Object)this.targetBeanName, (String)"Property 'targetBeanName' is required");
        Class beanType = beanFactory.getType(this.targetBeanName);
        if (beanType == null) {
            throw new IllegalStateException("Cannot create scoped proxy for bean '" + this.targetBeanName + "': Target type could not be determined at the time of proxy creation.");
        }
        if (!this.isProxyTargetClass() || beanType.isInterface() || Modifier.isPrivate(beanType.getModifiers())) {
            pf.setInterfaces(ClassUtils.getAllInterfacesForClass((Class)beanType, (ClassLoader)cbf.getBeanClassLoader()));
        }
        DefaultScopedObject scopedObject = new DefaultScopedObject(cbf, this.scopedTargetSource.getTargetBeanName());
        pf.addAdvice(new DelegatingIntroductionInterceptor(scopedObject));
        pf.addInterface(AopInfrastructureBean.class);
        this.proxy = pf.getProxy(cbf.getBeanClassLoader());
    }

    public Object getObject() {
        if (this.proxy == null) {
            throw new FactoryBeanNotInitializedException();
        }
        return this.proxy;
    }

    public Class<?> getObjectType() {
        if (this.proxy != null) {
            return this.proxy.getClass();
        }
        return this.scopedTargetSource.getTargetClass();
    }

    public boolean isSingleton() {
        return true;
    }
}

