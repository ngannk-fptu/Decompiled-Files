/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations.common.reflection.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.common.reflection.Filter;
import org.hibernate.annotations.common.reflection.ReflectionUtil;
import org.hibernate.annotations.common.reflection.XClass;
import org.hibernate.annotations.common.reflection.XMethod;
import org.hibernate.annotations.common.reflection.XProperty;
import org.hibernate.annotations.common.reflection.java.JavaReflectionManager;
import org.hibernate.annotations.common.reflection.java.JavaXAnnotatedElement;
import org.hibernate.annotations.common.reflection.java.generics.CompoundTypeEnvironment;
import org.hibernate.annotations.common.reflection.java.generics.TypeEnvironment;

final class JavaXClass
extends JavaXAnnotatedElement
implements XClass {
    private final TypeEnvironment context;
    private final Class clazz;

    public JavaXClass(Class clazz, TypeEnvironment env, JavaReflectionManager factory) {
        super(clazz, factory);
        this.clazz = clazz;
        this.context = env;
    }

    @Override
    public String getName() {
        return this.toClass().getName();
    }

    @Override
    public XClass getSuperclass() {
        return this.getFactory().toXClass(this.toClass().getSuperclass(), CompoundTypeEnvironment.create(this.getTypeEnvironment(), this.getFactory().getTypeEnvironment(this.toClass())));
    }

    @Override
    public XClass[] getInterfaces() {
        Class<?>[] classes = this.toClass().getInterfaces();
        int length = classes.length;
        XClass[] xClasses = new XClass[length];
        if (length != 0) {
            TypeEnvironment environment = CompoundTypeEnvironment.create(this.getTypeEnvironment(), this.getFactory().getTypeEnvironment(this.toClass()));
            for (int index = 0; index < length; ++index) {
                xClasses[index] = this.getFactory().toXClass(classes[index], environment);
            }
        }
        return xClasses;
    }

    @Override
    public boolean isInterface() {
        return this.toClass().isInterface();
    }

    @Override
    public boolean isAbstract() {
        return Modifier.isAbstract(this.toClass().getModifiers());
    }

    @Override
    public boolean isPrimitive() {
        return this.toClass().isPrimitive();
    }

    @Override
    public boolean isEnum() {
        return this.toClass().isEnum();
    }

    private List<XProperty> getDeclaredFieldProperties(Filter filter) {
        Field[] declaredFields = this.toClass().getDeclaredFields();
        ArrayList<XProperty> result = new ArrayList<XProperty>();
        for (Field f : declaredFields) {
            if (!ReflectionUtil.isProperty(f, this.getTypeEnvironment().bind(f.getGenericType()), filter)) continue;
            result.add(this.getFactory().getXProperty(f, this.getTypeEnvironment()));
        }
        result.trimToSize();
        return result;
    }

    private List<XProperty> getDeclaredMethodProperties(Filter filter) {
        Method[] declaredMethods;
        ArrayList<XProperty> result = new ArrayList<XProperty>();
        for (Method m : declaredMethods = this.toClass().getDeclaredMethods()) {
            if (!ReflectionUtil.isProperty(m, this.getTypeEnvironment().bind(m.getGenericReturnType()), filter)) continue;
            result.add(this.getFactory().getXProperty(m, this.getTypeEnvironment()));
        }
        result.trimToSize();
        return result;
    }

    @Override
    public List<XProperty> getDeclaredProperties(String accessType) {
        return this.getDeclaredProperties(accessType, XClass.DEFAULT_FILTER);
    }

    @Override
    public List<XProperty> getDeclaredProperties(String accessType, Filter filter) {
        if (accessType.equals("field")) {
            return this.getDeclaredFieldProperties(filter);
        }
        if (accessType.equals("property")) {
            return this.getDeclaredMethodProperties(filter);
        }
        throw new IllegalArgumentException("Unknown access type " + accessType);
    }

    @Override
    public List<XMethod> getDeclaredMethods() {
        Method[] declaredMethods = this.toClass().getDeclaredMethods();
        ArrayList<XMethod> result = new ArrayList<XMethod>(declaredMethods.length);
        for (Method m : declaredMethods) {
            result.add(this.getFactory().getXMethod(m, this.getTypeEnvironment()));
        }
        return result;
    }

    public Class<?> toClass() {
        return this.clazz;
    }

    @Override
    public boolean isAssignableFrom(XClass c) {
        return this.toClass().isAssignableFrom(((JavaXClass)c).toClass());
    }

    boolean isArray() {
        return this.toClass().isArray();
    }

    TypeEnvironment getTypeEnvironment() {
        return this.context;
    }

    @Override
    public String toString() {
        return this.getName();
    }
}

