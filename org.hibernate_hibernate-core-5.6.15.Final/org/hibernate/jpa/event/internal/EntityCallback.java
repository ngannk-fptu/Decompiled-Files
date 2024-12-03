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
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;

final class EntityCallback
extends AbstractCallback {
    private final Method callbackMethod;

    private EntityCallback(Method callbackMethod, CallbackType callbackType) {
        super(callbackType);
        this.callbackMethod = callbackMethod;
    }

    @Override
    public boolean performCallback(Object entity) {
        try {
            this.callbackMethod.invoke(entity, new Object[0]);
            return true;
        }
        catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException)e.getTargetException();
            }
            throw new RuntimeException(e.getTargetException());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class Definition
    implements CallbackDefinition {
        private final Method callbackMethod;
        private final CallbackType callbackType;

        public Definition(Method callbackMethod, CallbackType callbackType) {
            this.callbackMethod = callbackMethod;
            this.callbackType = callbackType;
        }

        @Override
        public Callback createCallback(ManagedBeanRegistry beanRegistry) {
            return new EntityCallback(this.callbackMethod, this.callbackType);
        }
    }
}

