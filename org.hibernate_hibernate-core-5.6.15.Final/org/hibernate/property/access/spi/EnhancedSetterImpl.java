/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.Field;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.spi.CompositeOwner;
import org.hibernate.engine.spi.CompositeTracker;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.property.access.internal.AbstractFieldSerialForm;
import org.hibernate.property.access.spi.SetterFieldImpl;

public class EnhancedSetterImpl
extends SetterFieldImpl {
    private final String propertyName;

    public EnhancedSetterImpl(Class containerClass, String propertyName, Field field) {
        super(containerClass, propertyName, field);
        this.propertyName = propertyName;
    }

    @Override
    public void set(Object target, Object value, SessionFactoryImplementor factory) {
        super.set(target, value, factory);
        if (target instanceof CompositeOwner && value instanceof CompositeTracker) {
            ((CompositeTracker)value).$$_hibernate_setOwner(this.propertyName, (CompositeOwner)target);
        }
        ManagedTypeHelper.processIfPersistentAttributeInterceptable(target, EnhancedSetterImpl::setAttributeInitialized, this.propertyName);
    }

    private static void setAttributeInitialized(PersistentAttributeInterceptable target, String propertyName) {
        PersistentAttributeInterceptor interceptor = target.$$_hibernate_getInterceptor();
        if (interceptor instanceof BytecodeLazyAttributeInterceptor) {
            ((BytecodeLazyAttributeInterceptor)interceptor).attributeInitialized(propertyName);
        }
    }

    private Object writeReplace() {
        return new SerialForm(this.getContainerClass(), this.propertyName, this.getField());
    }

    private static class SerialForm
    extends AbstractFieldSerialForm
    implements Serializable {
        private final Class containerClass;
        private final String propertyName;

        private SerialForm(Class containerClass, String propertyName, Field field) {
            super(field);
            this.containerClass = containerClass;
            this.propertyName = propertyName;
        }

        private Object readResolve() {
            return new EnhancedSetterImpl(this.containerClass, this.propertyName, this.resolveField());
        }
    }
}

