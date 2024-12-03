/*
 * Decompiled with CFR 0.152.
 */
package nonapi.io.github.classgraph.reflection;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import nonapi.io.github.classgraph.reflection.ReflectionDriver;
import nonapi.io.github.classgraph.reflection.StandardReflectionDriver;

class NarcissusReflectionDriver
extends ReflectionDriver {
    private final Class<?> narcissusClass;
    private final Method getDeclaredMethods;
    private final Method findClass;
    private final Method getDeclaredConstructors;
    private final Method getDeclaredFields;
    private final Method getField;
    private final Method setField;
    private final Method getStaticField;
    private final Method setStaticField;
    private final Method invokeMethod;
    private final Method invokeStaticMethod;

    NarcissusReflectionDriver() throws Exception {
        StandardReflectionDriver drv = new StandardReflectionDriver();
        this.narcissusClass = drv.findClass("io.github.toolfactory.narcissus.Narcissus");
        if (!((Boolean)drv.getStaticField(drv.findStaticField(this.narcissusClass, "libraryLoaded"))).booleanValue()) {
            throw new IllegalArgumentException("Could not load Narcissus native library");
        }
        this.findClass = drv.findStaticMethod(this.narcissusClass, "findClass", String.class);
        this.getDeclaredMethods = drv.findStaticMethod(this.narcissusClass, "getDeclaredMethods", Class.class);
        this.getDeclaredConstructors = drv.findStaticMethod(this.narcissusClass, "getDeclaredConstructors", Class.class);
        this.getDeclaredFields = drv.findStaticMethod(this.narcissusClass, "getDeclaredFields", Class.class);
        this.getField = drv.findStaticMethod(this.narcissusClass, "getField", Object.class, Field.class);
        this.setField = drv.findStaticMethod(this.narcissusClass, "setField", Object.class, Field.class, Object.class);
        this.getStaticField = drv.findStaticMethod(this.narcissusClass, "getStaticField", Field.class);
        this.setStaticField = drv.findStaticMethod(this.narcissusClass, "setStaticField", Field.class, Object.class);
        this.invokeMethod = drv.findStaticMethod(this.narcissusClass, "invokeMethod", Object.class, Method.class, Object[].class);
        this.invokeStaticMethod = drv.findStaticMethod(this.narcissusClass, "invokeStaticMethod", Method.class, Object[].class);
    }

    @Override
    public boolean isAccessible(Object instance, AccessibleObject obj) {
        return true;
    }

    @Override
    public boolean makeAccessible(Object instance, AccessibleObject accessibleObject) {
        return true;
    }

    @Override
    Class<?> findClass(String className) throws Exception {
        return (Class)this.findClass.invoke(null, className);
    }

    @Override
    Method[] getDeclaredMethods(Class<?> cls) throws Exception {
        return (Method[])this.getDeclaredMethods.invoke(null, cls);
    }

    @Override
    <T> Constructor<T>[] getDeclaredConstructors(Class<T> cls) throws Exception {
        return (Constructor[])this.getDeclaredConstructors.invoke(null, cls);
    }

    @Override
    Field[] getDeclaredFields(Class<?> cls) throws Exception {
        return (Field[])this.getDeclaredFields.invoke(null, cls);
    }

    @Override
    Object getField(Object object, Field field) throws Exception {
        return this.getField.invoke(null, object, field);
    }

    @Override
    void setField(Object object, Field field, Object value) throws Exception {
        this.setField.invoke(null, object, field, value);
    }

    @Override
    Object getStaticField(Field field) throws Exception {
        return this.getStaticField.invoke(null, field);
    }

    @Override
    void setStaticField(Field field, Object value) throws Exception {
        this.setStaticField.invoke(null, field, value);
    }

    @Override
    Object invokeMethod(Object object, Method method, Object ... args) throws Exception {
        return this.invokeMethod.invoke(null, object, method, args);
    }

    @Override
    Object invokeStaticMethod(Method method, Object ... args) throws Exception {
        return this.invokeStaticMethod.invoke(null, method, args);
    }
}

