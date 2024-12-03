/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PropertyDescriptor {
    private final String name;
    private final Method getter;
    private final Method setter;

    public PropertyDescriptor(String name, Method getter, Method setter) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Bad property name");
        }
        this.name = name;
        this.getter = this.checkGetter(getter);
        this.setter = this.checkSetter(setter);
    }

    public String getName() {
        return this.name;
    }

    public Method getReadMethod() {
        return this.getter;
    }

    public Method getWriteMethod() {
        return this.setter;
    }

    public Class<?> getPropertyType() {
        if (this.getter != null) {
            return this.getter.getReturnType();
        }
        if (this.setter != null) {
            Class<?>[] parameterTypes = this.setter.getParameterTypes();
            return parameterTypes[0];
        }
        return null;
    }

    private Method checkGetter(Method method) {
        if (method != null) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Modifier for getter method should be public");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 0) {
                throw new IllegalArgumentException("Number of parameters in getter method is not equal to 0");
            }
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(Void.TYPE)) {
                throw new IllegalArgumentException("Getter has return type void");
            }
            Class<?> propertyType = this.getPropertyType();
            if (propertyType != null && !returnType.equals(propertyType)) {
                throw new IllegalArgumentException("Parameter type in getter does not correspond to setter");
            }
        }
        return method;
    }

    private Method checkSetter(Method method) {
        if (method != null) {
            int modifiers = method.getModifiers();
            if (!Modifier.isPublic(modifiers)) {
                throw new IllegalArgumentException("Modifier for setter method should be public");
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            if (parameterTypes.length != 1) {
                throw new IllegalArgumentException("Number of parameters in setter method is not equal to 1");
            }
            Class<?> parameterType = parameterTypes[0];
            Class<?> propertyType = this.getPropertyType();
            if (propertyType != null && !propertyType.equals(parameterType)) {
                throw new IllegalArgumentException("Parameter type in setter does not correspond to getter");
            }
        }
        return method;
    }
}

