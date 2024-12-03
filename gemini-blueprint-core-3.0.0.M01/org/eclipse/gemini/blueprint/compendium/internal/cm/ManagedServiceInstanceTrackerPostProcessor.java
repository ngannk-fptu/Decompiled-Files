/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 */
package org.eclipse.gemini.blueprint.compendium.internal.cm;

import org.eclipse.gemini.blueprint.compendium.internal.cm.ConfigurationAdminManager;
import org.eclipse.gemini.blueprint.compendium.internal.cm.DefaultManagedServiceBeanManager;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class ManagedServiceInstanceTrackerPostProcessor
implements BeanFactoryAware,
BundleContextAware,
InitializingBean,
BeanPostProcessor,
DestructionAwareBeanPostProcessor,
DisposableBean {
    private final String trackedBean;
    private DefaultManagedServiceBeanManager managedServiceManager;
    private String pid;
    private String updateMethod;
    private boolean autowireOnUpdate = false;
    private BundleContext bundleContext;
    private BeanFactory beanFactory;

    public ManagedServiceInstanceTrackerPostProcessor(String beanNameToTrack) {
        this.trackedBean = beanNameToTrack;
    }

    public void afterPropertiesSet() throws Exception {
        ConfigurationAdminManager cam = new ConfigurationAdminManager(this.pid, this.bundleContext);
        this.managedServiceManager = new DefaultManagedServiceBeanManager(this.autowireOnUpdate, this.updateMethod, cam, this.beanFactory);
    }

    public void destroy() throws Exception {
        this.managedServiceManager.destroy();
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (this.trackedBean.equals(beanName)) {
            return this.managedServiceManager.register(bean);
        }
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (this.trackedBean.equals(beanName)) {
            this.managedServiceManager.unregister(bean);
        }
    }

    public boolean requiresDestruction(Object o) {
        return true;
    }

    @Override
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setPersistentId(String pid) {
        this.pid = pid;
    }

    public void setAutowireOnUpdate(boolean autowireOnUpdate) {
        this.autowireOnUpdate = autowireOnUpdate;
    }

    public void setUpdateMethod(String methodName) {
        this.updateMethod = methodName;
    }
}

