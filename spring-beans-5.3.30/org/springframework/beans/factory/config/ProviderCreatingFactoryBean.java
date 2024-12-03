/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.inject.Provider
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.beans.factory.config;

import java.io.Serializable;
import javax.inject.Provider;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ProviderCreatingFactoryBean
extends AbstractFactoryBean<Provider<Object>> {
    @Nullable
    private String targetBeanName;

    public void setTargetBeanName(String targetBeanName) {
        this.targetBeanName = targetBeanName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.hasText((String)this.targetBeanName, (String)"Property 'targetBeanName' is required");
        super.afterPropertiesSet();
    }

    @Override
    public Class<?> getObjectType() {
        return Provider.class;
    }

    @Override
    protected Provider<Object> createInstance() {
        BeanFactory beanFactory = this.getBeanFactory();
        Assert.state((beanFactory != null ? 1 : 0) != 0, (String)"No BeanFactory available");
        Assert.state((this.targetBeanName != null ? 1 : 0) != 0, (String)"No target bean name specified");
        return new TargetBeanProvider(beanFactory, this.targetBeanName);
    }

    private static class TargetBeanProvider
    implements Provider<Object>,
    Serializable {
        private final BeanFactory beanFactory;
        private final String targetBeanName;

        public TargetBeanProvider(BeanFactory beanFactory, String targetBeanName) {
            this.beanFactory = beanFactory;
            this.targetBeanName = targetBeanName;
        }

        public Object get() throws BeansException {
            return this.beanFactory.getBean(this.targetBeanName);
        }
    }
}

