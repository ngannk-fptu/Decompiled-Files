/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.BeanUtils
 *  org.springframework.beans.factory.BeanFactory
 *  org.springframework.beans.factory.BeanFactoryAware
 *  org.springframework.beans.factory.config.AutowireCapableBeanFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.web.socket.handler;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BeanCreatingHandlerProvider<T>
implements BeanFactoryAware {
    private final Class<? extends T> handlerType;
    @Nullable
    private AutowireCapableBeanFactory beanFactory;

    public BeanCreatingHandlerProvider(Class<? extends T> handlerType) {
        Assert.notNull(handlerType, (String)"handlerType must not be null");
        this.handlerType = handlerType;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (beanFactory instanceof AutowireCapableBeanFactory) {
            this.beanFactory = (AutowireCapableBeanFactory)beanFactory;
        }
    }

    public void destroy(T handler) {
        if (this.beanFactory != null) {
            this.beanFactory.destroyBean(handler);
        }
    }

    public Class<? extends T> getHandlerType() {
        return this.handlerType;
    }

    public T getHandler() {
        if (this.beanFactory != null) {
            return (T)this.beanFactory.createBean(this.handlerType);
        }
        return (T)BeanUtils.instantiateClass(this.handlerType);
    }

    public String toString() {
        return "BeanCreatingHandlerProvider[handlerType=" + this.handlerType + "]";
    }
}

