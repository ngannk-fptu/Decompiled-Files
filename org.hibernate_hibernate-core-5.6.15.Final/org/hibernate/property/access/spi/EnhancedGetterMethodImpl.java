/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Map;
import org.hibernate.PropertyAccessException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccessSerializationException;

@Deprecated
public class EnhancedGetterMethodImpl
implements Getter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EnhancedGetterMethodImpl.class);
    private final Class containerClass;
    private final String propertyName;
    private final Field field;
    private final Method getterMethod;

    public EnhancedGetterMethodImpl(Class containerClass, String propertyName, Field field, Method getterMethod) {
        this.containerClass = containerClass;
        this.propertyName = propertyName;
        this.field = field;
        this.getterMethod = getterMethod;
    }

    @Override
    public Object get(Object owner) {
        try {
            return this.field.get(owner);
        }
        catch (IllegalAccessException iae) {
            throw new PropertyAccessException(iae, "IllegalAccessException occurred while calling", false, this.containerClass, this.propertyName);
        }
        catch (IllegalArgumentException iae) {
            LOG.illegalPropertyGetterArgument(this.containerClass.getName(), this.propertyName);
            throw new PropertyAccessException(iae, "IllegalArgumentException occurred calling", false, this.containerClass, this.propertyName);
        }
    }

    @Override
    public Object getForInsert(Object owner, Map mergeMap, SharedSessionContractImplementor session) {
        return this.get(owner);
    }

    @Override
    public Class getReturnType() {
        return this.getterMethod.getReturnType();
    }

    @Override
    public Member getMember() {
        return this.getterMethod;
    }

    @Override
    public String getMethodName() {
        return this.getterMethod.getName();
    }

    @Override
    public Method getMethod() {
        return this.getterMethod;
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerialForm(this.containerClass, this.propertyName, this.getterMethod);
    }

    private static class SerialForm
    implements Serializable {
        private final Class containerClass;
        private final String propertyName;
        private final Class declaringClass;
        private final String methodName;

        private SerialForm(Class containerClass, String propertyName, Method method) {
            this.containerClass = containerClass;
            this.propertyName = propertyName;
            this.declaringClass = method.getDeclaringClass();
            this.methodName = method.getName();
        }

        private Object readResolve() {
            return new EnhancedGetterMethodImpl(this.containerClass, this.propertyName, this.resolveField(), this.resolveMethod());
        }

        private Method resolveMethod() {
            try {
                return this.declaringClass.getDeclaredMethod(this.methodName, new Class[0]);
            }
            catch (NoSuchMethodException e) {
                throw new PropertyAccessSerializationException("Unable to resolve getter method on deserialization : " + this.declaringClass.getName() + "#" + this.methodName);
            }
        }

        private Field resolveField() {
            try {
                return this.declaringClass.getDeclaredField(this.propertyName);
            }
            catch (NoSuchFieldException e) {
                throw new PropertyAccessSerializationException("Unable to resolve field on deserialization : " + this.declaringClass.getName() + "#" + this.propertyName);
            }
        }
    }
}

