/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 */
package com.atlassian.plugin.osgi.spring;

import com.atlassian.plugin.osgi.spring.BeanFactoryPostProcessorUtils;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class AutowireSupportBeanFactoryPostProcessor
implements OsgiBeanFactoryPostProcessor {
    public void postProcessBeanFactory(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory) {
        BeanFactoryPostProcessorUtils.registerPostProcessor(beanFactory, "org.springframework.context.annotation.internalAutowiredAnnotationProcessor", AutowiredAnnotationBeanPostProcessor.class, AutowiredAnnotationBeanPostProcessor::new);
    }
}

