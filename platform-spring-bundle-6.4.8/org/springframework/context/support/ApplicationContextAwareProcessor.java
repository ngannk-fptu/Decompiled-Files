/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.security.AccessControlContext;
import java.security.AccessController;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.EmbeddedValueResolver;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.ApplicationStartupAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

class ApplicationContextAwareProcessor
implements BeanPostProcessor {
    private final ConfigurableApplicationContext applicationContext;
    private final StringValueResolver embeddedValueResolver;

    public ApplicationContextAwareProcessor(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.embeddedValueResolver = new EmbeddedValueResolver(applicationContext.getBeanFactory());
    }

    @Override
    @Nullable
    public Object postProcessBeforeInitialization(Object bean2, String beanName) throws BeansException {
        if (!(bean2 instanceof EnvironmentAware || bean2 instanceof EmbeddedValueResolverAware || bean2 instanceof ResourceLoaderAware || bean2 instanceof ApplicationEventPublisherAware || bean2 instanceof MessageSourceAware || bean2 instanceof ApplicationContextAware || bean2 instanceof ApplicationStartupAware)) {
            return bean2;
        }
        AccessControlContext acc = null;
        if (System.getSecurityManager() != null) {
            acc = this.applicationContext.getBeanFactory().getAccessControlContext();
        }
        if (acc != null) {
            AccessController.doPrivileged(() -> {
                this.invokeAwareInterfaces(bean2);
                return null;
            }, acc);
        } else {
            this.invokeAwareInterfaces(bean2);
        }
        return bean2;
    }

    private void invokeAwareInterfaces(Object bean2) {
        if (bean2 instanceof EnvironmentAware) {
            ((EnvironmentAware)bean2).setEnvironment(this.applicationContext.getEnvironment());
        }
        if (bean2 instanceof EmbeddedValueResolverAware) {
            ((EmbeddedValueResolverAware)bean2).setEmbeddedValueResolver(this.embeddedValueResolver);
        }
        if (bean2 instanceof ResourceLoaderAware) {
            ((ResourceLoaderAware)bean2).setResourceLoader(this.applicationContext);
        }
        if (bean2 instanceof ApplicationEventPublisherAware) {
            ((ApplicationEventPublisherAware)bean2).setApplicationEventPublisher(this.applicationContext);
        }
        if (bean2 instanceof MessageSourceAware) {
            ((MessageSourceAware)bean2).setMessageSource(this.applicationContext);
        }
        if (bean2 instanceof ApplicationStartupAware) {
            ((ApplicationStartupAware)bean2).setApplicationStartup(this.applicationContext.getApplicationStartup());
        }
        if (bean2 instanceof ApplicationContextAware) {
            ((ApplicationContextAware)bean2).setApplicationContext(this.applicationContext);
        }
    }
}

