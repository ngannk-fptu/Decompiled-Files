/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.context.annotation.CommonAnnotationBeanPostProcessor
 */
package com.atlassian.plugin.osgi.spring;

import com.atlassian.plugin.osgi.spring.BeanFactoryPostProcessorUtils;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.osgi.framework.BundleContext;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;

public class CommonAnnotationBeanFactoryPostProcessor
implements OsgiBeanFactoryPostProcessor {
    public void postProcessBeanFactory(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory) {
        BeanFactoryPostProcessorUtils.registerPostProcessor(beanFactory, "org.springframework.context.annotation.internalCommonAnnotationProcessor", CommonAnnotationBeanPostProcessor.class, CommonAnnotationBeanPostProcessor::new);
    }
}

