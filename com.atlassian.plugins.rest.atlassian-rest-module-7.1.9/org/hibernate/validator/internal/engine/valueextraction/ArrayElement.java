/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.validator.internal.engine.valueextraction;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedArrayType;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import org.hibernate.validator.internal.util.ReflectionHelper;

public class ArrayElement
implements TypeVariable<Class<?>> {
    private final Class<?> containerClass;

    public ArrayElement(AnnotatedArrayType annotatedArrayType) {
        Type arrayElementType = annotatedArrayType.getAnnotatedGenericComponentType().getType();
        this.containerClass = arrayElementType == Boolean.TYPE ? boolean[].class : (arrayElementType == Integer.TYPE ? int[].class : (arrayElementType == Long.TYPE ? long[].class : (arrayElementType == Double.TYPE ? double[].class : (arrayElementType == Float.TYPE ? float[].class : (arrayElementType == Byte.TYPE ? byte[].class : (arrayElementType == Short.TYPE ? short[].class : (arrayElementType == Character.TYPE ? char[].class : Object[].class)))))));
    }

    public ArrayElement(Type arrayType) {
        Class<?> arrayClass = ReflectionHelper.getClassFromType(arrayType);
        this.containerClass = arrayClass.getComponentType().isPrimitive() ? arrayClass : Object[].class;
    }

    @Override
    public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Annotation[] getAnnotations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Annotation[] getDeclaredAnnotations() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Type[] getBounds() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getGenericDeclaration() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public AnnotatedType[] getAnnotatedBounds() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getContainerClass() {
        return this.containerClass;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        ArrayElement other = (ArrayElement)obj;
        return this.containerClass.equals(other.containerClass);
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + this.containerClass.hashCode();
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getSimpleName()).append("<").append(this.containerClass).append(">");
        return sb.toString();
    }
}

