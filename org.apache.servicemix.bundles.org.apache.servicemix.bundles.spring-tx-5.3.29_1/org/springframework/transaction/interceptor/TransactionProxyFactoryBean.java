/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.framework.AbstractSingletonProxyFactoryBean
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.DefaultPointcutAdvisor
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.lang.Nullable
 */
package org.springframework.transaction.interceptor;

import java.util.Properties;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.lang.Nullable;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.interceptor.TransactionalProxy;

public class TransactionProxyFactoryBean
extends AbstractSingletonProxyFactoryBean
implements BeanFactoryAware {
    private final TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
    @Nullable
    private Pointcut pointcut;

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionInterceptor.setTransactionManager(transactionManager);
    }

    public void setTransactionAttributes(Properties transactionAttributes) {
        this.transactionInterceptor.setTransactionAttributes(transactionAttributes);
    }

    public void setTransactionAttributeSource(TransactionAttributeSource transactionAttributeSource) {
        this.transactionInterceptor.setTransactionAttributeSource(transactionAttributeSource);
    }

    public void setPointcut(Pointcut pointcut) {
        this.pointcut = pointcut;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.transactionInterceptor.setBeanFactory(beanFactory);
    }

    protected Object createMainInterceptor() {
        this.transactionInterceptor.afterPropertiesSet();
        if (this.pointcut != null) {
            return new DefaultPointcutAdvisor(this.pointcut, (Advice)this.transactionInterceptor);
        }
        return new TransactionAttributeSourceAdvisor(this.transactionInterceptor);
    }

    protected void postProcessProxyFactory(ProxyFactory proxyFactory) {
        proxyFactory.addInterface(TransactionalProxy.class);
    }
}

