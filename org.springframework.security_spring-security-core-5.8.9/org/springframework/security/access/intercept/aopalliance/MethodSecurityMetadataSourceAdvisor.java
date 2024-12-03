/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.support.AbstractPointcutAdvisor
 *  org.springframework.aop.support.StaticMethodMatcherPointcut
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.util.Assert
 *  org.springframework.util.CollectionUtils
 */
package org.springframework.security.access.intercept.aopalliance;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.security.access.method.MethodSecurityMetadataSource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

@Deprecated
public class MethodSecurityMetadataSourceAdvisor
extends AbstractPointcutAdvisor
implements BeanFactoryAware {
    private transient MethodSecurityMetadataSource attributeSource;
    private transient MethodInterceptor interceptor;
    private final Pointcut pointcut = new MethodSecurityMetadataSourcePointcut();
    private BeanFactory beanFactory;
    private final String adviceBeanName;
    private final String metadataSourceBeanName;
    private volatile transient Object adviceMonitor = new Object();

    public MethodSecurityMetadataSourceAdvisor(String adviceBeanName, MethodSecurityMetadataSource attributeSource, String attributeSourceBeanName) {
        Assert.notNull((Object)adviceBeanName, (String)"The adviceBeanName cannot be null");
        Assert.notNull((Object)attributeSource, (String)"The attributeSource cannot be null");
        Assert.notNull((Object)attributeSourceBeanName, (String)"The attributeSourceBeanName cannot be null");
        this.adviceBeanName = adviceBeanName;
        this.attributeSource = attributeSource;
        this.metadataSourceBeanName = attributeSourceBeanName;
    }

    public Pointcut getPointcut() {
        return this.pointcut;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Advice getAdvice() {
        Object object = this.adviceMonitor;
        synchronized (object) {
            if (this.interceptor == null) {
                Assert.notNull((Object)this.adviceBeanName, (String)"'adviceBeanName' must be set for use with bean factory lookup.");
                Assert.state((this.beanFactory != null ? 1 : 0) != 0, (String)"BeanFactory must be set to resolve 'adviceBeanName'");
                this.interceptor = (MethodInterceptor)this.beanFactory.getBean(this.adviceBeanName, MethodInterceptor.class);
            }
            return this.interceptor;
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject();
        this.adviceMonitor = new Object();
        this.attributeSource = (MethodSecurityMetadataSource)this.beanFactory.getBean(this.metadataSourceBeanName, MethodSecurityMetadataSource.class);
    }

    class MethodSecurityMetadataSourcePointcut
    extends StaticMethodMatcherPointcut
    implements Serializable {
        MethodSecurityMetadataSourcePointcut() {
        }

        public boolean matches(Method m, Class<?> targetClass) {
            MethodSecurityMetadataSource source = MethodSecurityMetadataSourceAdvisor.this.attributeSource;
            return !CollectionUtils.isEmpty(source.getAttributes(m, targetClass));
        }
    }
}

