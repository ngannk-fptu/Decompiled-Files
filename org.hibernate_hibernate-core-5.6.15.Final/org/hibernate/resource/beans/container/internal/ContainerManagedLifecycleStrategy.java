/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.enterprise.context.ContextNotActiveException
 *  javax.enterprise.inject.Instance
 *  javax.enterprise.inject.spi.BeanManager
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.beans.container.internal;

import java.lang.annotation.Annotation;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.BeanManager;
import org.hibernate.resource.beans.container.internal.CdiBasedBeanContainer;
import org.hibernate.resource.beans.container.internal.NamedBeanQualifier;
import org.hibernate.resource.beans.container.internal.NoSuchBeanException;
import org.hibernate.resource.beans.container.internal.NotYetReadyException;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.BeanLifecycleStrategy;
import org.hibernate.resource.beans.container.spi.ContainedBeanImplementor;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.jboss.logging.Logger;

public class ContainerManagedLifecycleStrategy
implements BeanLifecycleStrategy {
    private static final Logger log = Logger.getLogger(ContainerManagedLifecycleStrategy.class);
    public static final ContainerManagedLifecycleStrategy INSTANCE = new ContainerManagedLifecycleStrategy();

    private ContainerManagedLifecycleStrategy() {
    }

    @Override
    public <B> ContainedBeanImplementor<B> createBean(Class<B> beanClass, BeanInstanceProducer fallbackProducer, BeanContainer beanContainer) {
        return new BeanImpl(beanClass, fallbackProducer, ((CdiBasedBeanContainer)beanContainer).getUsableBeanManager());
    }

    @Override
    public <B> ContainedBeanImplementor<B> createBean(String beanName, Class<B> beanClass, BeanInstanceProducer fallbackProducer, BeanContainer beanContainer) {
        return new NamedBeanImpl(beanName, beanClass, fallbackProducer, ((CdiBasedBeanContainer)beanContainer).getUsableBeanManager());
    }

    private static class NamedBeanImpl<B>
    extends AbstractBeanImpl<B> {
        private final String beanName;

        private NamedBeanImpl(String beanName, Class<B> beanType, BeanInstanceProducer fallbackProducer, BeanManager beanManager) {
            super(beanType, fallbackProducer, beanManager);
            this.beanName = beanName;
        }

        @Override
        protected Instance<B> resolveContainerInstance() {
            Instance root;
            try {
                root = this.beanManager.createInstance();
            }
            catch (Exception e) {
                throw new NotYetReadyException(e);
            }
            try {
                return root.select(this.beanType, new Annotation[]{new NamedBeanQualifier(this.beanName)});
            }
            catch (Exception e) {
                throw new NoSuchBeanException("Bean class not known to CDI : " + this.beanType.getName(), e);
            }
        }

        @Override
        protected B produceFallbackInstance() {
            return this.fallbackProducer.produceBeanInstance(this.beanName, this.beanType);
        }
    }

    private static class BeanImpl<B>
    extends AbstractBeanImpl<B> {
        private BeanImpl(Class<B> beanType, BeanInstanceProducer fallbackProducer, BeanManager beanManager) {
            super(beanType, fallbackProducer, beanManager);
        }

        @Override
        protected Instance<B> resolveContainerInstance() {
            Instance root;
            try {
                root = this.beanManager.createInstance();
            }
            catch (Exception e) {
                throw new NotYetReadyException(e);
            }
            try {
                return root.select(this.beanType, new Annotation[0]);
            }
            catch (Exception e) {
                throw new NoSuchBeanException("Bean class not known to CDI : " + this.beanType.getName(), e);
            }
        }

        @Override
        protected B produceFallbackInstance() {
            return this.fallbackProducer.produceBeanInstance(this.beanType);
        }
    }

    private static abstract class AbstractBeanImpl<B>
    implements ContainedBeanImplementor<B> {
        final Class<B> beanType;
        BeanInstanceProducer fallbackProducer;
        BeanManager beanManager;
        Instance<B> instance;
        B beanInstance;

        private AbstractBeanImpl(Class<B> beanType, BeanInstanceProducer fallbackProducer, BeanManager beanManager) {
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
                this.instance = this.resolveContainerInstance();
                this.beanInstance = this.instance.get();
            }
            catch (NotYetReadyException e) {
                throw e;
            }
            catch (Exception e) {
                log.debugf("Error resolving CDI bean [%s] - using fallback", new Object[0]);
                this.beanInstance = this.produceFallbackInstance();
                this.instance = null;
            }
            this.beanManager = null;
        }

        protected abstract Instance<B> resolveContainerInstance();

        @Override
        public void release() {
            if (this.beanInstance == null) {
                return;
            }
            try {
                if (this.instance == null) {
                    return;
                }
                this.instance.destroy(this.beanInstance);
            }
            catch (ContextNotActiveException e) {
                log.debugf("Error destroying managed bean instance [%s] - the context is not active anymore. The instance must have been destroyed already - ignoring.", this.instance, (Object)e);
            }
            finally {
                this.beanInstance = null;
                this.instance = null;
                this.beanManager = null;
                this.fallbackProducer = null;
            }
        }

        protected abstract B produceFallbackInstance();
    }
}

