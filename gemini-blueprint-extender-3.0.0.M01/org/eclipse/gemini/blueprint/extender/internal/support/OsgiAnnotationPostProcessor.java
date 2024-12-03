/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.eclipse.gemini.blueprint.OsgiException
 *  org.eclipse.gemini.blueprint.context.BundleContextAware
 *  org.eclipse.gemini.blueprint.util.OsgiStringUtils
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanClassLoaderAware
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 */
package org.eclipse.gemini.blueprint.extender.internal.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.gemini.blueprint.OsgiException;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.eclipse.gemini.blueprint.extender.OsgiBeanFactoryPostProcessor;
import org.eclipse.gemini.blueprint.util.OsgiStringUtils;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class OsgiAnnotationPostProcessor
implements OsgiBeanFactoryPostProcessor {
    private static final Log log = LogFactory.getLog(OsgiAnnotationPostProcessor.class);
    private static final String ANNOTATION_BPP_CLASS = "org.eclipse.gemini.blueprint.extensions.annotation.ServiceReferenceInjectionBeanPostProcessor";

    @Override
    public void postProcessBeanFactory(BundleContext bundleContext, ConfigurableListableBeanFactory beanFactory) throws BeansException, OsgiException {
        block2: {
            Bundle bundle = bundleContext.getBundle();
            try {
                Class annotationBppClass = bundle.loadClass(ANNOTATION_BPP_CLASS);
                BeanPostProcessor annotationBeanPostProcessor = (BeanPostProcessor)BeanUtils.instantiateClass((Class)annotationBppClass);
                ((BeanFactoryAware)annotationBeanPostProcessor).setBeanFactory((BeanFactory)beanFactory);
                ((BeanClassLoaderAware)annotationBeanPostProcessor).setBeanClassLoader(beanFactory.getBeanClassLoader());
                ((BundleContextAware)annotationBeanPostProcessor).setBundleContext(bundleContext);
                beanFactory.addBeanPostProcessor(annotationBeanPostProcessor);
            }
            catch (ClassNotFoundException exception) {
                log.info((Object)("Spring-DM annotation package could not be loaded from bundle [" + OsgiStringUtils.nullSafeNameAndSymName((Bundle)bundle) + "]; annotation processing disabled..."));
                if (!log.isDebugEnabled()) break block2;
                log.debug((Object)"Cannot load annotation injection processor", (Throwable)exception);
            }
        }
    }
}

