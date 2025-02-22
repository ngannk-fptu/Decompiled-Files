/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.jpa.event.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hibernate.jpa.event.internal.AbstractCallback;
import org.hibernate.jpa.event.spi.Callback;
import org.hibernate.jpa.event.spi.CallbackDefinition;
import org.hibernate.jpa.event.spi.CallbackType;
import org.hibernate.resource.beans.spi.ManagedBean;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;

class ListenerCallback
extends AbstractCallback {
    private final Method callbackMethod;
    private final ManagedBean<?> listenerManagedBean;

    ListenerCallback(ManagedBean<?> listenerManagedBean, Method callbackMethod, CallbackType callbackType) {
        super(callbackType);
        this.listenerManagedBean = listenerManagedBean;
        this.callbackMethod = callbackMethod;
    }

    @Override
    public boolean performCallback(Object entity) {
        try {
            this.callbackMethod.invoke(this.listenerManagedBean.getBeanInstance(), entity);
            return true;
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Definition
    implements CallbackDefinition {
        private final Class<?> listenerClass;
        private final Method callbackMethod;
        private final CallbackType callbackType;

        public Definition(Class<?> listenerClass, Method callbackMethod, CallbackType callbackType) {
            this.listenerClass = listenerClass;
            this.callbackMethod = callbackMethod;
            this.callbackType = callbackType;
        }

        @Override
        public Callback createCallback(ManagedBeanRegistry beanRegistry) {
            return new ListenerCallback(beanRegistry.getBean(this.listenerClass), this.callbackMethod, this.callbackType);
        }
    }
}

