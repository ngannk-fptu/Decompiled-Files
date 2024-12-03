/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.framework.autoproxy.target;

import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AopInfrastructureBean;
import org.springframework.aop.framework.autoproxy.TargetSourceCreator;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.Nullable;

public abstract class AbstractBeanFactoryBasedTargetSourceCreator
implements TargetSourceCreator,
BeanFactoryAware,
DisposableBean {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private ConfigurableBeanFactory beanFactory;
    private final Map<String, DefaultListableBeanFactory> internalBeanFactories = new HashMap<String, DefaultListableBeanFactory>();

    @Override
    public final void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof ConfigurableBeanFactory)) {
            throw new IllegalStateException("Cannot do auto-TargetSource creation with a BeanFactory that doesn't implement ConfigurableBeanFactory: " + beanFactory.getClass());
        }
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

    protected final BeanFactory getBeanFactory() {
        return this.beanFactory;
    }

    @Override
    @Nullable
    public final TargetSource getTargetSource(Class<?> beanClass, String beanName) {
        AbstractBeanFactoryBasedTargetSource targetSource = this.createBeanFactoryBasedTargetSource(beanClass, beanName);
        if (targetSource == null) {
            return null;
        }
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Configuring AbstractBeanFactoryBasedTargetSource: " + targetSource);
        }
        DefaultListableBeanFactory internalBeanFactory = this.getInternalBeanFactoryForBean(beanName);
        BeanDefinition bd = this.beanFactory.getMergedBeanDefinition(beanName);
        GenericBeanDefinition bdCopy = new GenericBeanDefinition(bd);
        if (this.isPrototypeBased()) {
            bdCopy.setScope("prototype");
        }
        internalBeanFactory.registerBeanDefinition(beanName, bdCopy);
        targetSource.setTargetBeanName(beanName);
        targetSource.setBeanFactory(internalBeanFactory);
        return targetSource;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected DefaultListableBeanFactory getInternalBeanFactoryForBean(String beanName) {
        Map<String, DefaultListableBeanFactory> map = this.internalBeanFactories;
        synchronized (map) {
            DefaultListableBeanFactory internalBeanFactory = this.internalBeanFactories.get(beanName);
            if (internalBeanFactory == null) {
                internalBeanFactory = this.buildInternalBeanFactory(this.beanFactory);
                this.internalBeanFactories.put(beanName, internalBeanFactory);
            }
            return internalBeanFactory;
        }
    }

    protected DefaultListableBeanFactory buildInternalBeanFactory(ConfigurableBeanFactory containingFactory) {
        DefaultListableBeanFactory internalBeanFactory = new DefaultListableBeanFactory(containingFactory);
        internalBeanFactory.copyConfigurationFrom(containingFactory);
        internalBeanFactory.getBeanPostProcessors().removeIf(beanPostProcessor -> beanPostProcessor instanceof AopInfrastructureBean);
        return internalBeanFactory;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void destroy() {
        Map<String, DefaultListableBeanFactory> map = this.internalBeanFactories;
        synchronized (map) {
            for (DefaultListableBeanFactory bf : this.internalBeanFactories.values()) {
                bf.destroySingletons();
            }
        }
    }

    protected boolean isPrototypeBased() {
        return true;
    }

    @Nullable
    protected abstract AbstractBeanFactoryBasedTargetSource createBeanFactoryBasedTargetSource(Class<?> var1, String var2);
}

