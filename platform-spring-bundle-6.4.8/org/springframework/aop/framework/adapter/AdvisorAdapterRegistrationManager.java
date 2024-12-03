/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.adapter;

import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class AdvisorAdapterRegistrationManager
implements BeanPostProcessor {
    private AdvisorAdapterRegistry advisorAdapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();

    public void setAdvisorAdapterRegistry(AdvisorAdapterRegistry advisorAdapterRegistry) {
        this.advisorAdapterRegistry = advisorAdapterRegistry;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) throws BeansException {
        if (bean2 instanceof AdvisorAdapter) {
            this.advisorAdapterRegistry.registerAdvisorAdapter((AdvisorAdapter)bean2);
        }
        return bean2;
    }
}

