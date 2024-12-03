/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.autoproxy;

import java.util.List;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.BeanFactoryAdvisorRetrievalHelper;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractAdvisorAutoProxyCreator
extends AbstractAutoProxyCreator {
    @Nullable
    private BeanFactoryAdvisorRetrievalHelper advisorRetrievalHelper;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        super.setBeanFactory(beanFactory);
        if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
            throw new IllegalArgumentException("AdvisorAutoProxyCreator requires a ConfigurableListableBeanFactory: " + beanFactory);
        }
        this.initBeanFactory((ConfigurableListableBeanFactory)beanFactory);
    }

    protected void initBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        this.advisorRetrievalHelper = new BeanFactoryAdvisorRetrievalHelperAdapter(beanFactory);
    }

    @Override
    @Nullable
    protected Object[] getAdvicesAndAdvisorsForBean(Class<?> beanClass, String beanName, @Nullable TargetSource targetSource) {
        List<Advisor> advisors = this.findEligibleAdvisors(beanClass, beanName);
        if (advisors.isEmpty()) {
            return DO_NOT_PROXY;
        }
        return advisors.toArray();
    }

    protected List<Advisor> findEligibleAdvisors(Class<?> beanClass, String beanName) {
        List<Advisor> candidateAdvisors = this.findCandidateAdvisors();
        List<Advisor> eligibleAdvisors = this.findAdvisorsThatCanApply(candidateAdvisors, beanClass, beanName);
        this.extendAdvisors(eligibleAdvisors);
        if (!eligibleAdvisors.isEmpty()) {
            eligibleAdvisors = this.sortAdvisors(eligibleAdvisors);
        }
        return eligibleAdvisors;
    }

    protected List<Advisor> findCandidateAdvisors() {
        Assert.state(this.advisorRetrievalHelper != null, "No BeanFactoryAdvisorRetrievalHelper available");
        return this.advisorRetrievalHelper.findAdvisorBeans();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected List<Advisor> findAdvisorsThatCanApply(List<Advisor> candidateAdvisors, Class<?> beanClass, String beanName) {
        ProxyCreationContext.setCurrentProxiedBeanName(beanName);
        try {
            List<Advisor> list = AopUtils.findAdvisorsThatCanApply(candidateAdvisors, beanClass);
            return list;
        }
        finally {
            ProxyCreationContext.setCurrentProxiedBeanName(null);
        }
    }

    protected boolean isEligibleAdvisorBean(String beanName) {
        return true;
    }

    protected List<Advisor> sortAdvisors(List<Advisor> advisors) {
        AnnotationAwareOrderComparator.sort(advisors);
        return advisors;
    }

    protected void extendAdvisors(List<Advisor> candidateAdvisors) {
    }

    @Override
    protected boolean advisorsPreFiltered() {
        return true;
    }

    private class BeanFactoryAdvisorRetrievalHelperAdapter
    extends BeanFactoryAdvisorRetrievalHelper {
        public BeanFactoryAdvisorRetrievalHelperAdapter(ConfigurableListableBeanFactory beanFactory) {
            super(beanFactory);
        }

        @Override
        protected boolean isEligibleBean(String beanName) {
            return AbstractAdvisorAutoProxyCreator.this.isEligibleAdvisorBean(beanName);
        }
    }
}

