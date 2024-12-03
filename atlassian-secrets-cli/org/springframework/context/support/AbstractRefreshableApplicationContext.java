/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.lang.Nullable;

public abstract class AbstractRefreshableApplicationContext
extends AbstractApplicationContext {
    @Nullable
    private Boolean allowBeanDefinitionOverriding;
    @Nullable
    private Boolean allowCircularReferences;
    @Nullable
    private DefaultListableBeanFactory beanFactory;
    private final Object beanFactoryMonitor = new Object();

    public AbstractRefreshableApplicationContext() {
    }

    public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
        super(parent);
    }

    public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
        this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
    }

    public void setAllowCircularReferences(boolean allowCircularReferences) {
        this.allowCircularReferences = allowCircularReferences;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected final void refreshBeanFactory() throws BeansException {
        if (this.hasBeanFactory()) {
            this.destroyBeans();
            this.closeBeanFactory();
        }
        try {
            DefaultListableBeanFactory beanFactory = this.createBeanFactory();
            beanFactory.setSerializationId(this.getId());
            this.customizeBeanFactory(beanFactory);
            this.loadBeanDefinitions(beanFactory);
            Object object = this.beanFactoryMonitor;
            synchronized (object) {
                this.beanFactory = beanFactory;
            }
        }
        catch (IOException ex) {
            throw new ApplicationContextException("I/O error parsing bean definition source for " + this.getDisplayName(), ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected void cancelRefresh(BeansException ex) {
        Object object = this.beanFactoryMonitor;
        synchronized (object) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
            }
        }
        super.cancelRefresh(ex);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    protected final void closeBeanFactory() {
        Object object = this.beanFactoryMonitor;
        synchronized (object) {
            if (this.beanFactory != null) {
                this.beanFactory.setSerializationId(null);
                this.beanFactory = null;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final boolean hasBeanFactory() {
        Object object = this.beanFactoryMonitor;
        synchronized (object) {
            return this.beanFactory != null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public final ConfigurableListableBeanFactory getBeanFactory() {
        Object object = this.beanFactoryMonitor;
        synchronized (object) {
            if (this.beanFactory == null) {
                throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
            }
            return this.beanFactory;
        }
    }

    @Override
    protected void assertBeanFactoryActive() {
    }

    protected DefaultListableBeanFactory createBeanFactory() {
        return new DefaultListableBeanFactory(this.getInternalParentBeanFactory());
    }

    protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
        if (this.allowBeanDefinitionOverriding != null) {
            beanFactory.setAllowBeanDefinitionOverriding(this.allowBeanDefinitionOverriding);
        }
        if (this.allowCircularReferences != null) {
            beanFactory.setAllowCircularReferences(this.allowCircularReferences);
        }
    }

    protected abstract void loadBeanDefinitions(DefaultListableBeanFactory var1) throws BeansException, IOException;
}

