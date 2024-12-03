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
import org.hibernate.property.access.spi.Getter;
import org.hibernate.resource.beans.spi.ManagedBeanRegistry;

final class EmbeddableCallback
extends AbstractCallback {
    private final Getter embeddableGetter;
    private final Method callbackMethod;

    private EmbeddableCallback(Getter embeddableGetter, Method callbackMethod, CallbackType callbackType) {
        super(callbackType);
        this.embeddableGetter = embeddableGetter;
        this.callbackMethod = callbackMethod;
    }

    @Override
    public boolean performCallback(Object entity) {
        try {
            Object embeddable = this.embeddableGetter.get(entity);
            if (embeddable != null) {
                this.callbackMethod.invoke(embeddable, new Object[0]);
            }
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
        private final Getter embeddableGetter;
        private final Method callbackMethod;
        private final CallbackType callbackType;

        public Definition(Getter embeddableGetter, Method callbackMethod, CallbackType callbackType) {
            this.embeddableGetter = embeddableGetter;
            this.callbackMethod = callbackMethod;
            this.callbackType = callbackType;
        }

        @Override
        public Callback createCallback(ManagedBeanRegistry beanRegistry) {
            return new EmbeddableCallback(this.embeddableGetter, this.callbackMethod, this.callbackType);
        }
    }
}

