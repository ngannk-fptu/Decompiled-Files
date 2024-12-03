/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableBeanFactory
 *  org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
 */
package org.eclipse.gemini.blueprint.service.dependency.internal;

import org.eclipse.gemini.blueprint.service.dependency.internal.DefaultMandatoryDependencyManager;
import org.eclipse.gemini.blueprint.service.dependency.internal.MandatoryServiceDependencyManager;
import org.eclipse.gemini.blueprint.service.exporter.support.OsgiServiceFactoryBean;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterControllerUtils;
import org.eclipse.gemini.blueprint.service.exporter.support.internal.controller.ExporterInternalActions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;

public class MandatoryDependencyBeanPostProcessor
implements BeanFactoryAware,
BeanPostProcessor,
DestructionAwareBeanPostProcessor {
    private MandatoryServiceDependencyManager manager;
    private ConfigurableBeanFactory beanFactory;

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof OsgiServiceFactoryBean && this.beanFactory.containsLocalBean(beanName)) {
            this.manager.addServiceExporter(bean, beanName);
        }
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof OsgiServiceFactoryBean && this.beanFactory.containsLocalBean(beanName)) {
            String exporterName = beanName;
            if (this.beanFactory.isFactoryBean(beanName)) {
                exporterName = "&" + beanName;
            }
            if (this.beanFactory.isSingleton(exporterName)) {
                ExporterInternalActions controller = ExporterControllerUtils.getControllerFor(bean);
                controller.registerServiceAtStartup(false);
            }
        }
        return bean;
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        DefaultMandatoryDependencyManager manager = new DefaultMandatoryDependencyManager();
        manager.setBeanFactory(beanFactory);
        this.manager = manager;
        this.beanFactory = (ConfigurableBeanFactory)beanFactory;
    }

    public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
        if (bean instanceof OsgiServiceFactoryBean && this.beanFactory.containsLocalBean(beanName)) {
            this.manager.removeServiceExporter(bean, beanName);
        }
    }

    public boolean requiresDestruction(Object o) {
        return true;
    }
}

