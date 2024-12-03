/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.property.access.spi;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.hibernate.PropertyAccessException;
import org.hibernate.PropertySetterAccessException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.spi.PropertyAccessSerializationException;
import org.hibernate.property.access.spi.Setter;

public class SetterMethodImpl
implements Setter {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(SetterMethodImpl.class);
    private final Class containerClass;
    private final String propertyName;
    private final Method setterMethod;
    private final boolean isPrimitive;

    public SetterMethodImpl(Class containerClass, String propertyName, Method setterMethod) {
        this.containerClass = containerClass;
        this.propertyName = propertyName;
        this.setterMethod = setterMethod;
        this.isPrimitive = setterMethod.getParameterTypes()[0].isPrimitive();
    }

    @Override
    public void set(Object target, Object value, SessionFactoryImplementor factory) {
        try {
            this.setterMethod.invoke(target, value);
        }
        catch (NullPointerException npe) {
            if (value == null && this.isPrimitive) {
                throw new PropertyAccessException(npe, "Null value was assigned to a property of primitive type", true, this.containerClass, this.propertyName);
            }
            throw new PropertyAccessException(npe, "NullPointerException occurred while calling", true, this.containerClass, this.propertyName);
        }
        catch (InvocationTargetException ite) {
            throw new PropertyAccessException(ite, "Exception occurred inside", true, this.containerClass, this.propertyName);
        }
        catch (IllegalAccessException iae) {
            throw new PropertyAccessException(iae, "IllegalAccessException occurred while calling", true, this.containerClass, this.propertyName);
        }
        catch (IllegalArgumentException iae) {
            if (value == null && this.isPrimitive) {
                throw new PropertyAccessException(iae, "Null value was assigned to a property of primitive type", true, this.containerClass, this.propertyName);
            }
            Class<?> expectedType = this.setterMethod.getParameterTypes()[0];
            LOG.illegalPropertySetterArgument(this.containerClass.getName(), this.propertyName);
            LOG.expectedType(expectedType.getName(), value == null ? null : value.getClass().getName());
            throw new PropertySetterAccessException(iae, this.containerClass, this.propertyName, expectedType, target, value);
        }
    }

    @Override
    public String getMethodName() {
        return this.setterMethod.getName();
    }

    @Override
    public Method getMethod() {
        return this.setterMethod;
    }

    private Object writeReplace() {
        return new SerialForm(this.containerClass, this.propertyName, this.setterMethod);
    }

    private static class SerialForm
    implements Serializable {
        private final Class containerClass;
        private final String propertyName;
        private final Class declaringClass;
        private final String methodName;
        private final Class argumentType;

        private SerialForm(Class containerClass, String propertyName, Method method) {
            this.containerClass = containerClass;
            this.propertyName = propertyName;
            this.declaringClass = method.getDeclaringClass();
            this.methodName = method.getName();
            this.argumentType = method.getParameterTypes()[0];
        }

        private Object readResolve() {
            return new SetterMethodImpl(this.containerClass, this.propertyName, this.resolveMethod());
        }

        private Method resolveMethod() {
            try {
                Method method = this.declaringClass.getDeclaredMethod(this.methodName, this.argumentType);
                ReflectHelper.ensureAccessibility(method);
                return method;
            }
            catch (NoSuchMethodException e) {
                throw new PropertyAccessSerializationException("Unable to resolve setter method on deserialization : " + this.declaringClass.getName() + "#" + this.methodName + "(" + this.argumentType.getName() + ")");
            }
        }
    }
}

