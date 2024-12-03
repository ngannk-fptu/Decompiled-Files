/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.hibernate.resource.beans.container.spi.BeanContainer
 *  org.hibernate.resource.beans.container.spi.BeanContainer$LifecycleOptions
 *  org.hibernate.resource.beans.container.spi.ContainedBean
 *  org.hibernate.resource.beans.spi.BeanInstanceProducer
 *  org.springframework.beans.BeansException
 *  org.springframework.beans.factory.BeanCreationException
 *  org.springframework.beans.factory.config.ConfigurableListableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ConcurrentReferenceHashMap
 */
package org.springframework.orm.hibernate5;

import java.util.Map;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public final class SpringBeanContainer
implements BeanContainer {
    private static final Log logger = LogFactory.getLog(SpringBeanContainer.class);
    private final ConfigurableListableBeanFactory beanFactory;
    private final Map<Object, SpringContainedBean<?>> beanCache = new ConcurrentReferenceHashMap();

    public SpringBeanContainer(ConfigurableListableBeanFactory beanFactory) {
        Assert.notNull((Object)beanFactory, (String)"ConfigurableListableBeanFactory is required");
        this.beanFactory = beanFactory;
    }

    public <B> ContainedBean<B> getBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        SpringContainedBean<?> bean;
        if (lifecycleOptions.canUseCachedReferences()) {
            bean = this.beanCache.get(beanType);
            if (bean == null) {
                bean = this.createBean(beanType, lifecycleOptions, fallbackProducer);
                this.beanCache.put(beanType, bean);
            }
        } else {
            bean = this.createBean(beanType, lifecycleOptions, fallbackProducer);
        }
        return bean;
    }

    public <B> ContainedBean<B> getBean(String name, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        SpringContainedBean<?> bean;
        if (lifecycleOptions.canUseCachedReferences()) {
            bean = this.beanCache.get(name);
            if (bean == null) {
                bean = this.createBean(name, beanType, lifecycleOptions, fallbackProducer);
                this.beanCache.put(name, bean);
            }
        } else {
            bean = this.createBean(name, beanType, lifecycleOptions, fallbackProducer);
        }
        return bean;
    }

    public void stop() {
        this.beanCache.values().forEach(SpringContainedBean::destroyIfNecessary);
        this.beanCache.clear();
    }

    private SpringContainedBean<?> createBean(Class<?> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        try {
            if (lifecycleOptions.useJpaCompliantCreation()) {
                return new SpringContainedBean<Object>(this.beanFactory.createBean(beanType, 3, false), arg_0 -> ((ConfigurableListableBeanFactory)this.beanFactory).destroyBean(arg_0));
            }
            return new SpringContainedBean<Object>(this.beanFactory.getBean(beanType));
        }
        catch (BeansException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Falling back to Hibernate's default producer after bean creation failure for " + beanType + ": " + (Object)((Object)ex)));
            }
            try {
                return new SpringContainedBean<Object>(fallbackProducer.produceBeanInstance(beanType));
            }
            catch (RuntimeException ex2) {
                if (ex instanceof BeanCreationException) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Fallback producer failed for " + beanType + ": " + ex2));
                    }
                    throw ex;
                }
                throw ex2;
            }
        }
    }

    private SpringContainedBean<?> createBean(String name, Class<?> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
        try {
            if (lifecycleOptions.useJpaCompliantCreation()) {
                if (this.beanFactory.containsBean(name)) {
                    Object bean = this.beanFactory.autowire(beanType, 3, false);
                    this.beanFactory.autowireBeanProperties(bean, 0, false);
                    this.beanFactory.applyBeanPropertyValues(bean, name);
                    bean = this.beanFactory.initializeBean(bean, name);
                    return new SpringContainedBean<Object>(bean, beanInstance -> this.beanFactory.destroyBean(name, beanInstance));
                }
                return new SpringContainedBean<Object>(this.beanFactory.createBean(beanType, 3, false), arg_0 -> ((ConfigurableListableBeanFactory)this.beanFactory).destroyBean(arg_0));
            }
            return this.beanFactory.containsBean(name) ? new SpringContainedBean<Object>(this.beanFactory.getBean(name, beanType)) : new SpringContainedBean<Object>(this.beanFactory.getBean(beanType));
        }
        catch (BeansException ex) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("Falling back to Hibernate's default producer after bean creation failure for " + beanType + " with name '" + name + "': " + (Object)((Object)ex)));
            }
            try {
                return new SpringContainedBean<Object>(fallbackProducer.produceBeanInstance(name, beanType));
            }
            catch (RuntimeException ex2) {
                if (ex instanceof BeanCreationException) {
                    if (logger.isDebugEnabled()) {
                        logger.debug((Object)("Fallback producer failed for " + beanType + " with name '" + name + "': " + ex2));
                    }
                    throw ex;
                }
                throw ex2;
            }
        }
    }

    private static final class SpringContainedBean<B>
    implements ContainedBean<B> {
        private final B beanInstance;
        @Nullable
        private Consumer<B> destructionCallback;

        public SpringContainedBean(B beanInstance) {
            this.beanInstance = beanInstance;
        }

        public SpringContainedBean(B beanInstance, Consumer<B> destructionCallback) {
            this.beanInstance = beanInstance;
            this.destructionCallback = destructionCallback;
        }

        public B getBeanInstance() {
            return this.beanInstance;
        }

        public void destroyIfNecessary() {
            if (this.destructionCallback != null) {
                this.destructionCallback.accept(this.beanInstance);
            }
        }
    }
}

