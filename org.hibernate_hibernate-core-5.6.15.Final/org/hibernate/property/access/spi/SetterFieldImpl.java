/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import org.hibernate.PropertyAccessException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.internal.AbstractFieldSerialForm;
import org.hibernate.property.access.spi.Setter;

public class SetterFieldImpl
implements Setter {
    private final Class containerClass;
    private final String propertyName;
    private final Field field;
    private final Method setterMethod;

    public SetterFieldImpl(Class containerClass, String propertyName, Field field) {
        this.containerClass = containerClass;
        this.propertyName = propertyName;
        this.field = field;
        this.setterMethod = ReflectHelper.setterMethodOrNull(containerClass, propertyName, field.getType());
    }

    public Class getContainerClass() {
        return this.containerClass;
    }

    public String getPropertyName() {
        return this.propertyName;
    }

    protected Field getField() {
        return this.field;
    }

    @Override
    public void set(Object target, Object value, SessionFactoryImplementor factory) {
        try {
            this.field.set(target, value);
        }
        catch (Exception e) {
            if (value == null && this.field.getType().isPrimitive()) {
                throw new PropertyAccessException(e, String.format(Locale.ROOT, "Null value was assigned to a property [%s.%s] of primitive type", this.containerClass, this.propertyName), true, this.containerClass, this.propertyName);
            }
            throw new PropertyAccessException(e, String.format(Locale.ROOT, "Could not set field value [%s] value by reflection : [%s.%s]", value, this.containerClass, this.propertyName), true, this.containerClass, this.propertyName);
        }
    }

    @Override
    public String getMethodName() {
        return this.setterMethod != null ? this.setterMethod.getName() : null;
    }

    @Override
    public Method getMethod() {
        return this.setterMethod;
    }

    private Object writeReplace() {
        return new SerialForm(this.containerClass, this.propertyName, this.field);
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
            return new SetterFieldImpl(this.containerClass, this.propertyName, this.resolveField());
        }
    }
}

