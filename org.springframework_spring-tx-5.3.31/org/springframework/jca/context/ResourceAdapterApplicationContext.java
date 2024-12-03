/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.resource.spi.BootstrapContext
 *  javax.resource.spi.work.WorkManager
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.support.GenericApplicationContext
 *  org.springframework.util.Assert
 */
package org.springframework.jca.context;

import javax.resource.spi.BootstrapContext;
import javax.resource.spi.work.WorkManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jca.context.BootstrapContextAware;
import org.springframework.jca.context.BootstrapContextAwareProcessor;
import org.springframework.util.Assert;

public class ResourceAdapterApplicationContext
extends GenericApplicationContext {
    private final BootstrapContext bootstrapContext;

    public ResourceAdapterApplicationContext(BootstrapContext bootstrapContext) {
        Assert.notNull((Object)bootstrapContext, (String)"BootstrapContext must not be null");
        this.bootstrapContext = bootstrapContext;
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new BootstrapContextAwareProcessor(this.bootstrapContext));
        beanFactory.ignoreDependencyInterface(BootstrapContextAware.class);
        beanFactory.registerResolvableDependency(BootstrapContext.class, (Object)this.bootstrapContext);
        beanFactory.registerResolvableDependency(WorkManager.class, () -> ((BootstrapContext)this.bootstrapContext).getWorkManager());
    }
}

