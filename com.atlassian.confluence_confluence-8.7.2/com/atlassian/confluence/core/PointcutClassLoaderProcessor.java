/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.aop.Pointcut
 *  org.springframework.aop.aspectj.AspectJExpressionPointcut
 *  org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.core.Ordered
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ConfluenceAspectJExpressionPointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultBeanFactoryPointcutAdvisor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

public final class PointcutClassLoaderProcessor
implements BeanPostProcessor,
BeanFactoryAware,
Ordered {
    private static final Logger LOG = LoggerFactory.getLogger(PointcutClassLoaderProcessor.class);
    private BeanFactory beanFactory;
    private int order = Integer.MAX_VALUE;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (!(bean instanceof DefaultBeanFactoryPointcutAdvisor)) {
            return bean;
        }
        DefaultBeanFactoryPointcutAdvisor advisor = (DefaultBeanFactoryPointcutAdvisor)bean;
        this.reconfigureAdvisor(advisor);
        return advisor;
    }

    private void reconfigureAdvisor(DefaultBeanFactoryPointcutAdvisor advisor) {
        Pointcut oldPc = advisor.getPointcut();
        if (!(oldPc instanceof AspectJExpressionPointcut)) {
            return;
        }
        ConfluenceAspectJExpressionPointcut confluencePointcut = new ConfluenceAspectJExpressionPointcut();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Post processing pointcut " + oldPc);
        }
        AspectJExpressionPointcut aspectJPc = (AspectJExpressionPointcut)oldPc;
        confluencePointcut.setExpression(aspectJPc.getExpression());
        confluencePointcut.setLocation(aspectJPc.getLocation());
        confluencePointcut.setBeanFactory(this.beanFactory);
        advisor.setPointcut((Pointcut)confluencePointcut);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOrder() {
        return this.order;
    }
}

