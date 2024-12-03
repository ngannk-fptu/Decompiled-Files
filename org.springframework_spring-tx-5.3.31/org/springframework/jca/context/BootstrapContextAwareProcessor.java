/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.BootstrapContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.lang.Nullable
 */
package org.springframework.jca.context;

import javax.resource.spi.BootstrapContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.jca.context.BootstrapContextAware;
import org.springframework.lang.Nullable;

class BootstrapContextAwareProcessor
implements BeanPostProcessor {
    @Nullable
    private final BootstrapContext bootstrapContext;

    public BootstrapContextAwareProcessor(@Nullable BootstrapContext bootstrapContext) {
        this.bootstrapContext = bootstrapContext;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.bootstrapContext != null && bean instanceof BootstrapContextAware) {
            ((BootstrapContextAware)bean).setBootstrapContext(this.bootstrapContext);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

