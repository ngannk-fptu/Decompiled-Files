/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext
 *  org.springframework.beans.factory.config.BeanPostProcessor
 *  org.springframework.beans.factory.support.AutowireCandidateResolver
 *  org.springframework.beans.factory.support.DefaultListableBeanFactory
 *  org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 *  org.springframework.core.ParameterNameDiscoverer
 */
package org.springframework.osgi.atlassian;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;
import org.eclipse.gemini.blueprint.context.support.OsgiBundleXmlApplicationContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.osgi.atlassian.ExcludableContextAnnotationAutowireCandidateResolver;
import org.springframework.osgi.context.BundleContextAware;

public class NonValidatingOsgiBundleXmlApplicationContext
extends OsgiBundleXmlApplicationContext {
    public NonValidatingOsgiBundleXmlApplicationContext(String[] configLocations) {
        super(configLocations);
    }

    protected void initBeanDefinitionReader(XmlBeanDefinitionReader beanDefinitionReader) {
        super.initBeanDefinitionReader(beanDefinitionReader);
        beanDefinitionReader.setValidationMode(0);
        beanDefinitionReader.setNamespaceAware(true);
    }

    protected void customizeBeanFactory(@Nonnull DefaultListableBeanFactory beanFactory) {
        if (Boolean.getBoolean("atlassian.disable.spring.cache.bean.metadata")) {
            beanFactory.setCacheBeanMetadata(false);
        }
        if (!Boolean.getBoolean("atlassian.enable.spring.parameter.name.discoverer")) {
            beanFactory.setParameterNameDiscoverer(new ParameterNameDiscoverer(){

                public String[] getParameterNames(@Nonnull Method method) {
                    return null;
                }

                public String[] getParameterNames(@Nonnull Constructor<?> ctor) {
                    return null;
                }
            });
        }
        super.customizeBeanFactory(beanFactory);
        beanFactory.addBeanPostProcessor((BeanPostProcessor)new ShimSpringDmBundleContextAwareBeanPostProcessor());
        beanFactory.setAutowireCandidateResolver((AutowireCandidateResolver)new ExcludableContextAnnotationAutowireCandidateResolver());
    }

    private class ShimSpringDmBundleContextAwareBeanPostProcessor
    implements BeanPostProcessor {
        private ShimSpringDmBundleContextAwareBeanPostProcessor() {
        }

        public Object postProcessBeforeInitialization(@Nonnull Object bean, String beanName) {
            if (bean instanceof BundleContextAware) {
                ((BundleContextAware)bean).setBundleContext(NonValidatingOsgiBundleXmlApplicationContext.this.getBundleContext());
            }
            return bean;
        }

        public Object postProcessAfterInitialization(@Nonnull Object bean, String beanName) {
            return bean;
        }
    }
}

