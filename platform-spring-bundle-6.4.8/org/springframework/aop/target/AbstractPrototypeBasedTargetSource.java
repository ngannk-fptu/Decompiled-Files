/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.aop.target;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import org.springframework.aop.target.AbstractBeanFactoryBasedTargetSource;
import org.springframework.aop.target.EmptyTargetSource;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;

public abstract class AbstractPrototypeBasedTargetSource
extends AbstractBeanFactoryBasedTargetSource {
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        super.setBeanFactory(beanFactory);
        if (!beanFactory.isPrototype(this.getTargetBeanName())) {
            throw new BeanDefinitionStoreException("Cannot use prototype-based TargetSource against non-prototype bean with name '" + this.getTargetBeanName() + "': instances would not be independent");
        }
    }

    protected Object newPrototypeInstance() throws BeansException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Creating new instance of bean '" + this.getTargetBeanName() + "'"));
        }
        return this.getBeanFactory().getBean(this.getTargetBeanName());
    }

    protected void destroyPrototypeInstance(Object target) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Destroying instance of bean '" + this.getTargetBeanName() + "'"));
        }
        if (this.getBeanFactory() instanceof ConfigurableBeanFactory) {
            ((ConfigurableBeanFactory)this.getBeanFactory()).destroyBean(this.getTargetBeanName(), target);
        } else if (target instanceof DisposableBean) {
            try {
                ((DisposableBean)target).destroy();
            }
            catch (Throwable ex) {
                this.logger.warn((Object)("Destroy method on bean with name '" + this.getTargetBeanName() + "' threw an exception"), ex);
            }
        }
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        throw new NotSerializableException("A prototype-based TargetSource itself is not deserializable - just a disconnected SingletonTargetSource or EmptyTargetSource is");
    }

    protected Object writeReplace() throws ObjectStreamException {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Disconnecting TargetSource [" + this + "]"));
        }
        try {
            Object target = this.getTarget();
            return target != null ? new SingletonTargetSource(target) : EmptyTargetSource.forClass(this.getTargetClass());
        }
        catch (Exception ex) {
            String msg = "Cannot get target for disconnecting TargetSource [" + this + "]";
            this.logger.error((Object)msg, (Throwable)ex);
            throw new NotSerializableException(msg + ": " + ex);
        }
    }
}

