/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.ObjectUtils;

class ApplicationListenerDetector
implements DestructionAwareBeanPostProcessor,
MergedBeanDefinitionPostProcessor {
    private static final Log logger = LogFactory.getLog(ApplicationListenerDetector.class);
    private final transient AbstractApplicationContext applicationContext;
    private final transient Map<String, Boolean> singletonNames = new ConcurrentHashMap<String, Boolean>(256);

    public ApplicationListenerDetector(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
        this.singletonNames.put(beanName, beanDefinition.isSingleton());
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean2, String beanName) {
        return bean2;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean2, String beanName) {
        if (bean2 instanceof ApplicationListener) {
            Boolean flag = this.singletonNames.get(beanName);
            if (Boolean.TRUE.equals(flag)) {
                this.applicationContext.addApplicationListener((ApplicationListener)bean2);
            } else if (Boolean.FALSE.equals(flag)) {
                if (logger.isWarnEnabled() && !this.applicationContext.containsBean(beanName)) {
                    logger.warn("Inner bean '" + beanName + "' implements ApplicationListener interface but is not reachable for event multicasting by its containing ApplicationContext because it does not have singleton scope. Only top-level listener beans are allowed to be of non-singleton scope.");
                }
                this.singletonNames.remove(beanName);
            }
        }
        return bean2;
    }

    @Override
    public void postProcessBeforeDestruction(Object bean2, String beanName) {
        if (bean2 instanceof ApplicationListener) {
            try {
                ApplicationEventMulticaster multicaster = this.applicationContext.getApplicationEventMulticaster();
                multicaster.removeApplicationListener((ApplicationListener)bean2);
                multicaster.removeApplicationListenerBean(beanName);
            }
            catch (IllegalStateException illegalStateException) {
                // empty catch block
            }
        }
    }

    @Override
    public boolean requiresDestruction(Object bean2) {
        return bean2 instanceof ApplicationListener;
    }

    public boolean equals(Object other) {
        return this == other || other instanceof ApplicationListenerDetector && this.applicationContext == ((ApplicationListenerDetector)other).applicationContext;
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.applicationContext);
    }
}

