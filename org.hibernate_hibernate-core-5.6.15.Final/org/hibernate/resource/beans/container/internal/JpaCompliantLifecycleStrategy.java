/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.inject.spi.AnnotatedType
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.BeanManager
 *  javax.enterprise.inject.spi.InjectionTarget
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.beans.container.internal;

import java.lang.annotation.Annotation;
import java.util.Set;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;
import org.hibernate.resource.beans.container.internal.CdiBasedBeanContainer;
import org.hibernate.resource.beans.container.internal.NamedBeanQualifier;
import org.hibernate.resource.beans.container.internal.NotYetReadyException;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.jboss.logging.Logger;

public class JpaCompliantLifecycleStrategy
implements BeanLifecycleStrategy {
    private static final Logger log = Logger.getLogger(JpaCompliantLifecycleStrategy.class);
    public static final JpaCompliantLifecycleStrategy INSTANCE = new JpaCompliantLifecycleStrategy();

    private JpaCompliantLifecycleStrategy() {
    }

    @Override
    public <B> ContainedBeanImplementor<B> createBean(Class<B> beanClass, BeanInstanceProducer fallbackProducer, BeanContainer beanContainer) {
        return new BeanImpl<B>(beanClass, fallbackProducer, ((CdiBasedBeanContainer)beanContainer).getUsableBeanManager());
    }

    @Override
    public <B> ContainedBeanImplementor<B> createBean(String beanName, Class<B> beanClass, BeanInstanceProducer fallbackProducer, BeanContainer beanContainer) {
        return new NamedBeanImpl(beanName, beanClass, fallbackProducer, ((CdiBasedBeanContainer)beanContainer).getUsableBeanManager());
    }

    private static class NamedBeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final Class<B> beanType;
        private final String beanName;
        private BeanInstanceProducer fallbackProducer;
        private BeanManager beanManager;
        private Bean<B> bean;
        private CreationalContext<B> creationalContext;
        private B beanInstance;

        private NamedBeanImpl(String beanName, Class<B> beanType, BeanInstanceProducer fallbackProducer, BeanManager beanManager) {
            this.beanName = beanName;
            this.beanType = beanType;
            this.fallbackProducer = fallbackProducer;
            this.beanManager = beanManager;
        }

        @Override
        public B getBeanInstance() {
            if (this.beanInstance == null) {
                this.initialize();
            }
            return this.beanInstance;
        }

        @Override
        public void initialize() {
            if (this.beanInstance != null) {
                return;
            }
            try {
                this.creationalContext = this.beanManager.createCreationalContext(null);
            }
            catch (Exception e) {
                throw new NotYetReadyException(e);
            }
            try {
                Set beans = this.beanManager.getBeans(this.beanType, new Annotation[]{new NamedBeanQualifier(this.beanName)});
                this.bean = this.beanManager.resolve(beans);
                this.beanInstance = this.bean.create(this.creationalContext);
            }
            catch (Exception e) {
                log.debugf("Error resolving CDI bean [%s] - using fallback", new Object[0]);
                this.beanInstance = this.fallbackProducer.produceBeanInstance(this.beanName, this.beanType);
                try {
                    if (this.creationalContext != null) {
                        this.creationalContext.release();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.creationalContext = null;
                this.bean = null;
            }
        }

        @Override
        public void release() {
            if (this.beanInstance == null) {
                return;
            }
            try {
                if (this.bean == null) {
                    return;
                }
                this.bean.destroy(this.beanInstance, this.creationalContext);
            }
            catch (Exception exception) {
            }
            finally {
                if (this.creationalContext != null) {
                    try {
                        this.creationalContext.release();
                    }
                    catch (Exception exception) {}
                }
                this.beanInstance = null;
                this.creationalContext = null;
                this.bean = null;
                this.beanManager = null;
            }
        }
    }

    private static class BeanImpl<B>
    implements ContainedBeanImplementor<B> {
        private final Class<B> beanType;
        private BeanInstanceProducer fallbackProducer;
        private BeanManager beanManager;
        private InjectionTarget<B> injectionTarget;
        private CreationalContext<B> creationalContext;
        private B beanInstance;

        public BeanImpl(Class<B> beanType, BeanInstanceProducer fallbackProducer, BeanManager beanManager) {
            this.beanType = beanType;
            this.fallbackProducer = fallbackProducer;
            this.beanManager = beanManager;
        }

        @Override
        public B getBeanInstance() {
            if (this.beanInstance == null) {
                this.initialize();
            }
            return this.beanInstance;
        }

        @Override
        public void initialize() {
            AnnotatedType annotatedType;
            if (this.beanInstance != null) {
                return;
            }
            try {
                annotatedType = this.beanManager.createAnnotatedType(this.beanType);
            }
            catch (Exception e) {
                throw new IllegalStateException((Throwable)((Object)new NotYetReadyException(e)));
            }
            try {
                this.injectionTarget = this.beanManager.getInjectionTargetFactory(annotatedType).createInjectionTarget((Bean)null);
                this.creationalContext = this.beanManager.createCreationalContext(null);
                this.beanInstance = this.injectionTarget.produce(this.creationalContext);
                this.injectionTarget.inject(this.beanInstance, this.creationalContext);
                this.injectionTarget.postConstruct(this.beanInstance);
            }
            catch (NotYetReadyException e) {
                throw e;
            }
            catch (Exception e) {
                log.debugf("Error resolving CDI bean [%s] - using fallback", this.beanType);
                this.beanInstance = this.fallbackProducer.produceBeanInstance(this.beanType);
                try {
                    if (this.creationalContext != null) {
                        this.creationalContext.release();
                    }
                }
                catch (Exception exception) {
                    // empty catch block
                }
                this.creationalContext = null;
                this.injectionTarget = null;
            }
            this.beanManager = null;
        }

        @Override
        public void release() {
            if (this.beanInstance == null) {
                return;
            }
            try {
                if (this.injectionTarget == null) {
                    return;
                }
                this.injectionTarget.preDestroy(this.beanInstance);
                this.injectionTarget.dispose(this.beanInstance);
                this.creationalContext.release();
            }
            catch (Exception exception) {
            }
            finally {
                this.beanInstance = null;
                this.creationalContext = null;
                this.injectionTarget = null;
            }
        }
    }
}

