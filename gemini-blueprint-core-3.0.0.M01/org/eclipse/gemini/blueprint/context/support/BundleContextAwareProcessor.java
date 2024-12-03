/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.BundleContext
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.config.BeanPostProcessor
 */
package org.eclipse.gemini.blueprint.context.support;

import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

public class BundleContextAwareProcessor
implements BeanPostProcessor {
    private final BundleContext bundleContext;

    public BundleContextAwareProcessor(BundleContext aContext) {
        this.bundleContext = aContext;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof BundleContextAware) {
            ((BundleContextAware)bean).setBundleContext(this.bundleContext);
        }
        return bean;
    }
}

